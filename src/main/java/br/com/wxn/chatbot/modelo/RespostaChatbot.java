package br.com.wxn.chatbot.modelo;

import java.util.List;

/**
 * O que o bot devolve a cada interação: as mensagens de texto, em ordem,
 * e as opções numeradas que o cliente pode escolher em seguida
 * (lista vazia quando não há menu para mostrar).
 */
public record RespostaChatbot(List<String> mensagens, List<ItemMenu> opcoes) {

    public RespostaChatbot {
        mensagens = List.copyOf(mensagens);
        opcoes = List.copyOf(opcoes);
    }

    /** Uma opção do menu como aparece para o cliente: "1 - Suporte". */
    public record ItemMenu(int numero, String titulo) {
    }
}
