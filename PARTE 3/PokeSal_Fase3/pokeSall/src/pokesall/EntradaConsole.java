package pokesall;

import java.util.Objects;
import java.util.Scanner;

/** Lê as escolhas feitas pelo jogador no terminal. */
public class EntradaConsole implements EntradaJogador {
  private final Scanner scanner;

  /**
   * Cria uma instancia de EntradaConsole.
   *
   * @param scanner valor de scanner
   */
  public EntradaConsole(Scanner scanner) {
    this.scanner = Objects.requireNonNull(scanner);
  }

  /**
   * Executa ler linha.
   *
   * @return resultado de ler linha
   */
  @Override
  public String lerLinha() {
    if (!scanner.hasNextLine()) {
      throw new IllegalStateException("Entrada encerrada antes do fim da batalha");
    }
    return scanner.nextLine();
  }
}
