package pokesall;

/**
 * Declaração da classe Jogador, que herda da classe Treinador.
 */

public class Jogador extends Treinador {

  /**
   * Construtor da classe, que chama o construtor da super classe.
   *
   * @param nome, uma String.
   * @param pokeSall, um PokeSal.
   */

  public Jogador(String nome, PokeSall pokeSall) {
    super(nome, pokeSall, false);
  }
}
