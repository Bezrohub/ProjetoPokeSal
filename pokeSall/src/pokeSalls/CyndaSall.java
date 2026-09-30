package pokesalls;

import pokesall.PokeSall;
import pokesall.Tipos;

/**
 * Declaração da classe CyndaSall, que herda da classe PokeSall.
 */

public class CyndaSall extends PokeSall {

  /**
   * Construtor da classe, chama o construtor da super classe e adiciona duas habilidades ao vetor
   * de habilidades do PokeSal.
   */

  public CyndaSall() {
    super("CyndaSal", 200, 80, 70, 60, Tipos.FOGO);
  }
}
