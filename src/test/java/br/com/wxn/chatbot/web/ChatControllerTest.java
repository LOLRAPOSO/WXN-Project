package br.com.wxn.chatbot.web;

import br.com.wxn.chatbot.modelo.RespostaChatbot;
import br.com.wxn.chatbot.servico.Chatbot;
import br.com.wxn.chatbot.servico.MotorTriagem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatControllerTest {

    private final ChatController controller = new ChatController(new Chatbot(new MotorTriagem()));

    @Test
    void inicioDevolveBoasVindasEMenu() {
        RespostaChatbot resposta = controller.iniciar();

        assertFalse(resposta.mensagens().isEmpty());
        assertEquals(3, resposta.opcoes().size());
    }

    @Test
    void mensagemRepassaOTextoParaOChatbot() {
        RespostaChatbot resposta = controller.responder(new ChatController.MensagemCliente("1"));

        assertTrue(resposta.mensagens().get(0).contains("Suporte"));
    }

    @Test
    void mensagemSemCorpoNaoQuebra() {
        assertEquals(3, controller.responder(null).opcoes().size());
    }
}
