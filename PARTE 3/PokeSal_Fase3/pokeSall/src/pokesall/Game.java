package pokesall;

import ataques.Habilidades;
import itens.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import pokesals.BulbaSall;
import pokesals.CharSall;
import pokesals.SquirtSall;

/**
 * Classe que gerencia todo o fluxo do jogo.
 */
public class Game implements Batalha {

  /** Terrenos possíveis para uma batalha. */
  public enum Terrenos {
    ASFALTO_QUENTE, POCA_DE_CHUVA, CANTEIRO_CENTRAL
  }

  private static final int LIMITE_ITENS_POR_BATALHA = 2;

  private final GeradorAleatorio gerador;
  private final ServicoTerreno servicoTerreno;
  private final RepositorioResultados repositorio;
  private final RegistroBatalha registro;
  private final ServicoSoneca servicoSoneca;
  private final ServicoDefesa servicoDefesa;

  // Gravação em grupos garante sempre a mesma ordem ao final de cada turno.
  private final List<String> eventosAtaque = new ArrayList<>();
  private final List<String> eventosDano = new ArrayList<>();
  private final List<String> eventosStatus = new ArrayList<>();
  private final List<String> eventosTerreno = new ArrayList<>();
  private final List<String> eventosItem = new ArrayList<>();

  private final Map<Treinador, Integer> venenos = new HashMap<>();
  private final Map<Treinador, Integer> queimaduras = new HashMap<>();
  private final Set<Treinador> defesasAtivas = new HashSet<>();
  private final EntradaJogador entrada;
  // Todos com J no final é o jogador, todos com I é o inimigo.
  private int nivelTorre = 0;
  private int turno = 0;
  private double dano;
  private double cura;
  private int itensUsadosNaBatalha = 0;
  private static final double ESCALA_DEFESA = 100;
  private Terrenos terrenoAtual; // Variável para guardar o terreno da partida
  private boolean terrenoDefinidoManualmente;

  private ArrayList<Treinador> inimigos = new ArrayList<Treinador>();
  private ArrayList<Treinador> players = new ArrayList<Treinador>();
  private PokeSall pokesall;
  private Treinador jogador;
  private Treinador npc;
  private Treinador inimigoAtual;

  /**
   * Cria uma batalha sem entrada interativa, para regras e testes isolados.
   *
   * @param jogador treinador principal
   * @param gerador fonte de aleatoriedade
   * @param servicoTerreno colaborador que calcula os efeitos de terreno
   * @param repositorio armazenamento dos resultados
   * @param registro registro ordenado dos eventos
   * @param servicoSoneca colaborador que calcula a recuperacao de HP
   * @param servicoDefesa colaborador que reduz o dano protegido
   */
  public Game(Treinador jogador, GeradorAleatorio gerador, ServicoTerreno servicoTerreno,
      RepositorioResultados repositorio, RegistroBatalha registro,
      ServicoSoneca servicoSoneca, ServicoDefesa servicoDefesa) {
    this(jogador, gerador, servicoTerreno, repositorio, registro, null,
        servicoSoneca, servicoDefesa);
  }

  /**
   * Cria o jogo com entrada humana e todas as dependencias injetadas.
   *
   * @param jogador treinador principal
   * @param gerador fonte de aleatoriedade
   * @param servicoTerreno colaborador que calcula os efeitos de terreno
   * @param repositorio armazenamento dos resultados
   * @param registro registro ordenado dos eventos
   * @param entrada entrada de texto do jogador
   * @param servicoSoneca colaborador que calcula a recuperacao de HP
   * @param servicoDefesa colaborador que reduz o dano protegido
   */
  public Game(Treinador jogador, GeradorAleatorio gerador, ServicoTerreno servicoTerreno,
      RepositorioResultados repositorio, RegistroBatalha registro, EntradaJogador entrada,
      ServicoSoneca servicoSoneca, ServicoDefesa servicoDefesa) {
    this.jogador = Objects.requireNonNull(jogador);
    this.gerador = Objects.requireNonNull(gerador);
    this.servicoTerreno = Objects.requireNonNull(servicoTerreno);
    this.repositorio = Objects.requireNonNull(repositorio);
    this.registro = Objects.requireNonNull(registro);
    this.entrada = entrada;
    this.servicoSoneca = Objects.requireNonNull(servicoSoneca);
    this.servicoDefesa = Objects.requireNonNull(servicoDefesa);
    criarInimigos();
  }

