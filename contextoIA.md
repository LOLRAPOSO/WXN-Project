# Contexto do projeto: Chatbot de Atendimento WXN

> Documento de alinhamento da equipe. Atualizado em 19/11/2026. Entrega: **11/12/2026**.
> Planilha de apoio com todos os pacotes e arquivos: `estrutura_projeto_wxn.xlsx`.

---

## 1. Resumo em 30 segundos

- **Cliente:** WXN Tecnologia, software house que quer atendimento automatizado 24/7.
- **O que vamos entregar:** um chatbot web em **Java (POO)** com menu de triagem, FAQ, protocolo, consulta de status, escalonamento para atendente humano, avaliação (CSAT), monitoramento de uso indevido e painel administrativo. O wireframe está no `Nia.pdf`.
- **Onde entra a IA:** o FAQ responde primeiro. Se não achar boa resposta, um **LLM via API HTTP** tenta responder usando só os trechos do FAQ do cliente. Se falhar ou não souber, o caso vai para um **atendente**.
- **Ninguém precisa saber aprendizado de máquina.** A IA é uma chamada HTTP que envia texto e recebe texto.
- **Regra de corte:** se a semana 2 atrasar, cortamos o LLM e o banco de dados. Um bot só com FAQ, bem feito e com testes, é entregável.

---

## 2. Estado atual do repositório

O repositório está praticamente **vazio de código**. Existem apenas:

| Existe | Observação |
|---|---|
| Pastas `admin`, `conhecimento`, `enums`, `modelo`, `servico` e `src/main/resources` | Cada uma só com um arquivo `placeholder` |
| `README.md` | Lista as classes previstas, mas elas **ainda não foram escritas** |
| `.gitignore` | Incompleto (ver seção 9) |
| `src/App.java` | "Hello World" fora de `src/main/java`; deve ser removido |
| `lib/placeholder` | Pasta desnecessária com Maven; deve ser removida |

Ou seja, o que temos de aproveitável é o **planejamento de pacotes e nomes de classes** do README, não código pronto.

---

## 3. Decisões tomadas

| Tema | Decisão | Motivo |
|---|---|---|
| Linguagem | Java, com programação orientada a objetos | Exigência da disciplina; Java é comum no backend empresarial |
| Framework web | Spring Boot (Spring Web) | O README já usa; muito material em português |
| IA | **Sem framework de IA.** Chamada HTTP direta com `java.net.http.HttpClient` | LangChain4j e Spring AI têm curva de aprendizado e mudanças entre versões (apontado na Desk Research); não cabe no prazo |
| Provedor de IA | Qualquer API **compatível com o formato OpenAI** | Trocar de provedor é mudar configuração, não código |
| Modelo | Aberto, de licença permissiva (Qwen3 ou gpt-oss; ver seção 4) | Custo zero e sem dependência de um fornecedor |
| Dados | Repositórios **em memória** atrás de interfaces | Protege o prazo; dá para trocar por banco depois sem mexer nos serviços |
| Front-end | HTML/CSS/JS simples servido pelo próprio back-end | Mais barato que React e suficiente para o wireframe |
| Multi-cliente | Campo `clienteId` em `PerguntaFAQ` e `Conversa` | Atende "cada cliente com sua própria base" |

### Por que Java e não Node.js, se a WXN usa Node?

O Relatório de ambiente cita Node.js, React, PostgreSQL e Vercel. A disciplina exige Java. A justificativa para a WXN é que o back-end Java expõe uma **API REST** e qualquer front-end (React inclusive) ou ERP consome essa API sem problemas. **O Relatório de ambiente precisa ser atualizado** para refletir isso (seção 9).

---

## 4. Tecnologias recomendadas

| Camada | Tecnologia | Observação |
|---|---|---|
| Linguagem | **Java LTS (21 ou 25)** | O README pede JDK 26, que não é LTS. Todos devem usar a **mesma** versão; confirmar qual o Spring Boot escolhido suporta |
| Build | **Maven** com Maven Wrapper (`mvnw`) | Ninguém precisa instalar Maven |
| API REST | **Spring Boot Web** | Controllers finos, sem regra de negócio |
| JSON | **Jackson** | Já vem com o Spring Web |
| Cliente HTTP | **`java.net.http.HttpClient`** | Já vem no JDK; sem dependência extra |
| Testes | **JUnit 5** (`spring-boot-starter-test`) | Alinha com TDD visto na disciplina |
| Front-end | **HTML + CSS + JS puro** em `resources/static` | React só se alguém do grupo já dominar |
| Persistência | **Em memória** (Map/List). H2 + JPA só se sobrar tempo | JPA ainda não foi visto em aula |
| IA, desenvolvimento | **Ollama** rodando um **Qwen3 pequeno (~8B)** local | R$ 0, sem internet e sem limite de requisições |
| IA, demo | **Groq** ou **Cerebras** com **gpt-oss** ou **Qwen** | Planos gratuitos, respostas rápidas, não dependem do notebook |
| IA, reserva | **FaqEngine** (palavras-chave) | Se o LLM cair, o bot continua funcionando |
| Versionamento | **Git/GitHub**, branches por funcionalidade | Chaves **nunca** vão para o repositório |

