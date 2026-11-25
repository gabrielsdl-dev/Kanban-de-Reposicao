package com.techinova.kanban.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techinova.kanban.databinding.ItemInsumoEditBinding;
import com.techinova.kanban.model.Insumo;

import java.util.ArrayList;
import java.util.List;

/** C1: insumos dentro do formulário de posto (item_insumo_edit.xml), com botão remover. Pronto. */
public class InsumosEditAdapter extends RecyclerView.Adapter<InsumosEditAdapter.ViewHolder> {

    public interface OnRemoverListener {
        void onRemover(int posicao, Insumo insumo);
    }

    private final List<Insumo> insumos = new ArrayList<>();
    private final OnRemoverListener listener;

    public InsumosEditAdapter(OnRemoverListener listener) {
        this.listener = listener;
    }

    public void submeterLista(List<Insumo> novos) {
        insumos.clear();
        if (novos != null) insumos.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInsumoEditBinding b = ItemInsumoEditBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Insumo insumo = insumos.get(position);
        holder.binding.txtNomeInsumo.setText(insumo.getNome());
        holder.binding.btnRemoverInsumo.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onRemover(pos, insumos.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return insumos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemInsumoEditBinding binding;

        ViewHolder(ItemInsumoEditBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