  /**
   * Executa ler opcao.
   *
   * @return resultado de ler opcao
   */
  private String lerOpcao() {
    if (entrada == null) {
      throw new IllegalStateException("Forneça uma EntradaJogador para batalhas humanas");
    }
    return entrada.lerLinha().trim();
  }

  /**
   * Executa evento.
   *
   * @param tipo valor de tipo
   * @param descricao valor de descricao
   */
  private void evento(String tipo, String descricao) {
    switch (tipo) {
      case "ATAQUE" -> eventosAtaque.add(descricao);
      case "DANO" -> eventosDano.add(descricao);
      case "STATUS" -> eventosStatus.add(descricao);
      case "TERRENO" -> eventosTerreno.add(descricao);
      case "ITEM" -> eventosItem.add(descricao);
      default -> throw new IllegalArgumentException("Evento desconhecido: " + tipo);
    }
  }

  /**
   * Envia, na ordem exigida, os eventos que ocorreram neste turno.
   */
  public void finalizarRegistroTurno() {
    enviarEventos("ATAQUE", eventosAtaque);
    enviarEventos("DANO", eventosDano);
    enviarEventos("STATUS", eventosStatus);
    enviarEventos("TERRENO", eventosTerreno);
    enviarEventos("ITEM", eventosItem);
  }

  /**
   * Executa enviar eventos.
   *
   * @param tipo valor de tipo
   * @param eventos valor de eventos
   */
  private void enviarEventos(String tipo, List<String> eventos) {
    for (String descricao : eventos) {
      registro.registrar(tipo, descricao);
    }
    eventos.clear();
  }

  /**
   * Executa reiniciar efeitos.
   */
  private void reiniciarEfeitos() {
    venenos.clear();
    queimaduras.clear();
    defesasAtivas.clear();
  }

  /**
   * Executa salvar resultado.
   *
   * @param vencedor valor de vencedor
   * @param perdedor valor de perdedor
   * @param turnos valor de turnos
   */
  private void salvarResultado(Treinador vencedor, Treinador perdedor, int turnos) {
    try {
      repositorio.salvar(new ResultadoBatalha(vencedor, perdedor, Math.max(1, turnos)));
    } catch (RuntimeException erro) {
      registro.registrarErro("Falha ao salvar a batalha", erro);
    }
  }

  /**
   * Define o terreno atual. O método existe também para permitir testes determinísticos.
   *
   * @param terreno terreno que será usado na batalha.
   */
  public void setTerrenoAtual(Terrenos terreno) {
    this.terrenoAtual = terreno;
    this.terrenoDefinidoManualmente = terreno != null;
  }

  /**
   * Retorna terreno atual.
   *
   * @return resultado de get terreno atual
   */
  public Terrenos getTerrenoAtual() {
    return this.terrenoAtual;
  }

  /**
   * Determina quem ataca primeiro usando o atributo SPD.
   * Em caso de empate, o desempate continua sendo aleatório, como no jogo original.
   *
   * @return 1 quando o primeiro treinador começa; 2 quando o segundo começa.
   *
   * @param primeiro valor de primeiro
   * @param segundo valor de segundo
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
    return gerador.proximoInt(2) == 0 ? 1 : 2;
  }

  /**
   * Reinicia a contagem de itens usados ao começar uma nova batalha.
   */
  public void resetarUsoDeItensNaBatalha() {
    itensUsadosNaBatalha = 0;
  }

