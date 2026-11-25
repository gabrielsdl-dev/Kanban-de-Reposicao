package com.techinova.kanban.data;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techinova.kanban.model.Usuario;

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
        // TODO [NC1] Devolver auth.getCurrentUser().
        return null;
    }

    /** Login com e-mail e senha. */
    public void login(String email, String senha, RepositorioCallback<FirebaseUser> callback) {
        // TODO [NC1] auth.signInWithEmailAndPassword(email, senha)
        //  .addOnSuccessListener(r -> callback.onSucesso(r.getUser()))
        //  .addOnFailureListener(callback::onErro);
    }

    /**
     * Cadastro com e-mail e senha. NÃO grava perfil (NC4).
     *
     * Atenção: as regras proíbem o app de escrever em usuarios/{uid} (allow write: if false).
     * Por isso o NOME é guardado no próprio Firebase Auth (displayName). O responsável copia o nome
     * para o documento usuarios/{uid} quando liberar o perfil pelo console.
     */
    public void cadastrar(String nome, String email, String senha, RepositorioCallback<FirebaseUser> callback) {
        // TODO [NC1] 1) auth.createUserWithEmailAndPassword(email, senha)
        //  2) no sucesso: UserProfileChangeRequest req = new UserProfileChangeRequest.Builder()
        //         .setDisplayName(nome).build();
        //     result.getUser().updateProfile(req) -> no fim chamar callback.onSucesso(user)
        //  3) qualquer falha -> callback.onErro(e)
        //  NÃO criar documento em usuarios/{uid}: o perfil é definido pelo console (NC4).
    }

    /**
     * Lê usuarios/{uid} (nome, perfil, postoId).
     * Devolve null no onSucesso se o documento não existir (perfil ainda não liberado).
     */
    public void buscarUsuario(String uid, RepositorioCallback<Usuario> callback) {
        // TODO [NC4] db.collection(Constantes.COL_USUARIOS).document(uid).get()
        //  .addOnSuccessListener(doc -> callback.onSucesso(doc.exists() ? doc.toObject(Usuario.class) : null))
        //  .addOnFailureListener(callback::onErro);
    }

    /** Sai da conta (item "Sair" do menu, NC1). */
    public void logout() {
        // TODO [NC1] auth.signOut();
    }
}
