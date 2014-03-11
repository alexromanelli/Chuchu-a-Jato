
package br.com.alexromanelli.chuchuajato.client.services;

import br.com.alexromanelli.chuchuajato.dados.Bebida;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BebidaServiceAsync {

    void getColecaoBebida(String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength,
            AsyncCallback<Bebida[]> callback);

    void getBebida(long id, AsyncCallback<Bebida> callback);

    void insertBebida(Bebida registro, AsyncCallback<Long> callback);

    void updateBebida(Bebida registro, AsyncCallback<Void> callback);

    void deleteBebida(long id, AsyncCallback<Boolean> callback);

    void getQuantidadeBebidasRegistrados(AsyncCallback<Integer> callback);

}
