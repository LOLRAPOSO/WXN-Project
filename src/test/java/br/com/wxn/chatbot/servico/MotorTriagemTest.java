package br.com.wxn.chatbot.servico;

import br.com.wxn.chatbot.conhecimento.OpcaoMenu;
import br.com.wxn.chatbot.enums.Setor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MotorTriagemTest {

    private final MotorTriagem motor = new MotorTriagem();

    @Test
    void menuInicialTemNoMinimoSuporteFinanceiroEFalarComHumano() {
        List<String> titulos = motor.getMenuInicial().stream().map(OpcaoMenu::getTitulo).toList();

        assertTrue(titulos.size() >= 3);
        assertTrue(titulos.contains("Suporte"));
        assertTrue(titulos.contains("Financeiro"));
        assertTrue(titulos.contains("Falar com humano"));
    }

    @Test
    void cadaOpcaoDoMenuPadraoApontaParaUmSetor() {
        assertEquals(Setor.SUPORTE, motor.escolher(1).orElseThrow().getSetor());
        assertEquals(Setor.FINANCEIRO, motor.escolher(2).orElseThrow().getSetor());
        assertEquals(Setor.ATENDIMENTO_HUMANO, motor.escolher(3).orElseThrow().getSetor());
    }

    @Test
    void numeroForaDoMenuNaoEscolheNenhumaOpcao() {
        assertTrue(motor.escolher(0).isEmpty());
        assertTrue(motor.escolher(4).isEmpty());
        assertTrue(motor.escolher(-1).isEmpty());
    }

    @Test
    void naoAceitaMenuVazio() {
        assertThrows(IllegalArgumentException.class, () -> new MotorTriagem(List.of()));
    }
}
