package main;

import java.time.Clock;
import java.util.Random;
import java.util.Scanner;
import pokesall.Clima;
import pokesall.EntradaConsole;
import pokesall.Game;
import pokesall.GeradorAleatorioPadrao;
import pokesall.Jogador;
import pokesall.PokeSall;
import pokesall.RegistroConsole;
import pokesall.RepositorioEmMemoria;
import pokesall.ServicoClimaFixo;
import pokesall.ServicoTerrenoPadrao;
import pokesall.ServicoSonecaPadrao;
import pokesall.ServicoDefesaPadrao;
import pokesall.Treinador;
import pokesals.BulbaSall;
import pokesals.CharSall;
import pokesals.SquirtSall;

/**
 * Declaração da classe Main.
 *
 */
public class Main {
  /**
   * Método estático onde o menu inicial é executado chamando a classe "Game".
   *
   * @param args.
   *
   * @param args valor de args
   */
  public static void main(String[] args) {
    Scanner ler = new Scanner(System.in);
    System.out.println("=== POKÉSAL ===");
    System.out.println("1 - Torre clássica | 2 - Torneio com jogador | 3 - Sair");
    String modo;
    do {
      modo = ler.nextLine().trim();
      if (modo.equals("2")) {
        MainTorneio.executar(ler);
        return;
      }
      if (modo.equals("3")) {
        return;
      }
      if (!modo.equals("1")) {
        System.out.println("Escolha 1, 2 ou 3:");
      }
    } while (!modo.equals("1"));
    String nome;
    boolean rodando = true;

    while (rodando) {
      System.out.println("Bem vindo ao jogo PokeSal!");
      System.out.println("Digite o seu nome: ");
      nome = ler.nextLine();

      PokeSall pokesall;
      Treinador jogador;
      Game game;
      String opcao;

      do {
        System.out.println("===========================");
        System.out.println("Escolha o seu PokeSal!");
        System.out.println("1- BulbaSal");
        System.out.println("2- CharSal");
        System.out.println("3- SquirtSal");
        opcao = ler.nextLine();
      } while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")));

      switch (opcao) {

        case "1":
          pokesall = new BulbaSall();
          jogador = new Jogador(nome, pokesall);
          game = criarGame(jogador, ler);
          game.rodarGame();
          break;

        case "2":
          pokesall = new CharSall();
          jogador = new Jogador(nome, pokesall);
          game = criarGame(jogador, ler);
          game.rodarGame();
          break;

        case "3":
          pokesall = new SquirtSall();
          jogador = new Jogador(nome, pokesall);
          game = criarGame(jogador, ler);
          game.rodarGame();
          break;

        default:
          System.out.println("Opção inválida!");
          ler.close();
          break;
      }
      do {
        System.out.println("Deseja jogar novamente?");
        System.out.println("1- Sim");
        System.out.println("2- Não");
        opcao = ler.nextLine();
        if (opcao.equals("2")) {
          rodando = false;
        }
      } while (!(opcao.equals("1") || opcao.equals("2")));

    }
  }

  /**
   * As implementações reais só são criadas aqui, na entrada do programa.
   *
   * @param jogador valor de jogador
   * @param ler valor de ler
   * @return resultado da operacao
   */
  private static Game criarGame(Treinador jogador, Scanner ler) {
    return new Game(jogador,
        new GeradorAleatorioPadrao(new Random()),
        new ServicoTerrenoPadrao(Clock.systemDefaultZone(), new ServicoClimaFixo(Clima.ENSOLARADO)),
        new RepositorioEmMemoria(),
        new RegistroConsole(),
        new EntradaConsole(ler),
        new ServicoSonecaPadrao(), new ServicoDefesaPadrao());
  }

}
