package com.techinova.kanban.ui.admin;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
import com.techinova.kanban.R;
import com.techinova.kanban.data.AuthRepository;
import com.techinova.kanban.data.PostoRepository;
import com.techinova.kanban.data.RepositorioCallback;
import com.techinova.kanban.model.Insumo;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.util.ErrosFirebase;

import java.util.ArrayList;
import java.util.List;

/**
 * C1 / NC2 / NC7: lista de postos (PostosActivity) e formulário (PostoFormActivity).
 * A lista de insumos que está sendo montada no formulário fica AQUI, para não sumir ao girar (NC7).
 * Responsável: Pessoa A.
 */
public class PostoViewModel extends ViewModel {

    private final PostoRepository repositorio = new PostoRepository();
    private final AuthRepository authRepositorio = new AuthRepository();

    // ---- Lista ----
    private final MutableLiveData<List<Posto>> postos = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration registroPostos;

    // ---- Formulário ----
    private final MutableLiveData<List<Insumo>> insumosFormulario = new MutableLiveData<>(new ArrayList<>());
    /** Nome do posto carregado para edição (a Activity preenche o campo só uma vez). */
    private final MutableLiveData<String> nomeCarregado = new MutableLiveData<>();
    /** Id do posto em edição (null = novo posto). */
    private String postoIdEmEdicao;
    private boolean postoJaCarregado = false;

    // ---- Estado geral ----
    private final MutableLiveData<Boolean> carregando = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> mensagemErro = new MutableLiveData<>();
    private final MutableLiveData<Boolean> salvo = new MutableLiveData<>(false);

    public LiveData<List<Posto>> getPostos() {
        return postos;
    }

    public LiveData<List<Insumo>> getInsumosFormulario() {
        return insumosFormulario;
    }

    public LiveData<String> getNomeCarregado() {
        return nomeCarregado;
    }

    public LiveData<Boolean> getCarregando() {
        return carregando;
    }

    public LiveData<Integer> getMensagemErro() {
        return mensagemErro;
    }

    public LiveData<Boolean> getSalvo() {
        return salvo;
    }

    public boolean isEdicao() {
        return postoIdEmEdicao != null;
    }

    public void limparMensagem() {
        mensagemErro.setValue(null);
    }

    // ================= Lista (PostosActivity) =================

    /** Chamar no onStart() da PostosActivity (NC2). */
    public void ouvirPostos() {
        pararDeOuvirPostos();
        registroPostos = repositorio.ouvirPostos(new RepositorioCallback<List<Posto>>() {
            @Override
            public void onSucesso(List<Posto> lista) {
                postos.setValue(lista);
            }

            @Override
            public void onErro(Exception e) {
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /** Chamar no onStop() da PostosActivity (NC2). */
    public void pararDeOuvirPostos() {
        if (registroPostos != null) {
            registroPostos.remove();
            registroPostos = null;
        }
    }

    // ================= Formulário (PostoFormActivity) =================

    /** Carrega um posto existente para edição. Só busca uma vez (rotação não recarrega). */
    public void carregarPosto(String postoId) {
        if (postoJaCarregado) return;
        postoJaCarregado = true;
        postoIdEmEdicao = postoId;

        carregando.setValue(true);
        repositorio.buscarPorId(postoId, new RepositorioCallback<Posto>() {
            @Override
            public void onSucesso(Posto posto) {
                carregando.setValue(false);
                if (posto != null) {
                    nomeCarregado.setValue(posto.getNome());
                    List<Insumo> lista = posto.getInsumos() != null
                            ? new ArrayList<>(posto.getInsumos())
                            : new ArrayList<>();
                    insumosFormulario.setValue(lista);
                }
            }

            @Override
            public void onErro(Exception e) {
                carregando.setValue(false);
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /**
     * Adiciona um insumo na lista do formulário (ainda não grava no Firestore).
     *
     * @return 0 se deu certo, ou o @StringRes do erro para mostrar no tilNomeInsumo.
     */
    @StringRes
    public int adicionarInsumo(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return R.string.erro_nome_insumo;
        }

        String nomeLimpo = nome.trim();
        List<Insumo> atual = insumosFormulario.getValue() != null
                ? insumosFormulario.getValue()
                : new ArrayList<>();

        for (Insumo item : atual) {
            if (item.getNome() != null && item.getNome().trim().equalsIgnoreCase(nomeLimpo)) {
                return R.string.erro_insumo_duplicado;
            }
        }

        List<Insumo> copia = new ArrayList<>(atual);
        copia.add(new Insumo(null, nomeLimpo));
        insumosFormulario.setValue(copia);
        return 0;
    }

    /** Remove o insumo da posição informada (botão lixeira do item_insumo_edit). */
    public void removerInsumo(int posicao) {
        List<Insumo> atual = insumosFormulario.getValue();
        if (atual == null || posicao < 0 || posicao >= atual.size()) return;

        List<Insumo> copia = new ArrayList<>(atual);
        copia.remove(posicao);
        insumosFormulario.setValue(copia);
    }

    /**
     * Salva o posto (nome + insumos da lista). Os campos já foram validados na Activity.
     * Na Activity: se a lista estiver vazia, mostrar txtErroInsumos e não chamar este método.
     */
    public void salvar(String nomePosto) {
        Posto posto = new Posto(nomePosto, insumosFormulario.getValue());
        posto.setId(postoIdEmEdicao);

        carregando.setValue(true);
        repositorio.salvar(posto, new RepositorioCallback<Void>() {
            @Override
            public void onSucesso(Void resultado) {
                carregando.setValue(false);
                salvo.setValue(true);
            }

            @Override
            public void onErro(Exception e) {
                carregando.setValue(false);
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        pararDeOuvirPostos();
        authRepositorio.logout();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pararDeOuvirPostos();
    }
}