package pokesalls;

import pokesall.PokeSall;
import pokesall.Tipos;

/**
 * Declaração da classe ChikoSall, que herda da classe PokeSall.
 */

public class ChikoSall extends PokeSall {

  /**
   * Construtor da classe, chama o construtor da super classe e adiciona duas habilidades ao vetor
   * de habilidades do PokeSal.
   */

  public ChikoSall() {

    super("ChikoSal", 200, 80, 70, 60, Tipos.PLANTA);
  }
}
