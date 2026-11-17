package br.com.wxn.chatbot.servico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.wxn.chatbot.conhecimento.OpcaoMenu;
import br.com.wxn.chatbot.enums.Setor;
import br.com.wxn.chatbot.modelo.RespostaChatbot;
import br.com.wxn.chatbot.modelo.RespostaChatbot.ItemMenu;


/**
 * Regras da conversa com o cliente.
 *
 * US01: ao iniciar a conversa, o bot envia o menu de opções numeradas,
 * com no mínimo Suporte, Financeiro e Falar com humano.
 */
public class Chatbot {

    static final String BOAS_VINDAS = "Olá! Eu sou o assistente virtual da WXN.";
    static final String AVISO_ATENDENTE =
            "Você pode pedir para falar com um atendente humano a qualquer momento.";
    static final String PEDIDO_ESCOLHA = "Sobre o que você quer falar? Escolha uma opção:";
    static final String MENSAGEM_VAZIA = "Digite o número de uma das opções.";

    /** Aceita "2", " 2 ", "2." ou "2)". */
    private static final Pattern NUMERO = Pattern.compile("^\\s*(\\d{1,3})\\s*[.)]?\\s*$");
    private final MotorTriagem motorTriagem;

    public Chatbot(MotorTriagem motorTriagem) {
        this.motorTriagem = motorTriagem;
    }

    /** Primeira mensagem do bot quando o cliente abre o chat. */
    public RespostaChatbot iniciarConversa() {
        return new RespostaChatbot(List.of(BOAS_VINDAS, AVISO_ATENDENTE, PEDIDO_ESCOLHA), itensDoMenu());
    }

    /** Mostra o menu inicial de novo, sem repetir as boas-vindas. */
    public RespostaChatbot mostrarMenu() {
        return new RespostaChatbot(List.of(PEDIDO_ESCOLHA), itensDoMenu());
    }
    
    /**
     * Trata o que o cliente digitou ou clicou.
     * Um número válido escolhe a opção. Qualquer outra coisa mostra o menu de novo.
     */
    public RespostaChatbot responder(String texto) {
        if (texto == null || texto.isBlank()) {
            return new RespostaChatbot(List.of(MENSAGEM_VAZIA), itensDoMenu());
        }

        Matcher matcher = NUMERO.matcher(texto);
        if (matcher.matches()) {
            
            Optional<OpcaoMenu> escolhida = motorTriagem.escolher(Integer.parseInt(matcher.group(1)));
            if (escolhida.isPresent()) {
                if((escolhida.get().getSetor()).equals(Setor.ATENDIMENTO_HUMANO))
                    return new RespostaChatbot(List.of(AtendimentoMock.getRespostaAtendente()),List.of());
                return new RespostaChatbot(List.of(escolhida.get().getResposta()), List.of());
            }
        }

        String erro = "Não encontrei essa opção. Digite um número de 1 a "
                + motorTriagem.getMenuInicial().size() + ".";
        return new RespostaChatbot(List.of(erro), itensDoMenu());
    }

    private List<ItemMenu> itensDoMenu() {
        List<OpcaoMenu> menu = motorTriagem.getMenuInicial();
        List<ItemMenu> itens = new ArrayList<>();
        for (int i = 0; i < menu.size(); i++) {
            itens.add(new ItemMenu(i + 1, menu.get(i).getTitulo()));
        }
        return itens;
    }
}
