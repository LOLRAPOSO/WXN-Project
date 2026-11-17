package br.com.wxn.chatbot.config;

import br.com.wxn.chatbot.servico.Chatbot;
import br.com.wxn.chatbot.servico.MotorTriagem;
import br.com.wxn.chatbot.servico.AcessoDados;
import br.com.wxn.chatbot.servico.PerguntaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;

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

    @Bean
    public CommandLineRunner carregarPerguntas(PerguntaService perguntaService) {
        return args -> {
            if (perguntaService.listar().isEmpty()) {
                perguntaService.salvar(new AcessoDados(1, "Qual é a sua dúvida?"));
                perguntaService.salvar(new AcessoDados(2, "Você precisa de ajuda com um pedido?"));
                perguntaService.salvar(new AcessoDados(3, "Deseja falar com um atendente humano?"));
            }
        };
    }
}
