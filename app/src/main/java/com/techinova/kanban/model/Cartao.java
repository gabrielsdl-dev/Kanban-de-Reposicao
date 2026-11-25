package com.techinova.kanban.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.ServerTimestamp;
import com.techinova.kanban.util.Constantes;

import java.util.HashMap;
import java.util.Map;

/**
 * Documento cartoes/{postoId_insumoId} (C2, C3, C4, C6).
 *
 * - O id do documento é postoId + "_" + insumoId (C3: impede dois cartões abertos do mesmo insumo no posto).
 * - postoNome e insumoNome ficam duplicados aqui para o quadro não precisar de consultas extras.
 * - urgencia é int (1 normal, 2 alta, 3 crítica) para o orderBy("urgencia", DESCENDING) funcionar.
 *
 * Sobre os horários:
 * - abertoEm tem @ServerTimestamp: se estiver null quando o cartão for gravado, o servidor preenche.
 * - emRotaEm e entregueEm NÃO têm @ServerTimestamp de propósito: senão o servidor preencheria os três
 *   já na criação. Eles são gravados no update com FieldValue.serverTimestamp() (ver CartaoRepository).
 */
public class Cartao {

    @DocumentId
    private String id;
    private String postoId;
    private String postoNome;
    private String insumoId;
    private String insumoNome;
    private int urgencia;
    private String status;
    private String abertoPor;

    @ServerTimestamp
    private Timestamp abertoEm;
    private Timestamp emRotaEm;
    private Timestamp entregueEm;

    /** Construtor vazio obrigatório para o Firestore. */
    public Cartao() {
    }

    public Cartao(String postoId, String postoNome, String insumoId, String insumoNome,
                  int urgencia, String abertoPor) {
        this.postoId = postoId;
        this.postoNome = postoNome;
        this.insumoId = insumoId;
        this.insumoNome = insumoNome;
        this.urgencia = urgencia;
        this.abertoPor = abertoPor;
        this.status = Status.ABERTO;
    }

    /**
     * Monta os dados que vão para historico/{autoId} na confirmação (C5):
     * todos os campos do cartão + concluidoEm com o horário do servidor.
     */
    @Exclude
    public Map<String, Object> toHistoricoMap() {
        Map<String, Object> dados = new HashMap<>();
        dados.put(Constantes.CAMPO_POSTO_ID, postoId);
        dados.put(Constantes.CAMPO_POSTO_NOME, postoNome);
        dados.put(Constantes.CAMPO_INSUMO_ID, insumoId);
        dados.put(Constantes.CAMPO_INSUMO_NOME, insumoNome);
        dados.put(Constantes.CAMPO_URGENCIA, urgencia);
        dados.put(Constantes.CAMPO_STATUS, status);
        dados.put(Constantes.CAMPO_ABERTO_POR, abertoPor);
        dados.put(Constantes.CAMPO_ABERTO_EM, abertoEm);
        dados.put(Constantes.CAMPO_EM_ROTA_EM, emRotaEm);
        dados.put(Constantes.CAMPO_ENTREGUE_EM, entregueEm);
        dados.put(Constantes.CAMPO_CONCLUIDO_EM, FieldValue.serverTimestamp());
        return dados;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostoId() {
        return postoId;
    }

    public void setPostoId(String postoId) {
        this.postoId = postoId;
    }

    public String getPostoNome() {
        return postoNome;
    }

    public void setPostoNome(String postoNome) {
        this.postoNome = postoNome;
    }

    public String getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(String insumoId) {
        this.insumoId = insumoId;
    }

    public String getInsumoNome() {
        return insumoNome;
    }

    public void setInsumoNome(String insumoNome) {
        this.insumoNome = insumoNome;
    }

    public int getUrgencia() {
        return urgencia;
    }

    public void setUrgencia(int urgencia) {
        this.urgencia = urgencia;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAbertoPor() {
        return abertoPor;
    }

    public void setAbertoPor(String abertoPor) {
        this.abertoPor = abertoPor;
    }

    public Timestamp getAbertoEm() {
        return abertoEm;
    }

    public void setAbertoEm(Timestamp abertoEm) {
        this.abertoEm = abertoEm;
    }

    public Timestamp getEmRotaEm() {
        return emRotaEm;
    }

    public void setEmRotaEm(Timestamp emRotaEm) {
        this.emRotaEm = emRotaEm;
    }

    public Timestamp getEntregueEm() {
        return entregueEm;
    }

    public void setEntregueEm(Timestamp entregueEm) {
        this.entregueEm = entregueEm;
    }
}
