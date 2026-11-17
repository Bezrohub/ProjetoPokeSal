package pokesall;

/**
 * Declaração da classe Npc, que herda da classe Treinador.
 */
public class Npc extends Treinador {

  /**
   * Construtor da classe, chama o construtor da super classe.
   *
   * @param nome uma String.
   * @param pokesall um Pokesal.
   */
  public Npc(String nome, PokeSall pokesall) {
    super(nome, pokesall, true);
  }
}
