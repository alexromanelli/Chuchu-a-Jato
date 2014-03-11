/**
 * 
 */
package br.com.alexromanelli.chuchuajato.dados;

import java.io.Serializable;
import java.util.Date;

/**
 * @author alexandre
 *
 */
@SuppressWarnings("serial")
public abstract class PedidoItemCardapio implements Serializable {

	public static String KEY_ID = "id";
	public static String KEY_ID_MESA = "idMesa";
	public static String KEY_TIPO_ITEM = "tipo";
	public static String KEY_ID_ITEM = "idItem";
	public static String KEY_QUANTIDADE = "quantidade";
	public static String KEY_HORARIO_PEDIDO = "horarioPedido";
	public static String KEY_ESTADO_PEDIDO = "estadoPedido";
	public static String KEY_HORARIO_CONCLUSAO = "horarioConclusao";
	
	public PedidoItemCardapio() {
		super();
	}

	public static class EstadoPedido {
		public static final String[] TEXTO_ESTADO_PEDIDO = { 
			"pendente",
			"entregue",
			"cancelado"
		};
		public static final int PEDIDO_PENDENTE = 0;
		public static final int PEDIDO_ENTREGUE = 1;
		public static final int PEDIDO_CANCELADO = 2;
		public static int getReverseEstadoPedido(String textoEstadoPedido) {
			if (TEXTO_ESTADO_PEDIDO[PEDIDO_PENDENTE].equals(textoEstadoPedido))
				return PEDIDO_PENDENTE;
			if (TEXTO_ESTADO_PEDIDO[PEDIDO_ENTREGUE].equals(textoEstadoPedido))
				return PEDIDO_ENTREGUE;
			if (TEXTO_ESTADO_PEDIDO[PEDIDO_CANCELADO].equals(textoEstadoPedido))
				return PEDIDO_CANCELADO;
			return -1;
		}
	}
	
	private long id;
	private long idMesa;
	private long idItem;
	private int quantidade;
	private Date horarioPedido;
	private int estadoPedido;
	private Date horarioConclusao;
	
	public PedidoItemCardapio(long id, long idMesa, long idItem, int quantidade,
			Date horarioPedido, int estadoPedido, Date horarioConclusao) {
		super();
		this.id = id;
		this.idMesa = idMesa;
		this.idItem = idItem;
		this.quantidade = quantidade;
		this.horarioPedido = horarioPedido;
		this.estadoPedido = estadoPedido;
		this.horarioConclusao = horarioConclusao;
	}

	public long getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(long idMesa) {
		this.idMesa = idMesa;
	}

	public long getIdItem() {
		return idItem;
	}

	public void setIdItem(long idItem) {
		this.idItem = idItem;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public Date getHorarioPedido() {
		return horarioPedido;
	}

	public void setHorarioPedido(Date horarioPedido) {
		this.horarioPedido = horarioPedido;
	}

	public int getEstadoPedido() {
		return estadoPedido;
	}

	public void setEstadoPedido(int estadoPedido) {
		this.estadoPedido = estadoPedido;
	}

	public Date getHorarioConclusao() {
		return horarioConclusao;
	}

	public void setHorarioConclusao(Date horarioConclusao) {
		this.horarioConclusao = horarioConclusao;
	}

	public long getId() {
		return id;
	}
	
}
