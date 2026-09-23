
package pokeSall;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ataques.Habilidades;
import itens.Item;
import pokeSalls.BulbaSall;
import pokeSalls.CharSall;
import pokeSalls.SquirtSall;

/**
 * Classe principal que gerencia o fluxo do jogo PokeSall.
 * Controla as batalhas, turnos, mecânicas de terreno e uso de itens.
 */
public class Game {

    /**
     * Enumeração dos terrenos possíveis durante uma batalha.
     */
    private enum TERRENOS {
        ASFALTO_QUENTE,
        POCA_DE_CHUVA,
        CANTEIRO_CENTRAL
    }

    private static final double ESCALA_DEFESA = 100;

    private final Random rand = new Random();
    private final Scanner ler = new Scanner(System.in);

    private int acumuloVenenoJ;
    private int acumuloVenenoI;
    private int turnosVeneno;
    private int turnosFogoJ;
    private int turnosFogoI;
    private int nivelTorre;
    private int turno;

    private double dano;
    private double vida;
    private double cura;
    private int acao;

    private final String[] recompensas = {
        "Potion",
        "Potion",
        "SuperPotion",
        "SuperPotion",
        "Antidote"
    };

    private final ArrayList<Treinador> inimigos = new ArrayList<>();
    private final ArrayList<Treinador> players = new ArrayList<>();

    private PokeSall pokeSall;
    private Treinador jogador;
    private Treinador npc;
    private TERRENOS terreno;

    /**
     * Construtor do jogo.
     *
     * @param jogador treinador principal controlado pelo usuário
     */
  public Game(Treinador jogador) {
        this.jogador = jogador;
        criarInimigos();
  }

    /**
     * Inicializa a lista de inimigos da torre
     * com seus respectivos PokeSalls.
     */
    public void criarInimigos() {
        pokeSall = new CharSall();
        npc = new Npc("Ashley", pokeSall);
        inimigos.add(npc);

        pokeSall = new BulbaSall();
        npc = new Npc("Joe Lois", pokeSall);
        inimigos.add(npc);

        pokeSall = new SquirtSall();
        npc = new Npc("Mary", pokeSall);
        inimigos.add(npc);
    }

    /**
     * Método principal que roda o loop de batalhas da torre.
     */
    public void rodarGame() {
        int tempTerreno;
        boolean rodando = true;

        while (rodando && nivelTorre < inimigos.size()) {
            Treinador inimigoAtual = inimigos.get(nivelTorre);

            players.clear();
            players.add(jogador);
            players.add(inimigoAtual);

            turno = 0;
            boolean batalhando = true;

            System.out.println("==============================");
            System.out.println("Você está no nível " + (nivelTorre + 1));
            System.out.println("Enfrentará " + inimigoAtual.getNome() + "!");
            System.out.println("==============================");
            System.out.println();
            System.out.println("PRESSIONE ENTER PARA CONTINUAR!");

            ler.nextLine();
            limparTela();

            tempTerreno = rand.nextInt(0, 3);

            if (tempTerreno == 0) {
                terreno = TERRENOS.ASFALTO_QUENTE;
            } else if (tempTerreno == 1) {
                terreno = TERRENOS.POCA_DE_CHUVA;
            } else {
                terreno = TERRENOS.CANTEIRO_CENTRAL;
            }

            System.out.println("------------------------------");
            System.out.println("      BATALHA INICIADA!");
            System.out.println("Terreno atual: " + terreno);
            System.out.println("------------------------------");

            while (batalhando) {
                turno++;

                int resultado = verificarVitoria(jogador, inimigoAtual);

                if (resultado != 0) {
                    batalhando = false;

                    if (resultado == 1) {
                        nivelTorre++;
                    }

                    if (resultado == 2) {
                        rodando = false;
                    }

                    break;
                }

                System.out.println();
                System.out.println("------");
                System.out.println("TURNO " + turno);
                System.out.println("------");
                System.out.println();

                if (jogador.getPokeSall().getSPD()
                        > inimigos.get(nivelTorre).getPokeSall().getSPD()) {

                    System.out.println(
                            "O " + jogador.getPokeSall().getNome() + " começa!");

                    if (!turnoJogador(inimigoAtual)) {
                        batalhando = false;
                        rodando = false;
                        break;
                    }

                    if (inimigoAtual.getPokeSall().getHP() > 0) {
                        turnoInimigo(inimigoAtual, batalhando, rodando);
                    }

                } else {
                    System.out.println(
                            "O " + inimigoAtual.getPokeSall().getNome() + " começa!");

                    turnoInimigo(inimigoAtual, batalhando, rodando);

                    if (jogador.getPokeSall().getHP() > 0) {
                        if (!turnoJogador(inimigoAtual)) {
                            batalhando = false;
                            rodando = false;
                            break;
                        }
                    }
                }

                if (nivelTorre >= inimigos.size()
                        && jogador.getPokeSall().getHP() > 0) {
                    System.out.println(
                            "Parabéns! Você derrotou todos os inimigos da torre!");
                }

                verificarStatus(players);
                aplicarEfeitosTerreno(players);
            }
        }
    }

