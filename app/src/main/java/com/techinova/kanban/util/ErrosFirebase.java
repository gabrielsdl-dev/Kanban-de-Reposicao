package com.techinova.kanban.util;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.techinova.kanban.R;

/**
 * Traduz exceções do Firebase para mensagens em português (strings.xml).
 * Os ViewModels guardam o @StringRes devolvido e a tela mostra com getString().
 *
 * Responsável: Pessoa A (os outros usam).
 */
public final class ErrosFirebase {

    private ErrosFirebase() {
    }

    @StringRes
    public static int traduzir(@Nullable Exception e) {
        // TODO [NC1/NC6] Verificar o tipo da exceção (instanceof) e devolver a mensagem certa:
        //  - FirebaseAuthWeakPasswordException          -> R.string.erro_fb_senha_fraca
        //    (verificar ANTES de InvalidCredentials, pois é subclasse dela)
        //  - FirebaseAuthInvalidCredentialsException    -> se e.getErrorCode() == "ERROR_INVALID_EMAIL"
        //                                                  -> R.string.erro_fb_email_invalido;
        //                                                  senão -> R.string.erro_fb_credenciais
        //  - FirebaseAuthInvalidUserException           -> R.string.erro_fb_usuario_nao_encontrado
        //  - FirebaseAuthUserCollisionException         -> R.string.erro_fb_email_em_uso
        //  - FirebaseNetworkException                   -> R.string.erro_fb_sem_conexao
        //  - FirebaseFirestoreException:
        //       getCode() == Code.PERMISSION_DENIED     -> R.string.erro_fb_permissao_negada
        //       getCode() == Code.ALREADY_EXISTS        -> R.string.erro_cartao_duplicado (C3)
        //       getCode() == Code.UNAVAILABLE           -> R.string.erro_fb_sem_conexao
        //  - qualquer outro caso                        -> R.string.erro_generico
        //  Dica: registre o erro original com Log.e("Kanban", "erro", e) para depurar.
        return R.string.erro_generico;
    }
}
