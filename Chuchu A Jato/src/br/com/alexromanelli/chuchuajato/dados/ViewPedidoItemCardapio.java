package br.com.alexromanelli.chuchuajato.dados;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ViewPedidoItemCardapio implements Serializable {
	private PedidoItemCardapio pedido;
	private ItemCardapio item;
	private Mesa mesa;
	
	public ViewPedidoItemCardapio() {
		super();
	}

	public ViewPedidoItemCardapio(PedidoItemCardapio pedido, 
			ItemCardapio item, Mesa mesa) {
		this.pedido = pedido;
		this.item = item;
		this.mesa = mesa;
	}
	
	/**
	 * @return the pedido
	 */
	public PedidoItemCardapio getPedido() {
		return pedido;
	}

	/**
	 * @return the item
	 */
	public ItemCardapio getItem() {
		return item;
	}

	/**
	 * @return the mesa
	 */
	public Mesa getMesa() {
		return mesa;
	}

	public Date getHorarioPedido() {
		return pedido.getHorarioPedido();
	}
	
	public int getNumeroMesa() {
		return mesa.getNumeroMesa();
	}
	
	public String getNomeItem() {
		return item.getNome();
	}
	
	public int getQuantidade() {
		return pedido.getQuantidade();
	}
	
	public double getValorPedido() {
		return pedido.getQuantidade() * item.getPreco();
	}

	public String getEstadoPedido() {
		return PedidoItemCardapio
				.EstadoPedido.TEXTO_ESTADO_PEDIDO[pedido.getEstadoPedido()];
	}
	
	public Date getHorarioConclusao() {
		return pedido.getHorarioConclusao();
	}
	
}
