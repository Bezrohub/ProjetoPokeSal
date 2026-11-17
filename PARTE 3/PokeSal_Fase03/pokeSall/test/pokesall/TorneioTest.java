package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pokesall.Batalha;
import pokesall.Npc;
import pokesall.RegistroBatalha;
import pokesall.RepositorioResultados;
import pokesall.ResultadoBatalha;
import pokesall.Torneio;
import pokesall.Treinador;
import pokesals.BulbaSall;

/** Representa TorneioTest no projeto PokeSal. */
@ExtendWith(MockitoExtension.class)
class TorneioTest {
  @Mock Batalha batalha;
  @Mock RepositorioResultados repositorio;
  @Mock RegistroBatalha registro;

  private Torneio torneio;
  private List<Treinador> participantes;

  @BeforeEach
  void preparar() {
    torneio = new Torneio(batalha, repositorio, registro);
    participantes = new ArrayList<>();
    for (int i = 0; i < 8; i++) {
      participantes.add(new Npc("Treinador " + i, new BulbaSall()));
    }
  }

  @Test
  void torneioQuatroParticipantesTemTresBatalhasERanking() {
    // Para simplificar, o primeiro da dupla sempre vence.
    when(batalha.disputar(any(), any())).thenAnswer(chamada ->
        new ResultadoBatalha(chamada.getArgument(0), chamada.getArgument(1), 4));
    Treinador campeao = torneio.realizar(participantes.subList(0, 4));
    assertSame(participantes.get(0), campeao);
    assertSame(campeao, torneio.getRanking().get(0));
    verify(batalha, times(3)).disputar(any(), any());
    verify(repositorio, times(3)).salvar(any(ResultadoBatalha.class));
    assertEquals(4, torneio.getRanking().size());
  }

  @Test
  void torneioOitoParticipantesTemSeteBatalhas() {
    when(batalha.disputar(any(), any())).thenAnswer(chamada ->
        new ResultadoBatalha(chamada.getArgument(0), chamada.getArgument(1), 2));
    assertSame(participantes.get(0), torneio.realizar(participantes));
    verify(batalha, times(7)).disputar(any(), any());
    verify(repositorio, times(7)).salvar(any(ResultadoBatalha.class));
  }

  @Test
  void erroNoRepositorioNaoInterrompeOTorneio() {
    when(batalha.disputar(any(), any())).thenAnswer(chamada ->
        new ResultadoBatalha(chamada.getArgument(0), chamada.getArgument(1), 7));
    doThrow(new IllegalStateException("Banco fora do ar"))
        .doNothing().doNothing()
        .when(repositorio).salvar(any(ResultadoBatalha.class));

    Treinador campeao = torneio.realizar(participantes.subList(0, 4));
    assertSame(participantes.get(0), campeao);
    verify(repositorio, times(3)).salvar(any(ResultadoBatalha.class));
    verify(registro).registrarErro(contains("Falha"), any(IllegalStateException.class));
  }

  @Test
  void naoAceitaQuantidadeErradaOuRepeticao() {
    assertThrows(IllegalArgumentException.class,
        () -> torneio.realizar(participantes.subList(0, 3)));
    assertThrows(IllegalArgumentException.class,
        () -> torneio.realizar(List.of(participantes.get(0), participantes.get(0),
            participantes.get(1), participantes.get(2))));
    verifyNoInteractions(batalha);
  }

  @Test
  void resultadoContemVencedorPerdedorTurnosEPokesal() {
    ResultadoBatalha r = new ResultadoBatalha(participantes.get(0), participantes.get(1), 5);
    assertSame(participantes.get(0), r.getVencedor());
    assertSame(participantes.get(1), r.getPerdedor());
    assertEquals(5, r.getNumeroTurnos());
    assertEquals("BulbaSall", r.getPokeSalVencedor());
    assertEquals("BulbaSall", r.getPokeSalPerdedor());
  }
}
