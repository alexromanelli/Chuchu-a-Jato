
package br.com.alexromanelli.chuchuajato.dados;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class Expediente implements Serializable {

    public static class EstadoExpediente {
        public static final Character EXPEDIENTE_ABERTO = new Character('A');
        public static final Character EXPEDIENTE_FECHADO = new Character('F');
    }

    public static String KEY_ID = "id";
    public static String KEY_DATA_ABERTURA = "dataAbertura";
    public static String KEY_NUMERO_MESAS = "numeroMesas";
    public static String KEY_ESTADO_EXPEDIENTE = "estadoExpediente";

    private long id;
    private Date dataAbertura;
    private int numeroMesas;
    private Character estadoExpediente;

    public Expediente() {
		super();
	}

	public Expediente(long id, Date dataAbertura, int numeroMesas,
            Character estadoExpediente) {
        super();
        this.id = id;
        this.dataAbertura = dataAbertura;
        this.numeroMesas = numeroMesas;
        this.estadoExpediente = estadoExpediente;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public int getNumeroMesas() {
        return numeroMesas;
    }

    public void setNumeroMesas(int numeroMesas) {
        this.numeroMesas = numeroMesas;
    }

    public Character getEstadoExpediente() {
        return estadoExpediente;
    }

    public void setEstadoExpediente(Character estadoExpediente) {
        this.estadoExpediente = estadoExpediente;
    }

    public long getId() {
        return id;
    }

}
