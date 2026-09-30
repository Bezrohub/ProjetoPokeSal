# Cobertura de Testes com JUnit 5

Os testes estão em `test/pokesall/GameTest.java`.

## Testes obrigatórios

1. `testVantagemElemental()` — valida multiplicadores 2.0, 1.0 e 0.5 entre Fogo, Água e Planta.
2. `testEfeitoTerrenoEstacionamentoUCSal()` — valida os três terrenos da mecânica do Estacionamento da UCSal:
   - `ASFALTO_QUENTE`: golpes de Fogo recebem +15% de dano;
   - `POCA_DE_CHUVA`: golpes de Água recebem +10% de dano;
   - `CANTEIRO_CENTRAL`: PokeSall de Planta recupera 5% do HP máximo ao final do turno.
3. `testOrdemDeAtaquePorVelocidade()` — valida que o maior SPD inicia a batalha.
4. `testUsoLimiteDeItensExcedido()` — valida o limite de 2 itens por batalha e o lançamento de `IllegalStateException` no terceiro uso.
5. `testCalculoDanoBoundaryValues()` — valida limites de HP, ATK e DEF, incluindo HP não negativo, ATK/DEF em zero e DEF máxima.

## Testes de requisitos autorais

6. `testPotionNaoUltrapassaHpMaximo()` — garante que Potion não ultrapasse o HP máximo.
7. `testSuperPotionRemoveStatusNegativo()` — garante que SuperPotion cure 50% do HP máximo e remova status negativo.

## Como executar no Eclipse

1. Importe/abra o projeto `pokeSall`.
2. Aguarde o Eclipse reconhecer a biblioteca **JUnit 5** configurada no `.classpath`.
3. Clique com o botão direito em `test/pokesall/GameTest.java`.
4. Escolha **Run As > JUnit Test**.
5. Os 7 testes devem aparecer em verde.

## Ajustes feitos no código para permitir testes

- Extração da lógica de multiplicador elemental para `calcularMultiplicadorElemental(...)`.
- Extração da iniciativa por SPD para `determinarPrioridade(...)`.
- Manutenção dos três terrenos originais: `ASFALTO_QUENTE`, `POCA_DE_CHUVA` e `CANTEIRO_CENTRAL`.
- Ajuste dos bônus conforme o requisito: +15% para Fogo no Asfalto Quente e +10% para Água na Poça de Chuva.
- Validação da cura de 5% do HP máximo para Planta no Canteiro Central.
- Correção da aplicação do bônus de terreno para que ele participe do cálculo do dano antes da redução do HP.
- Controle testável do limite de 2 itens com `registrarUsoDeItem()` e `IllegalStateException`.
- Cálculo de dano centralizado em `calcularDanoHabilidade(...)`.
- Proteção para o HP nunca ficar abaixo de zero ao receber dano direto.