**Sobre os modelos e provedores:** nomes de modelos, limites e preços dos planos gratuitos **mudam com frequência**. Confirmem na documentação oficial de cada provedor na hora de implementar. Testem com 15 a 20 perguntas reais do FAQ **em português** antes de decidir o modelo.

**Escolhas descartadas:** LangChain4j, Spring AI, Embabel e Google ADK (curva de aprendizado e risco de prazo), treinar ou ajustar modelos (fora do escopo), React e Vercel (back-end é Java).

---

## 5. Arquitetura

### 5.1 Visão geral

```
Navegador (HTML/JS)
      │  JSON
      ▼
   api  (controllers)
      │
      ▼
 servico.Chatbot ──► pipeline (mascarar dados → regras de uso → frustração)
      │
      ▼
 fluxo (estado atual da conversa: Menu / Faq / Status / Atendente / Avaliacao)
      │  só o EstadoFaq usa IA
      ▼
 ia.RoteadorDeResposta
      ├─► 1. FaqEngine           (busca no FAQ do cliente)
      ├─► 2. LlmEngine           (se o FAQ não resolveu)
      │        └─► ia.cliente.LlmClient ──► API de IA (Ollama, Groq...)
      └─► 3. Escalar para atendente (se falhou ou o modelo não soube)
```

As opções do menu "Suporte técnico", "Consultar status" e "Falar com atendente" **nunca chegam ao LLM**. São fluxos determinísticos, o que mantém custo baixo e comportamento previsível.

### 5.2 Estrutura de pastas e função de cada pacote

Base: `src/main/java/br/com/wxn/chatbot`

```
chatbot
├── ChatbotApplication.java   Classe principal (main) do Spring Boot
├── modelo/                   Entidades: Cliente, Conversa, Mensagem, Protocolo, Chamado,
│                             CasoEscalado, Resposta, Avaliacao, Ocorrencia
├── conhecimento/             OpcaoMenu, PerguntaFAQ (com clienteId), PerguntaSemResposta
├── enums/                    StatusChamado, Gravidade, Setor, Prioridade, Perfil,
│                             Remetente, OrigemResposta (FAQ, LLM, FALLBACK)
├── servico/                  Chatbot (fachada), FilaAtendimento, ChamadoService, MetricasService
├── admin/                    UsuarioInterno (abstrata) ← Atendente, Gestor, Administrador; Relatorio
├── pipeline/                 EtapaPipeline, PipelineMensagem, MascaradorDadosSensiveis,
│                             VerificadorRegrasUso, DetectorFrustracao
├── fluxo/                    EstadoConversa (interface) + EstadoMenu, EstadoFaq, EstadoStatus,
│                             EstadoAtendente, EstadoAvaliacao
├── ia/                       RespostaEngine (interface), FaqEngine, LlmEngine, RoteadorDeResposta,
│                             ContextoConversa, PromptBuilder, LimitadorDeUso
│   └── cliente/              LlmClient (interface), MensagemLlm, HttpLlmClient, LlmConfig
├── repositorio/              FaqRepository, ConversaRepository, ChamadoRepository
│                             + implementações *Memoria
├── api/                      ChatController, AdminController, MensagemRequest, RespostaResponse
└── excecao/                  EngineIndisponivelException, ProtocoloNaoEncontradoException,
                              LimiteExcedidoException
```

Outros locais:

```
src/main/resources/
├── application.properties    Porta e configurações (sem segredos)
├── static/                   index.html, admin.html, chat.js, admin.js, estilo.css
└── prompts/sistema.txt       Prompt de sistema do LLM (editável sem recompilar)
src/test/java/br/com/wxn/chatbot/   Espelha os pacotes acima (modelo, servico, pipeline, fluxo, ia...)
```

