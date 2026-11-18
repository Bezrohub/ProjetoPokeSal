package pokesall;

/** Fornece as condicoes climaticas, sem acoplar o combate a uma API especifica. */
public interface ServicoClima {

  /**
   * Consulta o clima atual.
   *
   * @return o clima da regiao da batalha
   */
  Clima consultarClima();
}
