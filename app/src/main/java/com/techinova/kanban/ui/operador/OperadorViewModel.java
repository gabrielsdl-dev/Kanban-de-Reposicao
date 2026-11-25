package com.techinova.kanban.ui.operador;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
import com.techinova.kanban.data.AuthRepository;
import com.techinova.kanban.data.CartaoRepository;
import com.techinova.kanban.data.PostoRepository;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.model.Usuario;

import java.util.ArrayList;
import java.util.List;

/**
 * C2 / C3 / C5 / NC2 / NC7: usado pela OperadorActivity (Meus pedidos) e pela NovoCartaoActivity.
 * Cada Activity tem a sua instância (ViewModelProvider(this)).
 *
 * NC7: o insumo escolhido no dropdown e a urgência escolhida ficam aqui, para não se perderem na rotação.
 * Responsável: Pessoa B.
 */
public class OperadorViewModel extends ViewModel {

    /** Nenhum insumo / urgência escolhidos ainda. */
    public static final int NENHUM = -1;

    private final AuthRepository authRepositorio = new AuthRepository();
    private final PostoRepository postoRepositorio = new PostoRepository();
    private final CartaoRepository cartaoRepositorio = new CartaoRepository();

    private final MutableLiveData<Usuario> usuario = new MutableLiveData<>();
    private final MutableLiveData<Posto> posto = new MutableLiveData<>();
    private final MutableLiveData<List<Cartao>> cartoes = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration registroCartoes;

    // ---- Estado do formulário Novo cartão (NC7) ----
    private final MutableLiveData<Integer> indiceInsumoSelecionado = new MutableLiveData<>(NENHUM);
    private final MutableLiveData<Integer> urgenciaSelecionada = new MutableLiveData<>(NENHUM);

    // ---- Estado geral ----
    private final MutableLiveData<Boolean> carregando = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> mensagemErro = new MutableLiveData<>();
    private final MutableLiveData<Integer> mensagemSucesso = new MutableLiveData<>();
    private final MutableLiveData<Boolean> pedidoAberto = new MutableLiveData<>(false);
    private boolean dadosCarregados = false;

    public LiveData<Usuario> getUsuario() {
        return usuario;
    }

    public LiveData<Posto> getPosto() {
        return posto;
    }

    public LiveData<List<Cartao>> getCartoes() {
        return cartoes;
    }

    public LiveData<Integer> getIndiceInsumoSelecionado() {
        return indiceInsumoSelecionado;
    }

    public LiveData<Integer> getUrgenciaSelecionada() {
        return urgenciaSelecionada;
    }

    public LiveData<Boolean> getCarregando() {
        return carregando;
    }

    public LiveData<Integer> getMensagemErro() {
        return mensagemErro;
    }

    public LiveData<Integer> getMensagemSucesso() {
        return mensagemSucesso;
    }

    public LiveData<Boolean> getPedidoAberto() {
        return pedidoAberto;
    }

    public void limparMensagens() {
        mensagemErro.setValue(null);
        mensagemSucesso.setValue(null);
    }

    /**
     * Carrega usuarios/{uid} e depois postos/{postoId} (nome do posto + insumos).
     * Só busca uma vez: na rotação os dados já estão no LiveData.
     */
    public void carregarUsuarioEPosto() {
        // TODO [C2] if (dadosCarregados) return; dadosCarregados = true;
        //  String uid = authRepositorio.getUsuarioAtual().getUid();
        //  authRepositorio.buscarUsuario(uid, cb1):
        //    onSucesso(u) -> usuario.setValue(u);
        //                    postoRepositorio.buscarPorId(u.getPostoId(), cb2):
        //                       onSucesso(p) -> posto.setValue(p)   (só os insumos DESTE posto, C2)
        //    erros -> mensagemErro.setValue(ErrosFirebase.traduzir(e))
    }

    // ================= Meus pedidos (OperadorActivity) =================

    /** Chamar no onStart() da OperadorActivity, quando o postoId já for conhecido (NC2). */
    public void ouvirCartoes(String postoId) {
        // TODO [C5/NC2] pararDeOuvirCartoes();
        //  registroCartoes = cartaoRepositorio.ouvirDoPosto(postoId, callback):
        //   - onSucesso(lista) -> cartoes.setValue(lista)
        //   - onErro(e)        -> mensagemErro.setValue(ErrosFirebase.traduzir(e))
    }

    /** Chamar no onStop() da OperadorActivity (NC2). */
    public void pararDeOuvirCartoes() {
        // TODO [NC2] if (registroCartoes != null) { registroCartoes.remove(); registroCartoes = null; }
    }

    /** C5: confirma o recebimento (só para cartão "entregue"). */
    public void confirmarRecebimento(Cartao cartao) {
        // TODO [C5] Se !Status.ENTREGUE.equals(cartao.getStatus()) -> return.
        //  cartaoRepositorio.confirmar(cartao, callback):
        //   - onSucesso -> mensagemSucesso.setValue(R.string.msg_recebimento_confirmado)
        //                  (o cartão some sozinho da lista pelo snapshot listener)
        //   - onErro    -> mensagemErro.setValue(ErrosFirebase.traduzir(e))
    }

    // ================= Novo cartão (NovoCartaoActivity) =================

    /** Dropdown: guarda a posição escolhida na lista de insumos do posto (NC7). */
    public void selecionarInsumo(int indice) {
        indiceInsumoSelecionado.setValue(indice);
    }

    /** Toggle de urgência: Status.Urgencia.NORMAL / ALTA / CRITICA (NC7). */
    public void selecionarUrgencia(int urgencia) {
        urgenciaSelecionada.setValue(urgencia);
    }

    /**
     * C2 + C3: abre o pedido com o insumo e a urgência escolhidos.
     * A Activity já validou (insumo e urgência escolhidos) antes de chamar.
     */
    public void abrirPedido() {
        // TODO [C2] Posto p = posto.getValue(); Insumo insumo = p.getInsumos().get(indiceInsumoSelecionado);
        //  Cartao cartao = new Cartao(p.getId(), p.getNome(), insumo.getId(), insumo.getNome(),
        //                             urgenciaSelecionada.getValue(), authRepositorio.getUsuarioAtual().getUid());
        //  carregando = true; cartaoRepositorio.abrir(cartao, callback):
        //   - onSucesso -> carregando = false; pedidoAberto.setValue(true)  (Activity mostra msg e finish())
        //   - onErro    -> carregando = false; mensagemErro = ErrosFirebase.traduzir(e)
        //     (C3: cartão duplicado vira "Já existe um pedido em andamento para este insumo")
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        // TODO [NC1] pararDeOuvirCartoes(); authRepositorio.logout();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pararDeOuvirCartoes();
    }
}
