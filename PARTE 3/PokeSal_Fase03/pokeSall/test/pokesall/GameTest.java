package pokesall;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import ataques.Habilidades;
import java.util.ArrayList;
import java.util.Arrays;
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
import pokesall.Status;
import pokesall.Tipos;
import pokesall.Treinador;
import pokesals.BulbaSall;
import pokesals.CharSall;
import pokesals.SquirtSall;

/** Representa GameTest no projeto PokeSal. */
@ExtendWith(MockitoExtension.class)
class GameTest {
  @Mock GeradorAleatorio gerador;
  @Mock ServicoTerreno terreno;
  @Mock RepositorioResultados repositorio;
  @Mock RegistroBatalha registro;

  private Game game;
  private Treinador jogador;
  private Treinador inimigo;

  @BeforeEach
  void preparar() {
    jogador = new Jogador("Jogador", new CharSall());
    inimigo = new Npc("Inimigo", new BulbaSall());
    game = new Game(jogador, gerador, terreno, repositorio, registro);
  }

  // ---- Preservação dos requisitos da Fase 02 ----
  @Test
  void testVantagemElemental() {
    assertEquals(2.0, game.calcularMultiplicadorElemental(Tipos.FOGO, Tipos.PLANTA));
    assertEquals(0.5, game.calcularMultiplicadorElemental(Tipos.FOGO, Tipos.AGUA));
    assertEquals(1.0, game.calcularMultiplicadorElemental(Tipos.FOGO, Tipos.FOGO));
  }

  @Test
  void testEfeitoTerrenoEstacionamentoUCSal() {
    game.setTerrenoAtual(Game.Terrenos.ASFALTO_QUENTE);
    when(terreno.multiplicadorDano(Game.Terrenos.ASFALTO_QUENTE, Tipos.FOGO))
        .thenReturn(1.15);
    double comBonus = game.calcularDanoHabilidade(75, jogador, inimigo);
    assertEquals((75 + 30) * 2.0 * 1.15 * 100 / 200, comBonus, 0.001);

    game.setTerrenoAtual(Game.Terrenos.POCA_DE_CHUVA);
    Treinador agua = new Jogador("Água", new SquirtSall());
    when(terreno.multiplicadorDano(Game.Terrenos.POCA_DE_CHUVA, Tipos.AGUA))
        .thenReturn(1.10);
    assertTrue(game.calcularDanoHabilidade(65, agua, jogador) > 0);

    game.setTerrenoAtual(Game.Terrenos.CANTEIRO_CENTRAL);
    inimigo.getPokeSall().setHp(190);
    when(terreno.permiteCura(Game.Terrenos.CANTEIRO_CENTRAL, Tipos.PLANTA))
        .thenReturn(true);
    game.aplicarEfeitosTerreno(new ArrayList<>(Arrays.asList(inimigo)));
    assertEquals(200, inimigo.getPokeSall().getHp(), 0.001);
  }

  @Test
  void testOrdemDeAtaquePorVelocidade() {
    assertEquals(1, game.determinarPrioridade(jogador, inimigo));
    assertEquals(2, game.determinarPrioridade(inimigo, jogador));
    Treinador outro = new Jogador("Outro", new CharSall());
    when(gerador.proximoInt(2)).thenReturn(1);
    assertEquals(2, game.determinarPrioridade(jogador, outro));
    verify(gerador).proximoInt(2);
  }

  @Test
  void testUsoLimiteDeItensExcedido() {
    game.registrarUsoDeItem();
    game.registrarUsoDeItem();
    assertThrows(IllegalStateException.class, game::registrarUsoDeItem);
    game.resetarUsoDeItensNaBatalha();
    assertDoesNotThrow(game::registrarUsoDeItem);
  }

