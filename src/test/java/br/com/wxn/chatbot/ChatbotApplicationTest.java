package br.com.wxn.chatbot;

import br.com.wxn.chatbot.web.ChatController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Confere que a aplicação sobe e que o Spring liga o controller à camada de negócio. */
@SpringBootTest
class ChatbotApplicationTest {

    @Autowired
    private ChatController chatController;

    @Test
    void aplicacaoSobeComOControllerDoChat() {
        assertNotNull(chatController);
        assertNotNull(chatController.iniciar());
    }
}