    /**
     * Gerencia o turno do jogador, exibindo o menu
     * e realizando a ação escolhida.
     *
     * @param inimigoAtual treinador adversário
     * @return true se realizou uma ação, false se desistiu
     */
    public boolean turnoJogador(Treinador inimigoAtual) {
        String opcao;
        boolean turnoConcluido = false;

        while (!turnoConcluido) {
            System.out.println("==============================");
            System.out.println("Turno de: " + jogador.getNome());
            System.out.println("==============================");

            do {
                System.out.println("-----------------------------");
                System.out.println(
                        "PokeSall: " + jogador.getNomePokeSall());
                System.out.println(
                        "HP: " + jogador.getPokeSall().getHP());
                System.out.println(
                        "1--ATAQUES-- 2--MOCHILA-- 3--DESISTIR");
                System.out.println("-----------------------------");

                opcao = ler.nextLine();
            } while (!(opcao.equals("1")
                    || opcao.equals("2")
                    || opcao.equals("3")));

            switch (opcao) {
                case "1":
                    do {
                        jogador.getPokeSall().listarHabilidades();

                        System.out.println("-----------------------------");
                        System.out.println("1- Para usar o primeiro ataque");
                        System.out.println("2- Para usar o segundo ataque");
                        System.out.println("3- Para voltar");

                        opcao = ler.nextLine();
                    } while (!(opcao.equals("1")
                            || opcao.equals("2")
                            || opcao.equals("3")));

                    if (opcao.equals("3")) {
                        break;
                    }

                    Tipos tipoJogador = jogador.getPokeSall().getTipo();

                    if (opcao.equals("1")) {
                        if (tipoJogador == Tipos.PLANTA) {
                            System.out.println(
                                    jogador.getNomePokeSall()
                                    + " usou Folha Buzzer!");
                            calcularAtaque(
                                    Habilidades.FOLHA_BUZZER,
                                    jogador,
                                    inimigoAtual);
                        } else if (tipoJogador == Tipos.FOGO) {
                            System.out.println(
                                    jogador.getNomePokeSall()
                                    + " usou Hell Beam!");
                            calcularAtaque(
                                    Habilidades.HELL_BEAM,
                                    jogador,
                                    inimigoAtual);
                        } else if (tipoJogador == Tipos.AGUA) {
                            System.out.println(
                                    jogador.getNomePokeSall()
                                    + " usou Beat Bolha!");
                            calcularAtaque(
                                    Habilidades.BEAT_BOLHA,
                                    jogador,
                                    inimigoAtual);
                        }

                        vida = inimigoAtual.getPokeSall().getHP();

                        System.out.println(
                                "Causou " + dano + " de dano em "
                                + inimigoAtual.getNomePokeSall() + "!");

                        inimigoAtual.getPokeSall().setHP(vida - dano);

                    } else if (opcao.equals("2")) {
                        if (tipoJogador == Tipos.PLANTA) {
                            System.out.println(
                                    jogador.getNomePokeSall()
                                    + " usou Gás!");

                            calcularAtaque(
                                    Habilidades.GAS,
                                    jogador,
                                    inimigoAtual);

                            System.out.println(
                                    inimigoAtual.getNomePokeSall()
                                    + " foi envenenado!");

                        } else if (tipoJogador == Tipos.FOGO) {
                            System.out.println(
                                    jogador.getNomePokeSall()
                                    + " usou Queimar!");

                            calcularAtaque(
                                    Habilidades.QUEIMAR,
                                    jogador,
                                    inimigoAtual);

                            System.out.println(
                                    inimigoAtual.getNomePokeSall()
                                    + " está queimando!");

                        } else if (tipoJogador == Tipos.AGUA) {
                            System.out.println(
                                    jogador.getNomePokeSall()
                                    + " usou Água Termal!");

                            calcularAtaque(
                                    Habilidades.AGUA_TERMAL,
                                    jogador,
                                    inimigoAtual);

                            System.out.println(
                                    inimigoAtual.getNomePokeSall()
                                    + " foi paralisado!");
                        }
                    }

                    return true;

                case "2":
                    do {
                        int temp = 0;

                        System.out.println("-----------------------------");

                        for (Item item : jogador.getMochila().itens) {
                            if (item.getQuantidade() > 0) {
                                temp++;

                                System.out.println(
                                        "Digite " + temp
                                        + " para usar " + item.getNome()
                                        + ": " + item.getQuantidade());

                                System.out.println(item.getDescricao());
                                System.out.println(
                                        "-----------------------------");
                            }
                        }

                        System.out.println("-----------------------------");
                        System.out.println("0- Para voltar");

                        opcao = ler.nextLine();

                    } while (!(opcao.equals("1")
                            || opcao.equals("2")
                            || opcao.equals("3")
                            || opcao.equals("0")));

                    if (opcao.equals("0")) {
                        break;
                    }

                    if (opcao.equals("1")) {
                        usarPotion(jogador);
                    } else if (opcao.equals("2")) {
                        usarAntidote(jogador);
                    } else if (opcao.equals("3")) {
                        usarSuperPotion(jogador);
                    }

                    return true;

                case "3":
                    System.out.println("Você fugiu da batalha!");
                    return false;

                default:
                    break;
            }
        }

        return true;
    }

