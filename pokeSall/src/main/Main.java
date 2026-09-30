package main;

import java.util.List;
import java.util.Scanner;
import pokesall.Game;
import pokesall.Jogador;
import pokesall.PokeSall;
import pokesall.Treinador;
import pokesalls.BulbaSall;
import pokesalls.CharSall;
import pokesalls.SquirtSall;

/**
 * Declaração da classe Main.
 *
 */

public class Main {
  /**
   * Método estático onde o menu inicial é executado chamando a classe "Game".
   *
   * @param args.
   */
  public static void main(String[] args) {
    Scanner ler = new Scanner(System.in);
    String nome;
    boolean rodando = true;

    while (rodando) {
      System.out.println("Bem vindo ao jogo PokeSal!");
      System.out.println("Digite o seu nome: ");
      nome = ler.nextLine();

      PokeSall pokeSall;
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
          pokeSall = new BulbaSall();
          jogador = new Jogador(nome, pokeSall);
          game = new Game(jogador);
          game.rodarGame();
          break;

        case "2":
          pokeSall = new CharSall();
          jogador = new Jogador(nome, pokeSall);
          game = new Game(jogador);
          game.rodarGame();
          break;

        case "3":
          pokeSall = new SquirtSall();
          jogador = new Jogador(nome, pokeSall);
          game = new Game(jogador);
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
}
