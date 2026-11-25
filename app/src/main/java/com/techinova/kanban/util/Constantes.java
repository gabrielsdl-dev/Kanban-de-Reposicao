package com.techinova.kanban.util;

/**
 * Nomes de coleções, campos e outras constantes. Use SEMPRE estas constantes
 * em vez de escrever "cartoes", "status" etc. direto no código.
 */
public final class Constantes {

    private Constantes() {
    }

    // ---- Coleções ----
    public static final String COL_USUARIOS = "usuarios";
    public static final String COL_POSTOS = "postos";
    public static final String COL_CARTOES = "cartoes";
    public static final String COL_HISTORICO = "historico";

    // ---- Campos de usuarios/{uid} ----
    public static final String CAMPO_NOME = "nome";
    public static final String CAMPO_PERFIL = "perfil";
    public static final String CAMPO_POSTO_ID = "postoId";

    // ---- Campos de postos/{postoId} ----
    public static final String CAMPO_INSUMOS = "insumos";
    public static final String CAMPO_INSUMO_ITEM_ID = "id";
    public static final String CAMPO_INSUMO_ITEM_NOME = "nome";

    // ---- Campos de cartoes/{postoId_insumoId} e historico/{autoId} ----
    public static final String CAMPO_POSTO_NOME = "postoNome";
    public static final String CAMPO_INSUMO_ID = "insumoId";
    public static final String CAMPO_INSUMO_NOME = "insumoNome";
    public static final String CAMPO_URGENCIA = "urgencia";
    public static final String CAMPO_STATUS = "status";
    public static final String CAMPO_ABERTO_POR = "abertoPor";
    public static final String CAMPO_ABERTO_EM = "abertoEm";
    public static final String CAMPO_EM_ROTA_EM = "emRotaEm";
    public static final String CAMPO_ENTREGUE_EM = "entregueEm";
    public static final String CAMPO_CONCLUIDO_EM = "concluidoEm";

    // ---- Perfis (definidos pelo console, NC4) ----
    public static final String PERFIL_OPERADOR = "operador";
    public static final String PERFIL_LOGISTICA = "logistica";

    // ---- Extras de Intent / argumentos ----
    public static final String EXTRA_POSTO_ID = "extra_posto_id";
    public static final String ARG_STATUS = "arg_status";

    // ---- Regras ----
    /** O Firebase Auth exige no mínimo 6 caracteres. */
    public static final int SENHA_TAMANHO_MINIMO = 6;
    /** Intervalo do timer que atualiza o "há X min" (C6). */
    public static final long INTERVALO_ATUALIZACAO_MS = 30_000L;
    /** Separador do id do cartão: postoId + "_" + insumoId (C3). */
    public static final String SEPARADOR_ID_CARTAO = "_";
    /** Mensagem técnica da exceção de cartão duplicado (a mensagem ao usuário vem de strings.xml). */
    public static final String MSG_CARTAO_DUPLICADO = "Já existe um pedido em andamento para este insumo";
}
