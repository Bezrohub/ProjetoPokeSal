# PokeSal

Projeto acadêmico em Java que simula batalhas por turnos entre Pokésals. O cenário é o estacionamento da UCSal Pituaçu, onde o jogador enfrenta uma torre de adversários até chegar ao chefe do estádio.

## Como funciona

O jogador escolhe um dos seis iniciais disponíveis:

- BulbaSal
- CharSal
- SquirtSal
- ChikoSal
- CyndaSal
- TotoSal

Cada Pokésal possui vida (HP), ataque (ATK), defesa (DEF), velocidade (SPD) e um tipo elemental.

As batalhas consideram vantagens entre Fogo, Água e Planta, efeitos do terreno, status e uso de itens. Quem possui maior velocidade ataca primeiro. Em caso de empate, a ordem é sorteada.

## A torre

A torre possui cinco adversários controlados pela CPU. O último é o chefe do estádio.

A cada vitória, o jogador avança para a próxima batalha e recupera todo o HP. Ao vencer o quinto adversário, conclui a torre.

Se perder, a tentativa termina: o jogador retorna ao primeiro adversário com o HP completo e a mochila inicial.

## Mochila e recompensas

O jogador começa com **uma Potion e um Antidote**. A mochila comporta até **quatro itens**, mas só é permitido usar **dois por batalha**. Cada uso consome o turno.

Os itens restantes são mantidos entre as batalhas. Após vencer, o jogador recebe um item sorteado, desde que exista espaço na mochila:

| Item | Chance |
|---|---:|
| Potion | 45% |
| Antidote | 45% |
| Super Potion | 10% |

Em caso de derrota, os itens acumulados são perdidos e a mochila volta ao estoque inicial.

## Terrenos

| Terreno | Efeito |
|---|---|
| Asfalto quente | Aumenta em 15% o dano dos golpes de Fogo. |
| Poça de chuva | Concede um bônus de 10% aos golpes de Água, com aplicação em precisão ou dano ainda a definir. |
| Canteiro central | Recupera 5% do HP máximo dos Pokésals de Planta ao final de cada turno. |

## Tecnologias e ferramentas previstas

| Tecnologia ou ferramenta | Finalidade |
|---|---|
| Java | Implementação do jogo |
| JUnit | Testes automatizados |
| Checkstyle | Verificação do padrão de código |
| SonarQube | Análise de qualidade e métricas |
| Git e GitHub | Versionamento e entrega |

## Andamento

O projeto está na etapa de definição das regras e organização da modelagem. A implementação ainda não foi iniciada.

As próximas etapas incluem:

- Concluir as regras de dano, habilidades, itens e status.
- Elaborar os diagramas de casos de uso e de classes.
- Implementar as batalhas e a progressão da torre.
- Revisar o código e desenvolver os testes.
- Documentar os resultados da análise de qualidade.

## Como executar

As instruções de instalação e execução serão adicionadas quando a primeira versão estiver disponível.

## Equipe

- [José Luiz Saldanha Filho (https://github.com/Zelusalda)]
- [Maria Eduarda Rocha Queiroz Lima (https://github.com/Bezrohub)]
- [Matheus Samá de Souza Lima (https://github.com/Matheus-Sama)]
- [Matheus Ferreira Passos (https://github.com/matheusfpassos)]
