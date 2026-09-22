package pokeSall;

import java.util.ArrayList;

import itens.Antidote;
import itens.Item;
import itens.Potion;

public class Mochila {
	private ArrayList<Item> itens = new ArrayList<Item>();
	private Item item;

	public Mochila(ArrayList<Item> itens) {
		this.itens = itens;
	}

	public Mochila() {
		item = new Potion();
		this.itens.add(item);
		item = new Antidote();
		this.itens.add(item);
	}

	public void addItem(Item item) {
		itens.add(item);
	}

	public void consumirItem(Item item) {
		for(Item item1: this.itens) {
			if(item1.equals(item)) {
				itens.remove(item);
				return; 
			}
		}
	}
}
