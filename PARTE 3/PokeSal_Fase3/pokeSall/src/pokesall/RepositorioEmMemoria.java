package pokesall;

import java.util.ArrayList;
import java.util.List;

/** Persistência em memória; os testes podem usar mock no lugar desta classe. */
public class RepositorioEmMemoria implements RepositorioResultados {
  private final List<ResultadoBatalha> resultados = new ArrayList<>();

  /**
   * Executa salvar.
   *
   * @param resultado valor de resultado
   */
  @Override
  public void salvar(ResultadoBatalha resultado) {
    resultados.add(resultado);
  }

  /**
   * Retorna resultados.
   *
   * @return resultado de get resultados
   */
  public List<ResultadoBatalha> getResultados() {
    return new ArrayList<>(resultados);
  }
}
