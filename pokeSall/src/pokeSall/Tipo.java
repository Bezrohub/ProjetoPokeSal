package pokeSall;

public abstract class Tipo {
	
	public enum Tipos {
		PLANTA,
		FOGO,
		AGUA
	}
	
	public Tipos TIPO;
	public Tipos VANTAGEM;
	public Tipos DESVANTAGEM;
	
	public Tipo(Tipos TIPO, Tipos VANTAGEM, Tipos DESVANTAGEM) {
		this.TIPO = TIPO;
		this.VANTAGEM = VANTAGEM;
		this.DESVANTAGEM = DESVANTAGEM;
	}
}
