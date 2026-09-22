package pokeSalls;

import pokeSall.PokeSall;
import pokeSall.Tipos;

public class BubaSall extends PokeSall {

	public BubaSall() {
		super(200, 80, 70, 60, Tipos.PLANTA);
	}
	
	public double folhaBeam() {
		return 60 + this.ATK;
	}
	
	public double recuperar() {
		return 1;
	}
}
