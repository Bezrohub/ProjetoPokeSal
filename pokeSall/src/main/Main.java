package main;

import java.util.List;
import java.util.Scanner;
import pokeSall.Game;
import pokeSall.Jogador;
import pokeSall.PokeSall;
import pokeSall.Treinador;
import pokeSalls.BulbaSall;
import pokeSalls.CharSall;
import pokeSalls.SquirtSall;

public class Main {
  public static void main(String[] args) {
    Scanner ler = new Scanner(System.in);
    System.out.println("Bem vindo ao jogo PokeSal!");
    System.out.println("Digite o seu nome: ");
    Treinador jogador;
    String opcao;
    do {
      System.out.println("===========================");
      System.out.println("Escolha o seu PokeSal!");
      System.out.println("1- BulbaSal");
      System.out.println("2- CharSal");
      System.out.println("3- SquirtSal");
      opcao = ler.nextLine();
    } while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")));
    String nome;
    Game game;
    nome = ler.nextLine();
    PokeSall pokeSall;
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
        break;
    }
  }
}
