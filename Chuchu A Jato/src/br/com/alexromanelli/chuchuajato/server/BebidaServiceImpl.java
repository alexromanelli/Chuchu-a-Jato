
package br.com.alexromanelli.chuchuajato.server;

import java.util.Arrays;
import java.util.List;

import br.com.alexromanelli.chuchuajato.client.services.BebidaService;
import br.com.alexromanelli.chuchuajato.dados.Bebida;
import br.com.alexromanelli.chuchuajato.dados.ItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class BebidaServiceImpl extends RemoteServiceServlet implements
        BebidaService {

    @Override
    public Bebida[] getColecaoBebida(String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Bebida");

        // define a ordenação na query
        q.addSort(sortField, sortAscending ? SortDirection.ASCENDING
                : SortDirection.DESCENDING);
        PreparedQuery pq = ds.prepare(q);

        // define o intervalo a considerar
        List<Entity> l = pq.asList(FetchOptions.Builder.withOffset(
                visibleRangeStart).limit(visibleRangeLength));

        Bebida[] v = new Bebida[l.size()];
        int i = 0;
        for (Entity e : l) {
            v[i++] = new Bebida(e.getKey().getId(),
                    (String) e.getProperty(Bebida.KEY_NOME),
                    ((Text) e.getProperty(Bebida.KEY_DESCRICAO)).getValue(),
                    ((Long) e.getProperty(Bebida.KEY_RENDIMENTO)).intValue(),
                    (Double) e.getProperty(Bebida.KEY_PRECO));
        }
        return v;
    }

    public void mapeiaAtributosRegistro(Bebida prato, Entity regBebida) {
        regBebida.setProperty(Bebida.KEY_NOME, prato.getNome());
        regBebida.setProperty(Bebida.KEY_DESCRICAO,
                new Text(prato.getDescricao()));
        regBebida
                .setProperty(Bebida.KEY_RENDIMENTO, prato.getRendimentoDoses());
        regBebida.setProperty(Bebida.KEY_PRECO, prato.getPreco());
    }

    @Override
    public long insertBebida(Bebida registro) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regBebida = new Entity("Bebida");
        mapeiaAtributosRegistro(registro, regBebida);

        datastore.put(regBebida);

        return regBebida.getKey().getId();
    }

    @Override
    public void updateBebida(Bebida registro) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regBebida;
        try {
            regBebida = datastore.get(KeyFactory.createKey(
                    Bebida.class.getSimpleName(), registro.getId()));
            mapeiaAtributosRegistro(registro, regBebida);

            datastore.put(regBebida);
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteBebida(long id) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        // verifica se há algum pedido registrado com esta bebida
        if (!checkBebidaTemPedido(id)) {
        	datastore.delete(KeyFactory.createKey(Bebida.class.getSimpleName(), id));
        	return true;
        }
        
        return false;
    }

    private boolean checkBebidaTemPedido(long id) {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    
	    Query qPedido = new Query("Pedido");
	    
	    Filter filtroItem = new Query.FilterPredicate(
	    		PedidoItemCardapio.KEY_ID_ITEM,
	            FilterOperator.EQUAL,
	            id);
	    Filter filtroTipoItem = new Query.FilterPredicate(
	    		PedidoItemCardapio.KEY_TIPO_ITEM,
	            FilterOperator.EQUAL,
	            ItemCardapio.TEXTO_TIPO_ITEM_CARDAPIO[ItemCardapio.TIPO_ITEM_BEBIDA]);
	    Filter filtro = new Query.CompositeFilter(CompositeFilterOperator.AND, 
	    		Arrays.asList(filtroItem, filtroTipoItem));
	    qPedido.setFilter(filtro);
	    
	    PreparedQuery pqItem = ds.prepare(qPedido);
	    int contagemItens = 
	    		pqItem.countEntities(FetchOptions.Builder.withDefaults());
	    
	    return contagemItens > 0;
    }

    @Override
    public int getQuantidadeBebidasRegistrados() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Bebida");
        PreparedQuery pq = ds.prepare(q);
        int contagemDeLinhas = pq.countEntities(FetchOptions.Builder
                .withDefaults());

        return contagemDeLinhas;
    }

    @Override
    public Bebida getBebida(long id) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regBebida;
        try {
            regBebida = datastore.get(KeyFactory.createKey(
                    Bebida.class.getSimpleName(), id));
            Bebida registro = new Bebida(
                    regBebida.getKey().getId(),
                    (String) regBebida.getProperty(Bebida.KEY_NOME),
                    ((Text) regBebida.getProperty(Bebida.KEY_DESCRICAO))
                            .getValue(),
                    ((Long) regBebida.getProperty(Bebida.KEY_RENDIMENTO))
                            .intValue(),
                    (Double) regBebida.getProperty(Bebida.KEY_PRECO));

            return registro;
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
