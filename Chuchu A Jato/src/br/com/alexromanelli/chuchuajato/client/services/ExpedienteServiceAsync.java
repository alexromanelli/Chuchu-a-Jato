
package br.com.alexromanelli.chuchuajato.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExpedienteServiceAsync {

	void abrirExpediente(int numeroMesas, AsyncCallback<Boolean> callback);
	
	void fecharExpediente(AsyncCallback<Boolean> callback);
	
    void getNumeroMesas(AsyncCallback<Integer> callback);

    void getEstadoExpediente(AsyncCallback<Character> callback);

}
