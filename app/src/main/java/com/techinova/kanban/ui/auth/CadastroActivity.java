package com.techinova.kanban.ui.auth;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.databinding.ActivityCadastroBinding;
import com.techinova.kanban.util.InsetsUtil;

/**
 * NC1 / NC4 / NC6 / NC7: cadastro com nome, e-mail, senha e confirmação.
 * NÃO tem campo de perfil (NC4): o perfil é criado pelo responsável no console em usuarios/{uid}.
 * Responsável: Pessoa A.
 */
public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        binding.btnCadastrar.setOnClickListener(v -> {
            // TODO [NC6] boolean ok = Validacao.obrigatorio(binding.tilNome, R.string.erro_nome_vazio)
            //          & Validacao.email(binding.tilEmail)
            //          & Validacao.senha(binding.tilSenha)
            //          & Validacao.senhasIguais(binding.tilSenha, binding.tilConfirmarSenha);
            //  if (!ok) return;
            // TODO [NC1] viewModel.cadastrar(nome, email, senha);
        });
    }

    private void observarViewModel() {
        // TODO [NC1] getCarregando() -> progresso visível e btnCadastrar desabilitado enquanto carrega.
        // TODO [NC1/NC6] getMensagemErro() -> Snackbar com a mensagem traduzida e limparMensagem().
        // TODO [NC1/NC4] getCadastroConcluido() == true ->
        //  Toast.makeText(this, R.string.msg_conta_criada, Toast.LENGTH_LONG).show();
        //  ir para SplashActivity (FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK) — ela mostrará
        //  "perfil ainda não liberado" até o responsável criar usuarios/{uid} no console.
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
