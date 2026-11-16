/*
 * Tela do chat (US01).
 * Toda a regra fica no backend: esta tela só chama a API /api/chat e mostra
 * as mensagens e as opções numeradas que vierem na resposta.
 */
(function () {
    "use strict";

    const API = "/api/chat";

    const areaMensagens = document.getElementById("mensagens");
    const formulario = document.getElementById("form-mensagem");
    const campo = document.getElementById("campo-mensagem");
    const botaoEnviar = document.getElementById("btn-enviar");
    const botaoRecomecar = document.getElementById("btn-recomecar");

    let aguardando = false;

    // ---------- Chamadas à API ----------

    async function chamarApi(caminho, corpo) {
        const opcoes = corpo === undefined
            ? { method: "GET" }
            : {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(corpo)
            };
        const resposta = await fetch(API + caminho, opcoes);
        if (!resposta.ok) {
            throw new Error("HTTP " + resposta.status);
        }
        return resposta.json();
    }

    async function conversar(caminho, corpo, tentarDeNovo) {
        if (aguardando) return;
        definirAguardando(true);
        const indicador = mostrarDigitando();
        try {
            const resposta = await chamarApi(caminho, corpo);
            indicador.remove();
            mostrarRespostaDoBot(resposta);
        } catch (erro) {
            indicador.remove();
            mostrarErro(tentarDeNovo);
            console.error("Falha ao chamar " + caminho, erro);
        } finally {
            definirAguardando(false);
        }
    }

    function iniciarConversa() {
        areaMensagens.innerHTML = "";
        conversar("/inicio", undefined, iniciarConversa);
    }

    function verMenuDeNovo() {
        desativarAcoesAnteriores();
        conversar("/menu", undefined, verMenuDeNovo);
    }

    function enviarTexto(texto) {
        desativarAcoesAnteriores();
        adicionarMensagem(texto, "cliente");
        conversar("/mensagem", { texto: texto }, function () { enviarTexto(texto); });
    }

    // ---------- Montagem da tela ----------

    function mostrarRespostaDoBot(resposta) {
        resposta.mensagens.forEach(function (texto) {
            adicionarMensagem(texto, "bot");
        });

        if (resposta.opcoes.length > 0) {
            mostrarOpcoes(resposta.opcoes);
        } else {
            mostrarAcao("Ver o menu de novo", verMenuDeNovo);
        }
        rolarParaOFim();
    }

    function adicionarMensagem(texto, autor) {
        const balao = document.createElement("div");
        balao.className = "msg msg--" + autor;
        balao.textContent = texto;
        areaMensagens.appendChild(balao);
        rolarParaOFim();
    }

    function mostrarOpcoes(opcoes) {
        const lista = document.createElement("ul");
        lista.className = "opcoes";
        lista.setAttribute("aria-label", "Opções do menu");

        opcoes.forEach(function (opcao) {
            const item = document.createElement("li");
            const botao = document.createElement("button");
            botao.type = "button";
            botao.className = "opcao";
            botao.dataset.numero = opcao.numero;

            const numero = document.createElement("span");
            numero.className = "opcao__numero";
            numero.textContent = opcao.numero;

            const titulo = document.createElement("span");
            titulo.textContent = opcao.titulo;

            botao.append(numero, titulo);
            botao.addEventListener("click", function () {
                botao.classList.add("opcao--escolhida");
                enviarTexto(String(opcao.numero));
            });

            item.appendChild(botao);
            lista.appendChild(item);
        });

        areaMensagens.appendChild(lista);
    }

    function mostrarAcao(rotulo, aoClicar) {
        const botao = document.createElement("button");
        botao.type = "button";
        botao.className = "acao-menu";
        botao.textContent = rotulo;
        botao.addEventListener("click", aoClicar);
        areaMensagens.appendChild(botao);
    }

    function mostrarErro(tentarDeNovo) {
        const balao = document.createElement("div");
        balao.className = "msg msg--erro";
        balao.textContent = "Não consegui falar com o servidor. Verifique se a aplicação está rodando.";
        areaMensagens.appendChild(balao);
        mostrarAcao("Tentar de novo", function () {
            desativarAcoesAnteriores();
            tentarDeNovo();
        });
        rolarParaOFim();
    }

    function mostrarDigitando() {
        const indicador = document.createElement("div");
        indicador.className = "digitando";
        indicador.setAttribute("aria-label", "O assistente está digitando");
        indicador.innerHTML = "<span></span><span></span><span></span>";
        areaMensagens.appendChild(indicador);
        rolarParaOFim();
        return indicador;
    }

    /** Depois que o cliente responde, os botões antigos ficam só de histórico. */
    function desativarAcoesAnteriores() {
        areaMensagens.querySelectorAll(".opcao, .acao-menu").forEach(function (botao) {
            botao.disabled = true;
        });
        areaMensagens.querySelectorAll(".acao-menu").forEach(function (botao) {
            botao.remove();
        });
    }

    function definirAguardando(valor) {
        aguardando = valor;
        botaoEnviar.disabled = valor;
    }

    function rolarParaOFim() {
        areaMensagens.scrollTop = areaMensagens.scrollHeight;
    }

    // ---------- Eventos ----------

    formulario.addEventListener("submit", function (evento) {
        evento.preventDefault();
        const texto = campo.value.trim();
        if (!texto || aguardando) return;
        campo.value = "";
        enviarTexto(texto);
        campo.focus();
    });

    botaoRecomecar.addEventListener("click", function () {
        if (!aguardando) iniciarConversa();
    });

    // US01: assim que o cliente abre o chat, o bot envia o menu.
    iniciarConversa();
})();
