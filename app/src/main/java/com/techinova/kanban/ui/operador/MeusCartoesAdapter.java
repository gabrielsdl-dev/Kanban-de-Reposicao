package com.techinova.kanban.ui.operador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techinova.kanban.databinding.ItemMeuCartaoBinding;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.util.TempoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * C5 / C6: cartões do posto do operador (item_meu_cartao.xml). Pronto.
 * O botão "Confirmar recebimento" só aparece quando o status é "entregue".
 * O "há X min" é recalculado a cada bind: a Activity chama notifyDataSetChanged() a cada 30 s.
 */
public class MeusCartoesAdapter extends RecyclerView.Adapter<MeusCartoesAdapter.ViewHolder> {

    public interface OnConfirmarListener {
        void onConfirmar(Cartao cartao);
    }

    private final List<Cartao> cartoes = new ArrayList<>();
    private final OnConfirmarListener listener;

    public MeusCartoesAdapter(OnConfirmarListener listener) {
        this.listener = listener;
    }

    public void submeterLista(List<Cartao> novos) {
        cartoes.clear();
        if (novos != null) cartoes.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMeuCartaoBinding b = ItemMeuCartaoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cartao cartao = cartoes.get(position);
        ItemMeuCartaoBinding b = holder.binding;
        Context ctx = b.getRoot().getContext();

        b.txtInsumo.setText(cartao.getInsumoNome());
        b.txtTempo.setText(TempoUtil.formatarDecorrido(ctx, cartao.getAbertoEm()));

        // Urgência: barra lateral + chip colorido
        int urgencia = cartao.getUrgencia();
        b.barraUrgencia.setBackgroundResource(Status.Urgencia.barra(urgencia));
        b.chipUrgencia.setText(Status.Urgencia.rotulo(urgencia));
        b.chipUrgencia.setBackgroundResource(Status.Urgencia.fundoChip(urgencia));

        // Status: chip com fundo claro
        String status = cartao.getStatus();
        b.chipStatus.setText(Status.rotulo(status));
        b.chipStatus.setBackgroundResource(Status.fundoChip(status));

        // C5: confirmar só quando entregue
        boolean entregue = Status.ENTREGUE.equals(status);
        b.btnConfirmar.setVisibility(entregue ? View.VISIBLE : View.GONE);
        b.btnConfirmar.setOnClickListener(v -> {
            if (listener != null) listener.onConfirmar(cartao);
        });
    }

    @Override
    public int getItemCount() {
        return cartoes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemMeuCartaoBinding binding;

        ViewHolder(ItemMeuCartaoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
