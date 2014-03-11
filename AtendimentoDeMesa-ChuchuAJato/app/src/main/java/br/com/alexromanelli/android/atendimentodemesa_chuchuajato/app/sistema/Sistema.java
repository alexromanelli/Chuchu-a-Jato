package br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.sistema;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by alexandre on 3/7/14.
 */
public class Sistema {

    /**
     * Este método faz a abertura de uma conexão HTTP com um servidor remoto. É
     * usada para isto uma mensagem HTTP GET, enviada para a URL recebida.
     *
     * @param urlString
     *            é a URL que identifica o recurso remoto a ser acionado.
     * @return o fluxo de dados para que se possa receber a resposta do
     *         servidor.
     * @throws java.io.IOException
     *             é lançada uma exceção em caso de falha na conexão.
     */
    public static InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response;

        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }


}
