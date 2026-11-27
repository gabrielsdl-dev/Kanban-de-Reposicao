package com.techinova.kanban.data;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.util.Constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * C2-C6: cartões de reposição (coleção "cartoes") e histórico ("historico").
 *
 * Responsáveis:
 *  - Pessoa B: abrir(), ouvirDoPosto(), confirmar()
 *  - Pessoa C: ouvirPorStatus(), avancarStatus()
 *
 * Compatível com firestore.rules:
 *  - create: só operador, do próprio posto, status "aberto", id = postoId_insumoId, abertoPor = uid
 *  - update: só logística, aberto -> em_rota ou em_rota -> entregue
 *  - delete: só operador do posto, quando status == "entregue" (feito junto com o create em historico)
 */
public class CartaoRepository {

    private final FirebaseFirestore db;

    public CartaoRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /** C3: id do documento do cartão = postoId_insumoId. (Pronto: é a convenção do projeto.) */
    public static String gerarId(String postoId, String insumoId) {
        return postoId + Constantes.SEPARADOR_ID_CARTAO + insumoId;
    }

    /**
     * C2 + C3: abre um cartão com TRANSAÇÃO, garantindo que não haja dois do mesmo insumo no posto.
     *
     * Como funciona:
     *  1. O id do documento é postoId_insumoId. Então "o pedido do parafuso M8 no posto Montagem 3"
     *     sempre cai no MESMO documento.
     *  2. Dentro da transação, lemos esse documento. Se ele já existe, há um pedido em andamento:
     *     lançamos uma exceção com o código ALREADY_EXISTS e nada é gravado.
     *  3. Se não existe, gravamos o cartão. abertoEm fica null e o @ServerTimestamp do Cartao faz o
     *     servidor preencher com o horário dele (não o do celular).
     *
     * Por que transação e não "get() e depois set()"? Porque dois celulares poderiam ler "não existe"
     * ao mesmo tempo e os dois gravariam. Na transação, o Firestore refaz a leitura se o documento
     * mudou no meio do caminho, então só um consegue criar.
     */
    public void abrir(Cartao cartao, RepositorioCallback<Void> callback) {
        DocumentReference ref = db.collection(Constantes.COL_CARTOES)
                .document(gerarId(cartao.getPostoId(), cartao.getInsumoId()));

        db.runTransaction((Transaction.Function<Void>) transaction -> {
                    DocumentSnapshot snapshot = transaction.get(ref);
                    if (snapshot.exists()) {
                        throw new FirebaseFirestoreException(
                                Constantes.MSG_CARTAO_DUPLICADO,
                                FirebaseFirestoreException.Code.ALREADY_EXISTS);
                    }
                    cartao.setStatus(Status.ABERTO);
                    cartao.setAbertoEm(null); // o servidor preenche (@ServerTimestamp)
                    transaction.set(ref, cartao);
                    return null;
                })
                .addOnSuccessListener(x -> callback.onSucesso(null))
                .addOnFailureListener(callback::onErro);
    }

    /**
     * C4 + NC2: escuta os cartões de UMA coluna do quadro, em tempo real.
     * Ordem: mais urgente primeiro; empate -> o que está aberto há mais tempo primeiro.
     * Precisa do índice composto (firestore.indexes.json): status ASC + urgencia DESC + abertoEm ASC.
     *
     * @return o ListenerRegistration, que deve ser removido no onStop().
     */
    @Nullable
    public ListenerRegistration ouvirPorStatus(String status, RepositorioCallback<List<Cartao>> callback) {
        // TODO [C4/NC2] return db.collection(Constantes.COL_CARTOES)
        //      .whereEqualTo(Constantes.CAMPO_STATUS, status)
        //      .orderBy(Constantes.CAMPO_URGENCIA, Query.Direction.DESCENDING)
        //      .orderBy(Constantes.CAMPO_ABERTO_EM)
        //      .addSnapshotListener((snap, erro) -> {
        //          if (erro != null) { callback.onErro(erro); return; }
        //          callback.onSucesso(snap.toObjects(Cartao.class));
        //      });
        //  Se aparecer erro FAILED_PRECONDITION no Logcat, falta publicar o índice: o próprio erro
        //  traz um link que cria o índice no console.
        //  Dica (Pessoa B usou em ouvirDoPosto): converter com ServerTimestampBehavior.ESTIMATE
        //  faz o cartão recém-criado mostrar "agora" em vez de "enviando…".
        return null;
    }

