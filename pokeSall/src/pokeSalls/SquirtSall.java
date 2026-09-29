package pokesalls;

import ataques.Habilidades;
import pokesall.PokeSall;
import pokesall.Tipos;

/**
 * Declaração da classe SquirtSall, que herda da classe PokeSall.
 */

public class SquirtSall extends PokeSall {

  /**
   * Construtor da classe, chama o construtor da super classe e adiciona duas habilidades ao vetor
   * de habilidades do PokeSal.
   */

  public SquirtSall() {

    super("SquirtSall", 160, 60, 80, 40, Tipos.AGUA);
    addHabilidades(0, Habilidades.BEAT_BOLHA);
    addHabilidades(1, Habilidades.AGUA_TERMAL);

  }
}
