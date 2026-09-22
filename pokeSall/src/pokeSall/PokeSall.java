package pokeSall;

import ataques.Habilidades;

public abstract class PokeSall {

	private double HP;
	private int DEF;
	private int SPD;
	private double ATK;
	private Tipos Tipo;
	private Status Stat = Status.NORMAL;
	private Habilidades[] habilidades = new Habilidades[2];

	public PokeSall(double HP, int DEF, int SPD, double ATK, Tipos Tipo) {
		this.HP = HP;
		this.DEF = DEF;
		this.SPD = SPD;
		this.ATK = ATK;
		this.Tipo = Tipo;
	}
	public void addHabilidades(int indice, Habilidades habilidade){
		habilidades[indice] = habilidade;
	}
 
	public void setHabilidades(Habilidades[] habilidades){
		this.habilidades = habilidades;
	}
	public void listarHabilidades(){
		for(int k = 0; k < habilidades.length; k++){
			System.out.println(habilidades[k]);
		}
	}
	public Status getStatus(){
		return this.Stat;
	}
	public void setStatus(Status stat){
		this.Stat = stat;
	}
	public double getHP(){
		return this.HP;
	}
	public void setHP(double HP){
		this.HP = HP;
	}
	public int getDEF(){
		return this.DEF;
	}
	public int getSPD(){
		return this.SPD;
	}
	public double getATK(){
		return this.ATK;
	}
	public void setATK(double ATK){
		this.ATK = ATK;
	}
	public Tipos getTipo(){
		return this.Tipo;
	}
}
