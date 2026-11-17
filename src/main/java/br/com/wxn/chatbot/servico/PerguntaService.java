package br.com.wxn.chatbot.servico;

import br.com.wxn.chatbot.repositorio.AcessoDadosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serviço responsável por salvar e consultar perguntas persistidas.
 */
@Service
public class PerguntaService {

    private final AcessoDadosRepository repository;

    public PerguntaService(AcessoDadosRepository repository) {
        this.repository = repository;
    }

    public List<AcessoDados> listar() {
        return repository.findAll();
    }

    public AcessoDados salvar(AcessoDados pergunta) {
        return repository.save(pergunta);
    }
}