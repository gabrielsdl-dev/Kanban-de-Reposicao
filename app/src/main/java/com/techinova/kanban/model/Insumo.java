package com.techinova.kanban.model;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * Insumo usado em um posto. Fica dentro do documento do posto, na lista "insumos":
 * postos/{postoId}.insumos = [ {id: "...", nome: "..."}, ... ]
 */
public class Insumo {

    private String id;
    private String nome;

    /** Construtor vazio obrigatório para o Firestore. */
    public Insumo() {
    }

    public Insumo(String id, String nome) {
        this.id = id;
        this.nome = nome;
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

    /** Usado pelo ArrayAdapter do dropdown (mostra o nome do insumo). */
    @NonNull
    @Override
    public String toString() {
        return nome == null ? "" : nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insumo)) return false;
        Insumo outro = (Insumo) o;
        return Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