**Pacote = pasta; classe = arquivo `.java`.** O pacote `br.com.wxn.chatbot.ia` corresponde à pasta `src/main/java/br/com/wxn/chatbot/ia/`. A lista completa (101 arquivos, com prioridade e semana) está na planilha.

### 5.3 Mudanças em relação ao README

| No README | Na arquitetura atual |
|---|---|
| `servico/MotorTriagem` | `fluxo/EstadoMenu` |
| `servico/BuscadorFAQ` | `ia/FaqEngine` |
| `servico/Moderador` | dividido em `pipeline/VerificadorRegrasUso` e `pipeline/DetectorFrustracao` |
| (não existia) | pacotes novos: `pipeline`, `fluxo`, `ia`, `ia/cliente`, `repositorio`, `api`, `excecao` |

### 5.4 Padrões de projeto (todos já vistos ou próximos do conteúdo da disciplina)

| Padrão | Onde | Para quê |
|---|---|---|
| **Strategy** | `RespostaEngine` → `FaqEngine`, `LlmEngine` | Trocar ou combinar motores sem mexer no resto (polimorfismo por contrato) |
| **State** | `fluxo` | Cada estado trata a mensagem do seu jeito (telas 01 a 06 do wireframe) |
| **Chain of Responsibility** | `pipeline` | Mascarar dados, checar regras e detectar frustração em sequência |
| **Repository** | `repositorio` | Começar em memória e trocar por banco depois |
| **Adapter/Gateway** | `LlmClient` | Isolar a API externa atrás de uma interface |
| **Decorator** | `LimitadorDeUso` | Limitar uso sem alterar o `LlmEngine` |
| **Herança** | `admin` | `Atendente`, `Gestor` e `Administrador` herdam de `UsuarioInterno` |

### 5.5 Regras da arquitetura (valem para todo o time)

1. **Controllers não têm regra de negócio.** Só recebem e devolvem JSON.
2. **O domínio (`modelo`) não conhece HTTP nem provedores de IA.**
3. **Só o `EstadoFaq` chama o `RoteadorDeResposta`.** Os outros estados são determinísticos.
4. **Nenhum texto vai para a API externa sem passar pelo pipeline** (dados sensíveis mascarados).
5. **A chave da API fica só no back-end**, em variável de ambiente, nunca no Git.
6. **Toda dependência externa fica atrás de uma interface**, para poder ser trocada por um *fake* nos testes.

---

## 6. Como implementar a integração com IA

### 6.1 Ideia central

A API de IA recebe um JSON com a conversa e devolve um JSON com a resposta. Ollama, Groq, Cerebras, OpenRouter, OpenAI, xAI e o endpoint compatível do Gemini usam o mesmo formato (*chat completions*). **Uma única classe `HttpLlmClient` atende todos**; só mudam a URL, a chave e o nome do modelo.

Requisição:

```json
{
  "model": "qwen3:8b",
  "messages": [
    {"role": "system", "content": "Você é o assistente da WXN..."},
    {"role": "user", "content": "como pago a fatura atrasada?"}
  ]
}
```

Resposta (o texto está em `choices[0].message.content`):

```json
{ "choices": [ { "message": { "role": "assistant", "content": "Para pagar..." } } ] }
```

### 6.2 Configuração por variáveis de ambiente

| Variável | Exemplo (Ollama local) | Exemplo (nuvem) |
|---|---|---|
| `LLM_ATIVO` | `true` | `true` |
| `LLM_URL` | `http://localhost:11434/v1/chat/completions` | URL do provedor (**confirmar na documentação**) |
| `LLM_API_KEY` | (vazio) | chave gerada no painel do provedor |
| `LLM_MODELO` | `qwen3:8b` | nome do modelo no provedor |
| `LLM_TIMEOUT` | `20` | `20` |
| `LLM_MAX_TOKENS` | `300` | `300` |

URLs de referência (confirmem antes de usar): Groq `https://api.groq.com/openai/v1/chat/completions`, Cerebras `https://api.cerebras.ai/v1/chat/completions`, OpenRouter `https://openrouter.ai/api/v1/chat/completions`.

**Windows (PowerShell):** `$env:LLM_API_KEY="sua-chave"` · **Linux/macOS:** `export LLM_API_KEY="sua-chave"`.
Na IDE, configure em *Run Configuration → Environment variables*. Se quiserem um arquivo local, use `.env` ou `application-local.properties` e **coloquem no `.gitignore`**.

### 6.3 Preparar o Ollama (desenvolvimento local)

