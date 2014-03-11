<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="br.com.alexromanelli.chuchuajato.server.RegistrarPedidoHelper"%>
<%@page import="br.com.alexromanelli.chuchuajato.dados.ItemCardapio"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    // obter parâmetros
    String numeroMesa = request.getParameter("mesa");
    String prato = request.getParameter("prato");
    String bebida = request.getParameter("bebida");
    String quantidade = request.getParameter("quantidade");
    int tipoItem = -1;
    String idItem = ""; 
    if (prato != null) {
    	tipoItem = ItemCardapio.TIPO_ITEM_PRATO;
    	idItem = prato;
    }
    else if (bebida != null) {
    	tipoItem = ItemCardapio.TIPO_ITEM_BEBIDA;
    	idItem = bebida;
    }

    // efetua registro
    if (RegistrarPedidoHelper.registrarPedido(numeroMesa, tipoItem, 
    		idItem, quantidade)) {
%>
<resultado>1</resultado>
<%    	
    }
    else {
%>
<resultado>0</resultado>
<%      
    }
%>