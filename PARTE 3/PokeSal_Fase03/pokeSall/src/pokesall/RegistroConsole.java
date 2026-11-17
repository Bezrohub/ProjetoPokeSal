package pokesall;

/** Escrita simples no terminal (em testes pode ser um mock). */
public class RegistroConsole implements RegistroBatalha {
  /**
   * Executa registrar.
   *
   * @param tipo valor de tipo
   * @param descricao valor de descricao
   */
  @Override
  public void registrar(String tipo, String descricao) {
    System.out.println("[" + tipo + "] " + descricao);
  }

  /**
   * Executa registrar erro.
   *
   * @param descricao valor de descricao
   * @param erro valor de erro
   */
  @Override
  public void registrarErro(String descricao, Exception erro) {
    System.err.println("[ERRO] " + descricao + ": " + erro.getMessage());
  }
}
