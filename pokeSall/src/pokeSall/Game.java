package pokesall;

import ataques.Habilidades;
import itens.Item;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import pokesalls.BulbaSall;
import pokesalls.CharSall;
import pokesalls.SquirtSall;

/**
 * Classe que gerencia todo o fluxo do jogo.
 */
public class Game {

  /** Terrenos possíveis para uma batalha. */
  public enum Terrenos {
    ASFALTO_QUENTE, POCA_DE_CHUVA, CANTEIRO_CENTRAL
  }

  private static final int LIMITE_ITENS_POR_BATALHA = 2;

  private Random rand = new Random();
  private Scanner ler = new Scanner(System.in);
  // Todos com J no final é o jogador, todos com I é o inimigo.
  private int acumuloVenenoJ;
  private int acumuloVenenoI;
  private int turnosFogoJ;
  private int turnosFogoI;
  private int nivelTorre = 0;
  private int turno = 0;
  private double dano;
  private double cura;
  private int itensUsadosNaBatalha = 0;
  private static final double escalaDefesa = 100;
  private Terrenos terrenoAtual; // Variável para guardar o terreno da partida

  ArrayList<Treinador> inimigos = new ArrayList<Treinador>();
  ArrayList<Treinador> players = new ArrayList<Treinador>();
  PokeSall pokeSall;
  Treinador jogador;
  Treinador npc;
  Treinador inimigoAtual;

  /**
   * Construtor da classe game, que recebe um jogador como parametro.
   *
   * @param jogador, objeto do tipo Treinador.
   */
  public Game(Treinador jogador) {
    this.jogador = jogador;
    criarInimigos();
  }

  /**
   * Define o terreno atual. O método existe também para permitir testes determinísticos.
   *
   * @param terreno terreno que será usado na batalha.
   */
  public void setTerrenoAtual(Terrenos terreno) {
    this.terrenoAtual = terreno;
  }

  public Terrenos getTerrenoAtual() {
    return this.terrenoAtual;
  }

  /**
   * Determina quem ataca primeiro usando o atributo SPD.
   * Em caso de empate, o desempate continua sendo aleatório, como no jogo original.
   *
   * @return 1 quando o primeiro treinador começa; 2 quando o segundo começa.
   */
  public int determinarPrioridade(Treinador primeiro, Treinador segundo) {
    int velocidadePrimeiro = primeiro.getPokeSall().getSpd();
    int velocidadeSegundo = segundo.getPokeSall().getSpd();

    if (velocidadePrimeiro > velocidadeSegundo) {
      return 1;
    }
    if (velocidadePrimeiro < velocidadeSegundo) {
      return 2;
    }
    return rand.nextBoolean() ? 1 : 2;
  }

  /** Reinicia a contagem de itens usados ao começar uma nova batalha. */
  public void resetarUsoDeItensNaBatalha() {
    itensUsadosNaBatalha = 0;
  }

  /**
   * Registra o uso de um item e lança exceção quando o limite da batalha é excedido.
   */
  public void registrarUsoDeItem() {
    validarLimiteDeItens();
    itensUsadosNaBatalha++;
  }

  /**
   * Usa um item da mochila respeitando o limite da batalha.
   *
   * @param nome nome do item a ser usado.
   * @param treinador treinador que receberá o efeito do item.
   */
  public void usarItemNaBatalha(String nome, Treinador treinador) {
    validarLimiteDeItens();

    if (!treinador.getMochila().consumirItem(nome)) {
      throw new IllegalArgumentException("Item indisponível na mochila: " + nome);
    }

    switch (nome) {
      case "Potion":
        usarPotion(treinador);
        break;
      case "Antidote":
        usarAntidote(treinador);
        break;
      case "SuperPotion":
        usarSuperPotion(treinador);
        break;
      default:
        throw new IllegalArgumentException("Item desconhecido: " + nome);
    }

    registrarUsoDeItem();
  }

  private void validarLimiteDeItens() {
    if (itensUsadosNaBatalha >= LIMITE_ITENS_POR_BATALHA) {
      throw new IllegalStateException(
              "Limite máximo de " + LIMITE_ITENS_POR_BATALHA + " itens por batalha atingido!");
    }
  }

