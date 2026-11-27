package com.techinova.kanban.ui.operador;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityNovoCartaoBinding;
import com.techinova.kanban.model.Insumo;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.ui.auth.LoginActivity;
import com.techinova.kanban.util.InsetsUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * C2 / C3 / NC6 / NC7: operador escolhe o insumo (só os do seu posto) e a urgência e abre o pedido.
 * NC7: a seleção do dropdown e do toggle fica no OperadorViewModel (sobrevive à rotação).
 * Responsável: Pessoa B.
 */
public class NovoCartaoActivity extends AppCompatActivity {

    private ActivityNovoCartaoBinding binding;
    private OperadorViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityNovoCartaoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(OperadorViewModel.class);
        viewModel.carregarUsuarioEPosto(); // traz o posto com os insumos (só busca uma vez)

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        // Escolheu um insumo no dropdown: guarda a POSIÇÃO no ViewModel (NC7)
        binding.actvInsumo.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.selecionarInsumo(position);
            binding.tilInsumo.setError(null);
        });

        // Escolheu a urgência no toggle: guarda o NÚMERO (1, 2 ou 3) no ViewModel (NC7)
        binding.toggleUrgencia.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            viewModel.selecionarUrgencia(urgenciaDoBotao(checkedId));
            binding.txtErroUrgencia.setVisibility(View.GONE);
        });

        binding.btnAbrirPedido.setOnClickListener(v -> {
            if (!formularioValido()) return; // NC6: nada é criado se faltar algo
            viewModel.abrirPedido();          // C2 + C3
        });
    }

    /** NC6: mostra os erros em português em TODOS os campos que faltam, de uma vez. */
    private boolean formularioValido() {
        boolean ok = true;

        Integer indice = viewModel.getIndiceInsumoSelecionado().getValue();
        if (indice == null || indice == OperadorViewModel.NENHUM) {
            binding.tilInsumo.setError(getString(R.string.erro_escolha_insumo));
            ok = false;
        } else {
            binding.tilInsumo.setError(null);
        }

        Integer urgencia = viewModel.getUrgenciaSelecionada().getValue();
        if (urgencia == null || urgencia == OperadorViewModel.NENHUM) {
            binding.txtErroUrgencia.setVisibility(View.VISIBLE);
            ok = false;
        } else {
            binding.txtErroUrgencia.setVisibility(View.GONE);
        }
        return ok;
    }

    private void observarViewModel() {
        // Posto carregado: mostra o nome e preenche o dropdown só com os insumos dele (C2)
        viewModel.getPosto().observe(this, this::mostrarPosto);

        // NC7: depois de girar a tela, marca de novo o botão de urgência escolhido
        viewModel.getUrgenciaSelecionada().observe(this, urgencia -> {
            if (urgencia == null || urgencia == OperadorViewModel.NENHUM) return;
            binding.toggleUrgencia.check(botaoDaUrgencia(urgencia));
        });

        viewModel.getCarregando().observe(this, carregando -> {
            boolean c = Boolean.TRUE.equals(carregando);
            binding.progresso.setVisibility(c ? View.VISIBLE : View.GONE);
            binding.btnAbrirPedido.setEnabled(!c);
        });

        viewModel.getPedidoAberto().observe(this, aberto -> {
            if (Boolean.TRUE.equals(aberto)) {
                Toast.makeText(this, R.string.msg_pedido_aberto, Toast.LENGTH_LONG).show();
                finish(); // volta para Meus pedidos, que já mostra o cartão novo
            }
        });

        // C3 e erros do Firebase: ex. "Já existe um pedido em andamento para este insumo"
        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG).show();
            viewModel.limparMensagens();
        });
    }

    private void mostrarPosto(Posto posto) {
        if (posto == null) return;
        binding.txtPosto.setText(posto.getNome());

        List<Insumo> insumos = posto.getInsumos() != null ? posto.getInsumos() : new ArrayList<>();
        ArrayAdapter<Insumo> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, insumos);
        binding.actvInsumo.setAdapter(adapter);

        // NC7: se já havia insumo escolhido (rotação), mostra o nome dele no campo
        Integer indice = viewModel.getIndiceInsumoSelecionado().getValue();
        if (indice != null && indice >= 0 && indice < insumos.size()) {
            binding.actvInsumo.setText(insumos.get(indice).getNome(), false);
        }
    }

    private int urgenciaDoBotao(@IdRes int botaoId) {
        if (botaoId == R.id.btnUrgenciaCritica) return Status.Urgencia.CRITICA;
        if (botaoId == R.id.btnUrgenciaAlta) return Status.Urgencia.ALTA;
        return Status.Urgencia.NORMAL;
    }

    @IdRes
    private int botaoDaUrgencia(int urgencia) {
        if (urgencia == Status.Urgencia.CRITICA) return R.id.btnUrgenciaCritica;
        if (urgencia == Status.Urgencia.ALTA) return R.id.btnUrgenciaAlta;
        return R.id.btnUrgenciaNormal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
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
