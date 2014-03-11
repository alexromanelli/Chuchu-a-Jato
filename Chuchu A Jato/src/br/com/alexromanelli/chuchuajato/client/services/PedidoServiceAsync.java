package br.com.alexromanelli.chuchuajato.client.services;

import br.com.alexromanelli.chuchuajato.dados.Expediente;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.ViewPedidoItemCardapio;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PedidoServiceAsync {

	void getColecaoPedidos(boolean filtrarExpediente, long idExpediente,
			boolean filtrarMesa, long idMesa,
			boolean filtrarEstado, int estado,
			String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength,
			AsyncCallback<ViewPedidoItemCardapio[]> callback);
	
	void getColecaoMesasExpediente(long idExpediente, 
			AsyncCallback<Mesa[]> callback);
	
	void getColecaoExpedientes(AsyncCallback<Expediente[]> callback);
	
	void getTamanhoColecaoPedidos(boolean filtrarExpediente,
			long idExpediente, boolean filtrarMesa, long idMesa,
			boolean filtrarEstado, int estado,
			AsyncCallback<Integer> callback);
}
