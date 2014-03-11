/**
 * 
 */
package br.com.alexromanelli.chuchuajato.dados;

import java.util.Date;

/**
 * @author alexandre
 *
 */
@SuppressWarnings("serial")
public class PedidoBebida extends PedidoItemCardapio {

	public PedidoBebida() {
		super();
	}
	
	public PedidoBebida(long id, long idMesa, long idItem, int quantidade,
			Date horarioPedido, int estadoPedido, Date horarioConclusao) {
		super(id, idMesa, idItem, quantidade, horarioPedido, estadoPedido,
				horarioConclusao);
	}

}
