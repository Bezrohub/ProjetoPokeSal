package pokesall;

/** Permite testar o torneio com uma batalha simulada usando Mockito. */
public interface Batalha {
  /**
   * Executa disputar.
   *
   * @param primeiro valor de primeiro
   * @param segundo valor de segundo
   * @return resultado de disputar
   */
  ResultadoBatalha disputar(Treinador primeiro, Treinador segundo);
}
