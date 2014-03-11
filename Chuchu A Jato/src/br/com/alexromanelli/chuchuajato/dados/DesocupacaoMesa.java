package br.com.alexromanelli.chuchuajato.dados;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class DesocupacaoMesa implements Serializable {
	
	public static String KEY_ID = "id";
	public static String KEY_ID_MESA = "idMesa";
	public static String KEY_HORARIO = "horario";
	
	private int id;
	private int idMesa;
	private Date horario;
	
	public DesocupacaoMesa(int id, int idMesa, Date horario) {
		super();
		this.id = id;
		this.idMesa = idMesa;
		this.horario = horario;
	}

	public int getIdMesa() {
		return idMesa;
	}

	public void setIdMesa(int idMesa) {
		this.idMesa = idMesa;
	}

	public Date getHorario() {
		return horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}

	public int getId() {
		return id;
	}
	
	
}
