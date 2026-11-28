package com.techinova.kanban.ui.auth;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.techinova.kanban.data.AuthRepository;
import com.techinova.kanban.data.RepositorioCallback;
import com.techinova.kanban.model.Usuario;
import com.techinova.kanban.util.ErrosFirebase;

/**
 * NC1 / NC4 / NC7: estado das telas Splash, Login e Cadastro.
 * Sobrevive à rotação; por isso "carregando" e as mensagens ficam aqui e não na Activity.
 * Responsável: Pessoa A.
 */
public class AuthViewModel extends ViewModel {

    /** Para onde a Splash/Login devem mandar o usuário. */
    public enum Destino {
        LOGIN,               // não há usuário logado
        OPERADOR,            // perfil "operador" com postoId -> OperadorActivity
        LOGISTICA,           // perfil "logistica" -> QuadroActivity
        SEM_PERFIL,          // usuarios/{uid} não existe ou perfil vazio -> mostrar aviso (NC4)
        OPERADOR_SEM_POSTO   // operador sem postoId -> mostrar aviso
    }

    private final AuthRepository repositorio = new AuthRepository();

    private final MutableLiveData<Boolean> carregando = new MutableLiveData<>(false);
    /** @StringRes da mensagem de erro a mostrar (null = nada). */
    private final MutableLiveData<Integer> mensagemErro = new MutableLiveData<>();
    private final MutableLiveData<Destino> destino = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cadastroConcluido = new MutableLiveData<>(false);

    public LiveData<Boolean> getCarregando() {
        return carregando;
    }

    public LiveData<Integer> getMensagemErro() {
        return mensagemErro;
    }

    public LiveData<Destino> getDestino() {
        return destino;
    }

    public LiveData<Boolean> getCadastroConcluido() {
        return cadastroConcluido;
    }

    /** Chamar depois de mostrar a mensagem, para ela não reaparecer ao girar a tela. */
    public void limparMensagem() {
        mensagemErro.setValue(null);
    }

    /** Splash e pós-login: decide o destino a partir do usuário logado e do perfil em usuarios/{uid}. */
    public void verificarSessao() {
        FirebaseUser user = repositorio.getUsuarioAtual();
        if (user == null) {
            destino.setValue(Destino.LOGIN);
            return;
        }

        carregando.setValue(true);
        repositorio.buscarUsuario(user.getUid(), new RepositorioCallback<Usuario>() {
            @Override
            public void onSucesso(Usuario usuario) {
                carregando.setValue(false);

                if (usuario == null || TextUtils.isEmpty(usuario.getPerfil())) {
                    destino.setValue(Destino.SEM_PERFIL);
                } else if (usuario.isLogistica()) {
                    destino.setValue(Destino.LOGISTICA);
                } else if (usuario.isOperador()) {
                    if (!TextUtils.isEmpty(usuario.getPostoId())) {
                        destino.setValue(Destino.OPERADOR);
                    } else {
                        destino.setValue(Destino.OPERADOR_SEM_POSTO);
                    }
                } else {
                    destino.setValue(Destino.SEM_PERFIL);
                }
            }

            @Override
            public void onErro(Exception e) {
                carregando.setValue(false);
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /** Login com e-mail e senha (os campos já foram validados na Activity com Validacao). */
    public void login(String email, String senha) {
        carregando.setValue(true);
        repositorio.login(email, senha, new RepositorioCallback<FirebaseUser>() {
            @Override
            public void onSucesso(FirebaseUser resultado) {
                verificarSessao();
            }

            @Override
            public void onErro(Exception e) {
                carregando.setValue(false);
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /** Cadastro (sem perfil! NC4). */
    public void cadastrar(String nome, String email, String senha) {
        carregando.setValue(true);
        repositorio.cadastrar(nome, email, senha, new RepositorioCallback<FirebaseUser>() {
            @Override
            public void onSucesso(FirebaseUser resultado) {
                carregando.setValue(false);
                cadastroConcluido.setValue(true);
            }

            @Override
            public void onErro(Exception e) {
                carregando.setValue(false);
                mensagemErro.setValue(ErrosFirebase.traduzir(e));
            }
        });
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        repositorio.logout();
        destino.setValue(Destino.LOGIN);
    }
}