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
	
	private int acumuloVeneno, turnosVeneno, turnosFogo;
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
		npc = new Npc("Ashely", pokeSall);
		inimigos.add(npc);

		pokeSall = new BulbaSall();
		npc = new Npc("Joe Lois", pokeSall);
		inimigos.add(npc);

		pokeSall = new SquirtSall();
		npc = new Npc("Mary", pokeSall);
		inimigos.add(npc);
	}
}