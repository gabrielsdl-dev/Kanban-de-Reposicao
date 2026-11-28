package com.techinova.kanban.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.R;
import com.techinova.kanban.databinding.ActivitySplashBinding;
import com.techinova.kanban.ui.operador.OperadorActivity; // Ajuste o pacote se necessário (Alt+Enter)
import com.techinova.kanban.ui.logistica.QuadroActivity;     // Ajuste o pacote se necessário (Alt+Enter)
import com.techinova.kanban.util.InsetsUtil;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InsetsUtil.ativarEdgeToEdge(this);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InsetsUtil.aplicar(binding.getRoot(), null);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnSair.setOnClickListener(v -> viewModel.sair());

        observarViewModel();

        if (savedInstanceState == null) {
            viewModel.verificarSessao();
        }
    }

    private void observarViewModel() {
        viewModel.getDestino().observe(this, destino -> {
            if (destino == null) return;

            switch (destino) {
                case LOGIN:
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                    break;
                case LOGISTICA:
                    startActivity(new Intent(this, QuadroActivity.class));
                    finish();
                    break;
                case OPERADOR:
                    startActivity(new Intent(this, OperadorActivity.class));
                    finish();
                    break;
                case SEM_PERFIL:
                    binding.progresso.setVisibility(View.GONE);
                    binding.txtMensagem.setVisibility(View.VISIBLE);
                    binding.txtMensagem.setText(R.string.erro_perfil_nao_liberado);
                    binding.btnSair.setVisibility(View.VISIBLE);
                    break;
                case OPERADOR_SEM_POSTO:
                    binding.progresso.setVisibility(View.GONE);
                    binding.txtMensagem.setVisibility(View.VISIBLE);
                    binding.txtMensagem.setText(R.string.erro_operador_sem_posto);
                    binding.btnSair.setVisibility(View.VISIBLE);
                    break;
            }
        });

        viewModel.getMensagemErro().observe(this, res -> {
            if (res == null) return;
            binding.progresso.setVisibility(View.GONE);
            binding.txtMensagem.setVisibility(View.VISIBLE);
            binding.txtMensagem.setText(res);
            binding.btnSair.setVisibility(View.VISIBLE);
        });
    }
}