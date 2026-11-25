package com.techinova.kanban.model;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.techinova.kanban.R;

/**
 * Status do cartão e níveis de urgência, com os recursos visuais de cada um.
 *
 * Fluxo (seção 3.1):
 * aberto --(logística)--> em_rota --(logística)--> entregue --(operador confirma)--> historico
 */
public final class Status {

    public static final String ABERTO = "aberto";
    public static final String EM_ROTA = "em_rota";
    public static final String ENTREGUE = "entregue";

    /** Ordem das colunas do quadro (da esquerda para a direita). */
    public static final String[] COLUNAS = {ABERTO, EM_ROTA, ENTREGUE};

    private Status() {
    }

    /** Próximo status no fluxo da logística, ou null se não houver (entregue só sai pela confirmação). */
    @Nullable
    public static String proximo(String status) {
        if (ABERTO.equals(status)) return EM_ROTA;
        if (EM_ROTA.equals(status)) return ENTREGUE;
        return null;
    }

    @StringRes
    public static int rotulo(String status) {
        if (EM_ROTA.equals(status)) return R.string.status_em_rota;
        if (ENTREGUE.equals(status)) return R.string.status_entregue;
        return R.string.status_aberto;
    }

    @DrawableRes
    public static int fundoChip(String status) {
        if (EM_ROTA.equals(status)) return R.drawable.bg_chip_status_em_rota;
        if (ENTREGUE.equals(status)) return R.drawable.bg_chip_status_entregue;
        return R.drawable.bg_chip_status_aberto;
    }

    /** Faixa colorida no topo da coluna do quadro. */
    @DrawableRes
    public static int faixaColuna(String status) {
        if (EM_ROTA.equals(status)) return R.drawable.faixa_coluna_em_rota;
        if (ENTREGUE.equals(status)) return R.drawable.faixa_coluna_entregue;
        return R.drawable.faixa_coluna_aberto;
    }

    /** Níveis de urgência. Gravados como número para permitir ordenar no Firestore. */
    public static final class Urgencia {
        public static final int NORMAL = 1;
        public static final int ALTA = 2;
        public static final int CRITICA = 3;

        private Urgencia() {
        }

        @StringRes
        public static int rotulo(int urgencia) {
            if (urgencia == CRITICA) return R.string.urgencia_critica;
            if (urgencia == ALTA) return R.string.urgencia_alta;
            return R.string.urgencia_normal;
        }

        @DrawableRes
        public static int barra(int urgencia) {
            if (urgencia == CRITICA) return R.drawable.barra_urgencia_critica;
            if (urgencia == ALTA) return R.drawable.barra_urgencia_alta;
            return R.drawable.barra_urgencia_normal;
        }

        @DrawableRes
        public static int fundoChip(int urgencia) {
            if (urgencia == CRITICA) return R.drawable.bg_chip_urgencia_critica;
            if (urgencia == ALTA) return R.drawable.bg_chip_urgencia_alta;
            return R.drawable.bg_chip_urgencia_normal;
        }

        @ColorRes
        public static int cor(int urgencia) {
            if (urgencia == CRITICA) return R.color.vermelho_alerta;
            if (urgencia == ALTA) return R.color.laranja_alta;
            return R.color.azul_claro;
        }
    }
}
