package pokeSalls;

import ataques.Habilidades;
import pokeSall.PokeSall;
import pokeSall.Tipos;

public class BulbaSall extends PokeSall {

	public BulbaSall() {
		super("BulbaSall", 200, 80, 70, 60, Tipos.PLANTA);
		addHabilidades(0, Habilidades.FOLHA_BUZZER);
		addHabilidades(1, Habilidades.GAS);
	}
}
