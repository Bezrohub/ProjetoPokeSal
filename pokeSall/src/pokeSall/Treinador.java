package pokeSall;

public abstract class Treinador {
	String nome;
	PokeSall pokeSall;
	Mochila mochila;
	
	public Treinador(String nome, PokeSall pokeSall, Mochila mochila) {
		this.nome = nome;
		this.pokeSall = pokeSall;
		this.mochila = mochila;
	}
}
