package com.techinova.kanban.ui.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityPostoFormBinding;
import com.techinova.kanban.util.Constantes;
import com.techinova.kanban.util.InsetsUtil;

/**
 * C1 / NC6 / NC7: cadastro/edição de posto com seus insumos.
 * - Sem extra            -> novo posto
 * - EXTRA_POSTO_ID       -> edição (carrega o posto)
 * NC7: o texto dos campos sobrevive sozinho (têm id); a lista de insumos fica no PostoViewModel.
 * Responsável: Pessoa A.
 */
public class PostoFormActivity extends AppCompatActivity {

    private ActivityPostoFormBinding binding;
    private PostoViewModel viewModel;
    private InsumosEditAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityPostoFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(PostoViewModel.class);

        adapter = new InsumosEditAdapter((posicao, insumo) -> {
            // TODO [C1] viewModel.removerInsumo(posicao);
        });
        binding.rvInsumos.setAdapter(adapter);

        String postoId = getIntent().getStringExtra(Constantes.EXTRA_POSTO_ID);
        if (postoId != null) {
            binding.toolbar.setTitle(R.string.posto_form_titulo_editar);
            // TODO [C1] viewModel.carregarPosto(postoId);  (o ViewModel só busca uma vez)
        }

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        binding.btnAdicionarInsumo.setOnClickListener(v -> {
            // TODO [C1/NC6] int erro = viewModel.adicionarInsumo(Validacao.texto(binding.tilNomeInsumo));
            //  if (erro != 0) { binding.tilNomeInsumo.setError(getString(erro)); return; }
            //  binding.tilNomeInsumo.setError(null); binding.edtNomeInsumo.setText("");
            //  binding.txtErroInsumos.setVisibility(View.GONE);
        });

        binding.btnSalvar.setOnClickListener(v -> {
            // TODO [NC6] boolean ok = Validacao.obrigatorio(binding.tilNomePosto, R.string.erro_nome_posto);
            //  (critério de aceite: nome vazio -> "Informe o nome do posto" e nada é gravado)
            //  Lista de insumos vazia -> binding.txtErroInsumos.setVisibility(View.VISIBLE); ok = false;
            //  if (!ok) return;
            // TODO [C1] viewModel.salvar(Validacao.texto(binding.tilNomePosto));
        });
    }

    private void observarViewModel() {
        // TODO [C1/NC7] viewModel.getInsumosFormulario().observe(this, lista -> {
        //      adapter.submeterLista(lista);
        //      binding.txtSemInsumos.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE); });
        // TODO [C1] viewModel.getNomeCarregado().observe(...): preencher binding.edtNomePosto SÓ se o
        //  campo estiver vazio (senão a rotação apagaria o que o usuário já alterou).
        // TODO [C1] getCarregando() -> progresso e btnSalvar.setEnabled(!carregando).
        // TODO [C1] getSalvo() == true -> Toast R.string.msg_posto_salvo e finish().
        // TODO [NC6] getMensagemErro() -> Snackbar + limparMensagem().
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
            // TODO [NC1] Mesmo fluxo de sair da PostosActivity (diálogo -> viewModel.sair() -> Login).
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
