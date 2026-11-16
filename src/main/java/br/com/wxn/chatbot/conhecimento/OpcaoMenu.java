package br.com.wxn.chatbot.conhecimento;

import br.com.wxn.chatbot.enums.Setor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Um nó da árvore de decisão da triagem.
 *
 * Na US01 só o primeiro nível é usado (o menu inicial). A lista de subopções
 * já existe para a US02 (submenus) poder montar os próximos níveis sem mudar
 * esta classe.
 */
public class OpcaoMenu {

    private final String titulo;
    private final Setor setor;
    private final String resposta;
    private final List<OpcaoMenu> subopcoes = new ArrayList<>();

    /**
     * @param titulo   texto mostrado no menu (ex.: "Financeiro")
     * @param setor    setor de destino desta opção
     * @param resposta mensagem que o bot envia quando o cliente escolhe esta opção
     */
    public OpcaoMenu(String titulo, Setor setor, String resposta) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título da opção é obrigatório.");
        }
        if (resposta == null || resposta.isBlank()) {
            throw new IllegalArgumentException("A resposta da opção é obrigatória.");
        }
        this.titulo = titulo;
        this.setor = setor;
        this.resposta = resposta;
    }

    public OpcaoMenu adicionarSubopcao(OpcaoMenu subopcao) {
        subopcoes.add(subopcao);
        return this;
    }

    public String getTitulo() {
        return titulo;
    }

    public Setor getSetor() {
        return setor;
    }

    public String getResposta() {
        return resposta;
    }

    public List<OpcaoMenu> getSubopcoes() {
        return Collections.unmodifiableList(subopcoes);
    }

    public boolean temSubopcoes() {
        return !subopcoes.isEmpty();
    }
}
