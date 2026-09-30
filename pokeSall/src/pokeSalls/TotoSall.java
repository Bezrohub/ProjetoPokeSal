package pokesalls;

import pokesall.PokeSall;
import pokesall.Tipos;

/**
 * Declaração da classe TotoSall, que herda da classe PokeSall.
 */

public class TotoSall extends PokeSall {

  /**
   * Construtor da classe, chama o construtor da super classe e adiciona duas habilidades ao vetor
   * de habilidades do PokeSal.
   */

  public TotoSall() {
    super("TotoSal", 200, 80, 70, 60, Tipos.AGUA);
  }
}
