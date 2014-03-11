<?xml version="1.0" encoding="UTF-8" ?>
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
<%@ page import="com.google.appengine.api.datastore.Text" %>
<%@ page import="com.google.appengine.api.datastore.Query.SortDirection" %>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.Prato" %>
<%@ page import="java.util.List" %>
<pratos>
<%
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	Query q = new Query("Prato");
	
	// define a ordenação na query
	q.addSort(Prato.KEY_NOME, SortDirection.ASCENDING);
	PreparedQuery pq = ds.prepare(q);
	
	// define o intervalo a considerar
	List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());
	
	for (Entity e : l) {
        Prato p = new Prato(e.getKey().getId(),
                (String) e.getProperty(Prato.KEY_NOME),
                ((Text) e.getProperty(Prato.KEY_DESCRICAO)).getValue(),
                ((Long) e.getProperty(Prato.KEY_RENDIMENTO)).intValue(),
                (Double) e.getProperty(Prato.KEY_PRECO));
%>
    <prato>
        <id><%= p.getId() %></id>
        <nome><%= p.getNome() %></nome>
        <descricao><%= p.getDescricao() %></descricao>
        <porcoes><%= p.getRendimentoPorcoes() %></porcoes>
        <preco><%= p.getPreco() %></preco>
    </prato>
<%
	}
%>
</pratos>