1. Instalar o Ollama (ollama.com).
2. Baixar um modelo pequeno: `ollama pull qwen3:8b`. Se o notebook for fraco, escolham um tamanho menor da família; verifiquem os nomes na biblioteca do Ollama.
3. Testar: `ollama run qwen3:8b`.
4. O Ollama expõe um endpoint compatível com o formato OpenAI em `http://localhost:11434/v1/chat/completions`. Sem GPU, esperem respostas lentas (da ordem de poucos tokens por segundo em CPU). Se for inviável, usem um provedor gratuito na nuvem desde o começo.

### 6.4 Código, em ordem de implementação

#### Passo 1: `Resposta` (pacote `modelo`)

O motor devolve um objeto, não uma `String`, para o fluxo saber **de onde veio** a resposta e **se deve escalar**.

```java
package br.com.wxn.chatbot.modelo;

import br.com.wxn.chatbot.enums.OrigemResposta;

public record Resposta(String texto, OrigemResposta origem, boolean precisaEscalar) {
    public static Resposta doFaq(String t)   { return new Resposta(t, OrigemResposta.FAQ, false); }
    public static Resposta doLlm(String t)   { return new Resposta(t, OrigemResposta.LLM, false); }
    public static Resposta escalar(String t) { return new Resposta(t, OrigemResposta.FALLBACK, true); }
}
```

> `record` exige Java 16+. Se preferirem, escrevam uma classe `final` normal com os mesmos três campos.

#### Passo 2: exceção e interfaces

```java
package br.com.wxn.chatbot.excecao;

public class EngineIndisponivelException extends Exception {
    public EngineIndisponivelException(String msg)      { super(msg); }
    public EngineIndisponivelException(Throwable causa) { super(causa); }
}
```

```java
package br.com.wxn.chatbot.ia;

public interface RespostaEngine {
    Optional<Resposta> responder(ContextoConversa ctx) throws EngineIndisponivelException;
}
```

```java
package br.com.wxn.chatbot.ia.cliente;

public interface LlmClient {
    String completar(List<MensagemLlm> mensagens) throws EngineIndisponivelException;
}

public record MensagemLlm(String papel, String conteudo) {}   // papel: "system", "user" ou "assistant"
```

#### Passo 3: `ContextoConversa` (pacote `ia`)

Agrupa o que o motor precisa. **Quem monta** é o `EstadoFaq`.

```java
public record ContextoConversa(
    String clienteId,
    String mensagemAtual,              // JÁ mascarada pelo pipeline
    List<Mensagem> historicoRecente,   // últimas 4 a 6 mensagens
    List<PerguntaFAQ> faqCandidatos    // melhores trechos do FAQ DESTE cliente
) {}
```

#### Passo 4: `FaqEngine` (primeiro motor, sem IA)

Casamento simples por palavras-chave. Só devolve resposta se a confiança passar de um limite.

```java
public class FaqEngine implements RespostaEngine {
    private static final double LIMITE = 0.6;   // ajustar com testes

    @Override
    public Optional<Resposta> responder(ContextoConversa ctx) {
        PerguntaFAQ melhor = null;
        double melhorNota = 0;
        for (PerguntaFAQ f : ctx.faqCandidatos()) {
            double nota = nota(ctx.mensagemAtual(), f);
            if (nota > melhorNota) { melhorNota = nota; melhor = f; }
        }
        return (melhor != null && melhorNota >= LIMITE)
            ? Optional.of(Resposta.doFaq(melhor.getResposta()))
            : Optional.empty();
    }

    // fração das palavras-chave do FAQ presentes na mensagem (0 a 1)
    private double nota(String mensagem, PerguntaFAQ f) {
        String texto = normalizar(mensagem);
        List<String> chaves = f.getPalavrasChave();
        if (chaves.isEmpty()) return 0;
        long achadas = chaves.stream().filter(k -> texto.contains(normalizar(k))).count();
        return (double) achadas / chaves.size();
    }

    // minúsculas e sem acento
    static String normalizar(String s) {
        return Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD)
                         .replaceAll("\\p{M}", "");
    }
}
```

Os getters (`getResposta`, `getPalavrasChave`) dependem de como vocês escreverem `PerguntaFAQ`; ajustem os nomes. Para listar os candidatos, use o `FaqRepository.buscarPorCliente(clienteId)` e ordene pela mesma nota, pegando os 3 melhores.

#### Passo 5: `LlmConfig` (pacote `ia.cliente`)

