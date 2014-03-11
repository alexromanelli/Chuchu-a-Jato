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
<%@ page import="br.com.alexromanelli.chuchuajato.dados.Bebida" %>
<%@ page import="java.util.List" %>
<bebidas>
<%
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    Query q = new Query("Bebida");
    
    // define a ordenação na query
    q.addSort(Bebida.KEY_NOME, SortDirection.ASCENDING);
    PreparedQuery pq = ds.prepare(q);
    
    // define o intervalo a considerar
    List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());
    
    for (Entity e : l) {
        Bebida b = new Bebida(e.getKey().getId(),
                (String) e.getProperty(Bebida.KEY_NOME),
                ((Text) e.getProperty(Bebida.KEY_DESCRICAO)).getValue(),
                ((Long) e.getProperty(Bebida.KEY_RENDIMENTO)).intValue(),
                (Double) e.getProperty(Bebida.KEY_PRECO));
%>
    <bebida>
        <id><%= b.getId() %></id>
        <nome><%= b.getNome() %></nome>
        <descricao><%= b.getDescricao() %></descricao>
        <doses><%= b.getRendimentoDoses() %></doses>
        <preco><%= b.getPreco() %></preco>
    </bebida>
<%
    }
%>
</bebidas>
