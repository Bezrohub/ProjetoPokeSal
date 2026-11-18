package pokesall;

/** Saída de eventos; também recebe erros do repositório. */
public interface RegistroBatalha {
  /**
   * Executa registrar.
   *
   * @param tipo valor de tipo
   * @param descricao valor de descricao
   */
  void registrar(String tipo, String descricao);
  /**
   * Executa registrar erro.
   *
   * @param descricao valor de descricao
   * @param erro valor de erro
   */
  void registrarErro(String descricao, Exception erro);
}
