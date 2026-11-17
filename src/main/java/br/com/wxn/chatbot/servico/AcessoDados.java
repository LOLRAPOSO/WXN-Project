package br.com.wxn.chatbot.servico;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Registro persistido de uma pergunta que pode ser usada pelo chatbot.
 */
@Entity
public class AcessoDados{
    @Id
    private int id;

    @Column(nullable = false, length = 500)
    private String pergunta;

    protected AcessoDados() {
    }

    public AcessoDados(int id, String pergunta) {
        if (id < 1) {
            throw new IllegalArgumentException("O número da pergunta deve ser positivo.");
        }
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("O texto da pergunta é obrigatório.");
        }
        this.id=id;
        this.pergunta=pergunta;
    }

    public int getId() {
        return id;
    }

    public String getPergunta() {
        return pergunta;
    }
}
