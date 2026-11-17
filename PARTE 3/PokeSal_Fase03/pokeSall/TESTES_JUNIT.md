# Testes do PokéSal — Fases 01, 02 e 03

A pasta `pokeSall/test/pokesall` contém **37 métodos anotados com `@Test`**
distribuídos em sete arquivos:

| Arquivo | Testes | Principal foco |
|---|---:|---|
| `GameTest.java` | 14 | Regras antigas, dano, itens, eventos |
| `GameInterativoTest.java` | 7 | Jogador humano e NPCs |
| `TorneioTest.java` | 5 | Chaveamento, ranking e falha de repositório |
| `ServicoTerrenoPadraoTest.java` | 3 | Terreno com relógio fixo |
| `Fase03GameMockitoTest.java` | 4 | Crítico, precisão, InOrder e never() |
| `Fase03TorneioMockitoTest.java` | 3 | ArgumentCaptor, falha, sequência de vencedores |
| `Fase03TerrenoMockitoTest.java` | 1 | Clock e clima mockados |

## Oito testes obrigatórios do enunciado

1. `testAcertoCriticoComGeradorMockado()`
2. `testGolpeErraQuandoPrecisaoFalha()`
3. `testAsfaltoQuenteApenasDuranteODia()`
4. `testSequenciaDeEventosNoTurno()`
5. `testResultadoSalvoAoFimDaBatalha()`
6. `testFalhaNoRepositorioNaoInvalidaBatalha()`
7. `testRepositorioNaoChamadoSeBatalhaNaoTerminou()`
8. `testChaveamentoTorneioAvancaVencedores()`

Os testes foram implementados com JUnit 5 e Mockito. **Os 37 métodos são
código de teste, e não 37 resultados de execução.** Sem Maven e bibliotecas de
teste não foi possível executá-los neste ambiente. A sintaxe dos arquivos foi
compilada com interfaces auxiliares de verificação, que não executam o Mockito.

### Executar com o ambiente completo

```bash
mvn clean verify
```

O comando executa JUnit 5/Mockito e gera o JaCoCo; também executa o Google
Checkstyle. Os resultados reais, inclusive percentuais de cobertura, estarão
em `target/surefire-reports/` e `target/site/jacoco/index.html`.

**Faltam:** os quatro testes específicos dos dois requisitos autorais a serem
concebidos pela própria equipe, conforme proibição de IA no enunciado.