  /**
   * Faz o PokeSal descansar por um turno, recuperando ate 10% do HP maximo.
   * O descanso nao e permitido com HP cheio ou igual a zero.
   *
   * @param treinador treinador que solicita o descanso
   * @return true se a soneca foi utilizada; false se o HP ja esta cheio ou zerado
   */
  public boolean usarSoneca(Treinador treinador) {
    PokeSall pokeSal = treinador.getPokeSall();
    double hpAnterior = pokeSal.getHp();
    if (hpAnterior <= 0 || hpAnterior >= pokeSal.getHpBase()) {
      return false;
    }
    double recuperacao = Math.max(0, servicoSoneca.calcularRecuperacao(pokeSal));
    double hpNovo = Math.min(pokeSal.getHpBase(), hpAnterior + recuperacao);
    pokeSal.setHp(hpNovo);
    evento("STATUS", pokeSal.getNome() + " descansou e recuperou "
        + String.format("%.1f", hpNovo - hpAnterior) + " HP");
    System.out.println(pokeSal.getNome() + " tirou uma soneca e recuperou "
        + String.format("%.1f", hpNovo - hpAnterior) + " HP!");
    return true;
  }

  /**
   * Prepara uma reducao de 10% apenas para o proximo golpe que causar dano.
   * A protecao nao acumula se o treinador a utilizar novamente.
   *
   * @param treinador treinador que usara a defesa
   */
  public void usarDefesa(Treinador treinador) {
    defesasAtivas.add(treinador);
    evento("STATUS", treinador.getNomePokeSall() + " preparou defesa para o proximo golpe");
    System.out.println(treinador.getNomePokeSall() + " se protegeu contra o proximo golpe!");
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

    if (!nome.equals("Potion") && !nome.equals("Antidote") && !nome.equals("SuperPotion")) {
      throw new IllegalArgumentException("Item desconhecido: " + nome);
    }
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
    evento("ITEM", treinador.getNome() + " usou " + nome);
  }

  /**
   * Executa validar limite de itens.
   */
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
    pokesall = new CharSall();
    npc = new Npc("Ashley", pokesall);
    inimigos.add(npc);

    pokesall = new BulbaSall();
    npc = new Npc("Joe Lois", pokesall);
    inimigos.add(npc);