```java
public record LlmConfig(boolean ativo, String url, String apiKey, String modelo,
                        int timeoutSegundos, int maxTokens) {

    public static LlmConfig doAmbiente() {
        return new LlmConfig(
            Boolean.parseBoolean(env("LLM_ATIVO", "true")),
            env("LLM_URL", "http://localhost:11434/v1/chat/completions"),
            env("LLM_API_KEY", ""),
            env("LLM_MODELO", "qwen3:8b"),
            Integer.parseInt(env("LLM_TIMEOUT", "20")),
            Integer.parseInt(env("LLM_MAX_TOKENS", "300")));
    }

    private static String env(String chave, String padrao) {
        String v = System.getenv(chave);
        return (v == null || v.isBlank()) ? padrao : v;
    }
}
```

#### Passo 6: `HttpLlmClient`, a única classe que toca a rede

```java
public class HttpLlmClient implements LlmClient {
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper json = new ObjectMapper();
    private final LlmConfig config;

    public HttpLlmClient(LlmConfig config) { this.config = config; }

    @Override
    public String completar(List<MensagemLlm> mensagens) throws EngineIndisponivelException {
        try {
            ObjectNode corpo = json.createObjectNode();
            corpo.put("model", config.modelo());
            corpo.put("temperature", 0.2);              // respostas mais estáveis
            corpo.put("max_tokens", config.maxTokens()); // alguns provedores usam max_completion_tokens
            ArrayNode msgs = corpo.putArray("messages");
            for (MensagemLlm m : mensagens) {
                msgs.addObject().put("role", m.papel()).put("content", m.conteudo());
            }

            HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(config.url()))
                    .timeout(Duration.ofSeconds(config.timeoutSegundos()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(corpo)));
            if (!config.apiKey().isBlank()) {
                req.header("Authorization", "Bearer " + config.apiKey());
            }

            HttpResponse<String> resp = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                throw new EngineIndisponivelException("HTTP " + resp.statusCode());
            }

            String texto = json.readTree(resp.body())
                    .path("choices").path(0).path("message").path("content").asText("");
            // alguns modelos com "raciocínio" (ex.: Qwen3) devolvem um bloco <think>...</think>
            texto = texto.replaceAll("(?s)<think>.*?</think>", "").trim();
            if (texto.isBlank()) throw new EngineIndisponivelException("Resposta vazia");
            return texto;

        } catch (IOException e) {
            throw new EngineIndisponivelException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EngineIndisponivelException(e);
        }
    }
}
```

> **Nunca** imprimam a chave nem o cabeçalho `Authorization` em log.

#### Passo 7: `PromptBuilder` (pacote `ia`)

Monta: prompt de sistema + trechos do FAQ do cliente (isso é o "RAG simples") + histórico + pergunta atual.

```java
public class PromptBuilder {
    private final String promptSistema;   // lido de resources/prompts/sistema.txt

    public PromptBuilder(String promptSistema) { this.promptSistema = promptSistema; }

    public List<MensagemLlm> montar(ContextoConversa ctx) {
        StringBuilder base = new StringBuilder(promptSistema).append("\n\nBASE DE CONHECIMENTO:\n");
        for (PerguntaFAQ f : ctx.faqCandidatos()) {
            base.append("- Pergunta: ").append(f.getPergunta()).append("\n")
                .append("  Resposta: ").append(f.getResposta()).append("\n");
        }

        List<MensagemLlm> lista = new ArrayList<>();
        lista.add(new MensagemLlm("system", base.toString()));
        for (Mensagem m : ctx.historicoRecente()) {
            String papel = (m.getRemetente() == Remetente.CLIENTE) ? "user" : "assistant";
            lista.add(new MensagemLlm(papel, m.getTexto()));
        }
        lista.add(new MensagemLlm("user", ctx.mensagemAtual()));
        return lista;
    }
}
```

Conteúdo sugerido para `resources/prompts/sistema.txt`:

```
Você é o assistente virtual de atendimento da WXN. Responda sempre em português do Brasil,
de forma curta e educada.

REGRAS:
1. Responda SOMENTE com base na BASE DE CONHECIMENTO abaixo. Não invente preços, prazos,
   políticas ou procedimentos.
2. Se a resposta não estiver na base, responda exatamente: [ESCALAR]
3. Nunca peça nem repita senhas, números de cartão ou CPF completos.
4. Se o usuário pedir algo que viole as regras de uso do canal, responda exatamente: [ESCALAR]
```

O marcador `[ESCALAR]` é o que liga o "não sei" do modelo ao escalonamento do wireframe.

#### Passo 8: `LlmEngine`

