package com.techinova.kanban.data;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.util.Constantes;
import com.google.firebase.firestore.FieldValue; //Ele é o import responsável por informar ao Firestore gravar a hora do servidor

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
     * O cartão chega com postoId, postoNome, insumoId, insumoNome, urgencia, abertoPor e status ABERTO
     * (use o construtor Cartao(...)). abertoEm fica null e o @ServerTimestamp preenche no servidor.
     */
    public void abrir(Cartao cartao, RepositorioCallback<Void> callback) {
        // TODO [C3] Usar db.runTransaction(): ler cartoes/{postoId_insumoId}.
        //  DocumentReference ref = db.collection(Constantes.COL_CARTOES)
        //          .document(gerarId(cartao.getPostoId(), cartao.getInsumoId()));
        //  db.runTransaction(transaction -> {
        //      DocumentSnapshot snap = transaction.get(ref);
        //      Se snapshot.exists() -> lançar new FirebaseFirestoreException(
        //              Constantes.MSG_CARTAO_DUPLICADO, FirebaseFirestoreException.Code.ALREADY_EXISTS);
        //      ("Já existe um pedido em andamento para este insumo"; o ErrosFirebase traduz pelo código)
        //      Senão -> transaction.set(ref, cartao) com status = Status.ABERTO
        //               e abertoEm = serverTimestamp (automático pelo @ServerTimestamp do Cartao).
        //      return null;
        //  })
        //  .addOnSuccessListener(x -> callback.onSucesso(null))
        //  .addOnFailureListener(callback::onErro);
        //  Obs.: transações NÃO funcionam offline; sem internet cai no onFailure (mostre a mensagem).
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
        return db.collection(Constantes.COL_CARTOES)
                .whereEqualTo(Constantes.CAMPO_STATUS, status)
                .orderBy(Constantes.CAMPO_URGENCIA, Query.Direction.DESCENDING)
                .orderBy(Constantes.CAMPO_ABERTO_EM, Query.Direction.ASCENDING)
                .addSnapshotListener((snap, erro) -> {
                    if(erro != null) {
                        callback.onErro(erro);
                        return;
                    }
                    if(snap != null) {
                        callback.onSucesso(snap.toObjects(Cartao.class));
                    }
                });
    }

    /**
     * C5 + NC2: escuta os cartões do posto do operador (todas as etapas), em tempo real.
     * Usa o índice composto postoId ASC + abertoEm ASC (já está no firestore.indexes.json).
     *
     * @return o ListenerRegistration, que deve ser removido no onStop().
     */
    @Nullable
    public ListenerRegistration ouvirDoPosto(String postoId, RepositorioCallback<List<Cartao>> callback) {
        // TODO [C5/NC2] return db.collection(Constantes.COL_CARTOES)
        //      .whereEqualTo(Constantes.CAMPO_POSTO_ID, postoId)
        //      .orderBy(Constantes.CAMPO_ABERTO_EM)
        //      .addSnapshotListener((snap, erro) -> { ...igual ao ouvirPorStatus... });
        return null;
    }

    /**
     * C4 + C6: logística move o cartão para a próxima coluna e grava o horário da mudança.
     *  aberto  -> em_rota  : grava emRotaEm
     *  em_rota -> entregue : grava entregueEm
     *  entregue            : não faz nada (só o operador encerra, em confirmar()).
     */
    public void avancarStatus(Cartao cartao, RepositorioCallback<Void> callback) {
        String proximo = Status.proximo(cartao.getStatus());
        if(proximo == null) return; //Confere se já foi entregue, caso sim, não tem para onde mais avançar o cartão

        String campoHorario = Status.EM_ROTA.equals(proximo)
               ? Constantes.CAMPO_EM_ROTA_EM
               : Constantes.CAMPO_ENTREGUE_EM;

        db.collection(Constantes.COL_CARTOES).document(cartao.getId())
                .update(Constantes.CAMPO_STATUS, proximo,
                        campoHorario, FieldValue.serverTimestamp())
                .addOnSuccessListener(x -> callback.onSucesso(null))
                .addOnFailureListener(callback::onErro);
    }

    /**
     * C5: operador confirma o recebimento. Com WriteBatch (tudo ou nada):
     *  1) set em historico/{autoId} com todos os campos + concluidoEm (serverTimestamp)
     *  2) delete em cartoes/{postoId_insumoId}
     * Assim o mesmo insumo pode ser pedido de novo e o histórico fica guardado (C3 / C6).
     */
    public void confirmar(Cartao cartao, RepositorioCallback<Void> callback) {
        // TODO [C5] Confirmação com WriteBatch: set em historico + delete em cartoes.
        //  WriteBatch batch = db.batch();
        //  DocumentReference histRef = db.collection(Constantes.COL_HISTORICO).document(); // autoId
        //  DocumentReference cartaoRef = db.collection(Constantes.COL_CARTOES).document(cartao.getId());
        //  batch.set(histRef, cartao.toHistoricoMap());   // já inclui concluidoEm = serverTimestamp
        //  batch.delete(cartaoRef);
        //  batch.commit()
        //       .addOnSuccessListener(x -> callback.onSucesso(null))
        //       .addOnFailureListener(callback::onErro);
        //  Só funciona se o status for "entregue" (regra de delete). Confira antes na tela também.
    }
}
