package com.techinova.kanban.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.techinova.kanban.data.AuthRepository;

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
        // TODO [NC1/NC4] 1) FirebaseUser user = repositorio.getUsuarioAtual();
        //     se null -> destino.setValue(Destino.LOGIN) e return.
        //  2) carregando.setValue(true); repositorio.buscarUsuario(user.getUid(), callback):
        //     - usuario == null ou perfil vazio            -> Destino.SEM_PERFIL
        //     - usuario.isLogistica()                      -> Destino.LOGISTICA
        //     - usuario.isOperador() e postoId preenchido  -> Destino.OPERADOR
        //     - usuario.isOperador() sem postoId           -> Destino.OPERADOR_SEM_POSTO
        //     - onErro -> mensagemErro.setValue(ErrosFirebase.traduzir(e))
        //     Em todos os casos: carregando.setValue(false).
    }

    /** Login com e-mail e senha (os campos já foram validados na Activity com Validacao). */
    public void login(String email, String senha) {
        // TODO [NC1] carregando = true; repositorio.login(email, senha, callback):
        //  - onSucesso -> verificarSessao() (lê o perfil e define o destino)
        //  - onErro    -> carregando = false; mensagemErro = ErrosFirebase.traduzir(e)
        //    (e-mail inválido, senha errada, usuário não encontrado... em português)
    }

    /** Cadastro (sem perfil! NC4). */
    public void cadastrar(String nome, String email, String senha) {
        // TODO [NC1/NC4] carregando = true; repositorio.cadastrar(nome, email, senha, callback):
        //  - onSucesso -> carregando = false; cadastroConcluido.setValue(true)
        //    (a Activity mostra R.string.msg_conta_criada e vai para a Splash, que mostrará SEM_PERFIL
        //     até o responsável criar usuarios/{uid} no console)
        //  - onErro    -> carregando = false; mensagemErro = ErrosFirebase.traduzir(e)
    }

    /** Item "Sair" do menu (NC1). */
    public void sair() {
        // TODO [NC1] repositorio.logout(); destino.setValue(Destino.LOGIN);
    }
}
