package pokesall;

/**
 * Declaração da classe abstrata Treinador.
 */

public abstract class Treinador {
  private String nome;
  private PokeSall pokeSall;
  private Mochila mochila;
  private boolean npc;

  /**
   * Construtor da classe.
   *
   * @param nome, recebe uma String que será o nome do Treinador.
   * @param pokeSall, recebe um objeto do tipo PokeSall.
   * @param npc, recebe um boolean dizendo se o Treinador é um npc ou humano.
   * @param mochila, recebe um objeto do tipo Mochila.
   */

  public Treinador(String nome, PokeSall pokeSall, boolean npc, Mochila mochila) {
    this.nome = nome;
    this.pokeSall = pokeSall;
    this.mochila = mochila;
  }

  /**
   * Sobrecarga do construtor da classe, caso o objeto do tipo mochila não seja informado.
   *
   * @param nome, recebe uma String que será o nome do Treinador.
   * @param pokeSall, recebe um objeto do tipo PokeSall.
   * @param npc, recebe um boolean dizendo se o Treinador é um npc ou humano.
   */

  public Treinador(String nome, PokeSall pokeSall, boolean npc) {
    this.npc = npc;
    this.nome = nome;
    this.pokeSall = pokeSall;
    this.mochila = new Mochila();
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getNome() {
    return nome;
  }

  public void setPokeSall(PokeSall pokeSall) {
    this.pokeSall = pokeSall;
  }

  public PokeSall getPokeSall() {
    return this.pokeSall;
  }

  public String getNomePokeSall() {
    return "" + this.pokeSall.getNome();
  }

  public void setMochila(Mochila mochila) {
    this.mochila = mochila;
  }

  public Mochila getMochila() {
    return this.mochila;
  }

  public boolean isNpc() {
    return this.npc;
  }
}
