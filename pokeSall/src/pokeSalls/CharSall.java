package pokeSalls;

import ataques.Habilidades;
import pokeSall.PokeSall;
import pokeSall.Tipos;

public class CharSall extends PokeSall{
    
    public CharSall() {
		super("CharSall", 200, 80, 70, 60, Tipos.FOGO);
		addHabilidades(0, Habilidades.HELL_BEAM);
		addHabilidades(1, Habilidades.QUEIMAR);
	}

}