```java
public class LlmEngine implements RespostaEngine {
    private final LlmClient client;
    private final PromptBuilder prompts;

    public LlmEngine(LlmClient client, PromptBuilder prompts) {
        this.client = client;
        this.prompts = prompts;
    }

    @Override
    public Optional<Resposta> responder(ContextoConversa ctx) throws EngineIndisponivelException {
        String texto = client.completar(prompts.montar(ctx));
        if (texto.contains("[ESCALAR]")) {
            return Optional.of(Resposta.escalar("Vou chamar um atendente para te ajudar."));
        }
        return Optional.of(Resposta.doLlm(texto));
    }
}
```

#### Passo 9: `RoteadorDeResposta`, que concentra a ordem FAQ → LLM → atendente

```java
public class RoteadorDeResposta {
    private static final String MSG_ESCALAR = "Vou chamar um atendente para te ajudar.";
    private static final String MSG_FALHA   = "Estou com instabilidade no momento. Vou chamar um atendente.";

    private final RespostaEngine faq;
    private final RespostaEngine llm;

    public RoteadorDeResposta(RespostaEngine faq, RespostaEngine llm) {
        this.faq = faq;
        this.llm = llm;
    }

    public Resposta responder(ContextoConversa ctx) {
        try {
            Optional<Resposta> doFaq = faq.responder(ctx);
            if (doFaq.isPresent()) return doFaq.get();
            return llm.responder(ctx).orElseGet(() -> Resposta.escalar(MSG_ESCALAR));
        } catch (EngineIndisponivelException e) {
            return Resposta.escalar(MSG_FALHA);   // o fluxo nunca vê a exceção
        }
    }
}
```

#### Passo 10: ligar tudo no Spring (pacote `ia`)

```java
@Configuration
public class IaConfig {

    @Bean LlmConfig llmConfig() { return LlmConfig.doAmbiente(); }

    @Bean LlmClient llmClient(LlmConfig cfg) { return new HttpLlmClient(cfg); }

    @Bean PromptBuilder promptBuilder() throws IOException {
        String texto = new ClassPathResource("prompts/sistema.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        return new PromptBuilder(texto);
    }

    @Bean RoteadorDeResposta roteador(LlmConfig cfg, LlmClient client, PromptBuilder prompts) {
        RespostaEngine faq = new FaqEngine();
        // LLM desligado (LLM_ATIVO=false): o roteador só usa o FAQ e escala o resto
        RespostaEngine llm = cfg.ativo() ? new LlmEngine(client, prompts) : ctx -> Optional.empty();
        return new RoteadorDeResposta(faq, llm);
    }
}
```

> Se a versão do Spring Boot escolhida não tiver `getContentAsString`, leiam o arquivo com `getInputStream().readAllBytes()`.

Para usar o `LimitadorDeUso` (semana 3), envolvam o `LlmEngine` nele: `new LimitadorDeUso(new LlmEngine(...), maxPorConversa)`.

#### Passo 11: usar no `EstadoFaq`

```java
// dentro de EstadoFaq.tratar(Conversa conversa, String textoJaMascarado)
List<PerguntaFAQ> candidatos = faqRepository.melhoresCandidatos(conversa.getClienteId(), textoJaMascarado, 3);
ContextoConversa ctx = new ContextoConversa(
        conversa.getClienteId(), textoJaMascarado, conversa.ultimasMensagens(6), candidatos);

Resposta r = roteador.responder(ctx);
metricasService.registrar(r.origem());          // alimenta o painel
if (r.precisaEscalar()) {
    conversa.mudarPara(estadoAtendente);        // padrão State
}
return r.texto();
```

### 6.5 Mascarar dados sensíveis (antes de qualquer envio à API)

Fica em `pipeline/MascaradorDadosSensiveis`. Exemplo de regras:

```java
texto = texto.replaceAll("\\b\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}\\b", "[CPF]");      // CPF
texto = texto.replaceAll("\\b(?:\\d[ -]?){13,16}\\b", "[CARTAO]");                  // cartão
```

Na tela de segurança do wireframe, o cartão aparece como `•••• 4821` e o aviso "Por segurança, não compartilhe números de cartão, senhas ou CPF por aqui." Isso também é parte da LGPD, que citamos no relatório: os dados saem do nosso sistema para terceiros quando usamos API externa.

### 6.6 Testes sem internet (TDD)

Nenhum teste da suíte principal chama a API real.

