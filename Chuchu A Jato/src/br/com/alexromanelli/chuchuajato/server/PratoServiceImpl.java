
package br.com.alexromanelli.chuchuajato.server;

import java.util.Arrays;
import java.util.List;

import br.com.alexromanelli.chuchuajato.client.services.PratoService;
import br.com.alexromanelli.chuchuajato.dados.ItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.Prato;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class PratoServiceImpl extends RemoteServiceServlet implements
        PratoService {

    @Override
    public Prato[] getColecaoPrato(String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength) {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Prato");

        // define a ordenação na query
        q.addSort(sortField, sortAscending ? SortDirection.ASCENDING
                : SortDirection.DESCENDING);
        PreparedQuery pq = ds.prepare(q);

        // define o intervalo a considerar
        List<Entity> l = pq.asList(FetchOptions.Builder.withOffset(
                visibleRangeStart).limit(visibleRangeLength));

        Prato[] v = new Prato[l.size()];
        int i = 0;
        for (Entity e : l) {
            v[i++] = new Prato(e.getKey().getId(),
                    (String) e.getProperty(Prato.KEY_NOME),
                    ((Text) e.getProperty(Prato.KEY_DESCRICAO)).getValue(),
                    ((Long) e.getProperty(Prato.KEY_RENDIMENTO)).intValue(),
                    (Double) e.getProperty(Prato.KEY_PRECO));
        }
        return v;
    }

    public void mapeiaAtributosRegistro(Prato prato, Entity regPrato) {
        regPrato.setProperty(Prato.KEY_NOME, prato.getNome());
        regPrato.setProperty(Prato.KEY_DESCRICAO,
                new Text(prato.getDescricao()));
        regPrato.setProperty(Prato.KEY_RENDIMENTO, prato.getRendimentoPorcoes());
        regPrato.setProperty(Prato.KEY_PRECO, prato.getPreco());
    }

    @Override
    public long insertPrato(Prato registro) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regPrato = new Entity("Prato");
        mapeiaAtributosRegistro(registro, regPrato);

        datastore.put(regPrato);

        return regPrato.getKey().getId();
    }

    @Override
    public void updatePrato(Prato registro) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regPrato;
        try {
            regPrato = datastore.get(KeyFactory.createKey(
                    Prato.class.getSimpleName(), registro.getId()));
            mapeiaAtributosRegistro(registro, regPrato);

            datastore.put(regPrato);
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean deletePrato(long id) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        // verifica se há algum pedido registrado com este prato
        if (!checkPratoTemPedido(id)) {
        	datastore.delete(KeyFactory.createKey(Prato.class.getSimpleName(), id));
        	return true;
        }
        
        return false;
    }

    private boolean checkPratoTemPedido(long id) {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    
	    Query qPedido = new Query("Pedido");
	    
	    Filter filtroItem = new Query.FilterPredicate(
	    		PedidoItemCardapio.KEY_ID_ITEM,
	            FilterOperator.EQUAL,
	            id);
	    Filter filtroTipoItem = new Query.FilterPredicate(
	    		PedidoItemCardapio.KEY_TIPO_ITEM,
	            FilterOperator.EQUAL,
	            ItemCardapio.TEXTO_TIPO_ITEM_CARDAPIO[ItemCardapio.TIPO_ITEM_PRATO]);
	    Filter filtro = new Query.CompositeFilter(CompositeFilterOperator.AND, 
	    		Arrays.asList(filtroItem, filtroTipoItem));
	    qPedido.setFilter(filtro);
	    
	    PreparedQuery pqItem = ds.prepare(qPedido);
	    int contagemItens = 
	    		pqItem.countEntities(FetchOptions.Builder.withDefaults());
	    
	    return contagemItens > 0;
	}

	@Override
    public int getQuantidadePratosRegistrados() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Prato");
        PreparedQuery pq = ds.prepare(q);
        int contagemDeLinhas = pq.countEntities(FetchOptions.Builder
                .withDefaults());

        return contagemDeLinhas;
    }

    @Override
    public Prato getPrato(long id) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regPrato;
        try {
            regPrato = datastore.get(KeyFactory.createKey(
                    Prato.class.getSimpleName(), id));
            Prato registro = new Prato(
                    regPrato.getKey().getId(),
                    (String) regPrato.getProperty(Prato.KEY_NOME),
                    ((Text) regPrato.getProperty(Prato.KEY_DESCRICAO))
                            .getValue(),
                    ((Long) regPrato.getProperty(Prato.KEY_RENDIMENTO))
                            .intValue(),
                    (Double) regPrato.getProperty(Prato.KEY_PRECO));

            return registro;
        } catch (EntityNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

}
