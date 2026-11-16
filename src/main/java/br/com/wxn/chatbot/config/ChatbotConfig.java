package br.com.wxn.chatbot.config;

import br.com.wxn.chatbot.servico.Chatbot;
import br.com.wxn.chatbot.servico.MotorTriagem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra as classes da camada de negócio no Spring.
 * Assim elas continuam em Java puro e podem ser testadas sem subir o servidor.
 */
@Configuration
public class ChatbotConfig {

    @Bean
    public MotorTriagem motorTriagem() {
        return new MotorTriagem();
    }

    @Bean
    public Chatbot chatbot(MotorTriagem motorTriagem) {
        return new Chatbot(motorTriagem);
    }
}
