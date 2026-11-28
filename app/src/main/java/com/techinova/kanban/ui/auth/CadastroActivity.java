package com.techinova.kanban.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivityCadastroBinding;
import com.techinova.kanban.util.InsetsUtil;
import com.techinova.kanban.util.Validacao;

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
            boolean ok = Validacao.obrigatorio(binding.tilNome, R.string.erro_nome_vazio)
                    & Validacao.email(binding.tilEmail)
                    & Validacao.senha(binding.tilSenha)
                    & Validacao.senhasIguais(binding.tilSenha, binding.tilConfirmarSenha);
            if (!ok) return;

            String nome = Validacao.texto(binding.tilNome);
            String email = Validacao.texto(binding.tilEmail);
            String senha = binding.tilSenha.getEditText() != null
                    ? binding.tilSenha.getEditText().getText().toString()
                    : "";

            viewModel.cadastrar(nome, email, senha);
        });
    }

    private void observarViewModel() {
        viewModel.getCarregando().observe(this, c -> {
            boolean carregando = Boolean.TRUE.equals(c);
            binding.progresso.setVisibility(carregando ? View.VISIBLE : View.GONE);
            binding.btnCadastrar.setEnabled(!carregando);
        });

        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG).show();
            viewModel.limparMensagem();
        });

        viewModel.getCadastroConcluido().observe(this, concluido -> {
            if (!Boolean.TRUE.equals(concluido)) return;

            Toast.makeText(this, R.string.msg_conta_criada, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
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