    /**
     * Gerencia a inteligência artificial do turno do inimigo.
     *
     * @param inimigoAtual treinador adversário
     * @param batalhando estado do loop da batalha
     * @param rodando estado geral do jogo
     */
    public void turnoInimigo(
            Treinador inimigoAtual,
            boolean batalhando,
            boolean rodando) {

        if (rodando && batalhando) {
            dano = 0;

            System.out.println();
            System.out.println("Turno de " + inimigoAtual.getNome());
            System.out.println();

            int temp = rand.nextInt(1, 3);
            Tipos tipoInimigo = inimigoAtual.getPokeSall().getTipo();

            vida = jogador.getPokeSall().getHP();

            if (tipoInimigo == Tipos.FOGO) {
                if (temp == 1) {
                    System.out.println(
                            inimigoAtual.getNomePokeSall()
                            + " usou Hell Beam!");

                    calcularAtaque(
                            Habilidades.HELL_BEAM,
                            inimigoAtual,
                            jogador);

                    System.out.println(
                            "Causou " + dano + " de dano em "
                            + jogador.getNomePokeSall() + "!");

                    jogador.getPokeSall().setHP(vida - dano);
                } else {
                    System.out.println(
                            inimigoAtual.getNomePokeSall()
                            + " usou Queimar!");

                    calcularAtaque(
                            Habilidades.QUEIMAR,
                            inimigoAtual,
                            jogador);
                }

            } else if (tipoInimigo == Tipos.PLANTA) {
                if (temp == 1) {
                    System.out.println(
                            inimigoAtual.getNomePokeSall()
                            + " usou Folha Buzzer!");

                    calcularAtaque(
                            Habilidades.FOLHA_BUZZER,
                            inimigoAtual,
                            jogador);

                    System.out.println(
                            "Causou " + dano + " de dano em "
                            + jogador.getNomePokeSall() + "!");

                    jogador.getPokeSall().setHP(vida - dano);
                } else {
                    System.out.println(
                            inimigoAtual.getNomePokeSall()
                            + " usou Gás!");

                    calcularAtaque(
                            Habilidades.GAS,
                            inimigoAtual,
                            jogador);
                }

            } else if (tipoInimigo == Tipos.AGUA) {
                if (temp == 1) {
                    System.out.println(
                            inimigoAtual.getNomePokeSall()
                            + " usou Beat Bolha!");

                    calcularAtaque(
                            Habilidades.BEAT_BOLHA,
                            inimigoAtual,
                            jogador);

                    System.out.println(
                            "Causou " + dano + " de dano em "
                            + jogador.getNomePokeSall() + "!");

                    jogador.getPokeSall().setHP(vida - dano);
                } else {
                    System.out.println(
                            inimigoAtual.getNomePokeSall()
                            + " usou Água Termal!");

                    calcularAtaque(
                            Habilidades.AGUA_TERMAL,
                            inimigoAtual,
                            jogador);
                }
            }
        }
    }

