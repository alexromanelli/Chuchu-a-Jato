/**
 * 
 */
package br.com.alexromanelli.chuchuajato.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.alexromanelli.chuchuajato.client.services.PedidoService;
import br.com.alexromanelli.chuchuajato.dados.Bebida;
import br.com.alexromanelli.chuchuajato.dados.Expediente;
import br.com.alexromanelli.chuchuajato.dados.ItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.Prato;
import br.com.alexromanelli.chuchuajato.dados.ViewPedidoItemCardapio;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author alexandre
 * 
 */
@SuppressWarnings("serial")
public class PedidoServiceImpl extends RemoteServiceServlet implements
		PedidoService {

	@Override
	public ViewPedidoItemCardapio[] getColecaoPedidos(
			boolean filtrarExpediente, long idExpediente, boolean filtrarMesa,
			long idMesa, boolean filtrarEstado, int estado, String sortField,
			boolean sortAscending, int visibleRangeStart, int visibleRangeLength) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = preparaQueryColecaoPedidos(filtrarExpediente, idExpediente, 
				filtrarMesa, idMesa, filtrarEstado, estado);

		// define a ordenação na query
		q.addSort(sortField, sortAscending ? SortDirection.ASCENDING
				: SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);

		// define o intervalo a considerar
		List<Entity> l = pq.asList(FetchOptions.Builder.withOffset(
				visibleRangeStart).limit(visibleRangeLength));

		ViewPedidoItemCardapio[] v = new ViewPedidoItemCardapio[l.size()];
		int i = 0;
		for (Entity regPedido : l) {

			long idItem = (Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_ITEM);
			String tipoItem = (String) regPedido.getProperty("tipo");
			long idMesaRegistro = (Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_MESA);

			try {
				PedidoItemCardapio pedido = null;
				ItemCardapio item = null;
				
				Entity regMesa = ds.get(KeyFactory.createKey(
						Mesa.class.getSimpleName(), idMesaRegistro));
				Mesa mesa = PedidosMesaHelper.formaMesa(regMesa);

				if (tipoItem.equals("p")) {
					pedido = PedidosMesaHelper.formaPedidoPrato(regPedido);

					Entity regItem = ds.get(KeyFactory.createKey(
							Prato.class.getSimpleName(), idItem));
					item = PedidosMesaHelper.formaItemPrato(regItem);
					
					v[i++] = new ViewPedidoItemCardapio(pedido, item, mesa);
				} else if (tipoItem.equals("b")) {
					pedido = PedidosMesaHelper.formaPedidoBebida(regPedido);

					Entity regItem = ds.get(KeyFactory.createKey(
							Bebida.class.getSimpleName(), idItem));
					item = PedidosMesaHelper.formaItemBebida(regItem);

					v[i++] = new ViewPedidoItemCardapio(pedido, item, mesa);
				}
			} catch (EntityNotFoundException enf) {
				// TODO Auto-generated catch block
				enf.printStackTrace();
			}
		}
		return v;
	}
	
	private List<Long> obtemColecaoMesasExpediente(long idExpediente) {
		Mesa[] mesas = getColecaoMesasExpediente(idExpediente);
		List<Long> colecao = new ArrayList<Long>();
		for (Mesa m : mesas)
			colecao.add((long) m.getId());
		return colecao;
	}

	@Override
	public Mesa[] getColecaoMesasExpediente(long idExpediente) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Mesa");
        q.setFilter(new FilterPredicate(Mesa.KEY_ID_EXPEDIENTE,
        		FilterOperator.EQUAL, idExpediente));

        // define a ordenação na query
        q.addSort(Mesa.KEY_NUMERO_MESA, SortDirection.ASCENDING);
        PreparedQuery pq = ds.prepare(q);

        // define o intervalo a considerar
        List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());

        Mesa[] v = new Mesa[l.size()];
        int i = 0;
        for (Entity e : l) {
            v[i++] = new Mesa(e.getKey().getId(),
                    ((Long) e.getProperty(Mesa.KEY_NUMERO_MESA)).intValue(),
                    (Long) e.getProperty(Mesa.KEY_ID_EXPEDIENTE),
                    ((Long) e.getProperty(Mesa.KEY_ESTADO_MESA)).intValue());
        }
        return v;
	}

	@Override
	public Expediente[] getColecaoExpedientes() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Expediente");

        // define a ordenação na query
        q.addSort(Expediente.KEY_DATA_ABERTURA, SortDirection.DESCENDING);
        PreparedQuery pq = ds.prepare(q);

        // define o intervalo a considerar
        List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());

        Expediente[] v = new Expediente[l.size()];
        int i = 0;
        for (Entity e : l) {
            v[i++] = new Expediente(e.getKey().getId(),
                    (Date) e.getProperty(Expediente.KEY_DATA_ABERTURA),
                    ((Long) e.getProperty(Expediente.KEY_NUMERO_MESAS)).intValue(),
                    ((String) e.getProperty(Expediente.KEY_ESTADO_EXPEDIENTE)).charAt(0));
        }
        return v;
	}
	
	private Query preparaQueryColecaoPedidos(boolean filtrarExpediente,
			long idExpediente, boolean filtrarMesa, long idMesa,
			boolean filtrarEstado, int estado) {
		Query q = new Query("Pedido");

		// prepara filtros
		Filter filtroExpediente = null;
		Filter filtroMesa = null;
		Filter filtroEstado = null;
		ArrayList<Filter> subfiltros = new ArrayList<Filter>();
		if (filtrarExpediente) {
			List<Long> listIdMesasExpediente = obtemColecaoMesasExpediente(idExpediente);
			filtroExpediente = new FilterPredicate(
					PedidoItemCardapio.KEY_ID_MESA, FilterOperator.IN,
					listIdMesasExpediente);
			subfiltros.add(filtroExpediente);
		}
		if (filtrarMesa) {
			filtroMesa = new FilterPredicate(PedidoItemCardapio.KEY_ID_MESA,
					FilterOperator.EQUAL, idMesa);
			subfiltros.add(filtroMesa);
		}
		if (filtrarEstado) {
			filtroEstado = new FilterPredicate(
					PedidoItemCardapio.KEY_ESTADO_PEDIDO, FilterOperator.EQUAL,
					estado);
			subfiltros.add(filtroEstado);
		}
		if (subfiltros.size() > 0) {
			if (subfiltros.size() == 1)
				q.setFilter(subfiltros.get(0));
			else {
				Filter filtro = new CompositeFilter(CompositeFilterOperator.AND,
						subfiltros);
				q.setFilter(filtro);
			}
		}
		
		return q;
	}

	@Override
	public int getTamanhoColecaoPedidos(boolean filtrarExpediente,
			long idExpediente, boolean filtrarMesa, long idMesa,
			boolean filtrarEstado, int estado) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = preparaQueryColecaoPedidos(filtrarExpediente, idExpediente,
				filtrarMesa, idMesa, filtrarEstado, estado);
		PreparedQuery pq = ds.prepare(q);
		return pq.countEntities(FetchOptions.Builder.withDefaults());
	}

}
