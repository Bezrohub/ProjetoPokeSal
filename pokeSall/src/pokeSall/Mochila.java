package pokeSall;

import java.util.ArrayList;

import itens.Antidote;
import itens.Item;
import itens.Potion;
import itens.SuperPotion;

public class Mochila {
	ArrayList<Item> itens = new ArrayList<Item>();
	private Item item;
	
  public Mochila(ArrayList<Item> itens) {
		this.itens = itens;
	}

  public Mochila() {
		item = new Potion();
		item.setQuantidade(2);
		this.itens.add(item);
		item = new Antidote();
		item.setQuantidade(2);
		this.itens.add(item);
		item = new SuperPotion();
		item.setQuantidade(0);
		this.itens.add(item);
	}
	public ArrayList getItens() {
		return this.itens;
	}
	public void listarMochila() {
		
		for(Item item: this.itens) {
			if(item.getQuantidade() > 0) {
				System.out.println(item.getNome()+": "+item.getQuantidade());
				System.out.println(item.getDescricao());
			}
			
		}
	}
	public void addItem(String nome) {
		int temp = 0;
		for(Item item: this.itens) {
			temp = item.getQuantidade();
			if(item.getNome().equals(nome))item.setQuantidade(temp++);
		}
	}

	public void consumirItem(String nome) {
		int temp = 0;
		for(Item item: this.itens) {
			temp = item.getQuantidade();
			if(item.getNome().equals(nome) && item.getQuantidade() > 0)item.setQuantidade(temp--);
		}
	}
}
