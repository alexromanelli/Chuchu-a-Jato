package br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.dados;

import java.util.HashMap;

@SuppressWarnings("serial")
public class ItemPedido extends HashMap<String, String> {
	
	public static final char TIPO_ITEM_PRATO = 'p'; 
	public static final char TIPO_ITEM_BEBIDA = 'b';
	
	public static String KEY_ITEM_PEDIDO = "item_pedido";
	public static String KEY_TAG_ITEM_PRATO = "prato";
	public static String KEY_TAG_ITEM_BEBIDA = "bebida";

	public static String KEY_ID = "id";
	public static String KEY_TIPO_ITEM = "tipoItem";
	public static String KEY_NOME = "nome";
	public static String KEY_DESCRICAO = "descricao";
	public static String KEY_RENDIMENTO = "rendimento";
	public static String KEY_RENDIMENTO_PRATO = "porcoes";
	public static String KEY_RENDIMENTO_BEBIDA = "doses";
	public static String KEY_PRECO = "preco";
	
	public static String getKeyTagItemPedido(char tipoItem) {
		switch (tipoItem) {
		case TIPO_ITEM_PRATO:
			return KEY_TAG_ITEM_PRATO;
		case TIPO_ITEM_BEBIDA:
			return KEY_TAG_ITEM_BEBIDA;
		}
		return null;
	}
	
	public static String getKeyTagRendimento(char tipoItem) {
		switch (tipoItem) {
		case TIPO_ITEM_PRATO:
			return KEY_RENDIMENTO_PRATO;
		case TIPO_ITEM_BEBIDA:
			return KEY_RENDIMENTO_BEBIDA;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return super.get(KEY_NOME);
	}

	public ItemPedido(long id, char tipoItem, String nome, String descricao, int rendimento, double preco) {
		setId(id);
		setTipoItem(tipoItem);
		setNome(nome);
		setDescricao(descricao);
		setRendimento(rendimento);
		setPreco(preco);
	}

	public void setPreco(double preco) {
		super.put(KEY_PRECO, Double.toString(preco));
	}

	public void setRendimento(int rendimento) {
		super.put(KEY_RENDIMENTO, Integer.toString(rendimento));
	}

	public void setDescricao(String descricao) {
		super.put(KEY_DESCRICAO, descricao);
	}

	public void setNome(String nome) {
		super.put(KEY_NOME, nome);
	}

	public void setId(long id) {
		super.put(KEY_ID, Long.toString(id));
	}
	
	public void setTipoItem(char tipoItem) {
		super.put(KEY_TIPO_ITEM, Character.toString(tipoItem));
	}
	
	public char getTipoItem() {
		return super.get(KEY_TIPO_ITEM).charAt(0);
	}
	
	public long getId() {
		return Long.parseLong(super.get(KEY_ID));
	}
	
	public String getNome() {
		return super.get(KEY_NOME);
	}
	
	public String getDescricao() {
		return super.get(KEY_DESCRICAO);
	}
	
	public int getRendimento() {
		return Integer.parseInt(super.get(KEY_RENDIMENTO));
	}
	
	public double getPreco() {
		return Double.parseDouble(super.get(KEY_PRECO));
	}
	
}
