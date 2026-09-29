package pokesalls;

import ataques.Habilidades;
import pokesall.PokeSall;
import pokesall.Tipos;

/**
 * Declaração da classe CharSall, que herda da classe PokeSall.
 */

public class CharSall extends PokeSall {

  /**
   * Construtor da classe, chama o construtor da super classe e adiciona duas habilidades ao vetor
   * de habilidades do PokeSal.
   */

  public CharSall() {
    super("CharSall", 200, 70, 75, 30, Tipos.FOGO);
    addHabilidades(0, Habilidades.HELL_BEAM);
    addHabilidades(1, Habilidades.QUEIMAR);
  }

}
