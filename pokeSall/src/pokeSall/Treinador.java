package pokeSall;

public abstract class Treinador {
	private String nome;
	private PokeSall pokeSall;
	private Mochila mochila;
	private boolean npc;
	
	public Treinador(String nome, PokeSall pokeSall, boolean npc, Mochila mochila) {
		this.nome = nome;
		this.pokeSall = pokeSall;
		this.mochila = mochila;
	}

	public Treinador(String nome, PokeSall pokeSall, boolean npc){
		this.npc = npc;
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
	
	public PokeSall getPokeSall() {
		return this.pokeSall;
	}
	public String getNomePokeSall(){
		return ""+this.pokeSall.getNome();
	}
	public void setMochila(Mochila mochila){
		this.mochila = mochila;
	}
	public Mochila getMochila(){
		return this.mochila;
	}
	public void getListarMochila() {
	  this.mochila.listarMochila();
	}
	public boolean isNpc() {
		return this.npc;
	}
}
