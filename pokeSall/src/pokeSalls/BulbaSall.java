package pokesalls;

import ataques.Habilidades;
import pokesall.PokeSall;
import pokesall.Tipos;

/**
 * Declaração da classe BulbaSall, que herda da classe PokeSall.
 */

public class BulbaSall extends PokeSall {

  /**
   * Construtor da classe, chama o construtor da super classe e adiciona duas habilidades ao vetor
   * de habilidades do PokeSal.
   */

  public BulbaSall() {
    super("BulbaSall", 200, 100, 70, 25, Tipos.PLANTA);
    addHabilidades(0, Habilidades.FOLHA_BUZZER);
    addHabilidades(1, Habilidades.GAS);
  }
}