```java
public class LlmClientFake implements LlmClient {
    private final String resposta;
    private final boolean falhar;

    public LlmClientFake(String resposta, boolean falhar) { this.resposta = resposta; this.falhar = falhar; }

    @Override
    public String completar(List<MensagemLlm> mensagens) throws EngineIndisponivelException {
        if (falhar) throw new EngineIndisponivelException("falha simulada");
        return resposta;
    }
}
```

```java
class RoteadorDeRespostaTest {
    private final ContextoConversa ctx =
        new ContextoConversa("cli1", "quero mudar meu plano", List.of(), List.of());

    @Test
    void usaFaqQuandoEncontra() {
        RespostaEngine faq = c -> Optional.of(Resposta.doFaq("do FAQ"));
        RespostaEngine llm = c -> { throw new AssertionError("não deveria chamar o LLM"); };
        assertEquals(OrigemResposta.FAQ, new RoteadorDeResposta(faq, llm).responder(ctx).origem());
    }

    @Test
    void usaLlmQuandoFaqNaoEncontra() {
        RespostaEngine faq = c -> Optional.empty();
        RespostaEngine llm = new LlmEngine(new LlmClientFake("resposta do modelo", false),
                                           new PromptBuilder("sistema"));
        assertEquals(OrigemResposta.LLM, new RoteadorDeResposta(faq, llm).responder(ctx).origem());
    }

    @Test
    void escalaQuandoModeloNaoSabe() {
        RespostaEngine faq = c -> Optional.empty();
        RespostaEngine llm = new LlmEngine(new LlmClientFake("[ESCALAR]", false), new PromptBuilder("sistema"));
        assertTrue(new RoteadorDeResposta(faq, llm).responder(ctx).precisaEscalar());
    }

    @Test
    void escalaQuandoApiFalha() {
        RespostaEngine faq = c -> Optional.empty();
        RespostaEngine llm = new LlmEngine(new LlmClientFake("", true), new PromptBuilder("sistema"));
        assertTrue(new RoteadorDeResposta(faq, llm).responder(ctx).precisaEscalar());
    }
}
```

Também testem o `PromptBuilder` (o prompt só contém FAQ do cliente correto) e o `FaqEngine` (palavras-chave, acentos, limite).

### 6.7 Segurança e custo (valem nota e evitam problema)

- **Chave só em variável de ambiente.** O repositório é público; uma chave versionada por engano deve ser **revogada** imediatamente.
- **Limites:** tamanho máximo da mensagem, máximo de requisições por conversa e `max_tokens` baixo, para um teste mal feito não gerar cobrança nem estourar o plano gratuito.
- **Timeout e fallback:** APIs caem e demoram. O timeout está no `HttpLlmClient` e o fallback está no `RoteadorDeResposta`.
- **Privacidade:** planos gratuitos de alguns provedores podem usar as requisições para melhorar modelos. Usem **só dados fictícios** nos testes e mantenham o mascaramento ativo.
- **Alucinação:** o prompt manda responder só com a base de conhecimento e usar `[ESCALAR]` quando não souber. Testem com perguntas fora do FAQ para confirmar.

### 6.8 Checklist da integração

- [ ] `Resposta`, `RespostaEngine`, `FaqEngine` e testes
- [ ] `RoteadorDeResposta` testado com fakes
- [ ] `LlmClient`, `MensagemLlm`, `LlmClientFake`
- [ ] `PromptBuilder` e `sistema.txt`
- [ ] `LlmEngine` testado com fake (resposta normal, `[ESCALAR]` e falha)
- [ ] `LlmConfig` e `HttpLlmClient` apontando para o Ollama
- [ ] `IaConfig` ligando tudo no Spring
- [ ] Teste manual com 15 a 20 perguntas reais em português
- [ ] `LimitadorDeUso` e troca para Groq ou Cerebras na demo

---

## 7. Cronograma e prioridades (até 11/12)

| Semana | Período | Foco |
|---|---|---|
| **S1** | até ~26/11 | Pom e projeto Spring, domínio, enums, `FaqEngine`, `EstadoMenu` e `EstadoFaq` no console, repositórios em memória, testes unitários, UML |
| **S2** | até ~03/12 | API REST, protocolo e status, escalonamento com resumo e fila, avaliação, pipeline de segurança, `RoteadorDeResposta` e `LlmEngine` com fake, primeira interface web |
| **S3** | até ~10/12 | `HttpLlmClient` com Ollama/Groq, painel admin (perguntas sem resposta e métricas), `LimitadorDeUso`, ensaio da demo, atualização dos relatórios |

**Terminal primeiro:** antes de qualquer interface, o bot deve funcionar no console. Assim testamos toda a lógica OO com TDD sem depender da web.

