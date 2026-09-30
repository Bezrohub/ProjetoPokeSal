package itens;

/**
 * Declaração da classe abstrata Item.
 */

public abstract class Item {
  private String nome;
  private String descricao;
  private int quantidade;

  /**
   * Construtor da classe.
   *
   * @param nome, recebe uma String.
   * @param descricao, recebe uma String.
   */

  public Item(String nome, String descricao) {
    this.nome = nome;
    this.descricao = descricao;
  }

  public String getNome() {
    return this.nome;
  }

  public String getDescricao() {
    return this.descricao;
  }

  public int getQuantidade() {
    return this.quantidade;
  }

  public void setQuantidade(int quantidade) {
    this.quantidade = quantidade;
  }
}
