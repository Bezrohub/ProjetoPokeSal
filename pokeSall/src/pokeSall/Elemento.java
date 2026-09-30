package pokeSall;

public abstract class Elemento {
	
	public enum Tipos {
		PLANTA,
		FOGO,
		AGUA
	}
	
	public Tipos TIPO;
	public Tipos VANTAGEM;
	public Tipos DESVANTAGEM;
	
  public Elemento(Tipos TIPO, Tipos VANTAGEM, Tipos DESVANTAGEM) {
		this.TIPO = TIPO;
		this.VANTAGEM = VANTAGEM;
		this.DESVANTAGEM = DESVANTAGEM;
	}
}
