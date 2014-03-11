package br.com.alexromanelli.chuchuajato.server;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.alexromanelli.chuchuajato.dados.Expediente;
import br.com.alexromanelli.chuchuajato.dados.ItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.PedidoBebida;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.PedidoPrato;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class RegistrarPedidoHelper {
	
	public static boolean registrarPedido(String strNumeroMesa, 
			int tipoItem, String strIdItem, 
			String strQuantidade) {

		// obtém dados dos parâmetros
		int numeroMesa = -1; 
		long idItem = -1; 
		int quantidade = -1;
		try {
			numeroMesa = Integer.parseInt(strNumeroMesa); 
			idItem = Long.parseLong(strIdItem); 
			quantidade = Integer.parseInt(strQuantidade);
		} catch (NumberFormatException e) {
			return false;
		}
		
	    // verifica validade de operandos
		boolean mesaValida = checkNumeroMesa(numeroMesa);
		boolean itemValido = checkItem(tipoItem, idItem);
		if (!mesaValida || !itemValido)
			return false;

	    // obtém identificador da mesa
		long idMesa = obtemIdMesa(numeroMesa);
		if (idMesa == -1)
			return false;
	    
	    // registra o pedido
	    Entity regPedido = null;
	    if (tipoItem == ItemCardapio.TIPO_ITEM_PRATO) {
	    	regPedido = new Entity("Pedido");
	        
	    	regPedido.setProperty(PedidoPrato.KEY_TIPO_ITEM,"p");
	    	regPedido.setProperty(PedidoPrato.KEY_ID_MESA, idMesa);
	        regPedido.setProperty(PedidoPrato.KEY_ID_ITEM, idItem);
	        regPedido.setProperty(PedidoPrato.KEY_QUANTIDADE, quantidade);
	        regPedido.setProperty(PedidoPrato.KEY_HORARIO_PEDIDO,  Calendar
	                .getInstance(TimeZone.getTimeZone("GMT-03:00")).getTime());
	        regPedido.setProperty(PedidoPrato.KEY_ESTADO_PEDIDO, 
	                PedidoItemCardapio.EstadoPedido.PEDIDO_PENDENTE);
	        regPedido.setProperty(PedidoPrato.KEY_HORARIO_CONCLUSAO, null);
	    }
	    else {
	    	regPedido = new Entity("Pedido");

	        regPedido.setProperty(PedidoPrato.KEY_TIPO_ITEM,"b");
	    	regPedido.setProperty(PedidoBebida.KEY_ID_MESA, idMesa);
	        regPedido.setProperty(PedidoBebida.KEY_ID_ITEM, idItem);
	        regPedido.setProperty(PedidoBebida.KEY_QUANTIDADE, quantidade);
	        regPedido.setProperty(PedidoBebida.KEY_HORARIO_PEDIDO,  Calendar
	                .getInstance(TimeZone.getTimeZone("GMT-03:00")).getTime());
	        regPedido.setProperty(PedidoBebida.KEY_ESTADO_PEDIDO, 
	                PedidoItemCardapio.EstadoPedido.PEDIDO_PENDENTE);
	        regPedido.setProperty(PedidoBebida.KEY_HORARIO_CONCLUSAO, null);
	    }
	    
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    ds.put(regPedido);
	    
	    // marca mesa como ocupada
	    Entity regMesa;
	    try {
	        regMesa = ds.get(KeyFactory.createKey(
	                Mesa.class.getSimpleName(), idMesa));
	        regMesa.setProperty(Mesa.KEY_ESTADO_MESA, 
	        		Mesa.EstadoMesa.MESA_OCUPADA);

	        ds.put(regMesa);
	    } catch (EntityNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
		
		return true;
	}

	private static long obtemIdMesa(int numeroMesa) {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    
	    Query qMesa = new Query("Mesa");
	    Filter filtroMesa = new Query.FilterPredicate(Mesa.KEY_NUMERO_MESA,
	            FilterOperator.EQUAL,
	            numeroMesa);
	    qMesa.setFilter(filtroMesa);
	    
	    PreparedQuery pqMesa = ds.prepare(qMesa);
	    List<Entity> listMesa = pqMesa.asList(FetchOptions.Builder.withDefaults());
	    
	    long idMesa = -1;
	    for (Entity mesa : listMesa) {
	    	long idExpediente = (Long) mesa.getProperty(Mesa.KEY_ID_EXPEDIENTE);
	    	try {
	    		Entity regExpediente = ds.get(KeyFactory.createKey(
	                Expediente.class.getSimpleName(), idExpediente));
	    		String estado = (String) regExpediente
	    				.getProperty(Expediente.KEY_ESTADO_EXPEDIENTE);
	    		if (estado.charAt(0) == 
	    				Expediente.EstadoExpediente.EXPEDIENTE_ABERTO) {
	    	        idMesa = mesa.getKey().getId();
	    			break;
	    		}
	    	} catch (EntityNotFoundException e) {
	    		e.printStackTrace();
	    	}
	    }
	    
		return idMesa;
	}

	private static boolean checkItem(int tipoItem, long idItem) {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    
	    String strNomeEntidade = (tipoItem == ItemCardapio.TIPO_ITEM_PRATO) ?
	    	"Prato" : "Bebida";
	    
	    boolean valido = false;
	    try {
			Entity regItem = ds.get(KeyFactory.createKey(
			        strNomeEntidade, idItem));
			valido = regItem != null;
		} catch (EntityNotFoundException e) {
			return false;
		}
	    
	    return valido;
	}

	private static boolean checkNumeroMesa(int numeroMesa) {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    
	    Query qMesa = new Query("Mesa");
	    
	    Filter filtroMesa = new Query.FilterPredicate(Mesa.KEY_NUMERO_MESA,
	            FilterOperator.EQUAL,
	            numeroMesa);
	    qMesa.setFilter(filtroMesa);
	    
	    PreparedQuery pqMesa = ds.prepare(qMesa);
	    int contagemMesas = 
	    		pqMesa.countEntities(FetchOptions.Builder.withDefaults());
	    
	    return contagemMesas > 0;
	}
}
