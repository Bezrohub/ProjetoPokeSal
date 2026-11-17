package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ataques.Habilidades;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokesall.Game;
import pokesall.GeradorAleatorio;
import pokesall.Jogador;
import pokesall.Npc;
import pokesall.RegistroBatalha;
import pokesall.RepositorioResultados;
import pokesall.ServicoTerreno;
import pokesall.Tipos;
import pokesall.Treinador;
import pokesals.BulbaSall;
import pokesals.CharSall;

/** Testa as regras de combate isolando gerador, terreno, repositorio e registro. */
@ExtendWith(MockitoExtension.class)
class Fase03GameMockitoTest {
  @Mock private GeradorAleatorio gerador;
  @Mock private ServicoTerreno servicoTerreno;
  @Mock private RepositorioResultados repositorio;
  @Mock private RegistroBatalha registro;

  private Game game;
  private Treinador jogador;
  private Treinador adversario;

  /** Prepara os dois treinadores reais e colaboradores simulados. */
  @BeforeEach
  void preparar() {
    jogador = new Jogador("Humano", new CharSall());
    adversario = new Npc("Adversario", new BulbaSall());
    game = new Game(jogador, gerador, servicoTerreno, repositorio, registro);
    game.setTerrenoAtual(Game.Terrenos.ASFALTO_QUENTE);
  }

  /** Confere a chance de critico de 6,25% e multiplicador de 1,5. */
  @Test
  void testAcertoCriticoComGeradorMockado() {
    when(gerador.proximoDouble()).thenReturn(0.10, 0.0624);
    when(servicoTerreno.multiplicadorDano(eq(Game.Terrenos.ASFALTO_QUENTE),
        eq(Tipos.FOGO))).thenReturn(1.0);
    double danoNormal = game.calcularDanoHabilidade(75, jogador, adversario);
    double hpInicial = adversario.getPokeSall().getHp();
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, adversario);
    assertEquals(hpInicial - danoNormal * 1.5, adversario.getPokeSall().getHp(), 0.001);
    verify(gerador, times(2)).proximoDouble();
  }

  /** Testa o erro de precisao e a ausencia de dano e de sorteio de critico. */
  @Test
  void testGolpeErraQuandoPrecisaoFalha() {
    when(gerador.proximoDouble()).thenReturn(0.99);
    double hpInicial = adversario.getPokeSall().getHp();
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, adversario);
    game.finalizarRegistroTurno();
    assertEquals(hpInicial, adversario.getPokeSall().getHp());
    verify(gerador, times(1)).proximoDouble();
    verify(registro).registrar(eq("DANO"), eq("Ataque errou: 0 de dano"));
  }

  /** Confere ataque, dano, status, terreno e item nesta ordem usando InOrder. */
  @Test
  void testSequenciaDeEventosNoTurno() {
    game.setTerrenoAtual(Game.Terrenos.CANTEIRO_CENTRAL);
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8, 0.1);
    when(servicoTerreno.multiplicadorDano(eq(Game.Terrenos.CANTEIRO_CENTRAL),
        eq(Tipos.FOGO))).thenReturn(1.0);
    when(servicoTerreno.permiteCura(eq(Game.Terrenos.CANTEIRO_CENTRAL),
        eq(Tipos.PLANTA))).thenReturn(true);
    adversario.getPokeSall().setHp(150);

    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, adversario);
    game.calcularAtaque(Habilidades.GAS, jogador, adversario);
    game.aplicarEfeitosTerreno(new ArrayList<>(List.of(adversario)));
    game.usarItemNaBatalha("Potion", jogador);
    game.finalizarRegistroTurno();

    InOrder ordem = inOrder(registro);
    ordem.verify(registro, times(2)).registrar(eq("ATAQUE"), anyString());
    ordem.verify(registro).registrar(eq("DANO"), anyString());
    ordem.verify(registro).registrar(eq("STATUS"), anyString());
    ordem.verify(registro).registrar(eq("TERRENO"), anyString());
    ordem.verify(registro).registrar(eq("ITEM"), anyString());
    ordem.verifyNoMoreInteractions();
  }

  /** Verifica que uma batalha sem vencedor ainda nao gera persistencia. */
  @Test
  void testRepositorioNaoChamadoSeBatalhaNaoTerminou() {
    when(gerador.proximoDouble()).thenReturn(0.99);
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, adversario);
    assertTrue(adversario.getPokeSall().getHp() > 0);
    verify(repositorio, never()).salvar(org.mockito.ArgumentMatchers.any());
    game.finalizarRegistroTurno();
    verify(registro, atLeastOnce()).registrar(eq("ATAQUE"), anyString());
  }
}