    /**
     * C5 + NC2: escuta os cartões do posto do operador (todas as etapas), em tempo real.
     * Usa o índice composto postoId ASC + abertoEm ASC (está no firestore.indexes.json).
     *
     * @return o ListenerRegistration, que a tela remove no onStop() com .remove().
     */
    @Nullable
    public ListenerRegistration ouvirDoPosto(String postoId, RepositorioCallback<List<Cartao>> callback) {
        return db.collection(Constantes.COL_CARTOES)
                .whereEqualTo(Constantes.CAMPO_POSTO_ID, postoId)
                .orderBy(Constantes.CAMPO_ABERTO_EM)
                .addSnapshotListener((snapshot, erro) -> {
                    if (erro != null) {
                        callback.onErro(erro);
                        return;
                    }
                    if (snapshot == null) return;
                    callback.onSucesso(converter(snapshot.getDocuments()));
                });
    }

    /**
     * Converte os documentos em Cartao. ESTIMATE: enquanto o horário do servidor ainda não chegou
     * (cartão acabou de ser criado), usa uma estimativa local em vez de null.
     */
    private static List<Cartao> converter(List<DocumentSnapshot> documentos) {
        List<Cartao> lista = new ArrayList<>();
        for (DocumentSnapshot doc : documentos) {
            Cartao cartao = doc.toObject(Cartao.class, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE);
            if (cartao != null) lista.add(cartao);
        }
        return lista;
    }

    /**
     * C4 + C6: logística move o cartão para a próxima coluna e grava o horário da mudança.
     *  aberto  -> em_rota  : grava emRotaEm
     *  em_rota -> entregue : grava entregueEm
     *  entregue            : não faz nada (só o operador encerra, em confirmar()).
     */
    public void avancarStatus(Cartao cartao, RepositorioCallback<Void> callback) {
        // TODO [C4/C6] String proximo = Status.proximo(cartao.getStatus()); se null -> return.
        //  String campoHorario = Status.EM_ROTA.equals(proximo)
        //          ? Constantes.CAMPO_EM_ROTA_EM : Constantes.CAMPO_ENTREGUE_EM;
        //  db.collection(Constantes.COL_CARTOES).document(cartao.getId())
        //    .update(Constantes.CAMPO_STATUS, proximo,
        //            campoHorario, FieldValue.serverTimestamp())
        //    .addOnSuccessListener(x -> callback.onSucesso(null))
        //    .addOnFailureListener(callback::onErro);
        //  As regras só aceitam aberto->em_rota e em_rota->entregue, e só para a logística.
    }

    /**
     * C5: operador confirma o recebimento. Com WriteBatch (tudo ou nada):
     *  1) set em historico/{autoId} com todos os campos + concluidoEm (horário do servidor)
     *  2) delete em cartoes/{postoId_insumoId}
     * Assim o mesmo insumo pode ser pedido de novo (o id fica livre) e o histórico fica guardado.
     * Se uma das duas operações falhar, nenhuma acontece: nunca fica cartão "pela metade".
     */
    public void confirmar(Cartao cartao, RepositorioCallback<Void> callback) {
        if (!Status.ENTREGUE.equals(cartao.getStatus()) || cartao.getId() == null) {
            // As regras também recusariam; avisamos antes para não gastar uma ida ao servidor.
            callback.onErro(new IllegalStateException("Só é possível confirmar um cartão entregue"));
            return;
        }

        WriteBatch batch = db.batch();
        DocumentReference historicoRef = db.collection(Constantes.COL_HISTORICO).document(); // id automático
        DocumentReference cartaoRef = db.collection(Constantes.COL_CARTOES).document(cartao.getId());

        batch.set(historicoRef, cartao.toHistoricoMap()); // já inclui concluidoEm = serverTimestamp
        batch.delete(cartaoRef);

        batch.commit()
                .addOnSuccessListener(x -> callback.onSucesso(null))
                .addOnFailureListener(callback::onErro);
    }
}