    /**
     * Limpa a tela do terminal.
     */
    public void limparTela() {
        for (int l = 0; l < 20; l++) {
            System.out.println();
        }
    }

    /**
     * Calcula o dano e aplica os efeitos das habilidades.
     * Exibe os buffs ativados pelos terrenos.
     *
     * @param habilidade habilidade utilizada
     * @param atacante treinador atacante
     * @param alvo treinador alvo
     */
    public void calcularAtaque(
            Habilidades habilidade,
            Treinador atacante,
            Treinador alvo) {

        double atkModificado = atacante.getPokeSall().getATK();
        dano = 0;

        if (terreno == TERRENOS.ASFALTO_QUENTE) {
            atkModificado *= 1.15;

            System.out.println(
                    "[TERRENO] Asfalto Quente aumentou o ataque de "
                    + atacante.getNomePokeSall() + " em 15%!");
        }

        if (habilidade == Habilidades.FOLHA_BUZZER) {
            if (alvo.getPokeSall().getTipo() == Tipos.AGUA) {
                dano = calcularDefesa(
                        (60 + atkModificado) * 2,
                        alvo);
            } else if (alvo.getPokeSall().getTipo() == Tipos.FOGO) {
                dano = calcularDefesa(
                        (60 + atkModificado) * 0.5,
                        alvo);
            } else {
                dano = calcularDefesa(
                        60 + atkModificado,
                        alvo);
            }
        }

        if (habilidade == Habilidades.GAS) {
            if (alvo.isNpc()) {
                if (acumuloVenenoI < 3) {
                    acumuloVenenoI++;
                }
            } else {
                if (acumuloVenenoJ < 3) {
                    acumuloVenenoJ++;
                }
            }

            alvo.getPokeSall().setStatus(Status.ENVENENADO);
        }

        if (habilidade == Habilidades.HELL_BEAM) {
            if (alvo.getPokeSall().getTipo() == Tipos.PLANTA) {
                dano = calcularDefesa(
                        (75 + atkModificado) * 2,
                        alvo);
            } else if (alvo.getPokeSall().getTipo() == Tipos.AGUA) {
                dano = calcularDefesa(
                        (75 + atkModificado) * 0.5,
                        alvo);
            } else {
                dano = calcularDefesa(
                        75 + atkModificado,
                        alvo);
            }
        }

        if (habilidade == Habilidades.QUEIMAR) {
            if (alvo.isNpc()) {
                turnosFogoI = 3;
            } else {
                turnosFogoJ = 3;
            }

            alvo.getPokeSall().setStatus(Status.QUEIMANDO);
        }

        if (habilidade == Habilidades.AGUA_TERMAL) {
            alvo.getPokeSall().setStatus(Status.PARALIZADO);
        }

        if (habilidade == Habilidades.BEAT_BOLHA) {
            if (alvo.getPokeSall().getTipo() == Tipos.FOGO) {
                dano = calcularDefesa(
                        (65 + atkModificado) * 2,
                        alvo);
            } else if (alvo.getPokeSall().getTipo() == Tipos.PLANTA) {
                dano = calcularDefesa(
                        (65 + atkModificado) * 0.5,
                        alvo);
            } else {
                dano = calcularDefesa(
                        65 + atkModificado,
                        alvo);
            }
        }

        if (terreno == TERRENOS.POCA_DE_CHUVA
                && atacante.getPokeSall().getTipo() == Tipos.AGUA) {

            dano *= 1.10;

            System.out.println(
                    "[TERRENO] Poça de Chuva aumentou o dano "
                    + "do ataque de água de "
                    + atacante.getNomePokeSall() + " em 10%!");
        }
    }

