package br.com.alexromanelli.chuchuajato.server;

import java.util.Calendar;
import java.util.TimeZone;

import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

public class RegistrarEntregaHelper {

	public static boolean registrarEntregaPedido(long idPedido) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        try {
            Entity regPedido = datastore.get(KeyFactory.createKey(
                    "Pedido", idPedido));
            
            regPedido.setProperty(PedidoItemCardapio.KEY_ESTADO_PEDIDO, 
            		PedidoItemCardapio.EstadoPedido.PEDIDO_ENTREGUE);
            regPedido.setProperty(PedidoItemCardapio.KEY_HORARIO_CONCLUSAO,  
            		Calendar
                    .getInstance(TimeZone.getTimeZone("GMT-03:00")).getTime());

            datastore.put(regPedido);
            
            return true;
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return false;
	}

}
