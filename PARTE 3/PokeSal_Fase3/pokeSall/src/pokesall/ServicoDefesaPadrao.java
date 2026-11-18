package pokesall;

/** Reduz em 10% o dano do proximo golpe que acertar e causar dano. */
public class ServicoDefesaPadrao implements ServicoDefesa {

  /**
   * Devolve 90% do dano recebido.
   *
   * @param dano valor original do golpe
   * @return dano apos reducao de 10%
   */
  @Override
  public double reduzirDano(double dano) {
    return Math.max(0, dano) * 0.90;
  }
}
