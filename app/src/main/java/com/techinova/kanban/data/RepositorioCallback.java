package com.techinova.kanban.data;

/**
 * Retorno assíncrono dos repositórios para os ViewModels.
 * Ex.: repo.login(email, senha, new RepositorioCallback<FirebaseUser>() { ... });
 * ou com lambda não dá (2 métodos), então use classe anônima.
 */
public interface RepositorioCallback<T> {
    void onSucesso(T resultado);

    void onErro(Exception e);
}
