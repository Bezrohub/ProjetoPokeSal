package pokesall;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ataques.Habilidades;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import pokesalls.BulbaSall;
import pokesalls.CharSall;
import pokesalls.SquirtSall;

/**
 * Testes automatizados das regras principais do PokeSall.
 */
public class GameTest {

  private static final double DELTA = 0.0001;

  /**
   * 1. Valida os multiplicadores de vantagem, neutralidade e desvantagem elemental.
   */
  @Test
  void testVantagemElemental() {
    Game game = criarGamePadrao();

    assertEquals(2.0, game.calcularMultiplicadorElemental(Tipos.FOGO, Tipos.PLANTA), DELTA);
    assertEquals(2.0, game.calcularMultiplicadorElemental(Tipos.AGUA, Tipos.FOGO), DELTA);
    assertEquals(2.0, game.calcularMultiplicadorElemental(Tipos.PLANTA, Tipos.AGUA), DELTA);

    assertEquals(1.0, game.calcularMultiplicadorElemental(Tipos.FOGO, Tipos.FOGO), DELTA);
    assertEquals(0.5, game.calcularMultiplicadorElemental(Tipos.FOGO, Tipos.AGUA), DELTA);
  }

  /**
   * 2. Valida os três efeitos da mecânica de terreno do Estacionamento da UCSal:
   * Asfalto Quente, Poça de Chuva e Canteiro Central.
   */
  @Test
  void testEfeitoTerrenoEstacionamentoUCSal() {
    assertAll(
        "Efeitos dos terrenos do Estacionamento da UCSal",

        // Asfalto Quente: golpes de Fogo causam 15% a mais de dano.
        () -> {
          Treinador atacanteFogo = new Jogador("Fogo", new CharSall());
          Treinador alvoPlanta = new Npc("Planta", new BulbaSall());
          Game game = new Game(atacanteFogo);
          game.setTerrenoAtual(Game.Terrenos.ASFALTO_QUENTE);

          double dano = game.calcularDanoHabilidade(75, atacanteFogo, alvoPlanta);

          // (75 + 30 ATK) * 2 (Fogo > Planta) * 1,15 * 100/(100+100 DEF)
          assertEquals(120.75, dano, DELTA);
        },

        // Poça de Chuva: golpes de Água causam 10% a mais de dano.
        () -> {
          Treinador atacanteAgua = new Jogador("Água", new SquirtSall());
          Treinador alvoFogo = new Npc("Fogo", new CharSall());
          Game game = new Game(atacanteAgua);
          game.setTerrenoAtual(Game.Terrenos.POCA_DE_CHUVA);

          double dano = game.calcularDanoHabilidade(65, atacanteAgua, alvoFogo);

          // (65 + 40 ATK) * 2 (Água > Fogo) * 1,10 * 100/(100+70 DEF)
          assertEquals(135.88235294117646, dano, DELTA);
        },

        // Canteiro Central: PokeSall de Planta recupera 5% do HP máximo no fim do turno.
        () -> {
          Treinador planta = new Jogador("Planta", new BulbaSall());
          Treinador fogo = new Npc("Fogo", new CharSall());
          Game game = new Game(planta);
          game.setTerrenoAtual(Game.Terrenos.CANTEIRO_CENTRAL);

          planta.getPokeSall().setHp(100.0);
          fogo.getPokeSall().setHp(100.0);

          ArrayList<Treinador> participantes = new ArrayList<>();
          participantes.add(planta);
          participantes.add(fogo);

          game.aplicarEfeitosTerreno(participantes);

          // BulbaSall possui 200 de HP máximo: 5% = 10 de cura.
          assertEquals(110.0, planta.getPokeSall().getHp(), DELTA);
          // O efeito não deve curar PokeSall que não seja do tipo Planta.
          assertEquals(100.0, fogo.getPokeSall().getHp(), DELTA);
        });
  }

