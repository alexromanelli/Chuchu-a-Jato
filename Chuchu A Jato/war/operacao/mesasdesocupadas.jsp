<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="com.google.appengine.api.datastore.Query.CompositeFilterOperator"%>
<%@page import="com.google.appengine.api.datastore.Query.FilterOperator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.EntityNotFoundException" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.PreparedQuery" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.Query.Filter" %>
<%@ page import="com.google.appengine.api.datastore.Query.FilterPredicate" %>
<%@ page import="com.google.appengine.api.datastore.Query.CompositeFilter" %>
<%@ page import="com.google.appengine.api.datastore.Text" %>
<%@ page import="com.google.appengine.api.datastore.Query.SortDirection" %>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.Mesa" %>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.Expediente" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Arrays" %>
<mesas>
<%
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

    // obtém o identificador do expediente aberto, se houver
    Query qExpediente = new Query("Expediente");
    Filter filtroExpedienteAberto = new FilterPredicate(
    		Expediente.KEY_ESTADO_EXPEDIENTE,
    		FilterOperator.EQUAL, 
    		Expediente.EstadoExpediente.EXPEDIENTE_ABERTO.toString());
    qExpediente.setFilter(filtroExpedienteAberto);
    
    qExpediente.addSort(Expediente.KEY_DATA_ABERTURA, SortDirection.DESCENDING);
    PreparedQuery pqExpediente = ds.prepare(qExpediente);
    List<Entity> listExpediente = pqExpediente
    		.asList(FetchOptions.Builder.withLimit(1));
    
    if (listExpediente.size() > 0) {
        long idExpediente = 
        		((Long)listExpediente.get(0).getKey().getId()).longValue();

        Query q = new Query("Mesa");
	    Filter filtro = new CompositeFilter(CompositeFilterOperator.AND, 
	    		Arrays.<Filter>asList(
		    		new FilterPredicate(Mesa.KEY_ID_EXPEDIENTE,
		    		                    FilterOperator.EQUAL,
		    		                    idExpediente),
                    new FilterPredicate(Mesa.KEY_ESTADO_MESA,
                                        FilterOperator.EQUAL,
                                        Mesa.EstadoMesa.MESA_LIVRE)));
	    q.setFilter(filtro);
	    q.addSort(Mesa.KEY_NUMERO_MESA, SortDirection.ASCENDING);
	    PreparedQuery pq = ds.prepare(q);
	    List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());

	    for (Entity e : l) {
%>
    <mesa><%= ((Long) e.getProperty(Mesa.KEY_NUMERO_MESA)).toString() %></mesa>
<%
        }
    }
%>
</mesas>
