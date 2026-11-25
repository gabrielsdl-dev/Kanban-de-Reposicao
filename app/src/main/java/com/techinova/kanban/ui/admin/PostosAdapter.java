package com.techinova.kanban.ui.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ItemPostoBinding;
import com.techinova.kanban.model.Posto;

import java.util.ArrayList;
import java.util.List;

/** C1: lista de postos (item_posto.xml). Pronto. */
public class PostosAdapter extends RecyclerView.Adapter<PostosAdapter.ViewHolder> {

    public interface OnPostoClickListener {
        void onPostoClick(Posto posto);
    }

    private final List<Posto> postos = new ArrayList<>();
    private final OnPostoClickListener listener;

    public PostosAdapter(OnPostoClickListener listener) {
        this.listener = listener;
    }

    /** Troca a lista inteira (chamado a cada atualização do snapshot listener). */
    public void submeterLista(List<Posto> novos) {
        postos.clear();
        if (novos != null) postos.addAll(novos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPostoBinding b = ItemPostoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Posto posto = postos.get(position);
        ItemPostoBinding b = holder.binding;

        b.txtNomePosto.setText(posto.getNome());
        int qtd = posto.getQuantidadeInsumos();
        if (qtd == 0) {
            b.txtQtdInsumos.setText(R.string.posto_sem_insumos);
        } else {
            b.txtQtdInsumos.setText(b.getRoot().getResources()
                    .getQuantityString(R.plurals.posto_qtd_insumos, qtd, qtd));
        }
        b.cardPosto.setOnClickListener(v -> {
            if (listener != null) listener.onPostoClick(posto);
        });
    }

    @Override
    public int getItemCount() {
        return postos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemPostoBinding binding;

        ViewHolder(ItemPostoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
