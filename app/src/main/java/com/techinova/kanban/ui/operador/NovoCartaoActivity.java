package com.techinova.kanban.ui.operador;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityNovoCartaoBinding;
import com.techinova.kanban.util.InsetsUtil;

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

        // TODO [C2] viewModel.carregarUsuarioEPosto();  (traz o posto com os insumos)

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        binding.actvInsumo.setOnItemClickListener((parent, view, position, id) -> {
            // TODO [C2/NC7] viewModel.selecionarInsumo(position); binding.tilInsumo.setError(null);
        });

        binding.toggleUrgencia.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // TODO [C2/NC7] if (!isChecked) return;
            //  checkedId == R.id.btnUrgenciaNormal  -> viewModel.selecionarUrgencia(Status.Urgencia.NORMAL)
            //  checkedId == R.id.btnUrgenciaAlta    -> Status.Urgencia.ALTA
            //  checkedId == R.id.btnUrgenciaCritica -> Status.Urgencia.CRITICA
            //  binding.txtErroUrgencia.setVisibility(View.GONE);
        });

        binding.btnAbrirPedido.setOnClickListener(v -> {
            // TODO [NC6] Sem insumo (getIndiceInsumoSelecionado() == OperadorViewModel.NENHUM)
            //  -> binding.tilInsumo.setError(getString(R.string.erro_escolha_insumo)) e não abre.
            //  Sem urgência -> binding.txtErroUrgencia.setVisibility(View.VISIBLE) e não abre.
            // TODO [C2/C3] viewModel.abrirPedido();
        });
    }

    private void observarViewModel() {
        // TODO [C2] viewModel.getPosto().observe(this, posto -> {
        //      binding.txtPosto.setText(posto.getNome());
        //      ArrayAdapter<Insumo> a = new ArrayAdapter<>(this,
        //              android.R.layout.simple_list_item_1, posto.getInsumos());
        //      binding.actvInsumo.setAdapter(a);   // só os insumos deste posto (C2)
        //      // NC7: se já havia insumo escolhido (rotação), mostrar o nome dele:
        //      int i = viewModel.getIndiceInsumoSelecionado().getValue();
        //      if (i != OperadorViewModel.NENHUM) binding.actvInsumo.setText(a.getItem(i).getNome(), false);
        //  });
        // TODO [NC7] viewModel.getUrgenciaSelecionada().observe(...): marcar o botão certo no toggle
        //  (binding.toggleUrgencia.check(R.id.btnUrgencia...)).
        // TODO [C2] getCarregando() -> progresso visível e btnAbrirPedido desabilitado.
        // TODO [C2] getPedidoAberto() == true -> Toast R.string.msg_pedido_aberto e finish().
        // TODO [C3] getMensagemErro() -> Snackbar (ex.: "Já existe um pedido em andamento para este insumo")
        //  e viewModel.limparMensagens().
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
            // TODO [NC1] Diálogo -> viewModel.sair() -> LoginActivity (limpando a pilha).
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
