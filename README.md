# Kanban de Reposição

App Android (Java) da **TechInova** que leva o kanban de reposição do chão de fábrica para o celular.
O operador de linha avisa o que está acabando no posto; a logística vê **o que falta, onde e há quanto tempo**, e atende por ordem de urgência.

> Projeto 06 · Desenvolvimento de Aplicativos Móveis · SENAI

## Integrantes

| Nome | Responsabilidade |
|---|---|
| Eduardo Sabio | Pessoa A · fundação e cadastros (Splash, Login, Cadastro, Postos e insumos) |
| Gabriel Santos | Pessoa B · fluxo do operador (Meus pedidos, Novo pedido, confirmação) |
| Rafael Ribeiro | Pessoa C · fluxo da logística (Quadro, colunas, timer dos minutos) |

> Ajustem a tabela se a divisão mudar.

## O problema

Quando um insumo está acabando no posto de montagem, o operador grita, acende uma luz ou vai buscar ele mesmo.
A logística acaba atendendo quem reclama mais alto, não quem precisa mais. Não há registro de quanto tempo cada posto ficou esperando.

## A solução

Um quadro digital em tempo real, no estilo Trello (colunas lado a lado):

1. O **operador** abre um cartão de reposição escolhendo o insumo do seu posto e a urgência.
2. A **logística** vê o cartão na hora, na coluna **Aberto**, ordenado por urgência e tempo de espera.
3. A logística move o cartão para **Em rota** e depois para **Entregue**.
4. O operador **confirma o recebimento**, e só então o pedido é encerrado e vai para o histórico.

Cada mudança grava o horário do servidor, e o app mostra há quantos minutos cada cartão está aberto.

### Perfis

| Perfil | Valor em `usuarios/{uid}.perfil` | O que faz |
|---|---|---|
| Operador de linha | `operador` | Abre cartões do seu posto e confirma o recebimento |
| Logística | `logistica` | Cadastra postos e insumos, vê o quadro e move os cartões |

O perfil **não é escolhido no app**: o responsável define pelo console do Firebase (veja abaixo).

## Tecnologias

- Android nativo em **Java 17** (minSdk 24, compileSdk/targetSdk 37)
- **Firebase Authentication** (e-mail e senha)
- **Cloud Firestore** (snapshot listeners, transação, WriteBatch, regras de segurança)
- **Cloudinary** (dependência e configuração prontas, não usado no CORE)
- Material Components 3, View Binding, RecyclerView, Fragments, Lifecycle (ViewModel + LiveData)
- Gradle Kotlin DSL com version catalog (`gradle/libs.versions.toml`)

## Arquitetura

**MVVM simples:** Activity/Fragment → ViewModel (LiveData) → Repository → Firestore.
Sem Hilt, Room, Kotlin ou Compose.

```
com.techinova.kanban
├── model/
│   ├── Usuario.java          POJO de usuarios/{uid}
│   ├── Posto.java            POJO de postos/{id} com List<Insumo>
│   ├── Insumo.java           {id, nome}
│   ├── Cartao.java           POJO de cartoes/{postoId_insumoId}
│   └── Status.java           ABERTO / EM_ROTA / ENTREGUE + Urgencia (NORMAL=1, ALTA=2, CRITICA=3)
├── data/
│   ├── RepositorioCallback.java  interface onSucesso/onErro
│   ├── AuthRepository.java       login, cadastro, logout, usuário atual, perfil
│   ├── PostoRepository.java      ouvir postos, salvar, buscar por id
│   └── CartaoRepository.java     abrir (transação), ouvirPorStatus, ouvirDoPosto, avancarStatus, confirmar (batch)
├── ui/
│   ├── auth/       SplashActivity, LoginActivity, CadastroActivity, AuthViewModel
│   ├── admin/      PostosActivity, PostoFormActivity, PostosAdapter, InsumosEditAdapter, PostoViewModel
│   ├── operador/   OperadorActivity, NovoCartaoActivity, MeusCartoesAdapter, OperadorViewModel
│   └── logistica/  QuadroActivity (quadro estilo Trello), ColunaFragment, CartaoAdapter, QuadroViewModel
└── util/
    ├── Constantes.java       nomes de coleções e campos
    ├── Validacao.java        validação com setError no TextInputLayout
    ├── ErrosFirebase.java    tradução das exceções do Firebase para português
    ├── TempoUtil.java        "há 12 min", "há 1 h 5 min"
    ├── InsetsUtil.java       ajuste de barras do sistema (edge-to-edge do Android 15+)
    └── CloudinaryConfig.java configuração do Cloudinary (chaves no local.properties)
```

Regras do projeto:

- Listeners do Firestore são registrados no `onStart()` e removidos no `onStop()` (`ListenerRegistration.remove()`).
- Estado de tela (seleções, listas em edição, carregando, mensagens) fica no **ViewModel**, para sobreviver à rotação. Não usamos `android:configChanges`.
- Todo texto visível está em `res/values/strings.xml`, em português.

## Modelo de dados (Firestore)

