package pokesall;

import java.util.Objects;

/** Implementacao local de clima para demonstracao, substituivel por API externa. */
public class ServicoClimaFixo implements ServicoClima {
  private final Clima clima;

  /**
   * Configura um clima fixo para a demonstracao.
   *
   * @param clima clima desejado
   */
  public ServicoClimaFixo(Clima clima) {
    this.clima = Objects.requireNonNull(clima);
  }

  /** {@inheritDoc} */
  @Override
  public Clima consultarClima() {
    return clima;
  }
}
