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
public class PedidoPrato extends PedidoItemCardapio {

	public PedidoPrato() {
		super();
	}
	
	public PedidoPrato(long id, long idMesa, long idItem, int quantidade,
			Date horarioPedido, int estadoPedido, Date horarioConclusao) {
		super(id, idMesa, idItem, quantidade, horarioPedido, estadoPedido,
				horarioConclusao);
	}

}
