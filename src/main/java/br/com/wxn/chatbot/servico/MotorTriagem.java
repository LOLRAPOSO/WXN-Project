package br.com.wxn.chatbot.servico;

import br.com.wxn.chatbot.conhecimento.OpcaoMenu;
import br.com.wxn.chatbot.enums.Setor;

import java.util.List;
import java.util.Optional;

/**
 * Guarda a árvore de decisão e resolve a escolha do cliente.
 *
 * Por enquanto o menu fica em memória. Na Semana 14, quando entrar a camada de
 * acesso a dados, basta passar no construtor o menu carregado do banco.
 */
public class MotorTriagem {

    private final List<OpcaoMenu> menuInicial;

    /** Cria o motor com o menu padrão da WXN. */
    public MotorTriagem() {
        this(menuPadrao());
    }

    public MotorTriagem(List<OpcaoMenu> menuInicial) {
        if (menuInicial == null || menuInicial.isEmpty()) {
            throw new IllegalArgumentException("O menu inicial precisa ter pelo menos uma opção.");
        }
        this.menuInicial = List.copyOf(menuInicial);
    }

    /** Opções do primeiro nível, na ordem em que são numeradas (1, 2, 3...). */
    public List<OpcaoMenu> getMenuInicial() {
        return menuInicial;
    }

    /**
     * Devolve a opção do menu inicial com o número escolhido pelo cliente.
     *
     * @param numero número mostrado no menu, começando em 1
     * @return a opção, ou vazio se o número não existir no menu
     */
    public Optional<OpcaoMenu> escolher(int numero) {
        if (numero < 1 || numero > menuInicial.size()) {
            return Optional.empty();
        }
        
        return Optional.of(menuInicial.get(numero - 1));
    }

    /** Menu exigido pelo critério de aceite da US01: Suporte, Financeiro e Falar com humano. */
    static List<OpcaoMenu> menuPadrao() {
        return List.of(
                new OpcaoMenu("Suporte", Setor.SUPORTE,
                        "Certo, seu assunto é Suporte. Vou direcionar seu atendimento para o setor de Suporte."),
                new OpcaoMenu("Financeiro", Setor.FINANCEIRO,
                        "Certo, seu assunto é Financeiro. Vou direcionar seu atendimento para o setor Financeiro."),
                new OpcaoMenu("Falar com humano", Setor.ATENDIMENTO_HUMANO,
                        "Certo, vou te direcionar para um atendente humano."));
    }
}