| Coleção | Campos |
|---|---|
| `usuarios/{uid}` | `nome` (String), `perfil` (`"operador"` ou `"logistica"`), `postoId` (String, só operador) |
| `postos/{postoId}` | `nome` (String), `insumos` (lista de `{id, nome}`) |
| `cartoes/{postoId_insumoId}` | `postoId`, `postoNome`, `insumoId`, `insumoNome`, `urgencia` (int 1/2/3), `status`, `abertoPor` (uid), `abertoEm`, `emRotaEm`, `entregueEm` (Timestamp) |
| `historico/{autoId}` | todos os campos do cartão + `concluidoEm` (Timestamp) |

- `urgencia` é número para o `orderBy("urgencia", DESCENDING)` funcionar.
- `postoNome` e `insumoNome` ficam duplicados no cartão para o quadro não precisar de consultas extras.
- O id `postoId_insumoId` garante que não existam dois cartões abertos do mesmo insumo no mesmo posto (C3).
- Todos os horários usam `FieldValue.serverTimestamp()` (horário do servidor, não do celular).

## Fluxo de status

```
aberto ──(logística)──▶ em_rota ──(logística)──▶ entregue ──(operador confirma)──▶ historico
```

- **Abrir** usa uma **transação**: lê `cartoes/{postoId_insumoId}`; se existir, recusa com
  "Já existe um pedido em andamento para este insumo"; se não existir, cria.
- **Confirmar** usa um **WriteBatch**: copia o cartão para `historico/{autoId}` (com `concluidoEm`) e apaga de `cartoes`.
  Assim o mesmo insumo pode ser pedido de novo e o histórico fica guardado.

## Como configurar o Firebase

