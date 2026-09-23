package pokeSall;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import itens.Item;
import pokeSalls.BulbaSall;
import pokeSalls.CharSall;
import pokeSalls.SquirtSall;
import ataques.Habilidades;

public class Game {

	enum TERRENOS {
		ASFALTO_QUENTE, POCA_DE_CHUVA, CANTEIRO_CENTRAL
	}

	private Random rand = new Random();
	private Scanner ler = new Scanner(System.in);
	private int acumuloVenenoJ, acumuloVenenoI, turnosVeneno, turnosFogoJ, turnosFogoI, nivelTorre = 0, turno = 0;
	private double dano;
	private double vida;
	private double cura;
	private int acao = 0;
	private static final double escalaDefesa = 100;

	ArrayList<Treinador> inimigos = new ArrayList<Treinador>();
	ArrayList<Treinador> players = new ArrayList<Treinador>();
	PokeSall pokeSall;
	Treinador jogador;
	Treinador npc;

	public Game(Treinador jogador) {
		this.jogador = jogador;
		criarInimigos();
	}

	public void criarInimigos() {
		pokeSall = new CharSall();
		npc = new Npc("Ashley", pokeSall);
		inimigos.add(npc);

		pokeSall = new BulbaSall();
		npc = new Npc("Joe Lois", pokeSall);
		inimigos.add(npc);

		pokeSall = new SquirtSall();
		npc = new Npc("Mary", pokeSall);
		inimigos.add(npc);
	}

	public void rodarGame() {
		String opcao;
		boolean rodando = true;

		while (rodando && nivelTorre < inimigos.size()) {
			Treinador inimigoAtual = inimigos.get(nivelTorre);

			players.clear();
			players.add(jogador);
			players.add(inimigoAtual);

			turno = 0;
			boolean batalhando = true;

			System.out.println("==============================");
			System.out.println("Você está no nível " + (nivelTorre + 1));
			System.out.println("Enfrentará " + inimigoAtual.getNome() + "!");
			System.out.println("==============================");
			System.out.println();
			System.out.println("PRESSIONE ENTER PARA CONTINUAR!");
			ler.nextLine();
			limparTela();
			System.out.println("------------------------------");
			System.out.println("      BATALHA INICIADA!");
			System.out.println("------------------------------");

			while (batalhando) {
				turno++;
				System.out.println();
				System.out.println("------");
				System.out.println("TURNO " + turno);
				System.out.println("------");
				System.out.println();
				System.out.println("==============================");
				System.out.println("Turno de: " + jogador.getNome());
				System.out.println("==============================");

				do {
					System.out.println("-----------------------------");
					System.out.println("PokeSall: " + jogador.getNomePokeSall());
					System.out.println("HP: " + jogador.getPokeSall().getHP());
					System.out.println("1--ATAQUES-- 2--MOCHILA-- 3--DESISTIR");
					System.out.println("-----------------------------");
					opcao = ler.nextLine();
				} while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")));

				switch (opcao) {
				case "1":
					do {
						jogador.getPokeSall().listarHabilidades();
						System.out.println("-----------------------------");
						System.out.println("1- Para usar o primeiro ataque");
						System.out.println("2- Para usar o segundo ataque");
						System.out.println("3- Para voltar");
						opcao = ler.nextLine();
					} while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3")));

					if (opcao.equals("1")) {
						calcularAtaque(Habilidades.FOLHA_BUZZER, jogador, inimigoAtual);
						vida = inimigoAtual.getPokeSall().getHP();
						System.out.println("Deu " + dano + " de dano!");
						inimigoAtual.getPokeSall().setHP(vida - dano);
					} else if (opcao.equals("2")) {
						calcularAtaque(Habilidades.GAS, jogador, inimigoAtual);
						System.out.println(inimigoAtual.getNomePokeSall() + " foi envenenado!");
					}
					break;

				case "2":
					do {
						int temp = 0;
						System.out.println("-----------------------------");
						for (Item item : jogador.getMochila().itens) {
							if (item.getQuantidade() > 0) {
								temp++;
								System.out.println("Digite " + temp + " para usar " + item.getNome() + ": "
										+ item.getQuantidade());
								System.out.println(item.getDescricao());
								System.out.println("-----------------------------");
							}
						}
						System.out.println("-----------------------------");
						System.out.println("0- Para voltar");
						opcao = ler.nextLine();
					} while (!(opcao.equals("1") || opcao.equals("2") || opcao.equals("3") || opcao.equals("0")));

					if (opcao.equals("1")) {
						usarPotion(jogador);
					} else if (opcao.equals("2")) {
						usarSuperPotion(jogador);
					} else if (opcao.equals("3")) {
						usarAntidote(jogador);
					}
					break;

				case "3":
					batalhando = false;
					rodando = false;
					System.out.println("Você fugiu da batalha!");
					break;
				}

				// Verifica se inimigo morreu antes de ele atacar
				int resultado = verificarVitoria(jogador, inimigoAtual);
				if (resultado != 0) {
					batalhando = false;
					if (resultado == 1)
						nivelTorre++;
					if (resultado == 2)
						rodando = false;
					continue;
				}

				if (rodando && batalhando) {
					dano = 0;
					System.out.println();
					System.out.println("Turno de " + inimigoAtual.getNome());
					System.out.println("");

					int temp = rand.nextInt(1, 3);

					switch (temp) {
					case 1:
						calcularAtaque(Habilidades.HELL_BEAM, inimigoAtual, jogador);
						vida = jogador.getPokeSall().getHP();
						System.out.println(inimigoAtual.getNomePokeSall() + " deu " + dano + " de dano!");
						jogador.getPokeSall().setHP(vida - dano);
						break;

					case 2:
						System.out.println(inimigoAtual.getNomePokeSall() + " usou Queimar!");
						calcularAtaque(Habilidades.QUEIMAR, inimigoAtual, jogador);
						break;
					}

					verificarStatus(players);

					resultado = verificarVitoria(jogador, inimigoAtual);
					if (resultado == 1) { // Jogador Venceu
						batalhando = false;
						nivelTorre++;
					} else if (resultado == 2) { // Jogador Perdeu
						batalhando = false;
						rodando = false;
					}
				}
			}
		}

		if (nivelTorre >= inimigos.size() && jogador.getPokeSall().getHP() > 0) {
			System.out.println("Parabéns! Você derrotou todos os inimigos da torre!");
		}
	}

