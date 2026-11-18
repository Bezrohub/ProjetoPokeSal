package pokesall;

/** Calcula a reducao temporaria do dano de um golpe recebido. */
public interface ServicoDefesa {

  /**
   * Reduz o dano de um unico golpe.
   *
   * @param dano valor do golpe antes da protecao temporaria
   * @return dano depois de aplicar a protecao
   */
  double reduzirDano(double dano);
}
