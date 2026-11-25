package com.techinova.kanban.ui.auth;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.techinova.kanban.databinding.ActivityLoginBinding;
import com.techinova.kanban.util.InsetsUtil;

/**
 * NC1 / NC6 / NC7: login com e-mail e senha.
 * NC7: os campos têm android:id, então o Android já guarda o texto digitado na rotação.
 * Responsável: Pessoa A.
 */
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
            // TODO [NC6] boolean ok = Validacao.email(binding.tilEmail) & Validacao.senha(binding.tilSenha);
            //  if (!ok) return;
            // TODO [NC1] viewModel.login(Validacao.texto(binding.tilEmail), senha digitada);
        });

        binding.btnCriarConta.setOnClickListener(v -> {
            // TODO [NC1] startActivity(new Intent(this, CadastroActivity.class));
        });

        // TODO [NC6] (opcional) binding.edtSenha.setOnEditorActionListener: IME_ACTION_DONE -> btnEntrar.performClick()
    }

    private void observarViewModel() {
        // TODO [NC1] viewModel.getCarregando().observe(this, c -> {
        //      binding.progresso.setVisibility(c ? View.VISIBLE : View.GONE);
        //      binding.btnEntrar.setEnabled(!c); });
        // TODO [NC1/NC6] viewModel.getMensagemErro().observe(this, res -> {
        //      if (res == null) return;
        //      Snackbar.make(binding.getRoot(), res, Snackbar.LENGTH_LONG).show();
        //      viewModel.limparMensagem(); });
        // TODO [NC1/NC4] viewModel.getDestino().observe(...): igual à SplashActivity
        //  (LOGISTICA -> QuadroActivity, OPERADOR -> OperadorActivity, SEM_PERFIL -> SplashActivity
        //  para mostrar o aviso). Use FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK e finish().
    }
}
