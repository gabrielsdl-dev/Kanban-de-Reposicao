package com.techinova.kanban.ui.operador;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityOperadorBinding;
import com.techinova.kanban.model.Posto;
import com.techinova.kanban.util.InsetsUtil;

import com.techinova.kanban.util.Constantes;

/**
 * C5 / C6 / NC2 / NC5: "Meus pedidos" do operador — cartões do posto dele, em tempo real,
 * com o botão "Confirmar recebimento" nos cartões entregues.
 * Responsável: Pessoa B.
 */
public class OperadorActivity extends AppCompatActivity {

    private ActivityOperadorBinding binding;
    private OperadorViewModel viewModel;
    private MeusCartoesAdapter adapter;

    /** C6: o "há X min" não se atualiza sozinho; este Handler redesenha a lista a cada 30 s. */
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
        adapter = new MeusCartoesAdapter(cartao -> {
            // TODO [C5] Confirmar com MaterialAlertDialogBuilder:
            //  título R.string.dialogo_confirmar_titulo,
            //  mensagem getString(R.string.dialogo_confirmar_mensagem, cartao.getInsumoNome()),
            //  "Sim" -> viewModel.confirmarRecebimento(cartao); "Cancelar" -> nada.
        });
        binding.rvCartoes.setAdapter(adapter);
        binding.estadoVazio.txtEstadoVazio.setText(R.string.operador_vazio);
        binding.estadoVazio.txtEstadoVazioDica.setText(R.string.operador_vazio_dica);

        binding.fabNovoPedido.setOnClickListener(v -> {
            // TODO [C2] startActivity(new Intent(this, NovoCartaoActivity.class));
        });

        // TODO [C2] viewModel.carregarUsuarioEPosto();  (nome do posto no cabeçalho + postoId)

        observarViewModel();
    }

    private void observarViewModel() {
        // TODO [C2] viewModel.getPosto().observe(this, posto -> {
        //      binding.txtNomePosto.setText(posto.getNome());
        //      viewModel.ouvirCartoes(posto.getId());  // começa a ouvir assim que souber o posto
        //  });
        // TODO [C5/NC5] viewModel.getCartoes().observe(this, lista -> {
        //      adapter.submeterLista(lista);
        //      boolean vazio = lista == null || lista.isEmpty();
        //      binding.estadoVazio.getRoot().setVisibility(vazio ? View.VISIBLE : View.GONE);
        //      binding.rvCartoes.setVisibility(vazio ? View.GONE : View.VISIBLE); });
        // TODO [C5] getMensagemSucesso() / getMensagemErro() -> Snackbar + limparMensagens().
    }

    @Override
    protected void onStart() {
        super.onStart();
        Posto posto = viewModel.getPosto().getValue();
        if(posto != null){
            viewModel.ouvirCartoes(posto.getId());
        }
        handler.postDelayed(atualizarTempo, Constantes.INTERVALO_ATUALIZACAO_MS);
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.pararDeOuvirCartoes();
        handler.removeCallbacks(atualizarTempo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acaoSair) {
            // TODO [NC1] Diálogo de confirmação -> viewModel.sair() -> LoginActivity
            //  (FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK) e finish().
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
