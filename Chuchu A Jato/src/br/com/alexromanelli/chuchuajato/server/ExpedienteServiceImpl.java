/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package br.com.alexromanelli.chuchuajato.server;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import br.com.alexromanelli.chuchuajato.client.services.ExpedienteService;
import br.com.alexromanelli.chuchuajato.dados.Expediente;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ExpedienteServiceImpl extends RemoteServiceServlet implements
        ExpedienteService {

    @Override
    public int getNumeroMesas() {
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

    @Override
    public char getEstadoExpediente() {
        DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        Query q = new Query("Expediente");
        q.addSort(Expediente.KEY_DATA_ABERTURA, SortDirection.DESCENDING);
        PreparedQuery pq = ds.prepare(q);
        List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());

        if (l.size() == 0)
            return 0;

        else {
            Entity expedienteRecente = l.get(0);
            Character estadoExpediente = ((String) expedienteRecente
                    // primeiro caractere da string de tamanho 1
                    .getProperty(Expediente.KEY_ESTADO_EXPEDIENTE)).charAt(0);
            
            return estadoExpediente.charValue();
        }
    }

    private void cadastrarMesasExpediente(long idExpediente, int numeroMesas) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        for (int i = 0; i < numeroMesas; i++) {
	        Entity regMesa = new Entity("Mesa");
	        
	        regMesa.setProperty(Mesa.KEY_NUMERO_MESA, i + 1);
	        regMesa.setProperty(Mesa.KEY_ID_EXPEDIENTE, idExpediente);
	        regMesa.setProperty(Mesa.KEY_ESTADO_MESA, 
	        		Mesa.EstadoMesa.MESA_LIVRE);
	
	        datastore.put(regMesa);
        }
	}

	private boolean checkPedidosConcluidos() {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	    // conta pedidos que estão pendentes
	    Query qPedido = new Query("Pedido");
	    
	    Filter filtroPendente = new Query.FilterPredicate(PedidoItemCardapio.KEY_ESTADO_PEDIDO,
	            FilterOperator.EQUAL,
	            PedidoItemCardapio.EstadoPedido.PEDIDO_PENDENTE);
	    qPedido.setFilter(filtroPendente);
	    
	    PreparedQuery pqPedido = ds.prepare(qPedido);
	    int contagemPedidos = 
	    		pqPedido.countEntities(FetchOptions.Builder.withDefaults());
	    
	    return contagemPedidos == 0;
	}

	@Override
	public boolean abrirExpediente(int numeroMesas) {
		if (!checkExpedienteFechado())
			return false;
		
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regExpediente = new Entity("Expediente");
        regExpediente.setProperty(Expediente.KEY_DATA_ABERTURA, Calendar
                .getInstance(TimeZone.getTimeZone("GMT-03:00")).getTime());
        regExpediente.setProperty(Expediente.KEY_NUMERO_MESAS, numeroMesas);
        regExpediente.setProperty(Expediente.KEY_ESTADO_EXPEDIENTE,
                Expediente.EstadoExpediente.EXPEDIENTE_ABERTO.toString());

        datastore.put(regExpediente);
        
        cadastrarMesasExpediente(regExpediente.getKey().getId(), numeroMesas);
        
        return true;
	}

	@Override
	public boolean fecharExpediente() {
        // verifica se todos os pedidos estão concluídos
        if (!checkPedidosConcluidos())
        	return false;
        if (!checkExpedienteAberto())
        	return false;
        
        Key keyExpedienteAberto = obtemKeyExpedienteAberto();
        if (keyExpedienteAberto == null)
        	return false;
        
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Entity regExpediente;
        try {
            // obtém o registro do expediente aberto para atualização
            regExpediente = datastore.get(keyExpedienteAberto);
            // atualiza a propriedade estadoExpediente para fechado
            regExpediente.setProperty(Expediente.KEY_ESTADO_EXPEDIENTE,
                    Expediente.EstadoExpediente.EXPEDIENTE_FECHADO.toString());

            // atualiza registro no datastore
            datastore.put(regExpediente);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
	}

	private Key obtemKeyExpedienteAberto() {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	    // obtém expediente aberto
	    Query qExpediente = new Query("Expediente");
	    
	    Filter filtroAberto = new Query.FilterPredicate(
	    		Expediente.KEY_ESTADO_EXPEDIENTE,
	            FilterOperator.EQUAL,
	            Expediente.EstadoExpediente.EXPEDIENTE_ABERTO.toString());
	    qExpediente.setFilter(filtroAberto);
	    
	    PreparedQuery pqExpediente = ds.prepare(qExpediente);
	    Entity expedienteAberto = null;
	    try {
	    	expedienteAberto = pqExpediente.asSingleEntity();
	    } catch (TooManyResultsException e) {
	    	return null;
	    }
	    
	    return expedienteAberto.getKey();
	}

	private boolean checkExpedienteFechado() {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	    // conta expedientes abertos
	    Query qExpediente = new Query("Expediente");
	    
	    Filter filtroAberto = new Query.FilterPredicate(
	    		Expediente.KEY_ESTADO_EXPEDIENTE,
	            FilterOperator.EQUAL,
	            Expediente.EstadoExpediente.EXPEDIENTE_ABERTO.toString());
	    qExpediente.setFilter(filtroAberto);
	    
	    PreparedQuery pqExpediente = ds.prepare(qExpediente);
	    int contagemExpedientesAbertos = 
	    		pqExpediente.countEntities(FetchOptions.Builder.withDefaults());
	    
	    return contagemExpedientesAbertos == 0;
	}

	private boolean checkExpedienteAberto() {
	    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	    // conta expedientes abertos
	    Query qExpediente = new Query("Expediente");
	    
	    Filter filtroAberto = new Query.FilterPredicate(
	    		Expediente.KEY_ESTADO_EXPEDIENTE,
	            FilterOperator.EQUAL,
	            Expediente.EstadoExpediente.EXPEDIENTE_ABERTO.toString());
	    qExpediente.setFilter(filtroAberto);
	    
	    PreparedQuery pqExpediente = ds.prepare(qExpediente);
	    int contagemExpedientesAbertos = 
	    		pqExpediente.countEntities(FetchOptions.Builder.withDefaults());
	    
	    return contagemExpedientesAbertos != 0;
	}

}
