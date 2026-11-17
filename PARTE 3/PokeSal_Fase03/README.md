# PokéSal — Fase 03 (torneio interativo)

Projeto Java 21 com combate por turnos, torre e torneio eliminatório de 4 ou 8
treinadores. O jogador escolhe ações quando chega sua vez; as batalhas entre
NPCs acontecem automaticamente.

## Executar

Com Java 21 e Maven instalados:

```bash
mvn clean verify
mvn -DskipTests compile
java -cp target/classes main.Main
```

Selecione **1 — Torre** ou **2 — Torneio** no menu. É possível iniciar somente
o torneio com `java -cp target/classes main.MainTorneio`.

Os resultados são salvos em memória (enquanto o programa está aberto). No
modo torneio, `Torneio` é responsável por chamar o repositório uma vez por
batalha, inclusive quando o jogador é eliminado.

## Requisitos da fase

- Precisão por habilidade, golpe que erra com dano zero e crítico de 6,25% ×1,5.
- Aleatoriedade fornecida por `GeradorAleatorio` injetado.
- Terreno consultado por `ServicoTerreno`, que usa `Clock` e `ServicoClima`.
- Torneio eliminatório, ranking, resultado detalhado e tolerância a falha de
  persistência com `RepositorioResultados` e `RegistroBatalha`.
- Registro dos eventos do turno em cinco categorias na sequência obrigatória.
- JUnit 5, Mockito, JaCoCo e Google Checkstyle configurados no `pom.xml`.

## Documentação

- [Diagrama de classes e dois diagramas de sequência](docs/DIAGRAMAS_UML.md)
- [Relatório de refatoração / SOLID](docs/RELATORIO_REFATORACAO.md)
- [Plano de cobertura e estilo](docs/COBERTURA_E_ESTILO.md)
- [Campos para os requisitos autorais da equipe](docs/REQUISITOS_AUTORAIS_A_PREENCHER.md)

**Importante:** o enunciado proíbe o uso de IA na concepção dos dois requisitos
autorais. Eles não foram criados aqui, e os quatro testes correspondentes
precisam ser implementados pela equipe após sua definição.

**Verificação deste pacote:** código principal compilado com `javac` em Java 21
e simulação de torneio executada. Maven, dependências de teste e JaCoCo não
estavam disponíveis neste ambiente; não se afirma que os testes ou as metas de
cobertura passaram.
