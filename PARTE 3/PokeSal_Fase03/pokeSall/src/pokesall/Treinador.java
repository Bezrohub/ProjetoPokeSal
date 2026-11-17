package pokesall;

/**
 * Declaração da classe abstrata Treinador.
 */
public abstract class Treinador {
  private String nome;
  private PokeSall pokesall;
  private Mochila mochila;
  private boolean npc;

  /**
   * Construtor da classe.
   *
   * @param nome recebe uma String que será o nome do Treinador.
   * @param pokesall recebe um objeto do tipo PokeSall.
   * @param npc recebe um boolean dizendo se o Treinador é um npc ou humano.
   * @param mochila recebe um objeto do tipo Mochila.
   */
  public Treinador(String nome, PokeSall pokesall, boolean npc, Mochila mochila) {
    this.nome = nome;
    this.pokesall = pokesall;
    this.mochila = mochila;
    this.npc = npc;
  }

  /**
   * Sobrecarga do construtor da classe, caso o objeto do tipo mochila não seja informado.
   *
   * @param nome recebe uma String que será o nome do Treinador.
   * @param pokesall recebe um objeto do tipo PokeSall.
   * @param npc recebe um boolean dizendo se o Treinador é um npc ou humano.
   */
  public Treinador(String nome, PokeSall pokesall, boolean npc) {
    this.npc = npc;
    this.nome = nome;
    this.pokesall = pokesall;
    this.mochila = new Mochila();
  }

  /**
   * Define nome.
   *
   * @param nome valor de nome
   */
  public void setNome(String nome) {
    this.nome = nome;
  }

  /**
   * Retorna nome.
   *
   * @return resultado de get nome
   */
  public String getNome() {
    return nome;
  }

  /**
   * Define poke sall.
   *
   * @param pokesall valor de pokesall
   */
  public void setPokeSall(PokeSall pokesall) {
    this.pokesall = pokesall;
  }

  /**
   * Retorna poke sall.
   *
   * @return resultado de get poke sall
   */
  public PokeSall getPokeSall() {
    return this.pokesall;
  }

  /**
   * Retorna nome poke sall.
   *
   * @return resultado de get nome poke sall
   */
  public String getNomePokeSall() {
    return "" + this.pokesall.getNome();
  }

  /**
   * Define mochila.
   *
   * @param mochila valor de mochila
   */
  public void setMochila(Mochila mochila) {
    this.mochila = mochila;
  }

  /**
   * Retorna mochila.
   *
   * @return resultado de get mochila
   */
  public Mochila getMochila() {
    return this.mochila;
  }

  /**
   * Verifica npc.
   *
   * @return resultado de is npc
   */
  public boolean isNpc() {
    return this.npc;
  }
}
