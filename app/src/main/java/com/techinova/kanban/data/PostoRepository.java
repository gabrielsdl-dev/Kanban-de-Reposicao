package com.techinova.kanban.data;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.techinova.kanban.model.Insumo;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.util.Constantes;

import java.util.List;
import java.util.UUID;

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
        return db.collection(Constantes.COL_POSTOS)
                .orderBy(Constantes.CAMPO_NOME)
                .addSnapshotListener((snap, erro) -> {
                    if (erro != null) {
                        callback.onErro(erro);
                        return;
                    }
                    if (snap != null) {
                        callback.onSucesso(snap.toObjects(Posto.class));
                    }
                });
    }

    /**
     * Cria (posto.getId() == null) ou atualiza (id preenchido) o posto.
     * Só a logística consegue: as regras recusam escrita de operador (critério de aceite C1).
     */
    public void salvar(Posto posto, RepositorioCallback<Void> callback) {
        if (posto.getInsumos() != null) {
            for (Insumo insumo : posto.getInsumos()) {
                if (insumo.getId() == null || insumo.getId().trim().isEmpty()) {
                    insumo.setId(UUID.randomUUID().toString());
                }
            }
        }

        if (posto.getId() == null || posto.getId().trim().isEmpty()) {
            db.collection(Constantes.COL_POSTOS)
                    .add(posto)
                    .addOnSuccessListener(docRef -> callback.onSucesso(null))
                    .addOnFailureListener(callback::onErro);
        } else {
            db.collection(Constantes.COL_POSTOS)
                    .document(posto.getId())
                    .set(posto)
                    .addOnSuccessListener(unused -> callback.onSucesso(null))
                    .addOnFailureListener(callback::onErro);
        }
    }

    /** Busca um posto pelo id (formulário de edição e tela Novo cartão do operador). */
    public void buscarPorId(String postoId, RepositorioCallback<Posto> callback) {
        db.collection(Constantes.COL_POSTOS)
                .document(postoId)
                .get()
                .addOnSuccessListener(doc -> callback.onSucesso(doc.toObject(Posto.class)))
                .addOnFailureListener(callback::onErro);
    }
}