package com.techinova.kanban.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Documento postos/{postoId}: nome do posto + lista de insumos usados nele (C1).
 */
public class Posto {

    @DocumentId
    private String id;
    private String nome;
    private List<Insumo> insumos = new ArrayList<>();

    /** Construtor vazio obrigatório para o Firestore. */
    public Posto() {
    }

    public Posto(String nome, List<Insumo> insumos) {
        this.nome = nome;
        this.insumos = insumos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Insumo> getInsumos() {
        return insumos;
    }

    public void setInsumos(List<Insumo> insumos) {
        this.insumos = insumos;
    }

    /** @Exclude: não vira campo no Firestore. */
    @Exclude
    public int getQuantidadeInsumos() {
        return insumos == null ? 0 : insumos.size();
    }
}
