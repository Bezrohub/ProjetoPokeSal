package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokesall.Clima;
import pokesall.Game;
import pokesall.ServicoClima;
import pokesall.ServicoTerrenoPadrao;
import pokesall.Tipos;

/** Testa as consultas de clima e horario substituiveis com Mockito. */
@ExtendWith(MockitoExtension.class)
class TerrenoMockitoTest {
  @Mock private Clock relogio;
  @Mock private ServicoClima servicoClima;
  @InjectMocks private ServicoTerrenoPadrao servico;

  /** Mocka duas chamadas sucessivas ao relogio: dia e noite. */
  @Test
  void testAsfaltoQuenteApenasDuranteODia() {
    when(relogio.getZone()).thenReturn(ZoneId.of("America/Bahia"));
    when(relogio.instant()).thenReturn(
        Instant.parse("2026-11-17T15:00:00Z"),
        Instant.parse("2026-11-18T02:00:00Z"));
    when(servicoClima.consultarClima()).thenReturn(Clima.ENSOLARADO);

    assertEquals(1.15, servico.multiplicadorDano(
        Game.Terrenos.ASFALTO_QUENTE, Tipos.FOGO));
    assertEquals(1.0, servico.multiplicadorDano(
        Game.Terrenos.ASFALTO_QUENTE, Tipos.FOGO));
    verify(relogio, times(2)).instant();
    verify(servicoClima, times(1)).consultarClima();
  }
}
