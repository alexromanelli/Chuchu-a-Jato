
package br.com.alexromanelli.chuchuajato.client.services;

import br.com.alexromanelli.chuchuajato.dados.Bebida;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("BebidaService")
public interface BebidaService extends RemoteService {
    /**
     * Utility class for simplifying access to the instance of async service.
     */
    public static class Util {
        private static BebidaServiceAsync instance;

        public static BebidaServiceAsync getInstance() {
            if (instance == null) {
                instance = GWT.create(BebidaService.class);
            }
            return instance;
        }
    }

    /**
     * Este método é responsável por obter uma coleção de registros de bebidas
     * do armazenamento de dados. Esta coleção deve ser entregue ordenada, de
     * acordo com o critério de ordenação (atributo) e sentido, e restrita a um
     * intervalo de registros, de uma posição inicial da coleção ordenada,
     * formando uma sequência com um número máximo determinado de elementos.
     * 
     * @param sortField
     *            Define o atributo da classe Bebida que será usado como
     *            critério de ordenação.
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
     *         bebidas.
     */
    Bebida[] getColecaoBebida(String sortField, boolean sortAscending,
            int visibleRangeStart, int visibleRangeLength);

    long insertBebida(Bebida registro);

    void updateBebida(Bebida registro);

    boolean deleteBebida(long id);

    int getQuantidadeBebidasRegistrados();

    Bebida getBebida(long id);

}
