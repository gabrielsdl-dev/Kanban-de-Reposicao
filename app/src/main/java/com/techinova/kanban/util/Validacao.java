package com.techinova.kanban.util;

import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;

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
        // TODO [NC6] Devolver til.getEditText().getText().toString().trim().
        //  Cuidado: getEditText() e getText() podem ser null -> devolver "" nesses casos.
        return "";
    }

    /** Campo obrigatório genérico. */
    public static boolean obrigatorio(TextInputLayout til, @StringRes int mensagemErro) {
        // TODO [NC6] Se texto(til).isEmpty() -> til.setError(til.getContext().getString(mensagemErro))
        //  e devolver false. Senão -> til.setError(null) e devolver true.
        return true;
    }

    /** E-mail obrigatório e com formato válido. */
    public static boolean email(TextInputLayout til) {
        // TODO [NC6] Vazio -> R.string.erro_email_vazio.
        //  Formato inválido -> R.string.erro_email_invalido (use android.util.Patterns.EMAIL_ADDRESS.matcher(x).matches()).
        //  Ok -> til.setError(null).
        return true;
    }

    /** Senha obrigatória com no mínimo Constantes.SENHA_TAMANHO_MINIMO caracteres. */
    public static boolean senha(TextInputLayout til) {
        // TODO [NC6] Vazia -> R.string.erro_senha_vazia.
        //  Menor que Constantes.SENHA_TAMANHO_MINIMO -> R.string.erro_senha_curta.
        //  Atenção: para senha NÃO use trim() (espaço pode fazer parte da senha).
        return true;
    }

    /** Confirmação igual à senha. */
    public static boolean senhasIguais(TextInputLayout tilSenha, TextInputLayout tilConfirmacao) {
        // TODO [NC6] Se os textos forem diferentes -> tilConfirmacao.setError(R.string.erro_senhas_diferentes).
        return true;
    }

    /**
     * Número inteiro válido (para futuros campos numéricos, ex.: quantidade).
     * Nunca deixe Integer.parseInt derrubar o app.
     */
    public static boolean inteiro(TextInputLayout til, @StringRes int mensagemErro) {
        // TODO [NC6] try { Integer.parseInt(texto(til)); til.setError(null); return true; }
        //  catch (NumberFormatException e) { til.setError(getString(mensagemErro)); return false; }
        return true;
    }
}
