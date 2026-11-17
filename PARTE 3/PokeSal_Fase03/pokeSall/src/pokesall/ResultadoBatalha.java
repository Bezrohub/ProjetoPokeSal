package pokesall;

/** Resultado imutável, guardando os nomes dos PokéSals usados. */
public class ResultadoBatalha {
  private final Treinador vencedor;
  private final Treinador perdedor;
  private final int numeroTurnos;
  private final String pokeSalVencedor;
  private final String pokeSalPerdedor;

  /**
   * Cria uma instancia de ResultadoBatalha.
   *
   * @param vencedor valor de vencedor
   * @param perdedor valor de perdedor
   * @param numeroTurnos valor de numero turnos
   */
  public ResultadoBatalha(Treinador vencedor, Treinador perdedor, int numeroTurnos) {
    if (vencedor == null || perdedor == null || vencedor == perdedor || numeroTurnos < 1) {
      throw new IllegalArgumentException("Resultado de batalha inválido");
    }
    this.vencedor = vencedor;
    this.perdedor = perdedor;
    this.numeroTurnos = numeroTurnos;
    this.pokeSalVencedor = vencedor.getNomePokeSall();
    this.pokeSalPerdedor = perdedor.getNomePokeSall();
  }

  /**
   * Retorna vencedor.
   *
   * @return resultado de get vencedor
   */
  public Treinador getVencedor() {
    return vencedor;
  }

  /**
   * Retorna perdedor.
   *
   * @return resultado de get perdedor
   */
  public Treinador getPerdedor() {
    return perdedor;
  }

  /**
   * Retorna numero turnos.
   *
   * @return resultado de get numero turnos
   */
  public int getNumeroTurnos() {
    return numeroTurnos;
  }

  /**
   * Retorna poke sal vencedor.
   *
   * @return resultado de get poke sal vencedor
   */
  public String getPokeSalVencedor() {
    return pokeSalVencedor;
  }

  /**
   * Retorna poke sal perdedor.
   *
   * @return resultado de get poke sal perdedor
   */
  public String getPokeSalPerdedor() {
    return pokeSalPerdedor;
  }
}
