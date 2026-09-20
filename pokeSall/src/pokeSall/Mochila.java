package pokeSall;

import java.util.ArrayList;

import itens.Item;

public class Mochila {
	ArrayList<Item> itens = new ArrayList<Item>();

	public Mochila(ArrayList<Item> itens) {
		this.itens = itens;
	}

	public Mochila() {
		
	}

	public void addItem(Item item) {
		itens.add(item);
	}

	public void consumirItem(Item item) {
		for(Item item1: itens) {
			if(item1.equals(item)) {
				itens.remove(item);
				return; 
			}
		}
	}
}
