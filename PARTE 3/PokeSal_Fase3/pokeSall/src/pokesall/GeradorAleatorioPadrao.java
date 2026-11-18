package pokesall;

import java.util.Random;

/** Implementação real. Nos testes, use mock de GeradorAleatorio. */
public class GeradorAleatorioPadrao implements GeradorAleatorio {
  private final Random random;

  /**
   * Cria uma instancia de GeradorAleatorioPadrao.
   *
   * @param random valor de random
   */
  public GeradorAleatorioPadrao(Random random) {
    this.random = random;
  }

  /**
   * Executa proximo int.
   *
   * @param limite valor de limite
   * @return resultado de proximo int
   */
  @Override
  public int proximoInt(int limite) {
    return random.nextInt(limite);
  }

  /**
   * Executa proximo double.
   *
   * @return resultado de proximo double
   */
  @Override
  public double proximoDouble() {
    return random.nextDouble();
  }
}
