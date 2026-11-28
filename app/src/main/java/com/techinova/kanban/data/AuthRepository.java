package com.techinova.kanban.data;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techinova.kanban.model.Usuario;
import com.techinova.kanban.util.Constantes;

/**
 * NC1 / NC4: login, cadastro, logout e leitura do perfil (usuarios/{uid}).
 * Responsável: Pessoa A.
 */
public class AuthRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    /** Usuário logado no Firebase Auth, ou null. A sessão persiste sozinha ao reabrir o app (NC1). */
    @Nullable
    public FirebaseUser getUsuarioAtual() {
        return auth.getCurrentUser();
    }

    /** Login com e-mail e senha. */
    public void login(String email, String senha, RepositorioCallback<FirebaseUser> callback) {
        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> callback.onSucesso(result.getUser()))
                .addOnFailureListener(callback::onErro);
    }

    /**
     * Cadastro com e-mail e senha. NÃO grava perfil (NC4).
     *
     * Atenção: as regras proíbem o app de escrever em usuarios/{uid} (allow write: if false).
     * Por isso o NOME é guardado no próprio Firebase Auth (displayName). O responsável copia o nome
     * para o documento usuarios/{uid} quando liberar o perfil pelo console.
     */
    public void cadastrar(String nome, String email, String senha, RepositorioCallback<FirebaseUser> callback) {
        auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    if (user == null) {
                        callback.onErro(new IllegalStateException("Usuário não retornado após cadastro."));
                        return;
                    }

                    UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nome)
                            .build();

                    user.updateProfile(req)
                            .addOnSuccessListener(unused -> callback.onSucesso(user))
                            .addOnFailureListener(callback::onErro);
                })
                .addOnFailureListener(callback::onErro);
    }

    /**
     * Lê usuarios/{uid} (nome, perfil, postoId).
     * Devolve null no onSucesso se o documento não existir (perfil ainda não liberado).
     */
    public void buscarUsuario(String uid, RepositorioCallback<Usuario> callback) {
        db.collection(Constantes.COL_USUARIOS)
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> callback.onSucesso(doc.exists() ? doc.toObject(Usuario.class) : null))
                .addOnFailureListener(callback::onErro);
    }

    /** Sai da conta (item "Sair" do menu, NC1). */
    public void logout() {
        auth.signOut();
    }
}