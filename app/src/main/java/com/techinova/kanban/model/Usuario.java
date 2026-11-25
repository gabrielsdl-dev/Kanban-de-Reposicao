package com.techinova.kanban.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.techinova.kanban.util.Constantes;

/**
 * Documento usuarios/{uid}.
 * O campo "perfil" é definido SOMENTE pelo console do Firebase (NC4), nunca pelo app.
 */
public class Usuario {

    @DocumentId
    private String uid;
    private String nome;
    private String perfil;   // "operador" ou "logistica"
    private String postoId;  // só para operador

    /** Construtor vazio obrigatório para o Firestore (toObject). */
    public Usuario() {
    }

    public Usuario(String nome, String perfil, String postoId) {
        this.nome = nome;
        this.perfil = perfil;
        this.postoId = postoId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getPostoId() {
        return postoId;
    }

    public void setPostoId(String postoId) {
        this.postoId = postoId;
    }

    @Exclude
    public boolean isOperador() {
        return Constantes.PERFIL_OPERADOR.equals(perfil);
    }

    @Exclude
    public boolean isLogistica() {
        return Constantes.PERFIL_LOGISTICA.equals(perfil);
    }
}
