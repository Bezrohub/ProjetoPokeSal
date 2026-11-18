package pokesall;

/** Calcula a recuperacao de HP oferecida pela acao Soneca. */
public interface ServicoSoneca {

  /**
   * Calcula quantos pontos de vida o PokeSal pode recuperar.
   *
   * @param pokeSal personagem que vai descansar
   * @return pontos de HP a recuperar
   */
  double calcularRecuperacao(PokeSall pokeSal);
}
