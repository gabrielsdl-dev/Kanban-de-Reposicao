package com.techinova.kanban.ui.admin;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
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
        pararDeOuvirPostos(); //evita listeners duplicados

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
        // TODO [C1] if (postoJaCarregado) return; postoJaCarregado = true; postoIdEmEdicao = postoId;
        //  repositorio.buscarPorId(postoId, callback):
        //   - onSucesso(posto) -> nomeCarregado.setValue(posto.getNome());
        //                         insumosFormulario.setValue(new ArrayList<>(posto.getInsumos()))
        //   - onErro(e)        -> mensagemErro = ErrosFirebase.traduzir(e)
    }

    /**
     * Adiciona um insumo na lista do formulário (ainda não grava no Firestore).
     *
     * @return 0 se deu certo, ou o @StringRes do erro para mostrar no tilNomeInsumo.
     */
    @StringRes
    public int adicionarInsumo(String nome) {
        // TODO [C1/NC6] - nome vazio -> return R.string.erro_nome_insumo
        //  - já existe insumo com o mesmo nome (equalsIgnoreCase) -> return R.string.erro_insumo_duplicado
        //  - senão: copiar a lista atual, add(new Insumo(null, nome)) e insumosFormulario.setValue(copia)
        //    (o id é gerado no PostoRepository.salvar)
        //  return 0;
        return 0;
    }

    /** Remove o insumo da posição informada (botão lixeira do item_insumo_edit). */
    public void removerInsumo(int posicao) {
        // TODO [C1] Copiar a lista atual, remove(posicao) e insumosFormulario.setValue(copia).
    }

    /**
     * Salva o posto (nome + insumos da lista). Os campos já foram validados na Activity.
     * Na Activity: se a lista estiver vazia, mostrar txtErroInsumos e não chamar este método.
     */
    public void salvar(String nomePosto) {
        // TODO [C1] Posto posto = new Posto(nomePosto, insumosFormulario.getValue());
        //  posto.setId(postoIdEmEdicao);
        //  carregando = true; repositorio.salvar(posto, callback):
        //   - onSucesso -> carregando = false; salvo.setValue(true)  (Activity mostra msg e finish())
        //   - onErro    -> carregando = false; mensagemErro = ErrosFirebase.traduzir(e)
        //     (se um operador tentar, o Firestore recusa: PERMISSION_DENIED)
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        // TODO [NC1] pararDeOuvirPostos(); authRepositorio.logout();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pararDeOuvirPostos();
    }
}
