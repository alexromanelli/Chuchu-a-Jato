package br.com.alexromanelli.chuchuajato.dados;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Mesa implements Serializable {
	
	public static class EstadoMesa {
		public static final String[] TEXTO_ESTADO_MESA = {
			"livre",
			"desocupada"
		};
		public static final int MESA_LIVRE = 0;
		public static final int MESA_OCUPADA = 1;
	}
	
	public static String KEY_ID = "id";
	public static String KEY_NUMERO_MESA = "numeroMesa";
	public static String KEY_ID_EXPEDIENTE = "idExpediente";
	public static String KEY_ESTADO_MESA = "estadoMesa";
	
	private long id;
	private int numeroMesa;
	private long idExpediente;
	private int estadoMesa;
	
	public Mesa(long id, int numeroMesa, long idExpediente, int estadoMesa) {
		super();
		this.id = id;
		this.numeroMesa = numeroMesa;
		this.idExpediente = idExpediente;
		this.estadoMesa = estadoMesa;
	}
	
	public Mesa() {
		super();
	}
	
	public int getNumeroMesa() {
		return numeroMesa;
	}
	
	public void setNumeroMesa(int numeroMesa) {
		this.numeroMesa = numeroMesa;
	}
	
	public long getIdExpediente() {
		return idExpediente;
	}
	
	public void setIdExpediente(long idExpediente) {
		this.idExpediente = idExpediente;
	}
	
	public long getId() {
		return id;
	}
	
	public int getEstadoMesa() {
		return estadoMesa;
	}

	public void setEstadoMesa(int estadoMesa) {
		this.estadoMesa = estadoMesa;
	}
	
}
