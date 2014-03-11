<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="br.com.alexromanelli.chuchuajato.server.RegistrarEntregaHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
try {    
    long idPedido = Long.parseLong(request.getParameter("pedido"));
    if (RegistrarEntregaHelper.registrarEntregaPedido(idPedido)) {
%>
<resultado>1</resultado>
<%
    }
    else {
%>
<resultado>0</resultado>
<%
    }
} catch (NumberFormatException e) {
%>
<resultado>0</resultado>
<%
}
%>
