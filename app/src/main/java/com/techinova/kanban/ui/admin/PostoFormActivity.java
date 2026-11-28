package com.techinova.kanban.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityPostoFormBinding;
import com.techinova.kanban.model.Insumo;
import com.techinova.kanban.ui.auth.LoginActivity;
import com.techinova.kanban.util.Constantes;
import com.techinova.kanban.util.InsetsUtil;
import com.techinova.kanban.util.Validacao;

import java.util.List;

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

        adapter = new InsumosEditAdapter((posicao, insumo) -> viewModel.removerInsumo(posicao));
        binding.rvInsumos.setAdapter(adapter);

        String postoId = getIntent().getStringExtra(Constantes.EXTRA_POSTO_ID);
        if (postoId != null) {
            binding.toolbar.setTitle(R.string.posto_form_titulo_editar);
            viewModel.carregarPosto(postoId);
        }

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        binding.btnAdicionarInsumo.setOnClickListener(v -> {
            int erro = viewModel.adicionarInsumo(Validacao.texto(binding.tilNomeInsumo));
            if (erro != 0) {
                binding.tilNomeInsumo.setError(getString(erro));
                return;
            }
            binding.tilNomeInsumo.setError(null);
            binding.edtNomeInsumo.setText("");
            binding.txtErroInsumos.setVisibility(View.GONE);
        });

        binding.btnSalvar.setOnClickListener(v -> {
            boolean ok = Validacao.obrigatorio(binding.tilNomePosto, R.string.erro_nome_posto);

            List<Insumo> insumos = viewModel.getInsumosFormulario().getValue();
            if (insumos == null || insumos.isEmpty()) {
                binding.txtErroInsumos.setVisibility(View.VISIBLE);
                ok = false;
            } else {
                binding.txtErroInsumos.setVisibility(View.GONE);
            }

            if (!ok) return;

            viewModel.salvar(Validacao.texto(binding.tilNomePosto));
        });
    }

    private void observarViewModel() {
        viewModel.getInsumosFormulario().observe(this, lista -> {
            adapter.submeterLista(lista);
            binding.txtSemInsumos.setVisibility(lista == null || lista.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getNomeCarregado().observe(this, nome -> {
            if (nome != null && Validacao.texto(binding.tilNomePosto).isEmpty()) {
                binding.edtNomePosto.setText(nome);
            }
        });

        viewModel.getCarregando().observe(this, c -> {
            boolean carregando = Boolean.TRUE.equals(c);
            binding.progresso.setVisibility(carregando ? View.VISIBLE : View.GONE);
            binding.btnSalvar.setEnabled(!carregando);
        });

        viewModel.getSalvo().observe(this, salvo -> {
            if (!Boolean.TRUE.equals(salvo)) return;
            Toast.makeText(this, R.string.msg_posto_salvo, Toast.LENGTH_SHORT).show();
            finish();
        });

        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG).show();
            viewModel.limparMensagem();
        });
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
            new MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.dialogo_sair_titulo)
                    .setMessage(R.string.dialogo_sair_mensagem)
                    .setNegativeButton(R.string.cancelar, null)
                    .setPositiveButton(R.string.sim, (dialog, which) -> {
                        viewModel.sair();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}