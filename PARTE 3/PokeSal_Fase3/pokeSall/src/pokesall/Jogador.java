package pokesall;

/**
 * Declaração da classe Jogador, que herda da classe Treinador.
 */
public class Jogador extends Treinador {

  /**
   * Construtor da classe, que chama o construtor da super classe.
   *
   * @param nome uma String.
   * @param pokesall um PokeSal.
   */
  public Jogador(String nome, PokeSall pokesall) {
    super(nome, pokesall, false);
  }
}