	public void limparTela() {
		for (int l = 0; l < 20; l++) {
			System.out.println();
		}
	}

	public void calcularAtaque(Habilidades habilidade, Treinador atacante, Treinador alvo) {
		if (habilidade == Habilidades.FOLHA_BUZZER) {
			dano = calcularDefesa(60 + atacante.getPokeSall().getATK(), alvo);
		}
		if (habilidade == Habilidades.GAS) {
			if (alvo.isNpc()) {
				if (acumuloVenenoI < 3)
					acumuloVenenoI++;
			} else {
				if (acumuloVenenoJ < 3)
					acumuloVenenoJ++;
			}
			alvo.getPokeSall().setStatus(Status.ENVENENADO);
		}
		if (habilidade == Habilidades.HELL_BEAM) {
			dano = calcularDefesa(75 + atacante.getPokeSall().getATK(), alvo);
		}
		if (habilidade == Habilidades.QUEIMAR) {
			if (alvo.isNpc()) {
				turnosFogoI = 3;
			} else {
				turnosFogoJ = 3;
			}
			alvo.getPokeSall().setStatus(Status.QUEIMANDO);
		}
		if (habilidade == Habilidades.AGUA_TERMAL) {
			alvo.getPokeSall().setStatus(Status.PARALIZADO);
		}
		if (habilidade == Habilidades.BEAT_BOLHA) {
			dano = calcularDefesa(65 + atacante.getPokeSall().getATK(), alvo);
		}
	}

	public double calcularDefesa(double dano, Treinador alvo) {
		return dano * (escalaDefesa / (escalaDefesa + alvo.getPokeSall().getDEF()));
	}

