package br.com.alexromanelli.chuchuajato.server;

import java.util.List;

import br.com.alexromanelli.chuchuajato.dados.Expediente;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

public class NumeroMesasHelper {

    public static int getNumeroMesas() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Expediente");
        q.addSort(Expediente.KEY_DATA_ABERTURA, SortDirection.DESCENDING);
        PreparedQuery pq = ds.prepare(q);
        List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());

        if (l.size() == 0)
            return 0;

        else {
            Entity expedienteRecente = l.get(0);
            Long numeroMesas = (Long) expedienteRecente
                    .getProperty(Expediente.KEY_NUMERO_MESAS);
            return numeroMesas.intValue();
        }
    }
	
}
