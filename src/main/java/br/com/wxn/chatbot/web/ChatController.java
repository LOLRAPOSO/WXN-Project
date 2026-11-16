package br.com.wxn.chatbot.web;

import br.com.wxn.chatbot.modelo.RespostaChatbot;
import br.com.wxn.chatbot.servico.Chatbot;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API que a tela do chat (src/main/resources/static) consome.
 * Não tem regra de negócio aqui: só repassa para o {@link Chatbot}.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final Chatbot chatbot;

    public ChatController(Chatbot chatbot) {
        this.chatbot = chatbot;
    }

    /** US01: chamado quando o cliente abre o chat. Devolve boas-vindas e o menu. */
    @GetMapping("/inicio")
    public RespostaChatbot iniciar() {
        return chatbot.iniciarConversa();
    }

    /** Mostra o menu inicial de novo. */
    @GetMapping("/menu")
    public RespostaChatbot menu() {
        return chatbot.mostrarMenu();
    }

    /** Recebe o que o cliente digitou ou a opção em que ele clicou. */
    @PostMapping("/mensagem")
    public RespostaChatbot responder(@RequestBody MensagemCliente mensagem) {
        return chatbot.responder(mensagem == null ? null : mensagem.texto());
    }

    /** Corpo do POST: {"texto": "1"} */
    public record MensagemCliente(String texto) {
    }
}
