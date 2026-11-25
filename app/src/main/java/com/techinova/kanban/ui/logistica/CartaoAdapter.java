package com.techinova.kanban.ui.logistica;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ItemCartaoQuadroBinding;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.util.TempoUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * C4 / C6: cartão no quadro da logística (item_cartao_quadro.xml). Pronto.
 * - Aberto   -> botão "Saiu para entrega" (ícone caminhão)
 * - Em rota  -> botão "Marcar entregue" (ícone check)
 * - Entregue -> sem botão, mostra "Aguardando confirmação do operador"
 * O "há X min" é recalculado a cada bind: o ColunaFragment chama notifyDataSetChanged() a cada 30 s.
 */
public class CartaoAdapter extends RecyclerView.Adapter<CartaoAdapter.ViewHolder> {

    public interface OnAcaoListener {
        /** Toque no botão de ação: o ViewModel decide o próximo status com Status.proximo(). */
        void onAvancar(Cartao cartao);
    }

    private final List<Cartao> cartoes = new ArrayList<>();
    private final OnAcaoListener listener;

    public CartaoAdapter(OnAcaoListener listener) {
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
        ItemCartaoQuadroBinding b = ItemCartaoQuadroBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cartao cartao = cartoes.get(position);
        ItemCartaoQuadroBinding b = holder.binding;
        Context ctx = b.getRoot().getContext();

        b.txtPosto.setText(cartao.getPostoNome());
        b.txtInsumo.setText(cartao.getInsumoNome());

        // Urgência
        int urgencia = cartao.getUrgencia();
        b.barraUrgencia.setBackgroundResource(Status.Urgencia.barra(urgencia));
        b.chipUrgencia.setText(Status.Urgencia.rotulo(urgencia));
        b.chipUrgencia.setBackgroundResource(Status.Urgencia.fundoChip(urgencia));

        // C6: tempo desde a abertura, em destaque (vermelho se crítico)
        b.txtTempo.setText(TempoUtil.formatarDecorrido(ctx, cartao.getAbertoEm()));
        if (urgencia == Status.Urgencia.CRITICA) {
            b.txtTempo.setTextColor(ContextCompat.getColor(ctx, R.color.vermelho_alerta));
        } else {
            b.txtTempo.setTextColor(b.txtInsumo.getCurrentTextColor());
        }

        // Botão de ação conforme o status
        String status = cartao.getStatus();
        if (Status.ABERTO.equals(status)) {
            b.btnAcao.setVisibility(View.VISIBLE);
            b.btnAcao.setText(R.string.btn_saiu_entrega);
            b.btnAcao.setIconResource(R.drawable.ic_caminhao);
            b.txtAguardando.setVisibility(View.GONE);
        } else if (Status.EM_ROTA.equals(status)) {
            b.btnAcao.setVisibility(View.VISIBLE);
            b.btnAcao.setText(R.string.btn_marcar_entregue);
            b.btnAcao.setIconResource(R.drawable.ic_check);
            b.txtAguardando.setVisibility(View.GONE);
        } else {
            b.btnAcao.setVisibility(View.GONE);
            b.txtAguardando.setVisibility(View.VISIBLE);
        }

        b.btnAcao.setOnClickListener(v -> {
            if (listener != null) listener.onAvancar(cartao);
        });
    }

    @Override
    public int getItemCount() {
        return cartoes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemCartaoQuadroBinding binding;

        ViewHolder(ItemCartaoQuadroBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
