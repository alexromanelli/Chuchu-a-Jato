/**
 * 
 */
package br.com.alexromanelli.chuchuajato.client.services;

import br.com.alexromanelli.chuchuajato.dados.Expediente;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.ViewPedidoItemCardapio;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author alexandre
 *
 */
@RemoteServiceRelativePath("PedidoService")
public interface PedidoService extends RemoteService {
    /**
     * Utility class for simplifying access to the instance of async service.
     */
    public static class Util {
        private static PedidoServiceAsync instance;

        public static PedidoServiceAsync getInstance() {
            if (instance == null) {
                instance = GWT.create(PedidoService.class);
            }
            return instance;
        }
    }

	ViewPedidoItemCardapio[] getColecaoPedidos(boolean filtrarExpediente,
			long idExpediente, boolean filtrarMesa, long idMesa,
			boolean filtrarEstado, int estado, String sortField,
			boolean sortAscending, int visibleRangeStart, int visibleRangeLength);

	Mesa[] getColecaoMesasExpediente(long idExpediente);

	Expediente[] getColecaoExpedientes();

	int getTamanhoColecaoPedidos(boolean filtrarExpediente, long idExpediente,
			boolean filtrarMesa, long idMesa, boolean filtrarEstado, int estado);


}