	public void verificarStatus(ArrayList<Treinador> players) {
		for (Treinador treinador : players) {
			if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
				System.out.println("O " + treinador.getPokeSall().getNome() + " está envenenado!");
			}
			if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
				System.out.println("O " + treinador.getPokeSall().getNome() + " está queimando!");
			}
			if (treinador.getPokeSall().getStatus() == Status.PARALIZADO) {
				System.out.println("O " + treinador.getPokeSall().getNome() + " está paralizado!");
			}
		}
		calcularDanoContinuo(players);
	}

	public void calcularDanoContinuo(ArrayList<Treinador> players) {
		for (Treinador treinador : players) {
			double vidaAtual = treinador.getPokeSall().getHP();

			if (treinador.getPokeSall().getStatus() == Status.ENVENENADO) {
				if (treinador.isNpc()) {
					dano = (acumuloVenenoI * 0.1) * treinador.getPokeSall().getHPbase();
				} else {
					dano = (acumuloVenenoJ * 0.1) * treinador.getPokeSall().getHPbase();
				}
				treinador.getPokeSall().setHP(vidaAtual - dano);
				System.out.println(treinador.getPokeSall().getNome() + " tomou " + dano + " de veneno!");
				vidaAtual = treinador.getPokeSall().getHP();
			}

			if (treinador.getPokeSall().getStatus() == Status.QUEIMANDO) {
				if (treinador.isNpc()) {
					turnosFogoI--;
				} else {
					turnosFogoJ--;
				}

				dano = 30;
				treinador.getPokeSall().setHP(vidaAtual - dano);
				System.out.println(treinador.getPokeSall().getNome() + " tomou " + dano + " de queimadura!");

				if (turnosFogoI <= 0 && treinador.isNpc() || turnosFogoJ <= 0 && !treinador.isNpc()) {
					treinador.getPokeSall().setStatus(Status.NORMAL);
					System.out.println(treinador.getPokeSall().getNome() + " não está mais queimando!");
				}
			}
		}
	}

	public void usarPotion(Treinador treinador) {
		double vidaAtual = treinador.getPokeSall().getHP();
		double vidaBase = treinador.getPokeSall().getHPbase();
		treinador.getMochila().consumirItem("Potion");
		cura = 0.3 * vidaBase;

		if ((vidaAtual + cura) > vidaBase) {
			treinador.getPokeSall().setHP(vidaBase);
		} else {
			treinador.getPokeSall().setHP(cura + vidaAtual);
		}
	}

	public void usarSuperPotion(Treinador treinador) {
		double vidaAtual = treinador.getPokeSall().getHP();
		double vidaBase = treinador.getPokeSall().getHPbase();
		treinador.getMochila().consumirItem("SuperPotion");
		cura = 0.5 * vidaBase;

		if ((vidaAtual + cura) > vidaBase) {
			treinador.getPokeSall().setHP(vidaBase);
		} else {
			treinador.getPokeSall().setHP(cura + vidaAtual);
		}
	}

	public void usarAntidote(Treinador treinador) {
		double vidaAtual = treinador.getPokeSall().getHP();
		double vidaBase = treinador.getPokeSall().getHPbase();
		treinador.getMochila().consumirItem("Antidote");
		cura = 0.5 * vidaBase;
		treinador.getPokeSall().setStatus(Status.NORMAL);

		if ((vidaAtual + cura) > vidaBase) {
			treinador.getPokeSall().setHP(vidaBase);
		} else {
			treinador.getPokeSall().setHP(cura + vidaAtual);
		}
	}

	public int verificarVitoria(Treinador jogador, Treinador inimigo) {
		if (jogador.getPokeSall().getHP() <= 0) {
			System.out.println("O jogador " + jogador.getNome() + " perdeu a batalha!");
			return 2;
		}
		if (inimigo.getPokeSall().getHP() <= 0) {
			System.out.println("O jogador " + jogador.getNome() + " venceu a batalha!");
			return 1;
		}
		return 0;
	}
}