1. Acesse o [console do Firebase](https://console.firebase.google.com) e clique em **Adicionar projeto**.
2. Em **Configurações do projeto › Seus apps**, adicione um app **Android** com o pacote `com.techinova.kanban`.
3. Baixe o arquivo **`google-services.json`** e coloque em **`app/google-services.json`**.
   Esse arquivo **não vai para o GitHub** (está no `.gitignore`). Cada integrante baixa o seu.
4. **Authentication › Método de login**: ative **E-mail/senha**.
5. **Firestore Database › Criar banco de dados** (modo produção, região `southamerica-east1`).
6. **Regras**: copie o conteúdo de [`firestore.rules`](firestore.rules) para a aba *Regras* e clique em **Publicar**.
7. **Índices**: crie os índices compostos de [`firestore.indexes.json`](firestore.indexes.json) em *Firestore › Índices › Composto*:
   - `cartoes`: `status` ↑, `urgencia` ↓, `abertoEm` ↑
   - `cartoes`: `postoId` ↑, `abertoEm` ↑

   Alternativa pela CLI (com `firebase-tools` instalado): `firebase deploy --only firestore`.
   Se esquecer um índice, o Logcat mostra um erro `FAILED_PRECONDITION` com um link que cria o índice.
8. **Crie os usuários de teste** pelo app (tela *Criar conta*) ou em *Authentication › Adicionar usuário*.
9. **Defina o perfil de cada usuário** (o app não deixa o usuário escolher):
   - Copie o **UID** do usuário em *Authentication*.
   - Em *Firestore*, crie a coleção `usuarios` e um documento com **ID = UID**:
     - logística: `nome: "Logística Teste"`, `perfil: "logistica"`
     - operador: `nome: "Operador Teste"`, `perfil: "operador"`, `postoId: "<id do posto>"`
10. Entre com a conta de logística, cadastre um posto (ex.: *Montagem 3*) e copie o **ID do documento** do posto
    no console para o campo `postoId` do operador.

### Cloudinary (opcional)

O CORE não usa fotos. Para usar, preencha no `local.properties` (que também fica fora do Git):

```properties
cloudinary.cloud_name=seu-cloud-name
cloudinary.upload_preset=seu-preset-unsigned
```

Os valores chegam no código pelo `BuildConfig` (veja `CloudinaryConfig`). Nunca coloque o *api secret* no app.

### Compilar sem o `google-services.json`

O build aplica o plugin do Google Services **só se** `app/google-services.json` existir. Sem ele, o projeto compila
(útil para conferir erros de código), mas o Firebase não funciona ao rodar o app.

```bash
./gradlew assembleDebug
```

## Contas de teste

| Perfil | E-mail | Senha | Posto |
|---|---|---|---|
| Operador | `operador@teste.com` | *(a definir)* | Montagem 3 |
| Logística | `logistica@teste.com` | *(a definir)* | — |

## Histórias de usuário

### C1 · Cadastro de postos e insumos
Como **logística**, quero cadastrar os postos de trabalho e os insumos de cada um para que o operador só possa pedir o que o posto realmente usa.

- **Dado** que estou logado como logística, **quando** salvo o posto "Montagem 3" com os insumos "Parafuso sextavado M8" e "Arruela lisa 1/4", **então** o posto aparece na lista com os 2 insumos.
- **Dado** que deixei o nome do posto vazio, **quando** toco em Salvar, **então** o app mostra "Informe o nome do posto" e nada é gravado.
- **Dado** que estou logado como operador, **quando** tento gravar um posto, **então** o Firestore recusa a escrita.

### C2 · Abrir cartão de reposição
Como **operador de linha**, quero abrir um cartão escolhendo o insumo e a urgência para avisar a logística do que está acabando no meu posto.

- **Dado** que estou logado como operador do posto Montagem 3, **quando** abro a tela Novo cartão, **então** a lista mostra só os insumos desse posto.
- **Dado** que escolhi "Parafuso sextavado M8" com urgência crítica, **quando** toco em Abrir, **então** o cartão é criado com status Aberto e o horário do servidor.
- **Dado** que não escolhi um insumo, **quando** toco em Abrir, **então** o app mostra "Escolha um insumo" e nada é criado.

### C3 · Impedir cartão duplicado
Como **operador de linha**, quero que o app impeça um segundo cartão do mesmo insumo para que a logística não faça duas viagens pelo mesmo pedido.

- **Dado** que existe um cartão em andamento para o parafuso M8 no posto Montagem 3, **quando** tento abrir outro igual, **então** o app mostra "Já existe um pedido em andamento para este insumo" e nada é criado.
- **Dado** que o cartão anterior desse insumo já foi confirmado, **quando** abro um novo, **então** ele é criado normalmente.

### C4 · Quadro em tempo real
Como **logística**, quero ver os cartões nas colunas Aberto, Em rota e Entregue e movê-los para saber o que falta repor e em que ordem.

- **Dado** que estou com o quadro aberto, **quando** um operador abre um cartão em outro aparelho, **então** ele aparece na coluna Aberto sem recarregar a tela.
- **Dado** um cartão em Aberto, **quando** toco em Saiu para entrega, **então** ele vai para Em rota e o horário é gravado.
- **Dado** dois cartões em Aberto, um crítico e um normal, **quando** abro a coluna, **então** o crítico aparece primeiro.
- **Dado** que não há cartões numa coluna, **quando** abro essa aba, **então** aparece a tela de estado vazio.

### C5 · Confirmar recebimento
Como **operador de linha**, quero confirmar que recebi o insumo para que o pedido só seja encerrado quando o material chegou de verdade.

- **Dado** um cartão do meu posto em Entregue, **quando** toco em Confirmar recebimento, **então** ele sai do quadro e vai para o histórico com o horário de conclusão.
- **Dado** um cartão ainda Em rota, **quando** olho a lista Meus cartões, **então** o botão Confirmar recebimento não aparece.

### C6 · Horários e tempo aberto
Como **logística**, quero ver há quantos minutos cada cartão está aberto para atender primeiro quem espera há mais tempo.

- **Dado** um cartão aberto há 12 minutos, **quando** olho o quadro, **então** ele mostra "há 12 min".
- **Dado** que deixo o quadro aberto sem mexer, **quando** passa 1 minuto, **então** o contador avança sozinho.
- **Dado** um cartão que passou por todas as etapas, **quando** abro o histórico, **então** vejo os horários de abertura, saída, entrega e confirmação.

## Fluxo de trabalho com Git

1. Cada história vira uma **issue** (template *História de usuário*) no **GitHub Projects**, com as colunas **A fazer**, **Fazendo** e **Feito**.
2. Para trabalhar numa história, crie uma branch a partir da `main`:
   `git checkout -b feature/c2-abrir-cartao`
3. Faça commits pequenos e com mensagem clara (`C2: valida insumo obrigatório`).
4. Abra um **pull request** (o template já vem preenchido) com **`Closes #N`** no texto, para fechar a issue ao fazer o merge.
5. **Outro integrante revisa** e aprova o PR antes do merge. Ninguém faz merge do próprio PR sem revisão.
6. Depois do merge, mova o card para **Feito**.

## Prints das telas

| Login | Quadro | Novo pedido | Meus pedidos | Postos |
|---|---|---|---|---|
| ![Login](docs/prints/login.png) | ![Quadro](docs/prints/quadro.png) | ![Novo pedido](docs/prints/novo_cartao.png) | ![Meus pedidos](docs/prints/operador.png) | ![Postos](docs/prints/postos.png) |

> Substituam pelos prints reais em `docs/prints/`.

## Checklist CORE

| ID | Requisito | Situação |
|---|---|---|
| C1 | Cadastro de postos e insumos | não feito |
| C2 | Operador abre cartão (insumo + urgência) | não feito |
| C3 | Sem cartão duplicado (`postoId_insumoId`, transação) | não feito |
| C4 | Quadro Aberto / Em rota / Entregue em tempo real | não feito |
| C5 | Operador confirma recebimento (batch → histórico) | não feito |
| C6 | Horários gravados e "há X min" | não feito |
| NC1 | Cadastro/login com e-mail, sessão persistente, Sair no menu | não feito |
| NC2 | Firestore com snapshot listener | não feito |
| NC3 | Regras de segurança com controle de perfil | não feito |
| NC4 | Perfil em `usuarios/{uid}`, definido pelo console | não feito |
| NC5 | RecyclerView + estado vazio | não feito |
| NC6 | Formulários validados em português | não feito |
| NC7 | Rotação não perde o que foi digitado | não feito |
| NC8 | GitHub + README, sem `google-services.json` | não feito |