  /**
   * 3. Valida que o PokeSall com maior SPD recebe a iniciativa da batalha.
   */
  @Test
  void testOrdemDeAtaquePorVelocidade() {
    Treinador rapido = new Jogador("Rápido", new SquirtSall()); // SPD 80
    Treinador lento = new Npc("Lento", new CharSall());        // SPD 75
    Game game = new Game(rapido);

    assertEquals(1, game.determinarPrioridade(rapido, lento));
    assertEquals(2, game.determinarPrioridade(lento, rapido));
  }

  /**
   * 4. Valida o limite de dois itens usáveis por batalha.
   */
  @Test
  void testUsoLimiteDeItensExcedido() {
    Treinador jogador = new Jogador("Jogador", new CharSall());
    Game game = new Game(jogador);
    game.resetarUsoDeItensNaBatalha();

    game.usarItemNaBatalha("Potion", jogador);
    game.usarItemNaBatalha("Antidote", jogador);

    assertThrows(
        IllegalStateException.class,
        () -> game.usarItemNaBatalha("Potion", jogador));
  }

  /**
   * 5. Valida valores-limite de HP, ATK e DEF no cálculo de dano.
   */
  @Test
  void testCalculoDanoBoundaryValues() {
    Treinador atacanteAtkZero = new Jogador(
        "ATK zero", new PokeSallTeste("AtkZero", 100, 0, 10, 0, Tipos.PLANTA));
    Treinador alvoDefZero = new Npc(
        "DEF zero", new PokeSallTeste("DefZero", 100, 0, 5, 0, Tipos.PLANTA));
    Game game = new Game(atacanteAtkZero);

    // Limite inferior de ATK e DEF: ATK = 0 e DEF = 0.
    assertEquals(60.0, game.calcularDanoHabilidade(60, atacanteAtkZero, alvoDefZero), DELTA);

    // Limite inferior de HP: o HP não pode ficar negativo.
    alvoDefZero.getPokeSall().setHp(1.0);
    game.calcularAtaque(Habilidades.FOLHA_BUZZER, atacanteAtkZero, alvoDefZero);
    assertEquals(0.0, alvoDefZero.getPokeSall().getHp(), DELTA);

    // Limite superior de DEF: o dano deve continuar não negativo e tender a zero.
    Treinador alvoDefMax = new Npc(
        "DEF máxima",
        new PokeSallTeste("DefMax", 100, Integer.MAX_VALUE, 5, 0, Tipos.PLANTA));
    double danoComDefMax = game.calcularDanoHabilidade(60, atacanteAtkZero, alvoDefMax);
    assertTrue(danoComDefMax >= 0.0);
    assertTrue(danoComDefMax < 0.001);
  }

  /**
   * Requisito autoral 1: Potion cura 30% do HP máximo, sem ultrapassar o limite de HP.
   */
  @Test
  void testPotionNaoUltrapassaHpMaximo() {
    Treinador jogador = new Jogador("Jogador", new CharSall());
    Game game = new Game(jogador);
    jogador.getPokeSall().setHp(190.0);

    game.usarPotion(jogador);

    assertEquals(jogador.getPokeSall().getHpBase(), jogador.getPokeSall().getHp(), DELTA);
  }

  /**
   * Requisito autoral 2: SuperPotion remove status negativo e restaura 50% do HP máximo.
   */
  @Test
  void testSuperPotionRemoveStatusNegativo() {
    Treinador jogador = new Jogador("Jogador", new CharSall());
    Game game = new Game(jogador);
    jogador.getPokeSall().setHp(80.0);
    jogador.getPokeSall().setStatus(Status.QUEIMANDO);

    game.usarSuperPotion(jogador);

    assertEquals(Status.NORMAL, jogador.getPokeSall().getStatus());
    assertEquals(180.0, jogador.getPokeSall().getHp(), DELTA);
  }

  private Game criarGamePadrao() {
    return new Game(new Jogador("Teste", new CharSall()));
  }

  /** PokeSall configurável usado apenas nos testes de valores-limite. */
  private static class PokeSallTeste extends PokeSall {
    PokeSallTeste(String nome, double hp, int def, int spd, double atk, Tipos tipo) {
      super(nome, hp, def, spd, atk, tipo);
    }
  }
}
