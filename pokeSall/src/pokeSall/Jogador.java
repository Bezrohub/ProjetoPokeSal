package pokeSall;

public abstract class Jogador {
	String nome;
	PokeSall pokeSall;
	Mochila mochila;
	
	public Jogador(String nome, PokeSall pokeSall, Mochila mochila) {
		this.nome = nome;
		this.pokeSall = pokeSall;
		this.mochila = mochila;
	}
}
