package pokesall;

/**
 * Declaração da classe Npc, que herda da classe Treinador.
 */

public class Npc extends Treinador {

  /**
   * Construtor da classe, chama o construtor da super classe.
   *
   * @param nome, uma String.
   * @param pokeSall, um Pokesal.
   */

  public Npc(String nome, PokeSall pokeSall) {
    super(nome, pokeSall, true);
  }
}
