package com.techinova.kanban.ui.operador;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.techinova.kanban.R;
import com.techinova.kanban.data.AuthRepository;
import com.techinova.kanban.data.CartaoRepository;
import com.techinova.kanban.data.PostoRepository;
import com.techinova.kanban.data.RepositorioCallback;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Insumo;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.model.Usuario;
import com.techinova.kanban.util.ErrosFirebase;

import java.util.ArrayList;
import java.util.List;

/**
 * C2 / C3 / C5 / NC2 / NC7: usado pela OperadorActivity (Meus pedidos) e pela NovoCartaoActivity.
 * Cada Activity tem a sua instância (ViewModelProvider(this)).
 *
 * NC7: o insumo escolhido no dropdown e a urgência escolhida ficam aqui. O ViewModel sobrevive
 * à rotação da tela; a Activity é destruída e recriada, mas lê de volta o que estava escolhido.
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
    private boolean confirmando = false;

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
        if (dadosCarregados) return;

        FirebaseUser atual = authRepositorio.getUsuarioAtual();
        if (atual == null) {
            mensagemErro.setValue(R.string.erro_generico);
            return;
        }

        dadosCarregados = true;
        carregando.setValue(true);

        authRepositorio.buscarUsuario(atual.getUid(), new RepositorioCallback<Usuario>() {
            @Override
            public void onSucesso(Usuario u) {
                if (u == null || u.getPostoId() == null || u.getPostoId().isEmpty()) {
                    carregando.setValue(false);
                    mensagemErro.setValue(R.string.erro_operador_sem_posto);
                    return;
                }
                usuario.setValue(u);
                carregarPosto(u.getPostoId());
            }

            @Override
            public void onErro(Exception e) {
                falhouAoCarregar(e);
            }
        });
    }

    private void carregarPosto(String postoId) {
        postoRepositorio.buscarPorId(postoId, new RepositorioCallback<Posto>() {
            @Override
            public void onSucesso(Posto p) {
                carregando.setValue(false);
                if (p == null) {
                    mensagemErro.setValue(R.string.erro_operador_sem_posto);
                    return;
                }
                posto.setValue(p); // só os insumos DESTE posto (C2)
            }

            @Override
            public void onErro(Exception e) {
                falhouAoCarregar(e);
            }
        });
    }

    private void falhouAoCarregar(Exception e) {
        carregando.setValue(false);
        dadosCarregados = false; // permite tentar de novo ao reabrir a tela
        mensagemErro.setValue(ErrosFirebase.traduzir(e));
    }

    // ================= Meus pedidos (OperadorActivity) =================

    /** Chamar no onStart() da OperadorActivity, quando o postoId já for conhecido (NC2). */
    public void ouvirCartoes(String postoId) {
        pararDeOuvirCartoes(); // nunca deixar dois listeners ligados ao mesmo tempo
        registroCartoes = cartaoRepositorio.ouvirDoPosto(postoId, new RepositorioCallback<List<Cartao>>() {
            @Override
            public void onSucesso(List<Cartao> lista) {
                cartoes.setValue(lista);
            }

            @Override
            public void onErro(Exception e) {
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /** Chamar no onStop() da OperadorActivity (NC2). */
    public void pararDeOuvirCartoes() {
        if (registroCartoes != null) {
            registroCartoes.remove();
            registroCartoes = null;
        }
    }

    /** C5: confirma o recebimento (só para cartão "entregue"). */
    public void confirmarRecebimento(Cartao cartao) {
        if (!Status.ENTREGUE.equals(cartao.getStatus()) || confirmando) return;
        confirmando = true;

        cartaoRepositorio.confirmar(cartao, new RepositorioCallback<Void>() {
            @Override
            public void onSucesso(Void resultado) {
                confirmando = false;
                // O cartão some da lista sozinho: o snapshot listener percebe o delete.
                mensagemSucesso.setValue(R.string.msg_recebimento_confirmado);
            }

            @Override
            public void onErro(Exception e) {
                confirmando = false;
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
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
     * A Activity já validou na tela; aqui conferimos de novo para nunca gravar dado inválido.
     */
    public void abrirPedido() {
        if (Boolean.TRUE.equals(carregando.getValue())) return; // evita toque duplo

        Posto p = posto.getValue();
        Integer indice = indiceInsumoSelecionado.getValue();
        Integer urgencia = urgenciaSelecionada.getValue();
        FirebaseUser atual = authRepositorio.getUsuarioAtual();

        if (p == null || p.getInsumos() == null || indice == null
                || indice < 0 || indice >= p.getInsumos().size()) {
            mensagemErro.setValue(R.string.erro_escolha_insumo);
            return;
        }
        if (urgencia == null || urgencia == NENHUM) {
            mensagemErro.setValue(R.string.erro_escolha_urgencia);
            return;
        }
        Insumo insumo = p.getInsumos().get(indice);
        if (atual == null || insumo.getId() == null) {
            mensagemErro.setValue(R.string.erro_generico);
            return;
        }

        Cartao cartao = new Cartao(p.getId(), p.getNome(), insumo.getId(), insumo.getNome(),
                urgencia, atual.getUid());

        carregando.setValue(true);
        cartaoRepositorio.abrir(cartao, new RepositorioCallback<Void>() {
            @Override
            public void onSucesso(Void resultado) {
                carregando.setValue(false);
                pedidoAberto.setValue(true);
            }

            @Override
            public void onErro(Exception e) {
                carregando.setValue(false);
                // C3: o abrir() lança ALREADY_EXISTS quando já há pedido desse insumo no posto.
                // Tratamos aqui mesmo, para a mensagem certa aparecer independente do ErrosFirebase.
                if (e instanceof FirebaseFirestoreException
                        && ((FirebaseFirestoreException) e).getCode()
                        == FirebaseFirestoreException.Code.ALREADY_EXISTS) {
                    mensagemErro.setValue(R.string.erro_cartao_duplicado);
                } else {
                    mensagemErro.setValue(ErrosFirebase.traduzir(e));
                }
            }
        });
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        pararDeOuvirCartoes();
        authRepositorio.logout();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        pararDeOuvirCartoes();
    }
}
