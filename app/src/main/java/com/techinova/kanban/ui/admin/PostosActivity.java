package com.techinova.kanban.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityPostosBinding;
import com.techinova.kanban.ui.auth.LoginActivity;
import com.techinova.kanban.util.Constantes;
import com.techinova.kanban.util.InsetsUtil;

/**
 * C1 / NC2 / NC5: lista de postos da logística, com FAB para cadastrar.
 * Responsável: Pessoa A.
 */
public class PostosActivity extends AppCompatActivity {

    private ActivityPostosBinding binding;
    private PostoViewModel viewModel;
    private PostosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityPostosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(PostoViewModel.class);

        // RecyclerView + estado vazio (NC5)
        adapter = new PostosAdapter(posto -> {
            Intent i = new Intent(this, PostoFormActivity.class);
            i.putExtra(Constantes.EXTRA_POSTO_ID, posto.getId());
            startActivity(i);
        });
        binding.rvPostos.setAdapter(adapter);
        binding.estadoVazio.txtEstadoVazio.setText(R.string.postos_vazio);
        binding.estadoVazio.txtEstadoVazioDica.setText(R.string.postos_vazio_dica);
        binding.estadoVazio.imgEstadoVazio.setImageResource(R.drawable.ic_fabrica);

        binding.fabNovoPosto.setOnClickListener(v ->
                startActivity(new Intent(this, PostoFormActivity.class))
        );

        observarViewModel();
    }

    private void observarViewModel() {
        viewModel.getPostos().observe(this, lista -> {
            adapter.submeterLista(lista);
            boolean vazio = lista == null || lista.isEmpty();
            binding.estadoVazio.getRoot().setVisibility(vazio ? View.VISIBLE : View.GONE);
            binding.rvPostos.setVisibility(vazio ? View.GONE : View.VISIBLE);
        });

        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG).show();
            viewModel.limparMensagem();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.ouvirPostos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.pararDeOuvirPostos();
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