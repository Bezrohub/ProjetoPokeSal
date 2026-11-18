package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ataques.Habilidades;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokesals.BulbaSall;
import pokesals.CharSall;

/** Testes isolados dos requisitos Soneca e Defesa da Fase 03. */
@ExtendWith(MockitoExtension.class)
class Fase03SonecaDefesaMockitoTest {

  @Mock private GeradorAleatorio gerador;
  @Mock private ServicoTerreno terreno;
  @Mock private RepositorioResultados repositorio;
  @Mock private RegistroBatalha registro;
  @Mock private EntradaJogador entrada;
  @Mock private ServicoSoneca servicoSoneca;
  @Mock private ServicoDefesa servicoDefesa;

  private Jogador jogador;
  private Npc adversario;
  private Game game;

  /** Cria colaboradores mockados para testar uma regra por vez. */
  @BeforeEach
  void preparar() {
    jogador = new Jogador("Humano", new CharSall());
    adversario = new Npc("Rival", new BulbaSall());
    game = new Game(jogador, gerador, terreno, repositorio, registro, entrada,
        servicoSoneca, servicoDefesa);
    game.setTerrenoAtual(Game.Terrenos.ASFALTO_QUENTE);
  }

  /** A soneca devolve 10% do HP maximo usando o servico mockado. */
  @Test
  void testSonecaRecuperaDezPorCentoDoHp() {
    jogador.getPokeSall().setHp(100);
    when(servicoSoneca.calcularRecuperacao(jogador.getPokeSall())).thenReturn(20.0);

    assertTrue(game.usarSoneca(jogador));
    assertEquals(120, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoSoneca, times(1)).calcularRecuperacao(jogador.getPokeSall());
    game.finalizarRegistroTurno();
    verify(registro).registrar(eq("STATUS"), contains("descansou"));
  }

  /** A soneca nao pode ultrapassar o HP maximo. */
  @Test
  void testSonecaRespeitaLimiteDeHp() {
    jogador.getPokeSall().setHp(195);
    when(servicoSoneca.calcularRecuperacao(jogador.getPokeSall())).thenReturn(20.0);

    assertTrue(game.usarSoneca(jogador));
    assertEquals(200, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoSoneca).calcularRecuperacao(jogador.getPokeSall());
  }

  /** Soneca com HP cheio nao executa o servico nem gasta uma acao. */
  @Test
  void testSonecaComHpCheioNaoAcionaServico() {
    assertFalse(game.usarSoneca(jogador));
    assertEquals(200, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoSoneca, never()).calcularRecuperacao(any());
  }

  /** Uma defesa protege o proximo dano em 10% usando o colaborador mockado. */
  @Test
  void testDefesaReduzDezPorCentoDoProximoGolpe() {
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double danoSemDefesa = game.calcularDanoHabilidade(60, adversario, jogador);
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8);
    when(servicoDefesa.reduzirDano(danoSemDefesa)).thenReturn(danoSemDefesa * 0.9);

    game.usarDefesa(jogador);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);

    assertEquals(200 - danoSemDefesa * 0.9, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoDefesa, times(1)).reduzirDano(danoSemDefesa);
  }

  /** A protecao dura um golpe e nao reduz um segundo acerto. */
  @Test
  void testDefesaEConsumidaAposUmGolpe() {
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double dano = game.calcularDanoHabilidade(60, adversario, jogador);
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8);
    when(servicoDefesa.reduzirDano(dano)).thenReturn(dano * 0.9);

    game.usarDefesa(jogador);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);

    assertEquals(200 - dano * 0.9 - dano, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoDefesa, times(1)).reduzirDano(dano);
  }

  /** Um golpe que erra nao consome a defesa para o proximo golpe valido. */
  @Test
  void testDefesaPermaneceQuandoGolpeErra() {
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double dano = game.calcularDanoHabilidade(60, adversario, jogador);
    when(gerador.proximoDouble()).thenReturn(0.99, 0.1, 0.8);
    when(servicoDefesa.reduzirDano(dano)).thenReturn(dano * 0.9);

    game.usarDefesa(jogador);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);
    assertEquals(200, jogador.getPokeSall().getHp(), 0.001);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);

    assertEquals(200 - dano * 0.9, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoDefesa, times(1)).reduzirDano(dano);
  }

  /** Dano de queimadura nao gasta a defesa destinada a um golpe direto. */
  @Test
  void testDefesaNaoEConsumidaPorQueimadura() {
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double dano = game.calcularDanoHabilidade(60, adversario, jogador);
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8);
    when(servicoDefesa.reduzirDano(dano)).thenReturn(dano * 0.9);

    game.usarDefesa(jogador);
    jogador.getPokeSall().setStatus(Status.QUEIMANDO);
    game.verificarStatus(new ArrayList<>(List.of(jogador)));
    assertEquals(170, jogador.getPokeSall().getHp(), 0.001);

    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);
    assertEquals(170 - dano * 0.9, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoDefesa, times(1)).reduzirDano(dano);
  }

  /** Defesa nao se acumula mesmo quando o jogador escolhe a acao duas vezes. */
  @Test
  void testDefesaNaoAcumula() {
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    double dano = game.calcularDanoHabilidade(60, adversario, jogador);
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8);
    when(servicoDefesa.reduzirDano(dano)).thenReturn(dano * 0.9);

    game.usarDefesa(jogador);
    game.usarDefesa(jogador);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, adversario, jogador);
    assertEquals(200 - dano * 0.9, jogador.getPokeSall().getHp(), 0.001);
    verify(servicoDefesa).reduzirDano(dano);
  }

  /** O jogador pode escolher Soneca no terminal quando perdeu HP. */
  @Test
  void testSonecaNoMenuGastaTurnoDepoisDeReceberDano() {
    when(entrada.lerLinha()).thenReturn("5", "4", "3");
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8, 0.1, 0.8);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    when(servicoDefesa.reduzirDano(anyDouble())).thenReturn(10.0);
    when(servicoSoneca.calcularRecuperacao(jogador.getPokeSall())).thenReturn(20.0);

    ResultadoBatalha resultado = game.disputar(jogador, adversario);

    assertSame(adversario, resultado.getVencedor());
    verify(entrada, times(3)).lerLinha();
    verify(servicoSoneca).calcularRecuperacao(jogador.getPokeSall());
    verify(registro).registrar(eq("STATUS"), contains("descansou"));
  }

  /** O jogador tem a opcao Defesa no menu e o NPC continua jogando sozinho. */
  @Test
  void testDefesaNoMenuConsomeTurnoEAdversarioAtaca() {
    when(entrada.lerLinha()).thenReturn("5", "3");
    when(gerador.proximoDouble()).thenReturn(0.1, 0.8);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    when(servicoDefesa.reduzirDano(anyDouble())).thenReturn(10.0);

    ResultadoBatalha resultado = game.disputar(jogador, adversario);

    assertSame(adversario, resultado.getVencedor());
    assertEquals(190, jogador.getPokeSall().getHp(), 0.001);
    verify(entrada, times(2)).lerLinha();
    verify(servicoDefesa).reduzirDano(anyDouble());
    verify(registro, atLeastOnce()).registrar(eq("STATUS"), contains("preparou defesa"));
    verifyNoInteractions(servicoSoneca);
  }
}
