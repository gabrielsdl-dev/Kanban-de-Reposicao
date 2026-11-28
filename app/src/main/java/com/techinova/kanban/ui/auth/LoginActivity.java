package com.techinova.kanban.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.techinova.kanban.databinding.ActivityLoginBinding;
import com.techinova.kanban.ui.operador.OperadorActivity;
import com.techinova.kanban.ui.logistica.QuadroActivity;
import com.techinova.kanban.util.InsetsUtil;
import com.techinova.kanban.util.Validacao;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), binding.appBar);
        setSupportActionBar(binding.toolbar);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        configurarCliques();
        observarViewModel();
    }

    private void configurarCliques() {
        binding.btnEntrar.setOnClickListener(v -> {
            boolean ok = Validacao.email(binding.tilEmail) & Validacao.senha(binding.tilSenha);
            if (!ok) return;

            String email = Validacao.texto(binding.tilEmail);
            String senha = binding.tilSenha.getEditText() != null
                    ? binding.tilSenha.getEditText().getText().toString()
                    : "";

            viewModel.login(email, senha);
        });

        binding.btnCriarConta.setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class))
        );

        binding.edtSenha.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.btnEntrar.performClick();
                return true;
            }
            return false;
        });
    }

    private void observarViewModel() {
        viewModel.getCarregando().observe(this, c -> {
            boolean carregando = Boolean.TRUE.equals(c);
            binding.progresso.setVisibility(carregando ? View.VISIBLE : View.GONE);
            binding.btnEntrar.setEnabled(!carregando);
        });

        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG).show();
            viewModel.limparMensagem();
        });

        viewModel.getDestino().observe(this, destino -> {
            if (destino == null) return;

            Intent intent = null;
            switch (destino) {
                case LOGISTICA:
                    intent = new Intent(this, QuadroActivity.class);
                    break;
                case OPERADOR:
                    intent = new Intent(this, OperadorActivity.class);
                    break;
                case SEM_PERFIL:
                case OPERADOR_SEM_POSTO:
                    intent = new Intent(this, SplashActivity.class);
                    break;
                case LOGIN:
                    return;
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}