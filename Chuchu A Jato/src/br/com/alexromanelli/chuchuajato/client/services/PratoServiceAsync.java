
package br.com.alexromanelli.chuchuajato.client.services;

import br.com.alexromanelli.chuchuajato.dados.Prato;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PratoServiceAsync {

    void getColecaoPrato(String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength,
            AsyncCallback<Prato[]> callback);

    void getPrato(long id, AsyncCallback<Prato> callback);

    void insertPrato(Prato registro, AsyncCallback<Long> callback);

    void updatePrato(Prato registro, AsyncCallback<Void> callback);

    void deletePrato(long id, AsyncCallback<Boolean> callback);

    void getQuantidadePratosRegistrados(AsyncCallback<Integer> callback);

}
