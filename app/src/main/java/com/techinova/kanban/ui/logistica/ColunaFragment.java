package com.techinova.kanban.ui.logistica;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.FragmentColunaBinding;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.util.Constantes;

/**
 * C4 / C6 / NC2 / NC5: UMA coluna do quadro. O mesmo fragment é usado nas 3 colunas,
 * mudando só o status recebido em newInstance(status).
 * Consulta: whereEqualTo("status", status).orderBy("urgencia", DESCENDING).orderBy("abertoEm")
 * (feita no CartaoRepository.ouvirPorStatus, via QuadroViewModel.ouvir).
 * Responsável: Pessoa C.
 */
public class ColunaFragment extends Fragment {

    private FragmentColunaBinding binding;
    private QuadroViewModel viewModel;
    private CartaoAdapter adapter;
    private String status;

    /** C6: o "há X min" não se atualiza sozinho; este Handler redesenha a lista a cada 30 s. */
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable atualizarTempo = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            handler.postDelayed(this, Constantes.INTERVALO_ATUALIZACAO_MS);
        }
    };

    /** Cria a coluna de um status (Status.ABERTO, Status.EM_ROTA ou Status.ENTREGUE). */
    public static ColunaFragment newInstance(String status) {
        ColunaFragment fragment = new ColunaFragment();
        Bundle args = new Bundle();
        args.putString(Constantes.ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        status = requireArguments().getString(Constantes.ARG_STATUS, Status.ABERTO);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentColunaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Compartilhado com a QuadroActivity (requireActivity(), não "this")
        viewModel = new ViewModelProvider(requireActivity()).get(QuadroViewModel.class);

        adapter = new CartaoAdapter(cartao -> {
            viewModel.avancar(cartao);
        });
        binding.rvCartoes.setAdapter(adapter);

        // Estado vazio (NC5)
        binding.estadoVazio.txtEstadoVazio.setText(R.string.coluna_vazia);
        binding.estadoVazio.txtEstadoVazioDica.setText(R.string.coluna_vazia_dica);

        viewModel.getCartoes(status).observe(getViewLifecycleOwner(), lista -> {
            adapter.submeterLista(lista);
            boolean vazio = lista == null || lista.isEmpty();
            binding.estadoVazio.getRoot().setVisibility(vazio ? View.VISIBLE : View.GONE);
            binding.rvCartoes.setVisibility(vazio ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.ouvir(status);
        handler.postDelayed(atualizarTempo, Constantes.INTERVALO_ATUALIZACAO_MS);
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.pararDeOuvir(status);
        handler.removeCallbacks(atualizarTempo);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(atualizarTempo);
        binding = null;
    }
}
