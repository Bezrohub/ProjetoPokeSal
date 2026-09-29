package itens;

import pokesall.PokeSall;

/**
 * Classe que herda da classe abstrata Item.
 */

public class SuperPotion extends Item {

  /**
   * Construtor da classe, chamando o construtor da super classe e passando seus atributos.
   */

  public SuperPotion() {
    super("SuperPotion", "Cura 50% do HP e retira todos os efeitos negativos");
  }
}