    /**
     * Calcula o dano reduzido pela defesa do alvo.
     *
     * @param dano dano pré-calculado
     * @param alvo treinador defensor
     * @return dano final mitigado
     */
    public double calcularDefesa(double dano, Treinador alvo) {
        return dano * (
                ESCALA_DEFESA
                / (ESCALA_DEFESA + alvo.getPokeSall().getDEF()));
    }

    /**
     * Aplica efeitos passivos ao fim do turno.
     *
     * @param players lista com os dois combatentes
     */
    public void aplicarEfeitosTerreno(ArrayList<Treinador> players) {
        if (terreno == TERRENOS.CANTEIRO_CENTRAL) {
            for (Treinador treinador : players) {
                if (treinador.getPokeSall().getTipo() == Tipos.PLANTA
                        && treinador.getPokeSall().getHP() > 0) {

                    double curaTerreno =
                            treinador.getPokeSall().getHPbase() * 0.05;

                    double vidaAtual =
                            treinador.getPokeSall().getHP();

                    double vidaMax =
                            treinador.getPokeSall().getHPbase();

                    if (vidaAtual + curaTerreno > vidaMax) {
                        treinador.getPokeSall().setHP(vidaMax);
                    } else {
                        treinador.getPokeSall().setHP(
                                vidaAtual + curaTerreno);
                    }

                    System.out.println(
                            "[TERRENO] Canteiro Central curou "
                            + curaTerreno + " de HP de "
                            + treinador.getPokeSall().getNome() + "!");
                }
            }
        }
    }

    /**
     * Informa o status atual dos combatentes e calcula danos contínuos.
     *
     * @param players lista com os dois combatentes
     */
    public void verificarStatus(ArrayList<Treinador> players) {
        for (Treinador treinador : players) {
            if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
                System.out.println(
                        "O " + treinador.getPokeSall().getNome()
                        + " está envenenado!");
            }

            if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
                System.out.println(
                        "O " + treinador.getPokeSall().getNome()
                        + " está queimando!");
            }

