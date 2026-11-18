package pokesall;

import ataques.Habilidades;

/**
 * Declaração da classe abstrata PokeSall, classe que armazena atributos, status e habilidades de um
 * pokeSal.
 */
public abstract class PokeSall {

  private String nome;
  private double hp;
  private final double hpBase;
  private int def;
  private int spd;
  private double atk;
  private Tipos tipo;
  private Status stat = Status.NORMAL;
  private Habilidades[] habilidades = new Habilidades[2];

  /**
   * Construtor da classe.
   *
   * @param nome recebe uma String que será o nome do PokeSal.
   * @param hp recebe um double, o valor do hp do PokeSal.
   * @param def recebe um int, o valor de defesa do PokeSal.
   * @param spd recebe um int, o valor de velocidade do PokeSal.
   * @param atk recebe um double, o valor de ataque do PokeSal.
   * @param tipo recebe um enum, informando o tipo do PokeSal.
   *
   * @param habilidades valor de habilidades
   */
  public PokeSall(String nome, double hp, int def, int spd, double atk, Tipos tipo,
      Habilidades[] habilidades) {
    this.hpBase = hp;
    this.nome = nome;
    this.hp = hp;
    this.def = def;
    this.spd = spd;
    this.atk = atk;
    this.tipo = tipo;
    this.habilidades = habilidades;
  }

  /**
   * Construtor simplificado usado pelos PokéSals existentes.
   *
   * @param nome valor de nome
   * @param hp valor de hp
   * @param def valor de def
   * @param spd valor de spd
   * @param atk valor de atk
   * @param tipo valor de tipo
   */
  public PokeSall(String nome, double hp, int def, int spd, double atk, Tipos tipo) {
    this(nome, hp, def, spd, atk, tipo, new Habilidades[2]);
  }

  /**
   * Método que adiciona as Habilidades do PokeSal em um vetor de enum.
   *
   * @param indice o indice do vetor.
   * @param habilidade o enum da Habilidade para adicionar ao vetor.
   */
  public void addHabilidades(int indice, Habilidades habilidade) {
    habilidades[indice] = habilidade;
  }

  /**
   * Retorna habilidades.
   *
   * @return resultado de get habilidades
   */
  public Habilidades[] getHabilidades() {
    return this.habilidades;
  }

  /**
   * Define habilidades.
   *
   * @param habilidades valor de habilidades
   */
  public void setHabilidades(Habilidades[] habilidades) {
    this.habilidades = habilidades;
  }

  /**
   * Método que lista todas as Habilidades do vetor.
   */
  public void listarHabilidades() {
    for (int k = 0; k < habilidades.length; k++) {
      System.out.println(habilidades[k]);
    }
  }

  /**
   * Retorna status.
   *
   * @return resultado de get status
   */
  public Status getStatus() {
    return this.stat;
  }

  /**
   * Define status.
   *
   * @param stat valor de stat
   */
  public void setStatus(Status stat) {
    this.stat = stat;
  }

  /**
   * Retorna hp.
   *
   * @return resultado de get hp
   */
  public double getHp() {
    return this.hp;
  }

  /**
   * Define hp.
   *
   * @param hp valor de hp
   */
  public void setHp(double hp) {
    this.hp = hp;
  }

  /**
   * Retorna def.
   *
   * @return resultado de get def
   */
  public int getDef() {
    return this.def;
  }

  /**
   * Retorna spd.
   *
   * @return resultado de get spd
   */
  public int getSpd() {
    return this.spd;
  }

  /**
   * Retorna atk.
   *
   * @return resultado de get atk
   */
  public double getAtk() {
    return this.atk;
  }

  /**
   * Define atk.
   *
   * @param atk valor de atk
   */
  public void setAtk(double atk) {
    this.atk = atk;
  }

  /**
   * Retorna tipo.
   *
   * @return resultado de get tipo
   */
  public Tipos getTipo() {
    return this.tipo;
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
   * Retorna hp base.
   *
   * @return resultado de get hp base
   */
  public double getHpBase() {
    return this.hpBase;
  }
}
