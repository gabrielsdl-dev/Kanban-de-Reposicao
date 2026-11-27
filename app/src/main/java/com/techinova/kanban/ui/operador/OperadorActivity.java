package com.techinova.kanban.ui.operador;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityOperadorBinding;
import com.techinova.kanban.model.Cartao;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.ui.auth.LoginActivity;
import com.techinova.kanban.util.Constantes;
import com.techinova.kanban.util.InsetsUtil;

import java.util.List;

/**
 * C5 / C6 / NC2 / NC5: "Meus pedidos" do operador — cartões do posto dele, em tempo real,
 * com o botão "Confirmar recebimento" nos cartões entregues.
 * Responsável: Pessoa B.
 */
public class OperadorActivity extends AppCompatActivity {

    private ActivityOperadorBinding binding;
    private OperadorViewModel viewModel;
    private MeusCartoesAdapter adapter;

    /**
     * C6: o "há X min" é calculado na hora em que o item é desenhado. Se ninguém mexer na lista,
     * ele não muda sozinho. Este Handler redesenha a lista a cada 30 s.
     */
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable atualizarTempo = new Runnable() {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
            handler.postDelayed(this, Constantes.INTERVALO_ATUALIZACAO_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityOperadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(OperadorViewModel.class);

        // RecyclerView + estado vazio (NC5)
        adapter = new MeusCartoesAdapter(this::perguntarConfirmacao);
        binding.rvCartoes.setAdapter(adapter);
        binding.estadoVazio.txtEstadoVazio.setText(R.string.operador_vazio);
        binding.estadoVazio.txtEstadoVazioDica.setText(R.string.operador_vazio_dica);

        binding.fabNovoPedido.setOnClickListener(v ->
                startActivity(new Intent(this, NovoCartaoActivity.class)));

        viewModel.carregarUsuarioEPosto(); // nome do posto no cabeçalho + postoId

        observarViewModel();
    }

    /** C5: pergunta antes de encerrar o pedido. */
    private void perguntarConfirmacao(Cartao cartao) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_confirmar_titulo)
                .setMessage(getString(R.string.dialogo_confirmar_mensagem, cartao.getInsumoNome()))
                .setPositiveButton(R.string.sim, (dialogo, qual) -> viewModel.confirmarRecebimento(cartao))
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }

    private void observarViewModel() {
        // Assim que soubermos o posto: mostra o nome e começa a ouvir os cartões dele
        viewModel.getPosto().observe(this, posto -> {
            if (posto == null) return;
            binding.txtNomePosto.setText(posto.getNome());
            viewModel.ouvirCartoes(posto.getId());
        });

        // NC2 + NC5: a lista se atualiza sozinha; sem cartões, mostra o estado vazio
        viewModel.getCartoes().observe(this, this::mostrarCartoes);

        viewModel.getMensagemSucesso().observe(this, res -> {
            if (res == null) return;
            mostrarMensagem(res);
        });
        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            mostrarMensagem(res);
        });
    }

    private void mostrarCartoes(List<Cartao> lista) {
        adapter.submeterLista(lista);
        boolean vazio = lista == null || lista.isEmpty();
        binding.estadoVazio.getRoot().setVisibility(vazio ? View.VISIBLE : View.GONE);
        binding.rvCartoes.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    private void mostrarMensagem(int res) {
        Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fabNovoPedido) // não fica por cima do botão
                .show();
        viewModel.limparMensagens(); // senão a mensagem volta ao girar a tela
    }

    @Override
    protected void onStart() {
        super.onStart();
        // NC2: ao voltar para a tela (ex.: depois de abrir um pedido), volta a ouvir
        Posto posto = viewModel.getPosto().getValue();
        if (posto != null) {
            viewModel.ouvirCartoes(posto.getId());
        }
        // C6: liga o relógio que redesenha o "há X min"
        handler.postDelayed(atualizarTempo, Constantes.INTERVALO_ATUALIZACAO_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.pararDeOuvirCartoes();          // NC2: ListenerRegistration.remove()
        handler.removeCallbacks(atualizarTempo);  // C6: para o relógio
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acaoSair) {
            confirmarSaida();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** NC1: pergunta antes de sair e volta para o Login limpando a pilha de telas. */
    private void confirmarSaida() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.dialogo_sair_titulo)
                .setMessage(R.string.dialogo_sair_mensagem)
                .setPositiveButton(R.string.sim, (dialogo, qual) -> {
                    viewModel.sair();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancelar, null)
                .show();
    }
}
