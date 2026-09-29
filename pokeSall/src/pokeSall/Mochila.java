package pokesall;

import itens.Antidote;
import itens.Item;
import itens.Potion;
import itens.SuperPotion;
import java.util.ArrayList;
import java.util.List;

/**
 * Declaração da classe Mochila.
 */

public class Mochila {
  private ArrayList<Item> itens = new ArrayList<Item>();
  private Item item;

  /**
   * Construtor da classe.
   *
   * @param itens, recebe um ArrayList do tipo itens.
   */

  public Mochila(ArrayList<Item> itens) {
    this.itens = itens;
  }

  /**
   * Sobrecarga do construtor, caso nenhum parametro seja informado, cria e adiciona itens padrões
   * para a mochila.
   */

  public Mochila() {
    item = new Potion();
    item.setQuantidade(2);
    this.itens.add(item);
    item = new Antidote();
    item.setQuantidade(2);
    this.itens.add(item);
    item = new SuperPotion();
    item.setQuantidade(0);
    this.itens.add(item);
  }

  public List<Item> getItens() {
    return this.itens;
  }

  /**
   * Método que lista todos os itens no ArrayList de Item.
   */

  public void listarMochila() {

    for (Item item : this.itens) {
      if (item.getQuantidade() > 0) {
        System.out.println(item.getNome() + ": " + item.getQuantidade());
        System.out.println(item.getDescricao());
      }

    }
  }

  /**
   * Método que adiciona um item ao ArrayList de Item.
   *
   * @param nome, recebe uma String e compara se esse item existe, se sim adiciona a lista.
   */

  public void addItem(String nome) {
    int temp = 0;
    for (Item item : this.itens) {
      temp = item.getQuantidade();
      if (item.getNome().equals(nome)) {
        temp++;
        item.setQuantidade(temp);
      }
    }
  }

  /**
   * Método que consome um item da mochila.
   *
   * @param nome, recebe uma String e compara se esse item existe, se sim remove uma unidade.
   *
   * @return, retorna true caso item tenha sido consumido, e false caso contrário.
   */

  public boolean consumirItem(String nome) {
    int temp = 0;
    for (Item item : this.itens) {
      temp = item.getQuantidade();
      if (item.getNome().equals(nome)) {
        temp--;
        item.setQuantidade(temp);
        return true;
      }
    }
    return false;
  }
}
