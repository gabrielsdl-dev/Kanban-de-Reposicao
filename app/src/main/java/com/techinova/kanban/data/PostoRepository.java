package com.techinova.kanban.data;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.techinova.kanban.model.Posto;

import java.util.List;

/**
 * C1: postos de trabalho e seus insumos (coleção "postos").
 * Responsável: Pessoa A. (buscarPorId também é usado pela Pessoa B na tela Novo cartão.)
 */
public class PostoRepository {

    private final FirebaseFirestore db;

    public PostoRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * NC2: escuta a lista de postos em tempo real, ordenada pelo nome.
     *
     * @return o ListenerRegistration, que a tela/ViewModel DEVE remover no onStop() com .remove().
     */
    @Nullable
    public ListenerRegistration ouvirPostos(RepositorioCallback<List<Posto>> callback) {
        // TODO [C1/NC2] return db.collection(Constantes.COL_POSTOS)
        //      .orderBy(Constantes.CAMPO_NOME)
        //      .addSnapshotListener((snap, erro) -> {
        //          if (erro != null) { callback.onErro(erro); return; }
        //          callback.onSucesso(snap.toObjects(Posto.class));
        //      });
        return null;
    }

    /**
     * Cria (posto.getId() == null) ou atualiza (id preenchido) o posto.
     * Só a logística consegue: as regras recusam escrita de operador (critério de aceite C1).
     */
    public void salvar(Posto posto, RepositorioCallback<Void> callback) {
        // TODO [C1] Cada insumo precisa de um id único ANTES de salvar
        //  (ex.: db.collection(Constantes.COL_POSTOS).document().getId() gera um id aleatório,
        //  ou UUID.randomUUID().toString()). Não troque o id de insumos que já existiam:
        //  ele faz parte do id do cartão (postoId_insumoId).
        //  Novo posto:  db.collection(COL_POSTOS).add(posto)
        //  Edição:      db.collection(COL_POSTOS).document(posto.getId()).set(posto)
        //  .addOnSuccessListener(x -> callback.onSucesso(null)).addOnFailureListener(callback::onErro);
    }

    /** Busca um posto pelo id (formulário de edição e tela Novo cartão do operador). */
    public void buscarPorId(String postoId, RepositorioCallback<Posto> callback) {
        // TODO [C1/C2] db.collection(Constantes.COL_POSTOS).document(postoId).get()
        //  .addOnSuccessListener(doc -> callback.onSucesso(doc.toObject(Posto.class)))
        //  .addOnFailureListener(callback::onErro);
    }
}
