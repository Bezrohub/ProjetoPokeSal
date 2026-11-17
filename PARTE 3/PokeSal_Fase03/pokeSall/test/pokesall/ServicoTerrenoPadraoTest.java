package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pokesall.Clima;
import pokesall.Game;
import pokesall.ServicoClimaFixo;
import pokesall.ServicoTerrenoPadrao;
import pokesall.Tipos;

/** Representa ServicoTerrenoPadraoTest no projeto PokeSal. */
@ExtendWith(MockitoExtension.class)
class ServicoTerrenoPadraoTest {
  /**
   * Executa horario.
   *
   * @param iso valor de iso
   * @return resultado de horario
   */
  private Clock horario(String iso) {
    return Clock.fixed(Instant.parse(iso), ZoneId.of("America/Bahia"));
  }

  @Test
  void asfaltoQuenteSoFuncionaDeDiaEnsolarado() {
    ServicoTerrenoPadrao dia = new ServicoTerrenoPadrao(
        horario("2026-11-17T15:00:00Z"), new ServicoClimaFixo(Clima.ENSOLARADO));
    ServicoTerrenoPadrao noite = new ServicoTerrenoPadrao(
        horario("2026-11-18T02:00:00Z"), new ServicoClimaFixo(Clima.ENSOLARADO));
    ServicoTerrenoPadrao chovendo = new ServicoTerrenoPadrao(
        horario("2026-11-17T15:00:00Z"), new ServicoClimaFixo(Clima.CHUVOSO));
    assertEquals(1.15, dia.multiplicadorDano(Game.Terrenos.ASFALTO_QUENTE, Tipos.FOGO));
    assertEquals(1.0, noite.multiplicadorDano(Game.Terrenos.ASFALTO_QUENTE, Tipos.FOGO));
    assertEquals(1.0, chovendo.multiplicadorDano(Game.Terrenos.ASFALTO_QUENTE, Tipos.FOGO));
  }

  @Test
  void pocaDaBonusDezPorCentoQuandoChove() {
    ServicoTerrenoPadrao chuvoso = new ServicoTerrenoPadrao(
        horario("2026-11-17T15:00:00Z"), new ServicoClimaFixo(Clima.CHUVOSO));
    assertEquals(1.10, chuvoso.multiplicadorDano(Game.Terrenos.POCA_DE_CHUVA, Tipos.AGUA));
    assertEquals(1.0, chuvoso.multiplicadorDano(Game.Terrenos.POCA_DE_CHUVA, Tipos.FOGO));
  }

  @Test
  void canteiroCuraApenasPlanta() {
    ServicoTerrenoPadrao service = new ServicoTerrenoPadrao(
        horario("2026-11-17T15:00:00Z"), new ServicoClimaFixo(Clima.NUBLADO));
    assertTrue(service.permiteCura(Game.Terrenos.CANTEIRO_CENTRAL, Tipos.PLANTA));
    assertFalse(service.permiteCura(Game.Terrenos.CANTEIRO_CENTRAL, Tipos.FOGO));
  }
}