**Se atrasar:** cortar nesta ordem: (1) `LimitadorDeUso`, estilos e alto contraste; (2) LLM real; (3) painel admin completo. Manter sempre: menu, FAQ, protocolo, status, escalonamento e testes.

Prioridade de cada arquivo (Essencial, Média ou Baixa) e a semana sugerida estão na aba **Arquivos** da planilha.

---

## 8. Mapeamento do wireframe (`Nia.pdf`) para funcionalidades

| Tela | O que implementar | Onde | Prioridade |
|---|---|---|---|
| 01 Menu | Menu numerado, identificação com CPF ou nº de associado | `EstadoMenu` | Essencial |
| 02 FAQ | Busca no FAQ, "Você quis dizer...?", "Ajudou? Sim/Não" | `EstadoFaq`, `FaqEngine` | Essencial |
| 03 Status | Consulta por protocolo | `EstadoStatus`, `ChamadoService` | Essencial |
| 04 Atendente | Escalonamento, fila, resumo para o atendente | `EstadoAtendente`, `FilaAtendimento` | Essencial |
| 05 Avaliação | Nota 1 a 5 e comentário | `EstadoAvaliacao`, `Avaliacao` | Média |
| 06 Segurança | Mascarar dados, bloquear abusos, registrar ocorrência | `pipeline`, `Ocorrencia` | Média |
| Admin: perguntas sem resposta | Listar e cadastrar resposta no FAQ | `AdminController`, `PerguntaSemResposta` | Média |
| Admin: métricas, casos escalados, ocorrências | Contagens e filtros | `MetricasService`, `FilaAtendimento` | Média/Baixa |
| Acessibilidade | Fonte maior, alto contraste, respostas curtas | `static/` | Baixa |

---

## 9. Ajustes pendentes no repositório e nos documentos

1. **`.gitignore`:** acrescentar `target/`, `.env`, `application-local.properties`, `.idea/` e a exceção `!.mvn/wrapper/maven-wrapper.jar` (a regra `*.jar` atual ignora o wrapper do Maven).
2. **Remover** `src/App.java` e a pasta `lib/`.
3. **`pom.xml` e `mvnw`:** não aparecem no que foi anexado. Se não existirem no repositório, gerar pelo Spring Initializr (Spring Web, Validation e, se for usar banco, Spring Data JPA + H2).
4. **Versão do Java:** o README pede JDK 26, mas cita Temurin 17 na ajuda. Padronizar em uma versão **LTS**.
5. **Dados iniciais:** o README fala em `data.sql`, que só roda com banco (H2/JPA). Se ficarmos com repositórios em memória, trocar por uma classe Java que carrega o menu, o FAQ e o usuário de teste ao iniciar.
6. **README:** atualizar estrutura de pastas, variáveis de ambiente do LLM e como rodar com Ollama.
7. **Relatório de ambiente:** ainda cita Node.js, React, PostgreSQL e Vercel. Atualizar para Java/Spring Boot, repositórios em memória (ou H2), front-end estático e a justificativa da seção 3.
8. **Desk Research:** a Tendência 5 não existe (a numeração pula do 4 para o 6) e há trechos incompletos ("sendo o melhor para uma..."). Corrigir antes de entregar.

---

## 10. Decisões em aberto para o grupo

- [ ] Qual **versão LTS do Java** vamos usar? (todos iguais)
- [ ] Alguém do grupo já usou **Spring Boot**? Quem lidera a API?
- [ ] Vamos usar **H2/JPA** ou ficar com repositórios em memória até o fim?
- [ ] Qual **modelo e provedor** de IA? (testar em português antes)
- [ ] Alguém tem **GPU ou 16 GB de RAM** para rodar o Ollama? Se não, ir direto para um provedor gratuito na nuvem.
- [ ] **Divisão de tarefas por pacote** (sugestão: dupla no domínio/FAQ, dupla em fluxo/pipeline, dupla em IA/API, dupla em front-end/admin/relatórios).

---

## 11. Combinados de equipe

- Uma **branch por tarefa** e pull request com revisão de outra pessoa.
- **Nunca** commitar chaves, senhas ou arquivos `.env`.
- Mensagens de commit curtas e no imperativo (ex.: `Adiciona FaqEngine com testes`).
- Toda classe nova de regra de negócio entra **com teste**.
- Não mover arquivos pelo explorador do sistema; usem a IDE, que atualiza o `package` automaticamente.
- Dúvida de arquitetura: voltar à seção 5.5 antes de abrir exceção.