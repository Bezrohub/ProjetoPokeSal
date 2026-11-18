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
   * @param nome recebe uma String.
   * @param descricao recebe uma String.
   */
  public Item(String nome, String descricao) {
    this.nome = nome;
    this.descricao = descricao;
  }

  /**
   * Retorna nome.
   *
   * @return resultado de get nome
   */
  public String getNome() {
    return this.nome;
  }

  /**
   * Retorna descricao.
   *
   * @return resultado de get descricao
   */
  public String getDescricao() {
    return this.descricao;
  }

  /**
   * Retorna quantidade.
   *
   * @return resultado de get quantidade
   */
  public int getQuantidade() {
    return this.quantidade;
  }

  /**
   * Define quantidade.
   *
   * @param quantidade valor de quantidade
   */
  public void setQuantidade(int quantidade) {
    this.quantidade = quantidade;
  }
}
