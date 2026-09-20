package itens;

public abstract class Item {
	String nome;
	String descricao;

	public Item(String nome, String descricao) {
		this.nome = nome;
		this.descricao = descricao;
	}

	public abstract void consumir();

}
