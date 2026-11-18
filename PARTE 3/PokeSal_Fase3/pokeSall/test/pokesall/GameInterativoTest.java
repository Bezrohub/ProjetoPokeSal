package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokesall.EntradaJogador;
import pokesall.Game;
import pokesall.GeradorAleatorio;
import pokesall.Jogador;
import pokesall.Npc;
import pokesall.RegistroBatalha;
import pokesall.RepositorioResultados;
import pokesall.ResultadoBatalha;
import pokesall.ServicoTerreno;
import pokesall.Torneio;
import pokesall.Treinador;
import pokesals.BulbaSall;
import pokesals.CharSall;
import pokesals.CyndaSall;
import pokesals.SquirtSall;

/** Representa GameInterativoTest no projeto PokeSal. */
@ExtendWith(MockitoExtension.class)
class GameInterativoTest {
  @Mock GeradorAleatorio gerador;
  @Mock ServicoTerreno terreno;
  @Mock RepositorioResultados repositorio;
  @Mock RegistroBatalha registro;
  @Mock ServicoSoneca servicoSoneca;
  @Mock ServicoDefesa servicoDefesa;
  @Mock EntradaJogador entrada;

  private Jogador jogador;
  private Npc npc;
  private Game game;

  @BeforeEach
  void preparar() {
    jogador = new Jogador("Você", new CharSall());
    npc = new Npc("Rival", new BulbaSall());
    game = new Game(jogador, gerador, terreno, repositorio, registro, entrada,
        servicoSoneca, servicoDefesa);
    game.setTerrenoAtual(Game.Terrenos.ASFALTO_QUENTE);
  }

  @Test
  void humanoEscolheAtaquesEnquantoNpcJogaAutomaticamente() {
    // Cada golpe do CharSal é crítico e o segundo derruba o BulbaSal.
    when(entrada.lerLinha()).thenReturn("1", "1", "1", "1");
    when(gerador.proximoDouble()).thenReturn(0.0);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);

    ResultadoBatalha resultado = game.disputar(jogador, npc);

    assertSame(jogador, resultado.getVencedor());
    assertEquals(2, resultado.getNumeroTurnos());
    verify(entrada, times(4)).lerLinha();
    verify(registro, atLeastOnce()).registrar(eq("ATAQUE"), contains("CharSall"));
    verify(registro, atLeastOnce()).registrar(eq("ATAQUE"), contains("BulbaSall"));
    verifyNoInteractions(repositorio); // quem salva é o Torneio
  }

  @Test
  void desistirEncerraBatalhaSemRodarNpc() {
    when(entrada.lerLinha()).thenReturn("3");

    ResultadoBatalha resultado = game.disputar(jogador, npc);

    assertSame(npc, resultado.getVencedor());
    assertEquals(1, resultado.getNumeroTurnos());
    verify(entrada, times(1)).lerLinha();
    verify(registro).registrar(eq("STATUS"), contains("desistiu"));
    verify(gerador, never()).proximoDouble();
  }

  @Test
  void usarItemGastaVezENpcAtacaDepois() {
    when(entrada.lerLinha()).thenReturn("2", "1", "3");
    ResultadoBatalha resultado = game.disputar(jogador, npc);

    assertSame(npc, resultado.getVencedor());
    assertEquals(1, jogador.getMochila().getItens().get(0).getQuantidade());
    verify(registro).registrar(eq("ITEM"), contains("Potion"));
    verify(registro, atLeastOnce()).registrar(eq("ATAQUE"), contains("BulbaSall"));
  }

  @Test
  void opcaoInvalidaNaoGastaVez() {
    when(entrada.lerLinha()).thenReturn("9", "1", "1", "3");
    ResultadoBatalha resultado = game.disputar(jogador, npc);

    assertSame(npc, resultado.getVencedor());
    verify(entrada, times(4)).lerLinha();
    verify(registro, atLeastOnce()).registrar(eq("ATAQUE"), contains("CharSall"));
  }

  @Test
  void confrontosEntreDoisNpcsNaoSolicitamTeclado() {
    Npc outroNpc = new Npc("Outro", new CharSall());
    when(gerador.proximoDouble()).thenReturn(0.0);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);

    ResultadoBatalha resultado = game.disputar(outroNpc, npc);

    assertNotNull(resultado.getVencedor());
    verifyNoInteractions(entrada);
  }

  @Test
  void semEntradaNaoPermiteBatalhaHumanaAutomatica() {
    Game semEntrada = new Game(jogador, gerador, terreno, repositorio, registro,
        servicoSoneca, servicoDefesa);
    assertThrows(IllegalStateException.class, () -> semEntrada.disputar(jogador, npc));
    verifyNoInteractions(entrada);
  }

  @Test
  void torneioContinuaEntreNpcsDepoisDaEliminacaoDoJogador() {
    when(entrada.lerLinha()).thenReturn("3");
    when(gerador.proximoDouble()).thenReturn(0.0);
    when(terreno.multiplicadorDano(any(), any())).thenReturn(1.0);
    Torneio torneio = new Torneio(game, repositorio, registro);

    Treinador campeao = torneio.realizar(List.of(jogador, npc,
        new Npc("Terceiro", new SquirtSall()),
        new Npc("Quarto", new CyndaSall())));

    assertNotSame(jogador, campeao);
    verify(entrada, times(1)).lerLinha();
    verify(repositorio, times(3)).salvar(any(ResultadoBatalha.class));
  }
}
