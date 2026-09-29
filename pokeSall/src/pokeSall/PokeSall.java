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
   * @param nome, recebe uma String que será o nome do PokeSal.
   * @param hp, recebe um double, o valor do hp do PokeSal.
   * @param def recebe um int, o valor de defesa do PokeSal.
   * @param spd recebe um int, o valor de velocidade do PokeSal.
   * @param atk recebe um double, o valor de ataque do PokeSal.
   * @param tipo recebe um enum, informando o tipo do PokeSal.
   */

  public PokeSall(String nome, double hp, int def, int spd, double atk, Tipos tipo) {
    this.hpBase = hp;
    this.nome = nome;
    this.hp = hp;
    this.def = def;
    this.spd = spd;
    this.atk = atk;
    this.tipo = tipo;
  }

  /**
   * Método que adiciona as Habilidades do PokeSal em um vetor de enum.
   *
   * @param indice, o indice do vetor.
   * @param habilidade, o enum da Habilidade para adicionar ao vetor.
   */

  public void addHabilidades(int indice, Habilidades habilidade) {
    habilidades[indice] = habilidade;
  }

  public Habilidades[] getHabilidades() {
    return this.habilidades;
  }

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

  public Status getStatus() {
    return this.stat;
  }

  public void setStatus(Status stat) {
    this.stat = stat;
  }

  public double getHp() {
    return this.hp;
  }

  public void setHp(double hp) {
    this.hp = hp;
  }

  public int getDef() {
    return this.def;
  }

  public int getSpd() {
    return this.spd;
  }

  public double getAtk() {
    return this.atk;
  }

  public void setAtk(double atk) {
    this.atk = atk;
  }

  public Tipos getTipo() {
    return this.tipo;
  }

  public String getNome() {
    return this.nome;
  }

  public double getHpBase() {
    return this.hpBase;
  }
}
