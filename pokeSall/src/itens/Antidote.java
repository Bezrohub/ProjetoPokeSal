package itens;

import pokesall.PokeSall;
import pokesall.Status;

/**
 * Classe que herda da classe abstrata Item.
 */

public class Antidote extends Item {

  /**
   * Construtor da classe, chamando o construtor da super classe e passando seus atributos.
   */

  public Antidote() {
    super("Antidote", "Retira todos os efeitos negativos");
  }

}
