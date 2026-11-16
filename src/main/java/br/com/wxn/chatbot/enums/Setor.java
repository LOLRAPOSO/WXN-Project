package br.com.wxn.chatbot.enums;

/**
 * Setores da WXN para onde a triagem pode direcionar o cliente.
 */
public enum Setor {

    SUPORTE("Suporte"),
    FINANCEIRO("Financeiro"),
    ATENDIMENTO_HUMANO("Atendimento humano");

    private final String nome;

    Setor(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
