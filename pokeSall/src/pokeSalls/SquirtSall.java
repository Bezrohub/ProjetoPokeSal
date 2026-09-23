package pokeSalls;

import ataques.Habilidades;
import pokeSall.PokeSall;
import pokeSall.Tipos;

public class SquirtSall extends PokeSall{
    
  public SquirtSall(){
    	
        super("SquirtSall", 200, 80, 70, 60, Tipos.AGUA);
        addHabilidades(0, Habilidades.BEAT_BOLHA);
        addHabilidades(1, Habilidades.AGUA_TERMAL);

    }
}
