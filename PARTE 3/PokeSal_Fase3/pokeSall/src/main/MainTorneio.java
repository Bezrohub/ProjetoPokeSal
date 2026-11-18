package main;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import pokesall.Clima;
import pokesall.EntradaConsole;
import pokesall.Game;
import pokesall.GeradorAleatorioPadrao;
import pokesall.Jogador;
import pokesall.Npc;
import pokesall.PokeSall;
import pokesall.RegistroConsole;
import pokesall.RepositorioEmMemoria;
import pokesall.ServicoClimaFixo;
import pokesall.ServicoTerrenoPadrao;
import pokesall.ServicoSonecaPadrao;
import pokesall.ServicoDefesaPadrao;
import pokesall.Torneio;
import pokesall.Treinador;
import pokesals.BulbaSall;
import pokesals.CharSall;
import pokesals.ChikoSall;
import pokesals.CyndaSall;
import pokesals.SquirtSall;
import pokesals.TotoSall;

/** Torneio com NPCs automáticos e um jogador humano que escolhe as ações. */
public class MainTorneio {
  /**
   * Inicia a aplicacao pelo terminal.
   *
   * @param args valor de args
   */
  public static void main(String[] args) {
    executar(new Scanner(System.in));
  }

  /**
   * Permite compartilhar o mesmo Scanner com o menu principal.
   *
   * @param teclado valor de teclado
   */
  public static void executar(Scanner teclado) {
    System.out.println("=== TORNEIO POKÉSAL ===");
    System.out.print("Seu nome: ");
    String nome = teclado.nextLine().trim();
    if (nome.isEmpty()) {
      nome = "Jogador";
    }

    System.out.println("Escolha seu PokéSal: 1 - BulbaSal, 2 - CharSal, 3 - SquirtSal");
    PokeSall escolhido = escolherPokeSal(teclado);
    Treinador jogador = new Jogador(nome, escolhido);

    System.out.println("Quantidade de treinadores: 4 ou 8?");
    int quantidade = escolherQuantidade(teclado);

    List<Treinador> participantes = new ArrayList<>();
    participantes.add(jogador);
    participantes.add(new Npc("Ashley", new CharSall()));
    participantes.add(new Npc("Joe Lois", new BulbaSall()));
    participantes.add(new Npc("Mary", new SquirtSall()));
    if (quantidade == 8) {
      participantes.add(new Npc("Luna", new ChikoSall()));
      participantes.add(new Npc("Leo", new CyndaSall()));
      participantes.add(new Npc("Bia", new TotoSall()));
      participantes.add(new Npc("Davi", new CharSall()));
    }

    RepositorioEmMemoria repositorio = new RepositorioEmMemoria();
    RegistroConsole registro = new RegistroConsole();
    Game game = new Game(jogador, new GeradorAleatorioPadrao(new Random()),
        new ServicoTerrenoPadrao(Clock.systemDefaultZone(), new ServicoClimaFixo(Clima.ENSOLARADO)),
        repositorio, registro, new EntradaConsole(teclado),
        new ServicoSonecaPadrao(), new ServicoDefesaPadrao());
    Torneio torneio = new Torneio(game, repositorio, registro);
    Treinador campeao = torneio.realizar(participantes);

    System.out.println("\n=== TORNEIO ENCERRADO ===");
    System.out.println("Campeão: " + campeao.getNome());
    System.out.println("Resultados salvos: " + repositorio.getResultados().size());
    System.out.println("Ranking:");
    int posicao = 1;
    for (Treinador treinador : torneio.getRanking()) {
      System.out.println(posicao++ + "º - " + treinador.getNome());
    }
  }

  /**
   * Executa escolher poke sal.
   *
   * @param teclado valor de teclado
   * @return resultado de escolher poke sal
   */
  private static PokeSall escolherPokeSal(Scanner teclado) {
    while (true) {
      switch (teclado.nextLine().trim()) {
        case "1":
          return new BulbaSall();
        case "2":
          return new CharSall();
        case "3":
          return new SquirtSall();
        default:
          System.out.println("Escolha 1, 2 ou 3:");
      }
    }
  }

  /**
   * Executa escolher quantidade.
   *
   * @param teclado valor de teclado
   * @return resultado de escolher quantidade
   */
  private static int escolherQuantidade(Scanner teclado) {
    while (true) {
      String entrada = teclado.nextLine().trim();
      if (entrada.equals("4") || entrada.equals("8")) {
        return Integer.parseInt(entrada);
      }
      System.out.println("Digite 4 ou 8:");
    }
  }
}
