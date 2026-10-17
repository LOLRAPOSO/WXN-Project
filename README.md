# Chatbot de Atendimento WXN

Chatbot de atendimento ao cliente da WXN, com triagem por menu, FAQ, escalonamento para atendente humano, protocolo automático, consulta de status, monitoramento de pedidos indevidos, modo acessível e painel administrativo.

---

## Como rodar o projeto localmente

### 1. Pré-requisitos

Instale antes de começar:

| Ferramenta | Versão | Como verificar |
|---|---|---|
| JDK (Java) | 26 | `java -version` |
| Git | qualquer recente | `git --version` |
| Maven | opcional (o projeto usa o Maven Wrapper `mvnw`) | `mvn -version` |

> Se `java -version` não funcionar, instale o JDK (ex.: [Eclipse Temurin 17](https://adoptium.net)) e confira se a variável de ambiente `JAVA_HOME` aponta para a pasta de instalação.

### 2. Clonar o repositório

```bash
git clone [URL-DO-REPOSITÓRIO]
cd [NOME-DA-PASTA-DO-PROJETO]
```

### 3. Configurar o ambiente

As configurações ficam em `src/main/resources/application.properties`. Para rodar localmente, os valores padrão já funcionam:

```properties
# Porta da aplicação
server.port=8080

# Banco de dados em memória (H2) — não precisa instalar nada
spring.datasource.url=jdbc:h2:mem:wxn
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop

# Console do banco (opcional, para ver as tabelas)
spring.h2.console.enabled=true
```

Ao iniciar, o banco é criado vazio e preenchido com os dados de exemplo (menu de triagem, perguntas do FAQ e um usuário administrador de teste). Por isso, os dados somem quando a aplicação é encerrada.

### 4. Compilar o projeto

**Linux / macOS**
```bash
./mvnw clean install
```

**Windows (Prompt de Comando ou PowerShell)**
```bash
mvnw.cmd clean install
```

> No Linux/macOS, se aparecer "permissão negada", rode antes: `chmod +x mvnw`

### 5. Executar a aplicação

**Linux / macOS**
```bash
./mvnw spring-boot:run
```

**Windows**
```bash
mvnw.cmd spring-boot:run
```

Ou, depois de compilar, execute o `.jar` gerado:

```bash
java -jar target/[NOME-DO-ARQUIVO].jar
```

Quando aparecer no terminal a mensagem `Started ... in X seconds`, a aplicação está no ar.

### 6. Acessar

| O quê | Endereço |
|---|---|
| Chat do cliente | http://localhost:8080 |
| Modo administrador | http://localhost:8080/admin |
| Console do banco H2 | http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:wxn`) |

O usuário administrador de teste é criado pelos dados de exemplo; o login e a senha estão no arquivo `src/main/resources/data.sql`.

### 7. Rodar os testes

```bash
./mvnw test        # Linux / macOS
mvnw.cmd test      # Windows
```

Os testes (JUnit) verificam os critérios de aceite das histórias de usuário do backlog.

---

## Problemas comuns

| Problema | Solução |
|---|---|
| `java: command not found` ou versão abaixo de 26 | Instale o JDK 26 e configure o `JAVA_HOME`. |
| `Port 8080 was already in use` | Feche o programa que usa a porta ou troque `server.port` no `application.properties` (ex.: `8081`). |
| `permission denied: ./mvnw` | Rode `chmod +x mvnw`. |
| Erro ao baixar dependências | Verifique a internet e rode de novo `./mvnw clean install`. |

---

## Estrutura do projeto

```
src/main/java/br/com/wxn/chatbot
├── modelo        → Cliente, Conversa, Mensagem, Protocolo, Chamado, CasoEscalado...
├── conhecimento  → OpcaoMenu, PerguntaFAQ, PerguntaSemResposta
├── servico       → Chatbot, MotorTriagem, BuscadorFAQ, Moderador, FilaAtendimento...
├── admin         → UsuarioInterno, Atendente, Gestor, Administrador, Relatorio
└── enums         → StatusChamado, Gravidade, Setor, Prioridade, Perfil, Remetente
src/main/resources
├── application.properties  → configurações (porta, banco)
└── data.sql                → dados de exemplo carregados ao iniciar
```

## Equipe

- [Eduardo Boxwell Simões]
- [Gustavo Rezende de Lima]
- [Juan Riquelme Xavier de Souza]
- [Leticia Soares do Nascimento de Camello Barros]
- [Maria Clara Pontes Ramalho]
- [Matheus Lima Ramalho]
- [Renato Augusto Soares da Silva]
- [Victor Carraly Henrique de Lima Menezes]