    pokesall = new SquirtSall();
    npc = new Npc("Mary", pokesall);
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
    // Prioridade 1 é o jogador, prioridade 2 é o bot.
    int prioridade;
    while (rodando && nivelTorre < inimigos.size()) {
      inimigoAtual = inimigos.get(nivelTorre);

      // Prepara a lista de players apenas para a batalha atual
      players.clear();
      players.add(jogador);
      players.add(inimigoAtual);
      // Define um terreno aleatório para a batalha
      terrenoAtual = Terrenos.values()[gerador.proximoInt(Terrenos.values().length)];

      turno = 0;
      reiniciarEfeitos();

      System.out.println("==============================");
      System.out.println("Você está no nível " + (nivelTorre + 1));
      System.out.println("Enfrentará " + inimigoAtual.getNome() + "!");
      System.out.println("Terreno da Batalha: " + terrenoAtual.name());
      System.out.println("==============================");
      System.out.println();
      System.out.println("PRESSIONE ENTER PARA CONTINUAR!");
      lerOpcao();
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
              System.out.println("1--ATAQUES-- 2--MOCHILA-- 3--DESISTIR-- 4--SONECA-- 5--DEFESA");
              System.out.println("-----------------------------");
              opcao = lerOpcao();
            } while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")
                || opcao.equals("4") || opcao.equals("5")));

            switch (opcao) {
              case "1":
                do {
                  System.out.println("-----------------------------");
                  System.out.println("1- Para usar " + jogador.getPokeSall().getHabilidades()[0]);
                  System.out.println("2- Para usar " + jogador.getPokeSall().getHabilidades()[1]);
                  System.out.println("3- Para voltar");
                  opcao = lerOpcao();
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
                  opcao = lerOpcao();

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

              case "4":
                if (usarSoneca(jogador)) {
                  acao = false;
                  prioridade = 2;
                } else {
                  System.out.println("Nao e possivel descansar com HP cheio ou zerado.");
                  acao = true;
                }
                break;

              case "5":
                usarDefesa(jogador);
                acao = false;
                prioridade = 2;
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
          salvarResultado(resultado == 1 ? jogador : inimigoAtual,
              resultado == 1 ? inimigoAtual : jogador, turno);
          finalizarRegistroTurno();
          if (resultado == 1) {
            nivelTorre++;
            jogador.getPokeSall().setHp(jogador.getPokeSall().getHpBase());
            jogador.getPokeSall().setStatus(Status.NORMAL);
            telaDeRecompensas();
          }
          if (resultado == 2) {
            rodando = false;
          }
          continue; // A batalha terminou; não aplica mais efeitos nesse turno.
        }
        if (prioridade == 2) {
          if (rodando && batalhando) {
            dano = 0;
            System.out.println();
            System.out.println("Turno de " + inimigoAtual.getNome());
            System.out.println("");

            int temp = gerador.proximoInt(2) + 1;

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
        finalizarRegistroTurno();

        if (resultado == 0) {
          resultado = verificarVitoria();
          if (resultado == 1) { // Jogador Venceu
            batalhando = false;
            salvarResultado(jogador, inimigoAtual, turno);
            nivelTorre++;
            // Recupera toda a vida ao subir a torre.
            jogador.getPokeSall().setHp(jogador.getPokeSall().getHpBase());
            jogador.getPokeSall().setStatus(Status.NORMAL);
            telaDeRecompensas();
          } else if (resultado == 2) { // Jogador Perdeu
            batalhando = false;
            salvarResultado(inimigoAtual, jogador, turno);
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
   * @param habilidade um enum com o nome da habilidade utilizada.
   *
   * @param atacante o objeto do treinador que está atacando.
   *
   * @param alvo o objeto do treinador que está sendo atacado..
   */
  public void calcularAtaque(Habilidades habilidade, Treinador atacante, Treinador alvo) {
    Objects.requireNonNull(habilidade);
    evento("ATAQUE", atacante.getNomePokeSall() + " usou " + habilidade.name());
    System.out.println(atacante.getNomePokeSall() + " usou " + habilidade.name() + "!");

    // 0.0 -> 0%, 0.95 -> 95%. Um erro nunca aplica dano nem status.
    if (gerador.proximoDouble() * 100 >= habilidade.getPrecisao()) {
      dano = 0;
      evento("DANO", "Ataque errou: 0 de dano");
      System.out.println("O golpe errou! Dano: 0");
      return;
    }

    double base = switch (habilidade) {
      case FOLHA_BUZZER -> 60;
      case HELL_BEAM -> 75;
      case BEAT_BOLHA -> 65;
      default -> 0;
    };

    if (base > 0) {
      dano = calcularDanoHabilidade(base, atacante, alvo);
      if (gerador.proximoDouble() < 0.0625) {
        dano *= 1.5;
        System.out.println("Acerto crítico!");
      }
      dano = aplicarDanoDeGolpe(alvo, dano);
      evento("DANO", "Dano causado a " + alvo.getNomePokeSall() + ": " + dano);
      System.out.println("Deu " + String.format("%.1f", dano) + " de dano!");
      return;
    }

    dano = 0;
    switch (habilidade) {
      case GAS -> {
        venenos.put(alvo, Math.min(3, venenos.getOrDefault(alvo, 0) + 1));
        alvo.getPokeSall().setStatus(Status.ENVENENADO);
        evento("STATUS", alvo.getNomePokeSall() + " está envenenado");
      }
      case QUEIMAR -> {
        queimaduras.put(alvo, 3);
        alvo.getPokeSall().setStatus(Status.QUEIMANDO);
        evento("STATUS", alvo.getNomePokeSall() + " está queimando");
      }
      case AGUA_TERMAL -> {
        alvo.getPokeSall().setStatus(Status.PARALIZADO);
        evento("STATUS", alvo.getNomePokeSall() + " está paralisado");
      }
      default -> {
        // As demais habilidades ainda não tinham efeitos na fase anterior.
      }
    }
  }

  /**
   * Retorna o multiplicador da relação entre os tipos elementais.
   *
   * @param tipoAtacante valor de tipoAtacante
   * @param tipoDefensor valor de tipoDefensor
   * @return resultado da operacao
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
   *
   * @param danoBase valor de danoBase
   * @param atacante valor de atacante
   * @param alvo valor de alvo
   * @return resultado da operacao
   */
  public double calcularDanoHabilidade(double danoBase, Treinador atacante, Treinador alvo) {
    double multiplicadorElemental = calcularMultiplicadorElemental(
            atacante.getPokeSall().getTipo(), alvo.getPokeSall().getTipo());
    double multiplicadorTerreno = calcularMultiplicadorTerreno(atacante);
    double danoAntesDaDefesa = (Math.max(0, danoBase)
        + Math.max(0, atacante.getPokeSall().getAtk()))
        * multiplicadorElemental * multiplicadorTerreno;

    return calcularDefesa(danoAntesDaDefesa, alvo);
  }

  /**
   * Executa calcular multiplicador terreno.
   *
   * @param atacante valor de atacante
   * @return resultado de calcular multiplicador terreno
   */
  private double calcularMultiplicadorTerreno(Treinador atacante) {
    return servicoTerreno.multiplicadorDano(terrenoAtual, atacante.getPokeSall().getTipo());
  }

  /**
   * Executa aplicar dano.
   *
   * @param alvo valor de alvo
   * @param danoCalculado valor de dano calculado
   */
  private void aplicarDano(Treinador alvo, double danoCalculado) {
    double hpAtual = Math.max(0, alvo.getPokeSall().getHp());
    alvo.getPokeSall().setHp(Math.max(0, hpAtual - Math.max(0, danoCalculado)));
  }

  /**
   * Método que calcula o dano resultante da defesa base do pokeSal alvo e o dano causado pelo
   * atacante.
   *
   * @param danoBase recebe o calculo de dano do pokeSal atacante com todos os calculos aplicados.
   *
   * @param alvo recebe o objeto do treinador inimigo para calcular a defesa base do seu pokeSal.
   *
   * @return resultado da operacao
   */
  public double calcularDefesa(double danoBase, Treinador alvo) {
    return danoBase * (ESCALA_DEFESA / (ESCALA_DEFESA + alvo.getPokeSall().getDef()));
  }

  /**
   * Método responsável por verificar o status dos pokeSals de ambos os treinadores.
   *
   * @param players recebe um arrayList de players contendo ambos os treinadores.
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
   * Aplica dano de um golpe e consome a defesa ativa apenas se houver dano.
   * O dano de veneno, queimadura e outros efeitos nao consome a defesa.
   *
   * @param alvo treinador atingido
   * @param danoRecebido dano depois da defesa base e dos demais multiplicadores
   * @return dano aplicado ao alvo
   */
  private double aplicarDanoDeGolpe(Treinador alvo, double danoRecebido) {
    double danoFinal = Math.max(0, danoRecebido);
    if (danoFinal > 0 && defesasAtivas.remove(alvo)) {
      danoFinal = Math.max(0, servicoDefesa.reduzirDano(danoFinal));
      evento("STATUS", alvo.getNomePokeSall() + " reduziu o dano do golpe usando defesa");
    }
    aplicarDano(alvo, danoFinal);
    return danoFinal;
  }

  /**
   * Método que calcula dano contínuo de ambos os PokeSals(veneno e queimadura).
   *
   * @param players recebe um arrayList contendo ambos os Treinadores e seus pokeSals.
   */
  public void calcularDanoContinuo(ArrayList<Treinador> players) {
    for (Treinador treinador : players) {
      if (treinador.getPokeSall().getHp() <= 0) {
        continue;
      }
      if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
        dano = (venenos.getOrDefault(treinador, 0) * 0.1)
            * treinador.getPokeSall().getHpBase();
        aplicarDano(treinador, dano);
        evento("STATUS", treinador.getNomePokeSall() + " sofreu veneno: " + dano);
        System.out.println(treinador.getNomePokeSall() + " tomou " + dano + " de veneno!");
      }
      if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
        int turnosRestantes = queimaduras.getOrDefault(treinador, 0) - 1;
        queimaduras.put(treinador, turnosRestantes);
        dano = 30;
        aplicarDano(treinador, dano);
        evento("STATUS", treinador.getNomePokeSall() + " sofreu queimadura: " + dano);
        if (turnosRestantes <= 0) {
          treinador.getPokeSall().setStatus(Status.NORMAL);
          evento("STATUS", treinador.getNomePokeSall() + " curou queimadura");
        }
      }
    }
  }

  /**
   * Método responsável por aplicar o efeito dos terrenos no fim do turno(caso esse seja o efeito do
   * terreno).
   *
   * @param players recebe um ArrayList com ambos os treinadores para verificar o tipo dos seus
   *     pokesals, assim, aplicando os devidos efeitos.
   */
  public void aplicarEfeitosTerreno(ArrayList<Treinador> players) {
    for (Treinador treinador : players) {
        if (servicoTerreno.permiteCura(terrenoAtual, treinador.getPokeSall().getTipo())) {
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
            evento("TERRENO", "Canteiro Central curou " + treinador.getNomePokeSall());
            System.out.println("O Canteiro Central curou " + String.format("%.1f", curaTerreno)
                    + " de HP do " + treinador.getPokeSall().getNome() + "!");
          }
        }
    }
  }

  /**
   * Método responsável por usar o item Potion.
   *
   * @param treinador recebe o objeto do treinador para calcular a cura provida devidamente.
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
   * @param treinador recebe o objeto do treinador para calcular a cura provida devidamente.
   */
  public void usarSuperPotion(Treinador treinador) {
    double vidaAtual = treinador.getPokeSall().getHp();
    double vidaBase = treinador.getPokeSall().getHpBase();
    cura = 0.5 * vidaBase;
    treinador.getPokeSall().setStatus(Status.NORMAL);
    venenos.remove(treinador);
    queimaduras.remove(treinador);
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
   * @param treinador recebe o objeto do treinador para remover todos os status negativos do
   *        pokesal.
   */
  public void usarAntidote(Treinador treinador) {
    treinador.getPokeSall().setStatus(Status.NORMAL);
    venenos.remove(treinador);
    queimaduras.remove(treinador);
    System.out.println("O pokeSal curou de todos os efeitos negativos!");
  }

  /**
   * Método responsável por gerar uma recompensa aleatória entre 3 possíveis de um vetor de 3
   * Strings.
   *
   * @return retorna a recompensa gerada aleatoriamente.
   */
  public String recompensaVitoria() {
    String[] recompensas = { "Potion", "SuperPotion", "Antidote" };

    String recompensa = recompensas[gerador.proximoInt(recompensas.length)];

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
    lerOpcao();
  }

  /**
   * Método responsável por verificar a vitória ou derrota do jogador.
   *
   * @return resultado da operacao
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

  /**
   * Disputa do torneio: os NPCs agem sozinhos, mas cada humano escolhe
   * seu golpe ou item no turno dele. O torneio salva o resultado.
   *
   * @param primeiro valor de primeiro
   * @param segundo valor de segundo
   * @return resultado da operacao
   */
  @Override
  public ResultadoBatalha disputar(Treinador primeiro, Treinador segundo) {
    if (primeiro == null || segundo == null || primeiro == segundo) {
      throw new IllegalArgumentException("São necessários dois treinadores distintos");
    }
    if ((!primeiro.isNpc() || !segundo.isNpc()) && entrada == null) {
      throw new IllegalStateException("A batalha de um jogador exige EntradaJogador");
    }

    reiniciarEfeitos();
    resetarUsoDeItensNaBatalha();
    primeiro.getPokeSall().setHp(primeiro.getPokeSall().getHpBase());
    segundo.getPokeSall().setHp(segundo.getPokeSall().getHpBase());
    primeiro.getPokeSall().setStatus(Status.NORMAL);
    segundo.getPokeSall().setStatus(Status.NORMAL);
    if (!terrenoDefinidoManualmente) {
      terrenoAtual = Terrenos.values()[gerador.proximoInt(Terrenos.values().length)];
    }

    System.out.println("\n=== BATALHA: " + primeiro.getNome() + " x " + segundo.getNome() + " ===");
    System.out.println("Terreno: " + terrenoAtual);
    ArrayList<Treinador> participantes = new ArrayList<>();
    participantes.add(primeiro);
    participantes.add(segundo);
    int turnos = 0;
    int prioridade = determinarPrioridade(primeiro, segundo);
    Treinador desistente = null;

    while (primeiro.getPokeSall().getHp() > 0 && segundo.getPokeSall().getHp() > 0
        && desistente == null && turnos < 100) {
      turnos++;
      System.out.println("\n--- Turno " + turnos + " ---");
      Treinador inicial = prioridade == 1 ? primeiro : segundo;
      Treinador proximo = prioridade == 1 ? segundo : primeiro;

      if (realizarAcao(inicial, proximo)) {
        desistente = inicial;
      }
      if (desistente == null && proximo.getPokeSall().getHp() > 0) {
        if (realizarAcao(proximo, inicial)) {
          desistente = proximo;
        }
      }
      if (desistente == null) {
        verificarStatus(participantes);
        aplicarEfeitosTerreno(participantes);
      }
      finalizarRegistroTurno();
    }

    Treinador vencedor;
    if (desistente != null) {
      vencedor = desistente == primeiro ? segundo : primeiro;
    } else {
      // Se atingir 100 turnos, desempata por HP e depois por velocidade.
      vencedor = primeiro;
      if (segundo.getPokeSall().getHp() > primeiro.getPokeSall().getHp()
          || (segundo.getPokeSall().getHp() == primeiro.getPokeSall().getHp()
              && segundo.getPokeSall().getSpd() > primeiro.getPokeSall().getSpd())) {
        vencedor = segundo;
      }
    }

    Treinador perdedor = vencedor == primeiro ? segundo : primeiro;
    ResultadoBatalha resultado = new ResultadoBatalha(vencedor, perdedor, turnos);
    // Vencedores começam a próxima rodada com o HP recuperado.
    vencedor.getPokeSall().setHp(vencedor.getPokeSall().getHpBase());
    vencedor.getPokeSall().setStatus(Status.NORMAL);
    return resultado;
  }

  /**
   * Retorna true somente quando o treinador desiste do confronto.
   *
   * @param atacante valor de atacante
   * @param alvo valor de alvo
   * @return resultado da operacao
   */
  private boolean realizarAcao(Treinador atacante, Treinador alvo) {
    if (atacante.getPokeSall().getStatus() == Status.PARALIZADO) {
      System.out.println(atacante.getNomePokeSall() + " perdeu a vez por paralisia.");
      atacante.getPokeSall().setStatus(Status.NORMAL);
      evento("STATUS", atacante.getNomePokeSall() + " perdeu a ação por paralisia");
      return false;
    }
    if (atacante.isNpc()) {
      atacarAutomaticamente(atacante, alvo);
      return false;
    }
    return escolherAcaoHumana(atacante, alvo);
  }

  /**
   * Executa escolher acao humana.
   *
   * @param humano valor de humano
   * @param adversario valor de adversario
   * @return resultado de escolher acao humana
   */
  private boolean escolherAcaoHumana(Treinador humano, Treinador adversario) {
    while (true) {
      System.out.println("\nSua vez, " + humano.getNome() + "!");
      System.out.printf("%s: %.1f HP | %s: %.1f HP%n",
          humano.getNomePokeSall(), humano.getPokeSall().getHp(),
          adversario.getNomePokeSall(), adversario.getPokeSall().getHp());
      System.out.println("1 - Atacar | 2 - Mochila | 3 - Desistir | 4 - Soneca | 5 - Defesa");
      String escolha = lerOpcao();
      switch (escolha) {
        case "1":
          if (escolherAtaque(humano, adversario)) {
            return false;
          }
          break;
        case "2":
          if (escolherItem(humano)) {
            return false;
          }
          break;
        case "3":
          System.out.println(humano.getNome() + " desistiu do confronto.");
          evento("STATUS", humano.getNome() + " desistiu da batalha");
          return true;
        case "4":
          if (usarSoneca(humano)) {
            return false;
          }
          System.out.println("Nao e possivel descansar com HP cheio ou zerado.");
          break;
        case "5":
          usarDefesa(humano);
          return false;
        default:
          System.out.println("Opção inválida. Escolha 1, 2, 3, 4 ou 5.");
      }
    }
  }

  /**
   * false significa que o jogador voltou ao menu sem gastar a ação.
   *
   * @param humano valor de humano
   * @param adversario valor de adversario
   * @return resultado da operacao
   */
  private boolean escolherAtaque(Treinador humano, Treinador adversario) {
    Habilidades[] habilidades = humano.getPokeSall().getHabilidades();
    while (true) {
      System.out.println("Escolha uma habilidade (0 para voltar):");
      for (int i = 0; i < habilidades.length; i++) {
        if (habilidades[i] != null) {
          System.out.println((i + 1) + " - " + habilidades[i] + " (precisão: "
              + habilidades[i].getPrecisao() + "%)");
        }
      }
      String escolha = lerOpcao();
      if (escolha.equals("0")) {
        return false;
      }
      for (int i = 0; i < habilidades.length; i++) {
        if (escolha.equals(Integer.toString(i + 1)) && habilidades[i] != null) {
          calcularAtaque(habilidades[i], humano, adversario);
          return true;
        }
      }
      System.out.println("Habilidade inválida.");
    }
  }

  /**
   * Executa escolher item.
   *
   * @param humano valor de humano
   * @return resultado de escolher item
   */
  private boolean escolherItem(Treinador humano) {
    while (true) {
      System.out.println("Mochila (0 para voltar):");
      List<Item> itens = humano.getMochila().getItens();
      for (int i = 0; i < itens.size(); i++) {
        Item item = itens.get(i);
        System.out.println((i + 1) + " - " + item.getNome() + " (" + item.getQuantidade() + ")");
      }
      String escolha = lerOpcao();
      if (escolha.equals("0")) {
        return false;
      }
      boolean encontrou = false;
      for (int i = 0; i < itens.size(); i++) {
        if (escolha.equals(Integer.toString(i + 1))) {
          encontrou = true;
          try {
            usarItemNaBatalha(itens.get(i).getNome(), humano);
            return true;
          } catch (IllegalArgumentException | IllegalStateException erro) {
            System.out.println(erro.getMessage());
          }
          break;
        }
      }
      if (!encontrou) {
        System.out.println("Item inválido.");
      }
    }
  }

  /**
   * Executa atacar automaticamente.
   *
   * @param atacante valor de atacante
   * @param alvo valor de alvo
   */
  private void atacarAutomaticamente(Treinador atacante, Treinador alvo) {
    Habilidades[] habilidades = atacante.getPokeSall().getHabilidades();
    Habilidades habilidade = habilidades[gerador.proximoInt(habilidades.length)];
    if (habilidade != null) {
      calcularAtaque(habilidade, atacante, alvo);
    }
  }

}
