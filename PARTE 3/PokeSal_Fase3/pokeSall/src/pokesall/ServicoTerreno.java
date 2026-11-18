package pokesall;

/** Contrato dos efeitos de terreno (substituível por mock). */
public interface ServicoTerreno {
  /**
   * Executa multiplicador dano.
   *
   * @param terreno valor de terreno
   * @param tipo valor de tipo
   * @return resultado de multiplicador dano
   */
  double multiplicadorDano(Game.Terrenos terreno, Tipos tipo);
  /**
   * Executa permite cura.
   *
   * @param terreno valor de terreno
   * @param tipo valor de tipo
   * @return resultado de permite cura
   */
  boolean permiteCura(Game.Terrenos terreno, Tipos tipo);
}
