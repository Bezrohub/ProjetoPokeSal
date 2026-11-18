package pokesall;

/** Recupera 10% do HP maximo quando o PokeSal tira uma soneca. */
public class ServicoSonecaPadrao implements ServicoSoneca {

  /**
   * Devolve 10% do HP maximo para o jogo aplicar respeitando o limite.
   *
   * @param pokeSal personagem que vai descansar
   * @return 10% do HP maximo
   */
  @Override
  public double calcularRecuperacao(PokeSall pokeSal) {
    return pokeSal.getHpBase() * 0.10;
  }
}
