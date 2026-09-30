package itens;

import pokesall.PokeSall;

/**
 * Classe que herda da classe abstrata Item.
 */

public class Potion extends Item {

  /**
   * Construtor da classe, chamando o construtor da super classe e passando seus atributos.
   */

  public Potion() {
    super("Potion", "Cura 30% do HP");
  }
}
