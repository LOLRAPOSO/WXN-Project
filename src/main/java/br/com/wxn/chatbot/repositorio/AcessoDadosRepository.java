package br.com.wxn.chatbot.repositorio;

import br.com.wxn.chatbot.servico.AcessoDados;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acesso persistente aos registros de perguntas.
 */
public interface AcessoDadosRepository extends JpaRepository<AcessoDados, Integer> {
}
 