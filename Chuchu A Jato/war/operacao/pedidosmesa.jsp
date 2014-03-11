<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="br.com.alexromanelli.chuchuajato.server.PedidosMesaHelper"%>
<%@page import="com.google.appengine.api.datastore.Query.CompositeFilterOperator"%>
<%@ page language="java" contentType="text/xml; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.Prato" %>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.Bebida" %>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.ItemCardapio" %>
<%@ page import="br.com.alexromanelli.chuchuajato.dados.ViewPedidoItemCardapio" %>
<%@ page import="java.util.ArrayList" %>

<pedidos>
    <pratos>
<%
	int numeroMesa = 0;

	String paramMesa = request.getParameter("mesa");
	if ("geral".equals(paramMesa)) {
		// buscar os pedidos de todas as mesas
		numeroMesa = PedidosMesaHelper.TODAS_AS_MESAS;
	}
	else {
    	numeroMesa = Integer.parseInt(request.getParameter("mesa"));
	}
	
    String estado = request.getParameter("estado");
    
    ArrayList<ViewPedidoItemCardapio> v = 
    		PedidosMesaHelper.getListaPedidosMesa(numeroMesa, estado);
    
    for (ViewPedidoItemCardapio item : v) {
    	if (item.getItem().getTipo() == ItemCardapio.TIPO_ITEM_PRATO) {
    		int quantidade = item.getPedido().getQuantidade();
    		long idPedido = item.getPedido().getId();
    		Prato prato = (Prato)item.getItem();
    		int numMesa = item.getNumeroMesa();
    %>
            <prato>
                <id><%= prato.getId() %></id>
                <nome><%= prato.getNome() %></nome>
                <descricao><%= prato.getDescricao() %></descricao>
                <porcoes><%= prato.getRendimentoPorcoes() %></porcoes>
                <preco><%= prato.getPreco() %></preco>
                <quantidade><%= quantidade %></quantidade>
                <idpedido><%= idPedido %></idpedido>
<%
			if (numeroMesa == PedidosMesaHelper.TODAS_AS_MESAS) {
%>
        		<mesa><%= numMesa %></mesa>
<%
			}
%>
            </prato>
<%
    	}
    }
%>
    </pratos>
    <bebidas>
<%
    for (ViewPedidoItemCardapio item : v) {
        if (item.getItem().getTipo() == ItemCardapio.TIPO_ITEM_BEBIDA) {
            int quantidade = item.getPedido().getQuantidade();
            long idPedido = item.getPedido().getId();
        	Bebida bebida = (Bebida)item.getItem();
        	int numMesa = item.getNumeroMesa();
%>
            <bebida>
                <id><%= bebida.getId() %></id>
                <nome><%= bebida.getNome() %></nome>
                <descricao><%= bebida.getDescricao() %></descricao>
                <doses><%= bebida.getRendimentoDoses() %></doses>
                <preco><%= bebida.getPreco() %></preco>
                <quantidade><%= quantidade %></quantidade>
                <idpedido><%= idPedido %></idpedido>
<%
			if (numeroMesa == PedidosMesaHelper.TODAS_AS_MESAS) {
%>
        		<mesa><%= numMesa %></mesa>
<%
			}
%>
            </bebida>
<%
        }
    }
%>
    </bebidas>
</pedidos>    