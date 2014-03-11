
package br.com.alexromanelli.chuchuajato.client.services;

import br.com.alexromanelli.chuchuajato.dados.Prato;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("PratoService")
public interface PratoService extends RemoteService {
    /**
     * Utility class for simplifying access to the instance of async service.
     */
    public static class Util {
        private static PratoServiceAsync instance;

        public static PratoServiceAsync getInstance() {
            if (instance == null) {
                instance = GWT.create(PratoService.class);
            }
            return instance;
        }
    }

    /**
     * Este método é responsável por obter uma coleção de registros de pratos do
     * armazenamento de dados. Esta coleção deve ser entregue ordenada, de
     * acordo com o critério de ordenação (atributo) e sentido, e restrita a um
     * intervalo de registros, de uma posição inicial da coleção ordenada,
     * formando uma sequência com um número máximo determinado de elementos.
     * 
     * @param sortField
     *            Define o atributo da classe Prato que será usado como critério
     *            de ordenação.
     * @param sortAscending
     *            Define se a ordenação será feita do menor valor para o maior
     *            valor. Falso significa ordem decrescente.
     * @param visibleRangeStart
     *            Início do intervalo de registros que compõem a coleção a
     *            retornar. Esta posição é relativa ao início da sequência de
     *            registros ordenados com o critério informado.
     * @param visibleRangeLength
     *            Comprimento do intervalo de registros que compõem a coleção a
     *            retornar. Indica a quantidade de registros, a partir da
     *            posição inicial do intervalo, que serão incluídos na sequência
     *            a ser retornada pelo método.
     * @return O valor retornado por este método é um vetor de registros de
     *         pratos.
     */
    Prato[] getColecaoPrato(String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength);

    long insertPrato(Prato registro);

    void updatePrato(Prato registro);

    boolean deletePrato(long id);

    int getQuantidadePratosRegistrados();

    Prato getPrato(long id);

}
