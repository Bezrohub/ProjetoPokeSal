package pokesall;

/** Persistência de resultados sem acoplar o torneio a arquivos ou banco de dados. */
public interface RepositorioResultados {
  /**
   * Executa salvar.
   *
   * @param resultado valor de resultado
   */
  void salvar(ResultadoBatalha resultado);
}
