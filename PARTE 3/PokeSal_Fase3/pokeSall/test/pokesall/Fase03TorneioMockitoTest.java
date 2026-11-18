package pokesall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
import pokesals.CharSall;
import pokesals.SquirtSall;
import pokesals.TotoSall;

/** Testa persistencia, tolerancia a erros e chaveamento sem realizar lutas reais. */
@ExtendWith(MockitoExtension.class)
class Fase03TorneioMockitoTest {
  @Mock private Batalha batalha;
  @Mock private RepositorioResultados repositorio;
  @Mock private RegistroBatalha registro;
  @InjectMocks private Torneio torneio;

  private Treinador a;
  private Treinador b;
  private Treinador c;
  private Treinador d;

  /** Prepara participantes diferentes para cada batalha. */
  @BeforeEach
  void preparar() {
    a = new Npc("A", new CharSall());
    b = new Npc("B", new BulbaSall());
    c = new Npc("C", new SquirtSall());
    d = new Npc("D", new TotoSall());
  }

  /** ArgumentCaptor confere o objeto efetivamente enviado ao repositorio. */
  @Test
  void testResultadoSalvoAoFimDaBatalha() {
    when(batalha.disputar(any(Treinador.class), any(Treinador.class)))
        .thenAnswer(chamada -> new ResultadoBatalha(
            chamada.getArgument(0), chamada.getArgument(1), 4));
    torneio.realizar(List.of(a, b, c, d));

    ArgumentCaptor<ResultadoBatalha> captor = ArgumentCaptor.forClass(ResultadoBatalha.class);
    verify(repositorio, times(3)).salvar(captor.capture());
    ResultadoBatalha primeiroResultado = captor.getAllValues().get(0);
    assertSame(a, primeiroResultado.getVencedor());
    assertSame(b, primeiroResultado.getPerdedor());
    assertEquals(4, primeiroResultado.getNumeroTurnos());
    assertEquals("CharSall", primeiroResultado.getPokeSalVencedor());
    assertEquals("BulbaSall", primeiroResultado.getPokeSalPerdedor());
  }

  /** O erro na persistencia nao interfere na definicao do vencedor. */
  @Test
  void testFalhaNoRepositorioNaoInvalidaBatalha() {
    when(batalha.disputar(any(Treinador.class), any(Treinador.class)))
        .thenAnswer(chamada -> new ResultadoBatalha(
            chamada.getArgument(0), chamada.getArgument(1), 3));
    doThrow(new IllegalStateException("Servico indisponivel"))
        .when(repositorio).salvar(any(ResultadoBatalha.class));

    assertSame(a, torneio.realizar(List.of(a, b, c, d)));
    verify(repositorio, times(3)).salvar(any(ResultadoBatalha.class));
    verify(registro, times(3)).registrarErro(eq("Falha ao salvar a batalha"),
        any(IllegalStateException.class));
  }

  /** Retornos em sequencia comprovam que os vencedores avancam no chaveamento. */
  @Test
  void testChaveamentoTorneioAvancaVencedores() {
    when(batalha.disputar(any(Treinador.class), any(Treinador.class))).thenReturn(
        new ResultadoBatalha(b, a, 2),
        new ResultadoBatalha(d, c, 5),
        new ResultadoBatalha(d, b, 8));

    assertSame(d, torneio.realizar(List.of(a, b, c, d)));
    verify(batalha).disputar(a, b);
    verify(batalha).disputar(c, d);
    verify(batalha).disputar(b, d);
    verify(batalha, times(3)).disputar(any(Treinador.class), any(Treinador.class));
    assertSame(d, torneio.getRanking().get(0));
  }
}
