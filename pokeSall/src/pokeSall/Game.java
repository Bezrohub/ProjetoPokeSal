package pokeSall;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import itens.Item;
import pokeSalls.BulbaSall;
import pokeSalls.CharSall;
import pokeSalls.SquirtSall;

public class Game {

	enum TERRENOS {
		ASFALTO_QUENTE, POCA_DE_CHUVA, CANTEIRO_CENTRAL
	}
	
	private Scanner ler = new Scanner(System.in);
	private int acumuloVeneno, turnosVeneno, turnosFogo, nivelTorre = 0, turno = 0;
	private Item item;
	
	ArrayList<Treinador> inimigos = new ArrayList<Treinador>();
	
	PokeSall pokeSall;
	Treinador jogador;
	Treinador npc;

	public Game(Treinador jogador){
		this.jogador = jogador;
	}

	public void criarInimigos(){
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

	public void rodarGame(){
		turno++;
		String opcao;

		System.out.println("==============================");
		System.out.println("Você está no nível " + nivelTorre);
		System.out.println("Enfrentará "+ inimigos.get(nivelTorre)+"!");
		System.out.println("==============================");
		System.out.println();
		System.out.println("PRESSIONE QUALQUER BOTÃO PARA CONTINUAR!");
		opcao = ler.nextLine();
		limparTela();
		System.out.println("------------------------------");
		System.out.println("BATALHA INICIADA!");
		System.out.println("------------------------------");
		System.out.println("======");
		System.out.println("TURNO "+ turno);
		System.out.println("======");

		System.out.println("-----------------------------");
		System.out.println("PokeSall: " +jogador.getPokeSall());
		System.out.println("1--ATAQUES-- 2--MOCHILA--");
		System.out.println("-----------------------------");

		System.out.println("==============================");
		System.out.println("Turno de: "+ jogador.getNome());
		System.out.println("==============================");

	}

	public void limparTela(){
		for(int l = 0; l < 20; l++){
			System.out.println();
		}
	}
}