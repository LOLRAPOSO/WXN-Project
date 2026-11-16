package br.com.wxn.chatbot.servico;

import br.com.wxn.chatbot.modelo.RespostaChatbot;
import br.com.wxn.chatbot.modelo.RespostaChatbot.ItemMenu;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Critério de aceite da US01:
 * Dado que o cliente abriu o chat,
 * Quando o bot envia a primeira mensagem,
 * Então é exibido o menu com no mínimo Suporte, Financeiro e Falar com humano.
 */
class ChatbotTest {

    private final Chatbot chatbot = new Chatbot(new MotorTriagem());

    @Test
    void primeiraMensagemExibeMenuComSuporteFinanceiroEFalarComHumano() {
        RespostaChatbot resposta = chatbot.iniciarConversa();

        List<String> titulos = resposta.opcoes().stream().map(ItemMenu::titulo).toList();
        assertTrue(titulos.containsAll(List.of("Suporte", "Financeiro", "Falar com humano")));
    }

    @Test
    void opcoesDoMenuSaoNumeradasEmSequenciaAPartirDeUm() {
        List<ItemMenu> opcoes = chatbot.iniciarConversa().opcoes();

        for (int i = 0; i < opcoes.size(); i++) {
            assertEquals(i + 1, opcoes.get(i).numero());
        }
    }

    @Test
    void boasVindasAvisamQueEPossivelFalarComUmAtendente() {
        RespostaChatbot resposta = chatbot.iniciarConversa();

        assertTrue(resposta.mensagens().stream().anyMatch(m -> m.contains("atendente")));
    }

    @Test
    void escolherNumeroValidoRespondeComOAssuntoEscolhido() {
        RespostaChatbot resposta = chatbot.responder("2");

        assertTrue(resposta.mensagens().get(0).contains("Financeiro"));
        assertTrue(resposta.opcoes().isEmpty());
    }

    @Test
    void aceitaNumeroComPontoParentesesOuEspacos() {
        assertTrue(chatbot.responder(" 1. ").mensagens().get(0).contains("Suporte"));
        assertTrue(chatbot.responder("3)").mensagens().get(0).contains("atendente humano"));
    }

    @Test
    void opcaoInexistenteAvisaEMostraOMenuDeNovo() {
        RespostaChatbot resposta = chatbot.responder("9");

        assertTrue(resposta.mensagens().get(0).startsWith("Não encontrei essa opção"));
        assertEquals(3, resposta.opcoes().size());
    }

    @Test
    void textoQueNaoEhNumeroMostraOMenuDeNovo() {
        assertEquals(3, chatbot.responder("quero ajuda").opcoes().size());
    }

    @Test
    void mensagemVaziaPedeUmNumero() {
        RespostaChatbot resposta = chatbot.responder("   ");

        assertEquals(Chatbot.MENSAGEM_VAZIA, resposta.mensagens().get(0));
        assertEquals(3, resposta.opcoes().size());
    }

    @Test
    void mostrarMenuNaoRepeteAsBoasVindas() {
        RespostaChatbot resposta = chatbot.mostrarMenu();

        assertEquals(List.of(Chatbot.PEDIDO_ESCOLHA), resposta.mensagens());
        assertEquals(3, resposta.opcoes().size());
    }
}
