package com.techinova.kanban.ui.logistica;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
import com.techinova.kanban.data.AuthRepository;
import com.techinova.kanban.data.CartaoRepository;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * C4 / C6 / NC2: estado do quadro. É COMPARTILHADO entre a QuadroActivity e os 3 ColunaFragment
 * (no fragment use new ViewModelProvider(requireActivity()), não "this").
 * Assim a Activity também enxerga quantos cartões há em cada coluna (contador no cabeçalho de cada coluna).
 * Responsável: Pessoa C.
 */
public class QuadroViewModel extends ViewModel {

    private final CartaoRepository repositorio = new CartaoRepository();
    private final AuthRepository authRepositorio = new AuthRepository();

    /** Uma lista por status: "aberto", "em_rota", "entregue". */
    private final Map<String, MutableLiveData<List<Cartao>>> cartoesPorStatus = new HashMap<>();
    /** Um listener por status, removido no onStop() do fragment da coluna. */
    private final Map<String, ListenerRegistration> registros = new HashMap<>();

    private final MutableLiveData<Integer> mensagemErro = new MutableLiveData<>();

    public QuadroViewModel() {
        for (String status : Status.COLUNAS) {
            cartoesPorStatus.put(status, new MutableLiveData<>(new ArrayList<>()));
        }
    }

    public LiveData<List<Cartao>> getCartoes(String status) {
        return cartoesPorStatus.get(status);
    }

    public LiveData<Integer> getMensagemErro() {
        return mensagemErro;
    }

    public void limparMensagem() {
        mensagemErro.setValue(null);
    }

    /** Chamar no onStart() do ColunaFragment (NC2). */
    public void ouvir(String status) {
        // TODO [C4/NC2] pararDeOuvir(status);   // evita listener duplicado
        //  ListenerRegistration reg = repositorio.ouvirPorStatus(status, callback):
        //   - onSucesso(lista) -> cartoesPorStatus.get(status).setValue(lista)
        //   - onErro(e)        -> mensagemErro.setValue(ErrosFirebase.traduzir(e))
        //  registros.put(status, reg);
    }

    /** Chamar no onStop() do ColunaFragment (NC2). */
    public void pararDeOuvir(String status) {
        // TODO [NC2] ListenerRegistration reg = registros.remove(status);
        //  if (reg != null) reg.remove();
    }

    /** C4: botão "Saiu para entrega" / "Marcar entregue" do cartão. */
    public void avancar(Cartao cartao) {
        // TODO [C4/C6] repositorio.avancarStatus(cartao, callback):
        //   - onSucesso -> nada a fazer: o snapshot listener move o cartão de coluna sozinho
        //   - onErro    -> mensagemErro.setValue(ErrosFirebase.traduzir(e))
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        // TODO [NC1] for (String s : Status.COLUNAS) pararDeOuvir(s); authRepositorio.logout();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        for (String status : Status.COLUNAS) {
            pararDeOuvir(status);
        }
    }
}
