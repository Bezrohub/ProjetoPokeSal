package ataques;

/** Habilidades e suas precisões de 0 a 100. */
public enum Habilidades {
  BEACH_PARK(95), BEAT_BOLHA(95), AGUA_TERMAL(90),
  TAPA_QUENTINHO(95), QUEIMAR(90), HELL_BEAM(80),
  FOLHA_BUZZER(95), RECUPERAR_VIDA(100), GAS(90);

  private final int precisao;

  Habilidades(int precisao) {
    if (precisao < 0 || precisao > 100) {
      throw new IllegalArgumentException("Precisão deve estar entre 0 e 100");
    }
    this.precisao = precisao;
  }

  /**
   * Retorna precisao.
   *
   * @return resultado de get precisao
   */
  public int getPrecisao() {
    return precisao;
  }
}
