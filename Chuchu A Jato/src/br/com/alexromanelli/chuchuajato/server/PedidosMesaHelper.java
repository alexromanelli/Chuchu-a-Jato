package br.com.alexromanelli.chuchuajato.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.alexromanelli.chuchuajato.dados.Bebida;
import br.com.alexromanelli.chuchuajato.dados.ItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.PedidoBebida;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.PedidoPrato;
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
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class PedidosMesaHelper {
	
	public static final int TODAS_AS_MESAS = -1;
	
	public static ArrayList<ViewPedidoItemCardapio> getListaPedidosMesa(int numeroMesa,
			String estado) {
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("Pedido");
		
	    int estadoPedido = PedidoItemCardapio.EstadoPedido.getReverseEstadoPedido(estado);

	    if (numeroMesa == TODAS_AS_MESAS) {
			// filtro
			Filter filtro = new FilterPredicate(PedidoItemCardapio.KEY_ESTADO_PEDIDO,
									FilterOperator.EQUAL,
									estadoPedido);
			q.setFilter(filtro);
	    }
	    else {
		    long idMesa = getIdentificadorMesa(numeroMesa);
			
			// filtro
			Filter filtro = new CompositeFilter(CompositeFilterOperator.AND,
					Arrays.<Filter>asList(new FilterPredicate(PedidoItemCardapio.KEY_ID_MESA,
							        FilterOperator.EQUAL,
							        idMesa),
							new FilterPredicate(PedidoItemCardapio.KEY_ESTADO_PEDIDO,
									FilterOperator.EQUAL,
									estadoPedido)));
			q.setFilter(filtro);
	    }

		// define a ordenação na query
		q.addSort(PedidoItemCardapio.KEY_HORARIO_PEDIDO, 
				SortDirection.ASCENDING);
		PreparedQuery pq = ds.prepare(q);

		List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());

		ArrayList<ViewPedidoItemCardapio> v = new ArrayList<ViewPedidoItemCardapio>();
		for (Entity regPedido : l) {

			long idItem = (Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_ITEM);
			String tipoItem = (String) regPedido.getProperty("tipo");
			long idMesaRegistro = (Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_MESA);

			try {
				PedidoItemCardapio pedido = null;
				ItemCardapio item = null;
				
				Entity regMesa = ds.get(KeyFactory.createKey(
						Mesa.class.getSimpleName(), idMesaRegistro));
				Mesa mesa = formaMesa(regMesa);

				if (tipoItem.equals("p")) {
					pedido = formaPedidoPrato(regPedido);

					Entity regItem = ds.get(KeyFactory.createKey(
							Prato.class.getSimpleName(), idItem));
					item = formaItemPrato(regItem);
					
					v.add(new ViewPedidoItemCardapio(pedido, item, mesa));
				} else if (tipoItem.equals("b")) {
					pedido = formaPedidoBebida(regPedido);

					Entity regItem = ds.get(KeyFactory.createKey(
							Bebida.class.getSimpleName(), idItem));
					item = formaItemBebida(regItem);

					v.add(new ViewPedidoItemCardapio(pedido, item, mesa));
				}
			} catch (EntityNotFoundException enf) {
				// TODO Auto-generated catch block
				enf.printStackTrace();
			}
		}
		return v;
	}

	private static long getIdentificadorMesa(int numeroMesa) {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    
	    Query qMesa = new Query("Mesa");
	    Filter filtroMesa = new FilterPredicate(Mesa.KEY_NUMERO_MESA,
	    		FilterOperator.EQUAL,
	    		numeroMesa);
	    qMesa.setFilter(filtroMesa);
	    PreparedQuery pqMesa = ds.prepare(qMesa);
	    List<Entity> listMesa = pqMesa.asList(FetchOptions.Builder.withLimit(1));
	    long idMesa = -1;
	    if (listMesa.size() > 0) {
	        idMesa = ((Long)listMesa.get(0).getKey().getId()).longValue();
	    }
	    return idMesa;
	}

	protected static Mesa formaMesa(Entity regMesa) {
		return new Mesa(
				regMesa.getKey().getId(),
				((Long)regMesa.getProperty(Mesa.KEY_NUMERO_MESA)).intValue(),
				(Long)regMesa.getProperty(Mesa.KEY_ID_EXPEDIENTE),
				((Long)regMesa.getProperty(Mesa.KEY_ESTADO_MESA)).intValue());
	}

	protected static PedidoPrato formaPedidoPrato(Entity regPedido) {
		return new PedidoPrato(
				regPedido.getKey().getId(),
				(Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_MESA),
				(Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_ITEM),
				((Long) regPedido
						.getProperty(PedidoItemCardapio.KEY_QUANTIDADE))
						.intValue(),
				(Date) regPedido
						.getProperty(PedidoItemCardapio.KEY_HORARIO_PEDIDO),
				((Long) regPedido
						.getProperty(PedidoItemCardapio.KEY_ESTADO_PEDIDO))
						.intValue(),
				(Date) regPedido
						.getProperty(PedidoItemCardapio.KEY_HORARIO_CONCLUSAO));
	}
	
	protected static PedidoBebida formaPedidoBebida(Entity regPedido) {
		return new PedidoBebida(
				regPedido.getKey().getId(),
				(Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_MESA),
				(Long) regPedido.getProperty(PedidoItemCardapio.KEY_ID_ITEM),
				((Long) regPedido
						.getProperty(PedidoItemCardapio.KEY_QUANTIDADE))
						.intValue(),
				(Date) regPedido
						.getProperty(PedidoItemCardapio.KEY_HORARIO_PEDIDO),
				((Long) regPedido
						.getProperty(PedidoItemCardapio.KEY_ESTADO_PEDIDO))
						.intValue(),
				(Date) regPedido
						.getProperty(PedidoItemCardapio.KEY_HORARIO_CONCLUSAO));
	}
	
	protected static Prato formaItemPrato(Entity regItem) {
		return new Prato(
				regItem.getKey().getId(),
				(String) regItem.getProperty(Prato.KEY_NOME),
				((Text) regItem.getProperty(Prato.KEY_DESCRICAO))
						.getValue(),
				((Long) regItem.getProperty(Prato.KEY_RENDIMENTO))
						.intValue(),
				(Double) regItem.getProperty(Prato.KEY_PRECO));
	}
	
	protected static Bebida formaItemBebida(Entity regItem) {
		return new Bebida(regItem.getKey().getId(),
				(String) regItem.getProperty(Prato.KEY_NOME),
				((Text) regItem.getProperty(Prato.KEY_DESCRICAO))
						.getValue(),
				((Long) regItem.getProperty(Prato.KEY_RENDIMENTO))
						.intValue(),
				(Double) regItem.getProperty(Prato.KEY_PRECO));
	}

}