            if (treinador.getPokeSall().getStatus() == Status.PARALIZADO) {
                System.out.println(
                        "O " + treinador.getPokeSall().getNome()
                        + " está paralisado!");
            }
        }

        calcularDanoContinuo(players);
    }

    /**
     * Calcula e aplica os danos contínuos dos status.
     *
     * @param players lista com os dois combatentes
     */
    public void calcularDanoContinuo(ArrayList<Treinador> players) {
        for (Treinador treinador : players) {
            double vidaAtual = treinador.getPokeSall().getHP();

            if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
                if (treinador.isNpc()) {
                    dano = (acumuloVenenoI * 0.1)
                            * treinador.getPokeSall().getHPbase();
                } else {
                    dano = (acumuloVenenoJ * 0.1)
                            * treinador.getPokeSall().getHPbase();
                }

                treinador.getPokeSall().setHP(vidaAtual - dano);

                System.out.println(
                        treinador.getPokeSall().getNome()
                        + " tomou " + dano + " de veneno!");

                vidaAtual = treinador.getPokeSall().getHP();
            }

            if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
                if (treinador.isNpc()) {
                    turnosFogoI--;
                } else {
                    turnosFogoJ--;
                }

                dano = 30;

                treinador.getPokeSall().setHP(vidaAtual - dano);

                System.out.println(
                        treinador.getPokeSall().getNome()
                        + " tomou " + dano + " de queimadura!");

                if ((turnosFogoI <= 0 && treinador.isNpc())
                        || (turnosFogoJ <= 0 && !treinador.isNpc())) {

                    treinador.getPokeSall().setStatus(Status.NORMAL);

                    System.out.println(
                            treinador.getPokeSall().getNome()
                            + " não está mais queimando!");
                }
            }
        }
    }

    /**
     * Sorteia e entrega um item ao jogador.
     *
     * @param jogador treinador que receberá o item
     */
    public void droparItem(Treinador jogador) {
        int temp = rand.nextInt(0, recompensas.length);
        jogador.getMochila().addItem(recompensas[temp]);
    }

    /**
     * Usa uma Potion e recupera 30% do HP máximo.
     *
     * @param treinador treinador que usará o item
     */
    public void usarPotion(Treinador treinador) {
        double vidaAtual = treinador.getPokeSall().getHP();
        double vidaBase = treinador.getPokeSall().getHPbase();

        treinador.getMochila().consumirItem("Potion");

        cura = 0.3 * vidaBase;

        if (vidaAtual + cura > vidaBase) {
            treinador.getPokeSall().setHP(vidaBase);
        } else {
            treinador.getPokeSall().setHP(cura + vidaAtual);
        }
    }

    /**
     * Usa uma Super Potion e recupera 50% do HP máximo.
     *
     * @param treinador treinador que usará o item
     */
    public void usarSuperPotion(Treinador treinador) {
        double vidaAtual = treinador.getPokeSall().getHP();
        double vidaBase = treinador.getPokeSall().getHPbase();

        treinador.getMochila().consumirItem("SuperPotion");

        cura = 0.5 * vidaBase;

        if (vidaAtual + cura > vidaBase) {
            treinador.getPokeSall().setHP(vidaBase);
        } else {
            treinador.getPokeSall().setHP(cura + vidaAtual);
        }
    }

    /**
     * Usa um Antidote e recupera 50% do HP máximo.
     *
     * @param treinador treinador que usará o item
     */
    public void usarAntidote(Treinador treinador) {
        double vidaAtual = treinador.getPokeSall().getHP();
        double vidaBase = treinador.getPokeSall().getHPbase();

        treinador.getMochila().consumirItem("Antidote");

        cura = 0.5 * vidaBase;

        treinador.getPokeSall().setStatus(Status.NORMAL);

        if (vidaAtual + cura > vidaBase) {
            treinador.getPokeSall().setHP(vidaBase);
        } else {
            treinador.getPokeSall().setHP(cura + vidaAtual);
        }
    }

    /**
     * Verifica se algum participante foi derrotado na batalha.
     *
     * @param jogador treinador do jogador
     * @param inimigo treinador adversário
     * @return 1 para vitória, 2 para derrota e 0 para batalha em andamento
     */
    public int verificarVitoria(
            Treinador jogador,
            Treinador inimigo) {

        if (jogador.getPokeSall().getHP() <= 0) {
            System.out.println(
                    "O jogador " + jogador.getNome()
                    + " perdeu a batalha!");
            return 2;
        }

        if (inimigo.getPokeSall().getHP() <= 0) {
            System.out.println(
                    "O jogador " + jogador.getNome()
                    + " venceu a batalha!");

            droparItem(jogador);
            return 1;
        }

        return 0;
    }
}

