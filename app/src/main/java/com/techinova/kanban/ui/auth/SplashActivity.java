package com.techinova.kanban.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.databinding.ActivitySplashBinding;
import com.techinova.kanban.util.InsetsUtil;

/**
 * NC1 / NC4: primeira tela. Verifica se já existe sessão (o Firebase Auth mantém o login
 * ao reabrir o app) e lê o perfil em usuarios/{uid} para direcionar:
 *  - sem usuário        -> LoginActivity
 *  - logistica          -> QuadroActivity
 *  - operador com posto -> OperadorActivity
 *  - sem perfil         -> mostra R.string.erro_perfil_nao_liberado + botão Sair
 * Responsável: Pessoa A.
 */
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

        // Opcional: CloudinaryConfig.inicializar(this);  (o CORE não usa fotos)

        binding.btnSair.setOnClickListener(v -> {
            // TODO [NC1] viewModel.sair(); (o observer do destino leva para o Login)
        });

        observarViewModel();

        // TODO [NC1/NC4] Só na primeira criação (savedInstanceState == null) chamar
        //  viewModel.verificarSessao(). Na rotação o destino já está no LiveData.
    }

    private void observarViewModel() {
        // TODO [NC1/NC4] viewModel.getDestino().observe(this, destino -> { switch (destino) ... }):
        //  - LOGIN      -> startActivity(new Intent(this, LoginActivity.class)); finish();
        //  - LOGISTICA  -> startActivity(new Intent(this, QuadroActivity.class)); finish();
        //  - OPERADOR   -> startActivity(new Intent(this, OperadorActivity.class)); finish();
        //  - SEM_PERFIL / OPERADOR_SEM_POSTO -> esconder binding.progresso, mostrar binding.txtMensagem
        //    com R.string.erro_perfil_nao_liberado / R.string.erro_operador_sem_posto e mostrar binding.btnSair.
        //  viewModel.getMensagemErro().observe(...) -> mostrar a mensagem em binding.txtMensagem.
    }
}