  /**
   * Método que criar todos os adversários iniciais.
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
   * Método responsável por gerenciar todo o fluxo do jogo, chamando outros métodos e encerrando o
   * jogo.
   */

  public void rodarGame() {
    String opcao;
    boolean rodando = true;
    boolean acao = true;
    ;
    // Prioridade 1 é o jogador, prioridade 2 é o bot.
    int prioridade;
    while (rodando && nivelTorre < inimigos.size()) {
      inimigoAtual = inimigos.get(nivelTorre);

      // Prepara a lista de players apenas para a batalha atual
      players.clear();
      players.add(jogador);
      players.add(inimigoAtual);
      // Define um terreno aleatório para a batalha
      terrenoAtual = Terrenos.values()[rand.nextInt(Terrenos.values().length)];

      turno = 0;

      System.out.println("==============================");
      System.out.println("Você está no nível " + (nivelTorre + 1));
      System.out.println("Enfrentará " + inimigoAtual.getNome() + "!");
      System.out.println("Terreno da Batalha: " + terrenoAtual.name());
      System.out.println("==============================");
      System.out.println();
      System.out.println("PRESSIONE ENTER PARA CONTINUAR!");
      ler.nextLine();
      limparTela();
      System.out.println("------------------------------");
      System.out.println("      BATALHA INICIADA!");
      System.out.println("------------------------------");

      // Comparação de velocidade base dos pokeSals.
      prioridade = determinarPrioridade(jogador, inimigoAtual);
      if (jogador.getPokeSall().getSpd() < inimigoAtual.getPokeSall().getSpd()) {
        System.out.println("O pokeSal " + inimigoAtual.getNomePokeSall() + " é mais rápido!");
        System.out.println("Ele começa!");
      } else if (jogador.getPokeSall().getSpd() > inimigoAtual.getPokeSall().getSpd()) {
        System.out.println("O seu pokeSal é mais rápido!");
        System.out.println("Você começa!");
      }
      boolean batalhando = true;
      resetarUsoDeItensNaBatalha();
      while (batalhando) {
        turno++;

        System.out.println();
        System.out.println("------");
        System.out.println("TURNO " + turno);
        System.out.println("------");

        // Verifica se o pokeSal do jogador está paralizado, se sim, pula o turno.
        if (jogador.getPokeSall().getStatus() == Status.PARALIZADO) {
          System.out.println(
                  "O pokeSal " + jogador.getNomePokeSall() + " está paralizado!, pulou o turno!");
          prioridade = 2;
          jogador.getPokeSall().setStatus(Status.NORMAL);
        }
        if (prioridade == 1) {
          do {

            do {
              System.out.println();
              System.out.println("==============================");
              System.out.println("Turno de: " + jogador.getNome());
              System.out.println("==============================");
              System.out.println("-----------------------------");
              System.out.println("Treinador: " + jogador.getNome());
              System.out.println("PokeSall: " + jogador.getNomePokeSall());
              System.out.println("HP: " + String.format("%.1f", jogador.getPokeSall().getHp()));
              System.out.println("==============================");
              System.out.println("------------VERSUS------------");
              System.out.println("==============================");
              System.out.println("Treinador: " + inimigos.get(nivelTorre).getNome());
              System.out.println("PokeSall: " + inimigos.get(nivelTorre).getNomePokeSall());
              System.out.println("HP: "
                      + String.format("%.1f", inimigos.get(nivelTorre).getPokeSall().getHp()));
              System.out.println("-----------------------------");
              System.out.println("1--ATAQUES-- 2--MOCHILA-- 3--DESISTIR");
              System.out.println("-----------------------------");
              opcao = ler.nextLine();
            } while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")));

            switch (opcao) {
              case "1":
                do {
                  System.out.println("-----------------------------");
                  System.out.println("1- Para usar " + jogador.getPokeSall().getHabilidades()[0]);
                  System.out.println("2- Para usar " + jogador.getPokeSall().getHabilidades()[1]);
                  System.out.println("3- Para voltar");
                  opcao = ler.nextLine();
                } while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")));

                if (opcao.equals("1")) {
                  calcularAtaque(jogador.getPokeSall().getHabilidades()[0], jogador, inimigoAtual);
                  acao = false;
                  prioridade = 2;
                } else if (opcao.equals("2")) {
                  calcularAtaque(jogador.getPokeSall().getHabilidades()[1], jogador, inimigoAtual);
                  acao = false;
                  prioridade = 2;
                } else {
                  acao = true;
                }
                break;

              case "2":
                do {
                  int temp = 0;
                  System.out.println("-----------------------------");
                  List<Item> itens = jogador.getMochila().getItens();
                  for (Item item : itens) {
                    if (item.getQuantidade() > 0) {
                      temp++;
                      System.out.println("Digite " + temp + " para usar " + item.getNome() + ": "
                              + item.getQuantidade());
                      System.out.println(item.getDescricao());
                      System.out.println("-----------------------------");
                    }

                  }
                  System.out.println("-----------------------------");
                  System.out.println("0- Para voltar");
                  opcao = ler.nextLine();

                } while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")
                        || opcao.equals("0")));
                // Limita a quantidade de itens por batalha em 2.
                if (opcao.equals("0")) {
                  acao = true;
                  break;
                }
                try {
                  if (opcao.equals("1")) {
                    usarItemNaBatalha("Potion", jogador);
                  } else if (opcao.equals("2")) {
                    usarItemNaBatalha("Antidote", jogador);
                  } else if (opcao.equals("3")) {
                    usarItemNaBatalha("SuperPotion", jogador);
                  }
                  acao = false;
                  prioridade = 2;
                } catch (IllegalStateException | IllegalArgumentException excecao) {
                  System.out.println(excecao.getMessage());
                  acao = true;
                }
                break;

              case "3":
                batalhando = false;
                rodando = false;
                System.out.println("Você fugiu da batalha!");
                acao = false;
                break;

              default:
                System.out.println("Opção inválida!");
            }
          } while (acao);
        }
        // Verifica se o pokeSal do inimigo está paralizado, se sim, pula o turno.
        if (inimigoAtual.getPokeSall().getStatus() == Status.PARALIZADO) {
          System.out.println("O pokeSal " + inimigoAtual.getNomePokeSall()
                  + " está paralizado!, pulou o turno!");
          prioridade = 1;
          inimigoAtual.getPokeSall().setStatus(Status.NORMAL);
        }
        // Verifica se inimigo morreu antes de ele atacar
        int resultado = verificarVitoria();
        if (resultado != 0) {
          batalhando = false;
          if (resultado == 1) {
            nivelTorre++;
            jogador.getPokeSall().setHp(jogador.getPokeSall().getHpBase());
            jogador.getPokeSall().setStatus(Status.NORMAL);
            telaDeRecompensas();
          }
          if (resultado == 2) {
            rodando = false;
            continue;
          }
        }
        if (prioridade == 2) {
          if (rodando && batalhando) {
            dano = 0;
            System.out.println();
            System.out.println("Turno de " + inimigoAtual.getNome());
            System.out.println("");

            int temp = rand.nextInt(1, 3);

            switch (temp) {
              case 1:
                calcularAtaque(inimigoAtual.getPokeSall().getHabilidades()[0], inimigoAtual,
                        jogador);

                prioridade = 1;
                break;

              case 2:

                calcularAtaque(inimigoAtual.getPokeSall().getHabilidades()[1], inimigoAtual,
                        jogador);
                prioridade = 1;
                break;

              default:
                break;
            }

          }
        }
        verificarStatus(players); // Verifica o status dos jogadores no final do turno
        aplicarEfeitosTerreno(players); // Aplica cura de terreno no final do turno

        if (resultado == 0) {
          resultado = verificarVitoria();
          if (resultado == 1) { // Jogador Venceu
            batalhando = false;
            // Recupera toda a vida ao subir a torre.
            jogador.getPokeSall().setHp(jogador.getPokeSall().getHpBase());
            jogador.getPokeSall().setStatus(Status.NORMAL);
            telaDeRecompensas();
          } else if (resultado == 2) { // Jogador Perdeu
            batalhando = false;
            rodando = false;
          }
        }
      }
    }

    if (nivelTorre >= inimigos.size() && jogador.getPokeSall().getHp() > 0) {
      System.out.println("Parabéns! Você derrotou todos os inimigos da torre!");
    }
  }

  /**
   * Limpa o terminal com um for loop e o System.out.println vázio.
   */

  public void limparTela() {
    for (int l = 0; l < 20; l++) {
      System.out.println();
    }
  }

  /**
   * Método responsável por calcular todo o dano causo por uma habilidade.
   *
   * @param habilidade, um enum com o nome da habilidade utilizada.
   *
   * @param atacante, o objeto do treinador que está atacando.
   *
   * @param alvo, o objeto do treinador que está sendo atacado..
   */

  public void calcularAtaque(Habilidades habilidade, Treinador atacante, Treinador alvo) {
    dano = 0;

    if (habilidade == Habilidades.FOLHA_BUZZER) {

      dano = calcularDanoHabilidade(60, atacante, alvo);
      aplicarDano(alvo, dano);
      System.out.println(atacante.getNomePokeSall() + " usou Folha Buzzer!");
      System.out.println("Deu " + String.format("%.1f", dano) + " de dano!");

    } else if (habilidade == Habilidades.GAS) {
      System.out.println(atacante.getNomePokeSall() + " usou Gas!");
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

    } else if (habilidade == Habilidades.HELL_BEAM) {

      dano = calcularDanoHabilidade(75, atacante, alvo);
      aplicarDano(alvo, dano);
      System.out.println(atacante.getNomePokeSall() + " usou Hell Beam!");
      System.out.println("Deu " + String.format("%.1f", dano) + " de dano!");

    } else if (habilidade == Habilidades.QUEIMAR) {
      System.out.println(atacante.getNomePokeSall() + " usou queimar!");
      if (alvo.isNpc()) {
        turnosFogoI = 3;
      } else {
        turnosFogoJ = 3;
      }

      alvo.getPokeSall().setStatus(Status.QUEIMANDO);

    } else if (habilidade == Habilidades.AGUA_TERMAL) {
      System.out.println(atacante.getNomePokeSall() + " usou Agua Termal!");
      alvo.getPokeSall().setStatus(Status.PARALIZADO);
    } else if (habilidade == Habilidades.BEAT_BOLHA) {
      dano = calcularDanoHabilidade(65, atacante, alvo);
      aplicarDano(alvo, dano);
      System.out.println(atacante.getNomePokeSall() + " usou Beat Bolha");
      System.out.println("Deu " + String.format("%.1f", dano) + " de dano!");
    }
  }

  /**
   * Retorna o multiplicador da relação entre os tipos elementais.
   */
  public double calcularMultiplicadorElemental(Tipos tipoAtacante, Tipos tipoDefensor) {
    if ((tipoAtacante == Tipos.PLANTA && tipoDefensor == Tipos.AGUA)
            || (tipoAtacante == Tipos.AGUA && tipoDefensor == Tipos.FOGO)
            || (tipoAtacante == Tipos.FOGO && tipoDefensor == Tipos.PLANTA)) {
      return 2.0;
    }

    if (tipoAtacante == tipoDefensor) {
      return 1.0;
    }

    return 0.5;
  }

  /**
   * Calcula o dano de uma habilidade considerando ATK, vantagem elemental, terreno e DEF.
   */
  public double calcularDanoHabilidade(double danoBase, Treinador atacante, Treinador alvo) {
    double multiplicadorElemental = calcularMultiplicadorElemental(
            atacante.getPokeSall().getTipo(), alvo.getPokeSall().getTipo());
    double multiplicadorTerreno = calcularMultiplicadorTerreno(atacante);
    double danoAntesDaDefesa = (Math.max(0, danoBase) + Math.max(0, atacante.getPokeSall().getAtk()))
            * multiplicadorElemental * multiplicadorTerreno;

    return calcularDefesa(danoAntesDaDefesa, alvo);
  }

  private double calcularMultiplicadorTerreno(Treinador atacante) {
    if (terrenoAtual == Terrenos.ASFALTO_QUENTE
            && atacante.getPokeSall().getTipo() == Tipos.FOGO) {
      return 1.15;
    }

    if (terrenoAtual == Terrenos.POCA_DE_CHUVA
            && atacante.getPokeSall().getTipo() == Tipos.AGUA) {
      return 1.10;
    }

    return 1.0;
  }

  private void aplicarDano(Treinador alvo, double danoCalculado) {
    double hpAtual = Math.max(0, alvo.getPokeSall().getHp());
    alvo.getPokeSall().setHp(Math.max(0, hpAtual - Math.max(0, danoCalculado)));
  }

  /**
   * Método que calcula o dano resultante da defesa base do pokeSal alvo e o dano causado pelo
   * atacante.
   *
   * @param danoBase, recebe o calculo de dano do pokeSal atacante com todos os calculos aplicados.
   *
   * @param alvo, recebe o objeto do treinador inimigo para calcular a defesa base do seu pokeSal.
   */
  public double calcularDefesa(double danoBase, Treinador alvo) {
    return danoBase * (escalaDefesa / (escalaDefesa + alvo.getPokeSall().getDef()));
  }

  /**
   * Método responsável por verificar o status dos pokeSals de ambos os treinadores.
   *
   * @param players, recebe um arrayList de players contendo ambos os treinadores.
   */
  public void verificarStatus(ArrayList<Treinador> players) {
    for (Treinador treinador : players) {
      if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
        System.out.println("O " + treinador.getPokeSall().getNome() + " está envenenado!");
      }
      if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
        System.out.println("O " + treinador.getPokeSall().getNome() + " está queimando!");
      }
      if (treinador.getPokeSall().getStatus() == Status.PARALIZADO) {
        System.out.println("O " + treinador.getPokeSall().getNome() + " está paralizado!");
      }
    }
    calcularDanoContinuo(players);
  }

  /**
   * Método que calcula dano contínuo de ambos os PokeSals(veneno e queimadura).
   *
   * @param players, recebe um arrayList contendo ambos os Treinadores e seus pokeSals.
   */
  public void calcularDanoContinuo(ArrayList<Treinador> players) {
    for (Treinador treinador : players) {
      double vidaAtual = treinador.getPokeSall().getHp();

      if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
        if (treinador.isNpc()) {
          dano = (acumuloVenenoI * 0.1) * treinador.getPokeSall().getHpBase();
        } else {
          dano = (acumuloVenenoJ * 0.1) * treinador.getPokeSall().getHpBase();
        }
        treinador.getPokeSall().setHp(vidaAtual - dano);
        System.out.println(treinador.getPokeSall().getNome() + " tomou "
                + String.format("%.1f", dano) + " dano de veneno!");
        vidaAtual = treinador.getPokeSall().getHp();
      }

      if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
        if (treinador.isNpc()) {
          turnosFogoI--;
        } else {
          turnosFogoJ--;
        }

        dano = 30;
        treinador.getPokeSall().setHp(vidaAtual - dano);
        System.out.println(
                treinador.getPokeSall().getNome() + " tomou " + dano + " dano de queimadura!");

        if ((turnosFogoI <= 0 && treinador.isNpc() || turnosFogoJ <= 0 && !treinador.isNpc())
                && treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
          treinador.getPokeSall().setStatus(Status.NORMAL);
          System.out.println(treinador.getPokeSall().getNome() + " não está mais queimando!");
        }

      }

    }
  }

  /**
   * Método responsável por aplicar o efeito dos terrenos no fim do turno(caso esse seja o efeito do
   * terreno).
   *
   * @param players, recebe um ArrayList com ambos os treinadores para verificar o tipo dos seus
   *     pokesals, assim, aplicando os devidos efeitos.
   */
  public void aplicarEfeitosTerreno(ArrayList<Treinador> players) {
    if (terrenoAtual == Terrenos.CANTEIRO_CENTRAL) {
      for (Treinador treinador : players) {
        if (treinador.getPokeSall().getTipo() == Tipos.PLANTA) {
          double hpAtual = treinador.getPokeSall().getHp();
          double hpMax = treinador.getPokeSall().getHpBase();

          // Só cura se ele estiver vivo e não estiver com a vida cheia
          if (hpAtual > 0 && hpAtual < hpMax) {
            double curaTerreno = hpMax * 0.05; // 5% do HP máximo
            if (hpAtual + curaTerreno > hpMax) {
              treinador.getPokeSall().setHp(hpMax);
            } else {
              treinador.getPokeSall().setHp(hpAtual + curaTerreno);
            }
            System.out.println("O Canteiro Central curou " + String.format("%.1f", curaTerreno)
                    + " de HP do " + treinador.getPokeSall().getNome() + "!");
          }
        }
      }
    }
  }

  /**
   * Método responsável por usar o item Potion.
   *
   * @param treinador, recebe o objeto do treinador para calcular a cura provida devidamente.
   */
  public void usarPotion(Treinador treinador) {
    double vidaAtual = treinador.getPokeSall().getHp();
    double vidaBase = treinador.getPokeSall().getHpBase();
    cura = 0.3 * vidaBase;

    if ((vidaAtual + cura) > vidaBase) {
      treinador.getPokeSall().setHp(vidaBase);
      System.out.println(treinador.getNomePokeSall() + " se curou totalmente!");
    } else {
      treinador.getPokeSall().setHp(cura + vidaAtual);
      System.out.println("Potion curou " + treinador.getNomePokeSall() + " em " + cura);
    }
  }

  /**
   * Método responsável por usar o item SuperPotion.
   *
   * @param treinador, recebe o objeto do treinador para calcular a cura provida devidamente.
   */
  public void usarSuperPotion(Treinador treinador) {
    double vidaAtual = treinador.getPokeSall().getHp();
    double vidaBase = treinador.getPokeSall().getHpBase();
    cura = 0.5 * vidaBase;
    treinador.getPokeSall().setStatus(Status.NORMAL);
    if ((vidaAtual + cura) > vidaBase) {
      treinador.getPokeSall().setHp(vidaBase);
      System.out.println(treinador.getNomePokeSall() + " se curou totalmente "
              + "e removeu todos os efeitos negativos!");
    } else {
      treinador.getPokeSall().setHp(cura + vidaAtual);
      System.out.println("SuperPotion curou " + treinador.getNomePokeSall() + " em " + cura
              + " e removeu efeitos negativos!");
    }
  }

  /**
   * Método responsável por usar o item Antidote.
   *
   * @param treinador, recebe o objeto do treinador para remover todos os status negativos do
   *        pokesal.
   */
  public void usarAntidote(Treinador treinador) {
    treinador.getPokeSall().setStatus(Status.NORMAL);
    System.out.println("O pokeSal curou de todos os efeitos negativos!");
  }

  /**
   * Método responsável por gerar uma recompensa aleatória entre 3 possíveis de um vetor de 3
   * Strings.
   *
   * @return, retorna a recompensa gerada aleatoriamente.
   */

  public String recompensaVitoria() {
    String[] recompensas = { "Potion", "SuperPotion", "Antidote" };

    String recompensa = recompensas[rand.nextInt(recompensas.length)];

    return recompensa;
  }

  /**
   * Métod responsável por adicionar o item gerada aleatoriamente a mochila do jogador e informalo o
   * item que ele ganhou.
   */
  public void telaDeRecompensas() {
    String recompensa = recompensaVitoria();
    jogador.getMochila().addItem(recompensa);
    System.out.println("Você derrotou " + inimigoAtual.getNome() + "!");
    System.out.println("Sua recompensa é: " + recompensa);
    System.out.println();
    System.out.println("------------------------------");
    System.out.println("PRESSIONE ENTER PARA CONTINUAR!");
    System.out.println("------------------------------");
    ler.nextLine();
  }

  /**
   * Método responsável por verificar a vitória ou derrota do jogador.
   */

  public int verificarVitoria() {
    if (jogador.getPokeSall().getHp() <= 0) {
      System.out.println("O jogador " + jogador.getNome() + " perdeu a batalha!");
      return 2;
    }
    if (inimigoAtual.getPokeSall().getHp() <= 0) {
      System.out.println("O jogador " + jogador.getNome() + " venceu a batalha!");
      return 1;
    }
    return 0; // Jogo continua
  }
}
