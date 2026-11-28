package com.techinova.kanban.util;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.util.Log;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
        if (e == null) {
            return R.string.erro_generico;
        }

        Log.e("Kanban", "erro", e);

        if (e instanceof FirebaseAuthWeakPasswordException) {
            return R.string.erro_fb_senha_fraca;
        }

        if (e instanceof FirebaseAuthInvalidCredentialsException) {
            FirebaseAuthInvalidCredentialsException authEx = (FirebaseAuthInvalidCredentialsException) e;
            if ("ERROR_INVALID_EMAIL".equals(authEx.getErrorCode())) {
                return R.string.erro_fb_email_invalido;
            }
            return R.string.erro_fb_credenciais;
        }

        if (e instanceof FirebaseAuthInvalidUserException) {
            return R.string.erro_fb_usuario_nao_encontrado;
        }

        if (e instanceof FirebaseAuthUserCollisionException) {
            return R.string.erro_fb_email_em_uso;
        }

        if (e instanceof FirebaseNetworkException) {
            return R.string.erro_fb_sem_conexao;
        }

        if (e instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException firestoreEx = (FirebaseFirestoreException) e;
            switch (firestoreEx.getCode()) {
                case PERMISSION_DENIED:
                    return R.string.erro_fb_permissao_negada;
                case ALREADY_EXISTS:
                    return R.string.erro_cartao_duplicado;
                case UNAVAILABLE:
                    return R.string.erro_fb_sem_conexao;
                default:
                    return R.string.erro_generico;
            }
        }

        return R.string.erro_generico;
    }
}