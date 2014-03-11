package br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.dados;

import java.util.HashMap;

@SuppressWarnings("serial")
public class PedidoMesa extends HashMap<String, String> {
	public static String KEY_PEDIDO_MESA = "PedidoMesa";
	public static String KEY_NUMERO_MESA = "numeroMesa";
	public static String KEY_ID_ITEM = "idItem"; 
	public static String KEY_NOME_ITEM = "nomeItem"; 
	public static String KEY_DESCRICAO_ITEM = "descricaoItem"; 
	public static String KEY_RENDIMENTO_ITEM = "rendimentoItem"; 
	public static String KEY_PRECO_ITEM = "precoItem"; 
	public static String KEY_QUANTIDADE_PEDIDO = "quantidadeItem"; 
	public static String KEY_ID_PEDIDO = "idPedido";
	public static String KEY_TIPO_ITEM = "tipoItem";
	
	public PedidoMesa(int numeroMesa, int idItem, char tipoItem, String nomeItem, String descricaoItem, int rendimentoItem, double precoUnidadeItem, int quantidadePedido, int idPedido) {
		super();
		setNumeroMesa(numeroMesa);
		setIdItem(idItem);
		setTipoItem(tipoItem);
		setNomeItem(nomeItem);
		setDescricaoItem(descricaoItem);
		setRendimentoItem(rendimentoItem);
		setPrecoItem(precoUnidadeItem);
		setQuantidadePedido(quantidadePedido);
		setIdPedido(idPedido);
	}
	
	/**
	 * Este construtor faz a conversão de um objeto de mapeamento String -> String,
	 * para um objeto da classe PedidoMesa.
	 * @param h é o objeto de mapeamento que será convertido.
	 */
	public PedidoMesa(HashMap<String, String> h) {
		// faz uma chamada ao outro construtor, usando os parâmetros obtidos do mapeamento h
		this(Integer.parseInt(h.get(PedidoMesa.KEY_NUMERO_MESA)),
			 Integer.parseInt(h.get(PedidoMesa.KEY_ID_ITEM)),
			 h.get(PedidoMesa.KEY_TIPO_ITEM).charAt(0),
			 h.get(PedidoMesa.KEY_NOME_ITEM),
			 h.get(PedidoMesa.KEY_DESCRICAO_ITEM),
			 Integer.parseInt(h.get(PedidoMesa.KEY_RENDIMENTO_ITEM)),
			 Double.parseDouble(h.get(PedidoMesa.KEY_PRECO_ITEM)),
			 Integer.parseInt(h.get(PedidoMesa.KEY_QUANTIDADE_PEDIDO)),
			 Integer.parseInt(h.get(PedidoMesa.KEY_ID_PEDIDO))
			 );
	}
	
	public void setNumeroMesa(int numeroMesa) {
		super.put(KEY_NUMERO_MESA, Integer.toString(numeroMesa));
	}
	
	public void setIdItem(int idItem) {
		super.put(KEY_ID_ITEM, Integer.toString(idItem));
	}
	
	public int getNumeroMesa() {
		return Integer.parseInt(super.get(KEY_NUMERO_MESA));
	}
	
	public void setTipoItem(char tipoItem) {
		super.put(KEY_TIPO_ITEM, Character.toString(tipoItem));
	}
	
	public char getTipoItem() {
		return super.get(KEY_TIPO_ITEM).charAt(0);
	}
	
	public int getIdItem() {
		return Integer.parseInt(super.get(KEY_ID_ITEM));
	}
	
	public void setNomeItem(String nomeItem) {
		super.put(KEY_NOME_ITEM, nomeItem);
	}
	
	public String getNomeItem() {
		return super.get(KEY_NOME_ITEM);
	}
	
	public void setDescricaoItem(String descricaoItem) {
		super.put(KEY_DESCRICAO_ITEM, descricaoItem);
	}
	
	public String getDescricaoItem() {
		return super.get(KEY_DESCRICAO_ITEM);
	}
	
	public void setRendimentoItem(int rendimentoItem) {
		super.put(KEY_RENDIMENTO_ITEM, Integer.toString(rendimentoItem));
	}
	
	public int getRendimentoItem() {
		return Integer.parseInt(super.get(KEY_RENDIMENTO_ITEM));
	}
	
	public void setPrecoItem(double precoItem) {
		super.put(KEY_PRECO_ITEM, Double.toString(precoItem));
	}
	
	public double getPrecoItem() {
		return Double.parseDouble(super.get(KEY_PRECO_ITEM));
	}
	
	public void setQuantidadePedido(int quantidadePedido) {
		super.put(KEY_QUANTIDADE_PEDIDO, Integer.toString(quantidadePedido));
	}
	
	public int getQuantidadePedido() {
		return Integer.parseInt(super.get(KEY_QUANTIDADE_PEDIDO));
	}
	
	public void setIdPedido(int idPedido) {
		super.put(KEY_ID_PEDIDO, Integer.toString(idPedido));
	}
	
	public int getIdPedido() {
		return Integer.parseInt(super.get(KEY_ID_PEDIDO));
	}
	
}
