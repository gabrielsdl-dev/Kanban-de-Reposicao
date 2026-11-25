package com.techinova.kanban.ui.admin;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityPostosBinding;
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
            // TODO [C1] Abrir PostoFormActivity para EDITAR:
            //  Intent i = new Intent(this, PostoFormActivity.class);
            //  i.putExtra(Constantes.EXTRA_POSTO_ID, posto.getId()); startActivity(i);
        });
        binding.rvPostos.setAdapter(adapter);
        binding.estadoVazio.txtEstadoVazio.setText(R.string.postos_vazio);
        binding.estadoVazio.txtEstadoVazioDica.setText(R.string.postos_vazio_dica);
        binding.estadoVazio.imgEstadoVazio.setImageResource(R.drawable.ic_fabrica);

        binding.fabNovoPosto.setOnClickListener(v -> {
            // TODO [C1] startActivity(new Intent(this, PostoFormActivity.class));  (sem extra = novo posto)
        });

        observarViewModel();
    }

    private void observarViewModel() {
        // TODO [C1/NC5] viewModel.getPostos().observe(this, lista -> {
        //      adapter.submeterLista(lista);
        //      boolean vazio = lista == null || lista.isEmpty();
        //      binding.estadoVazio.getRoot().setVisibility(vazio ? View.VISIBLE : View.GONE);
        //      binding.rvPostos.setVisibility(vazio ? View.GONE : View.VISIBLE); });
        // TODO [NC6] getMensagemErro() -> Snackbar + limparMensagem().
    }

    @Override
    protected void onStart() {
        super.onStart();
        // TODO [NC2] viewModel.ouvirPostos();  (registra o snapshot listener)
    }

    @Override
    protected void onStop() {
        super.onStop();
        // TODO [NC2] viewModel.pararDeOuvirPostos();  (ListenerRegistration.remove())
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
            // TODO [NC1] Confirmar com MaterialAlertDialogBuilder (R.string.dialogo_sair_titulo /
            //  R.string.dialogo_sair_mensagem, botões R.string.sim / R.string.cancelar).
            //  No "Sim": viewModel.sair(); abrir LoginActivity com
            //  FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK e finish().
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
