package pokeSall;

public abstract class Treinador {
	private String nome;
	private PokeSall pokeSall;
	private Mochila mochila;
	
	public Treinador(String nome, PokeSall pokeSall, Mochila mochila) {
		this.nome = nome;
		this.pokeSall = pokeSall;
		this.mochila = mochila;
	}

	public Treinador(String nome, PokeSall pokeSall){
		this.nome = nome;
		this.pokeSall = pokeSall;
		this.mochila = new Mochila();
	}

	public void setNome(String nome){
		this.nome = nome;
	}

	public String getNome(){
		return nome;
	}

	public void setPokeSall(PokeSall pokeSall){
		this.pokeSall = pokeSall;
	}

	public String getPokeSall(){
		return ""+this.pokeSall.getNome();
	}
	public void setMochila(Mochila mochila){
		this.mochila = mochila;
	}
	public String getMochila(){
		return ""+this.mochila;
	}
}
