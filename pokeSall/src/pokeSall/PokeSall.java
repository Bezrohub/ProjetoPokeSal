package pokeSall;

public abstract class PokeSall {

	public double HP;
	public int DEF;
	public int SPD;
	public double ATK;
	public Elemento TipoElemental;

	public PokeSall(double HP, int DEF, int SPD, double ATK, String TipoElemental) {
		this.HP = HP;
		this.DEF = DEF;
		this.SPD = SPD;
		this.ATK = ATK;
		this.TipoElemental = TipoElemental;
	}
	
}
