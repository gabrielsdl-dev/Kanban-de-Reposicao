package com.techinova.kanban.util;

import android.util.Patterns;
import android.widget.EditText;

import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;
import com.techinova.kanban.R;

/**
 * NC6: validação de formulários. Cada método valida o campo, chama setError() no
 * TextInputLayout com a mensagem em português (de strings.xml) e devolve true se estiver ok.
 *
 * Exemplo de uso na Activity:
 *   boolean ok = Validacao.email(binding.tilEmail) & Validacao.senha(binding.tilSenha);
 *   if (!ok) return;
 * (use "&" e não "&&" para mostrar o erro em TODOS os campos de uma vez)
 *
 * Responsável: Pessoa A (os outros usam).
 */
public final class Validacao {

    private Validacao() {
    }

    /** Texto do EditText dentro do TextInputLayout, sem espaços nas pontas (nunca null). */
    public static String texto(TextInputLayout til) {
        if (til == null || til.getEditText() == null || til.getEditText().getText() == null) {
            return "";
        }
        return til.getEditText().getText().toString().trim();
    }

    /** Método auxiliar interno para pegar o texto bruto (sem trim), usado nas senhas. */
    private static String textoBruto(TextInputLayout til) {
        if (til == null || til.getEditText() == null || til.getEditText().getText() == null) {
            return "";
        }
        return til.getEditText().getText().toString();
    }

    /** Campo obrigatório genérico. */
    public static boolean obrigatorio(TextInputLayout til, @StringRes int mensagemErro) {
        if (texto(til).isEmpty()) {
            til.setError(til.getContext().getString(mensagemErro));
            return false;
        }
        til.setError(null);
        return true;
    }

    /** E-mail obrigatório e com formato válido. */
    public static boolean email(TextInputLayout til) {
        String valor = texto(til);
        if (valor.isEmpty()) {
            til.setError(til.getContext().getString(R.string.erro_email_vazio));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(valor).matches()) {
            til.setError(til.getContext().getString(R.string.erro_email_invalido));
            return false;
        }
        til.setError(null);
        return true;
    }

    /** Senha obrigatória com no mínimo Constantes.SENHA_TAMANHO_MINIMO caracteres. */
    public static boolean senha(TextInputLayout til) {
        String valor = textoBruto(til);
        if (valor.isEmpty()) {
            til.setError(til.getContext().getString(R.string.erro_senha_vazia));
            return false;
        }
        if (valor.length() < Constantes.SENHA_TAMANHO_MINIMO) {
            til.setError(til.getContext().getString(R.string.erro_senha_curta));
            return false;
        }
        til.setError(null);
        return true;
    }

    /** Confirmação igual à senha. */
    public static boolean senhasIguais(TextInputLayout tilSenha, TextInputLayout tilConfirmacao) {
        String senha = textoBruto(tilSenha);
        String confirmacao = textoBruto(tilConfirmacao);

        if (!senha.equals(confirmacao)) {
            tilConfirmacao.setError(tilConfirmacao.getContext().getString(R.string.erro_senhas_diferentes));
            return false;
        }
        tilConfirmacao.setError(null);
        return true;
    }

    /**
     * Número inteiro válido (para futuros campos numéricos, ex.: quantidade).
     * Nunca deixe Integer.parseInt derrubar o app.
     */
    public static boolean inteiro(TextInputLayout til, @StringRes int mensagemErro) {
        try {
            Integer.parseInt(texto(til));
            til.setError(null);
            return true;
        } catch (NumberFormatException e) {
            til.setError(til.getContext().getString(mensagemErro));
            return false;
        }
    }
}