package pokesall;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Objects;

/** Calcula bonus de terreno usando o clima consultado e o horario injetado. */
public class ServicoTerrenoPadrao implements ServicoTerreno {
  private final Clock relogio;
  private final ServicoClima servicoClima;

  /**
   * Cria o servico com colaboradores externos injetados.
   *
   * @param relogio relogio substituivel para testes
   * @param servicoClima fornecedor do clima atual
   */
  public ServicoTerrenoPadrao(Clock relogio, ServicoClima servicoClima) {
    this.relogio = Objects.requireNonNull(relogio);
    this.servicoClima = Objects.requireNonNull(servicoClima);
  }

  /**
   * Informa se o horario local esta entre 6h e 18h.
   *
   * @return verdadeiro se for dia
   */
  private boolean ehDia() {
    int hora = LocalTime.now(relogio).getHour();
    return hora >= 6 && hora < 18;
  }

  /** {@inheritDoc} */
  @Override
  public double multiplicadorDano(Game.Terrenos terreno, Tipos tipo) {
    if (terreno == Game.Terrenos.ASFALTO_QUENTE && tipo == Tipos.FOGO
        && ehDia() && servicoClima.consultarClima() == Clima.ENSOLARADO) {
      return 1.15;
    }
    if (terreno == Game.Terrenos.POCA_DE_CHUVA && tipo == Tipos.AGUA
        && servicoClima.consultarClima() == Clima.CHUVOSO) {
      return 1.10;
    }
    return 1.0;
  }

  /** {@inheritDoc} */
  @Override
  public boolean permiteCura(Game.Terrenos terreno, Tipos tipo) {
    return terreno == Game.Terrenos.CANTEIRO_CENTRAL && tipo == Tipos.PLANTA;
  }
}
