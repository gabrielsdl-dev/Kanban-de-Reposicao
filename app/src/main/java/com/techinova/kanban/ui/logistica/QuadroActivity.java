package com.techinova.kanban.ui.logistica;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityQuadroBinding;
import com.techinova.kanban.databinding.LayoutCabecalhoColunaBinding;
import com.techinova.kanban.model.Status;
import com.techinova.kanban.util.InsetsUtil;

/**
 * C4 / NC2: quadro da logística no estilo Trello — as colunas Aberto, Em rota e Entregue
 * ficam lado a lado e o quadro rola na horizontal.
 * Cada coluna é um ColunaFragment (o mesmo fragment, mudando só o status), que escuta
 * os cartões daquele status e rola na vertical.
 * Responsável: Pessoa C.
 */
public class QuadroActivity extends AppCompatActivity {

    private ActivityQuadroBinding binding;
    private QuadroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityQuadroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);

        // Compartilhado com os ColunaFragment (eles usam requireActivity())
        viewModel = new ViewModelProvider(this).get(QuadroViewModel.class);

        // Cabeçalhos das colunas: título + faixa colorida (pronto)
        configurarCabecalho(binding.cabecalhoAberto, Status.ABERTO);
        configurarCabecalho(binding.cabecalhoEmRota, Status.EM_ROTA);
        configurarCabecalho(binding.cabecalhoEntregue, Status.ENTREGUE);

        // Coloca um ColunaFragment em cada coluna (pronto).
        // Só na primeira criação: na rotação o FragmentManager recria os fragments sozinho.
        if (savedInstanceState == null) {
            adicionarColuna(R.id.containerAberto, Status.ABERTO);
            adicionarColuna(R.id.containerEmRota, Status.EM_ROTA);
            adicionarColuna(R.id.containerEntregue, Status.ENTREGUE);
        }

        observarViewModel();
    }

    private void configurarCabecalho(LayoutCabecalhoColunaBinding cabecalho, String status) {
        cabecalho.txtTituloColuna.setText(Status.rotulo(status));
        cabecalho.faixaColuna.setBackgroundResource(Status.faixaColuna(status));
    }

    private void adicionarColuna(@IdRes int containerId, String status) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, ColunaFragment.newInstance(status))
                .commit();
    }

    private void observarViewModel() {
        // TODO [C4] Contador no cabeçalho de cada coluna. Para cada coluna:
        //  viewModel.getCartoes(Status.ABERTO).observe(this, lista ->
        //      binding.cabecalhoAberto.txtContador.setText(String.valueOf(lista.size())));
        //  (repita para Status.EM_ROTA -> binding.cabecalhoEmRota
        //   e Status.ENTREGUE -> binding.cabecalhoEntregue)
        // TODO [C4] viewModel.getMensagemErro().observe(...) -> Snackbar + limparMensagem().
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quadro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.acaoPostos) {
            // TODO [C1] startActivity(new Intent(this, PostosActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.acaoSair) {
            // TODO [NC1] Diálogo de confirmação -> viewModel.sair() -> LoginActivity
            //  (FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK) e finish().
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