  @Test
  void testCalculoDanoBoundaryValues() {
    game.setTerrenoAtual(Game.Terrenos.ASFALTO_QUENTE);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    assertTrue(game.calcularDanoHabilidade(0, jogador, inimigo) >= 0);
    when(gerador.proximoDouble()).thenReturn(0.0, 0.99);
    inimigo.getPokeSall().setHp(1);
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, inimigo);
    assertEquals(0, inimigo.getPokeSall().getHp(), 0.001);
  }

  @Test
  void testPotionNaoUltrapassaHpMaximo() {
    jogador.getPokeSall().setHp(195);
    game.usarPotion(jogador);
    assertEquals(200, jogador.getPokeSall().getHp(), 0.001);
  }

  @Test
  void testSuperPotionRemoveStatusNegativo() {
    jogador.getPokeSall().setHp(50);
    jogador.getPokeSall().setStatus(Status.ENVENENADO);
    game.usarSuperPotion(jogador);
    assertEquals(150, jogador.getPokeSall().getHp(), 0.001);
    assertEquals(Status.NORMAL, jogador.getPokeSall().getStatus());
  }

  // ---- Requisitos novos de Fase 03 ----
  @Test
  void golpeQueErraCausaDanoZero() {
    when(gerador.proximoDouble()).thenReturn(0.99); // Hell Beam: 80%
    double vida = inimigo.getPokeSall().getHp();
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, inimigo);
    assertEquals(vida, inimigo.getPokeSall().getHp(), 0.001);
    verify(gerador, times(1)).proximoDouble(); // sem sorteio de crítico
    game.finalizarRegistroTurno();
    verify(registro).registrar(eq("DANO"), contains("0"));
  }

  @Test
  void golpeQueErraNaoAplicaStatus() {
    when(gerador.proximoDouble()).thenReturn(0.95); // Queimar: 90%
    game.calcularAtaque(Habilidades.QUEIMAR, jogador, inimigo);
    assertEquals(Status.NORMAL, inimigo.getPokeSall().getStatus());
  }

  @Test
  void acertoCriticoMultiplicaDanoPorUmEVirgulaCinco() {
    when(gerador.proximoDouble()).thenReturn(0.1, 0.05);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double esperado = game.calcularDanoHabilidade(75, jogador, inimigo) * 1.5;
    double hpInicial = inimigo.getPokeSall().getHp();
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, inimigo);
    assertEquals(hpInicial - esperado, inimigo.getPokeSall().getHp(), 0.001);
  }

  @Test
  void naoCriticoMantemDanoNormal() {
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double esperado = game.calcularDanoHabilidade(75, jogador, inimigo);
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, inimigo);
    assertEquals(200 - esperado, inimigo.getPokeSall().getHp(), 0.001);
  }

  @Test
  void eventosDoTurnoSaemNaOrdemExigida() {
    game.setTerrenoAtual(Game.Terrenos.CANTEIRO_CENTRAL);
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8, 0.1);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    when(terreno.permiteCura(Game.Terrenos.CANTEIRO_CENTRAL, Tipos.PLANTA))
        .thenReturn(true);
    inimigo.getPokeSall().setHp(150);
    game.calcularAtaque(Habilidades.HELL_BEAM, jogador, inimigo); // dano
    game.calcularAtaque(Habilidades.GAS, jogador, inimigo); // status
    game.aplicarEfeitosTerreno(new ArrayList<>(Arrays.asList(inimigo))); // terreno
    game.usarItemNaBatalha("Potion", jogador); // item
    game.finalizarRegistroTurno();

    InOrder ordem = inOrder(registro);
    ordem.verify(registro, times(2)).registrar(eq("ATAQUE"), anyString());
    ordem.verify(registro).registrar(eq("DANO"), anyString());
    ordem.verify(registro).registrar(eq("STATUS"), anyString());
    ordem.verify(registro).registrar(eq("TERRENO"), anyString());
    ordem.verify(registro).registrar(eq("ITEM"), anyString());
    ordem.verifyNoMoreInteractions();
  }

  @Test
  void itemIndisponivelNaoConsomeLimite() {
    assertThrows(IllegalArgumentException.class,
        () -> game.usarItemNaBatalha("Inventado", jogador));
    game.registrarUsoDeItem();
    game.registrarUsoDeItem();
    assertThrows(IllegalStateException.class, game::registrarUsoDeItem);
  }

  @Test
  void precisaoDeCadaHabilidadeEstaEntreZeroECem() {
    for (Habilidades habilidade : Habilidades.values()) {
      assertTrue(habilidade.getPrecisao() >= 0 && habilidade.getPrecisao() <= 100);
    }
  }
}
