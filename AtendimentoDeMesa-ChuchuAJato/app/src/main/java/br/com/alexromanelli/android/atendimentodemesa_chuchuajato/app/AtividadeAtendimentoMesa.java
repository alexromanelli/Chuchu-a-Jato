package br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import android.app.ListActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.dados.ItemPedido;
import br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.dados.PedidoMesa;

/**
 * Esta atividade exibe um campo de edição no qual o usuário informa o número da
 * mesa para atendimento. Ao clicar no botão de seleção, os pedidos registrados
 * para aquela mesa que estiverem pendentes são obtidos do servidor remoto e
 * apresentados na tela.
 *
 * Através desta atividade é possível acessar a tela de operação de pedido. Com
 * o item de menu "Registrar novo pedido" é acionada a atividade de operação de
 * pedido para incluir um novo registro de pedido para a mesa informada.
 * Clicando sobre um pedido pendente da listagem, a atividade de operação de
 * pedido é acionada para permitir o registro de entrega do pedido, ou o
 * cancelamento deste.
 *
 * @author Alexandre Romanelli
 *
 */
public class AtividadeAtendimentoMesa extends ListActivity {

    // URL do recurso do servidor remoto usado para obter os pedidos pendentes
    // de uma mesa
    private static final String URL_PEDIDOS_MESA = "http://chuchuajato.alexromanelli.com.br/operacao/pedidosmesa.jsp";

    // objeto usado para armazenar dados dos pedidos pendentes de uma mesa
    private ArrayList<PedidoMesa> pedidosMesa;
    // objeto usado para vincular os dados de pedidos ao elemento da interface
    private SimpleAdapter adapterPedidosMesa;

    // variável usada para armazenar uma referência a um elemento da interface
    private ProgressBar progressIndicador;

    /*
     * handler é um objeto que está sendo usado para que a thread que faz a
     * comunicação com o servidor possa solicitar a execução de métodos dos
     * elementos da interface da atividade.
     */
    private Handler handler = new Handler();

    private int numeroMesa = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_atendimento_mesa);

		/*
		 * Origem e destino são usados para configurar o adaptador do ListView,
		 * sendo origem uma coleção dos nomes das chaves dos campos a exibir, e
		 * destino uma coleção dos identificadores dos elementos da interface
		 * que serão usados para exibir os campos. Esses elementos da interface
		 * são declarados no recurso de layout informado ao construtor do
		 * adaptador.
		 */
        String[] origem = { PedidoMesa.KEY_NOME_ITEM,
                PedidoMesa.KEY_PRECO_ITEM, PedidoMesa.KEY_QUANTIDADE_PEDIDO };
        int[] destino = { R.id.textNomeItem, R.id.textPrecoItem,
                R.id.textQuantidadePedido };
        pedidosMesa = new ArrayList<PedidoMesa>();
        adapterPedidosMesa = new SimpleAdapter(this, pedidosMesa,
                R.layout.linha_pedido_mesa, origem, destino);
        setListAdapter(adapterPedidosMesa);

        progressIndicador = (ProgressBar) findViewById(R.id.progressAtendimentoMesa);

        // configura o evento de clique no botão para selecionar a mesa
        Button buttonNumeroMesa = (Button) findViewById(R.id.buttonNumeroMesa);
        buttonNumeroMesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtemPedidosMesa();
            }
        });
    }

    /**
     * Este método obtém o número da mesa informado pelo usuário no elemento da
     * interface.
     *
     * @return o valor informado pelo usuário, convertido para o tipo int.
     */
    @SuppressWarnings("ConstantConditions")
    private int obtemNumeroMesa() {
        EditText editNumeroMesa = (EditText) findViewById(R.id.edittextNumeroMesa);
        numeroMesa = Integer.parseInt(editNumeroMesa.getText().toString());
        return numeroMesa;
    }

    /**
     * Este método cria um objeto da classe BackgroundTask e faz a chamada ao
     * método de execução deste objeto. Isto implica na criação de uma nova
     * thread para fazer a operação em "background". No caso, a operação é a
     * obtenção dos pedidos pendentes registrados para a mesa selecionada.
     */
    protected void obtemPedidosMesa() {
        new BackgroundTask().execute(Integer.toString(obtemNumeroMesa()));
    }

    /**
     * Carrega o menu com o conteúdo do recurso de menu
     * "atividade_atendimento_mesa".
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.atividade_atendimento_mesa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_registrar_pedido:
                if (numeroMesa != -1) {
                    exibeAtividadeOperacaoPedido(
                            AtividadeOperacaoPedido.REQUEST_CODE_REGISTRAR_NOVO_PEDIDO,
                            null);
                }
                // se não houver mesa selecionada pelo usuário, é exibida mensagem
                // solicitando isto
                else {
                    Toast.makeText(this, "selecione uma mesa", Toast.LENGTH_LONG)
                            .show();
                }
                return true;
            case R.id.action_voltar:
                encerrarAtividade();
                return true;
        }

        return false;
    }

    private void encerrarAtividade() {
        finish();
    }

    /**
     * Este método manipula o evento de clique sobre um item de pedido da lista
     * apresentada. É responsável por disparar a exibição da atividade de
     * operação de pedido para registrar a entrega ou o cancelamento do pedido.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        PedidoMesa p = pedidosMesa.get(position);
        exibeAtividadeOperacaoPedido(
                AtividadeOperacaoPedido.REQUEST_CODE_PROCESSAR_PEDIDO_REGISTRADO,
                p);
    }

    /**
     * Este método faz a exibição da atividade de operação de pedido. Ao acionar
     * a atividade alguns dados são passados pelo Intent, dependendo da origem
     * da execução deste método. Se for para registrar um novo pedido, apenas o
     * número da mesa é necessário. Se for a operação de um pedido já
     * registrado, este o objeto de dados deste pedido é enviado.
     *
     * @param operacao
     *            é a identificação de qual operação deverá ser realizada pela
     *            atividade de operação de pedido.
     * @param p
     *            é o registro de dados do pedido a ser processado.
     */
    private void exibeAtividadeOperacaoPedido(int operacao, PedidoMesa p) {
        Intent i = new Intent(AtividadeAtendimentoMesa.this,
                AtividadeOperacaoPedido.class);
        i.putExtra(AtividadeOperacaoPedido.KEY_OPERACAO, operacao);
        switch (operacao) {
            case AtividadeOperacaoPedido.REQUEST_CODE_PROCESSAR_PEDIDO_REGISTRADO:
                i.putExtra(PedidoMesa.KEY_PEDIDO_MESA, p);
                break;
            case AtividadeOperacaoPedido.REQUEST_CODE_REGISTRAR_NOVO_PEDIDO:
                i.putExtra(PedidoMesa.KEY_NUMERO_MESA, numeroMesa);
                break;
        }

        startActivityForResult(i, operacao);
    }

    /**
     * Processa o resultado da atividade executada a partir desta. Neste caso, é
     * a atividade de operação de pedido. Se for recebida uma confirmação de
     * pedido registrado, os dados de pedidos da mesa selecionada são
     * recarregados.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AtividadeOperacaoPedido.REQUEST_CODE_REGISTRAR_NOVO_PEDIDO:
                switch (resultCode) {
                    case AtividadeOperacaoPedido.RESULT_CODE_PEDIDO_REGISTRADO:
                        obtemPedidosMesa();
                        break;
                }
                break;
            case AtividadeOperacaoPedido.REQUEST_CODE_PROCESSAR_PEDIDO_REGISTRADO:
                switch (resultCode) {
                    case AtividadeOperacaoPedido.RESULT_CODE_PEDIDO_ENTREGUE:
                        obtemPedidosMesa();
                        break;
                    case AtividadeOperacaoPedido.RESULT_CODE_PEDIDO_CANCELADO:
                        obtemPedidosMesa();
                        break;
                }
                break;
        }
    }

    /**
     * A classe BackgroundTask é destinada a executar uma thread separada para
     * fazer a comunicação com o servidor remoto, via HTTP, para carregar os
     * dados de pedidos pendentes de uma mesa selecionada.
     *
     * @author Alexandre Romanelli
     *
     */
    private class BackgroundTask extends
            AsyncTask<String, Void, ArrayList<PedidoMesa>> {

        /**
         * Este método será executado automaticamente quando o objeto desta
         * classe for executado.
         */
        protected ArrayList<PedidoMesa> doInBackground(String... numeroMesa) {
			/*
			 * Uma chamada ao método post de handler permite, por exemplo,
			 * manipular um objeto da interface a partir desta thread separada.
			 * Neste caso, é exibido o indicador de progresso de operação.
			 */
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressIndicador.setVisibility(ProgressBar.VISIBLE);
                }
            });

            // executa o método que obtém os dados do servidor
            ArrayList<PedidoMesa> pedidos = carregaPedidosMesa(Integer
                    .parseInt(numeroMesa[0]));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return pedidos;
        }

        /**
         * Este método será executado quando o resultado da execução do método
         * doInBackground for obtido.
         */
        protected void onPostExecute(ArrayList<PedidoMesa> pedidos) {
            // atualiza dados do listview
            pedidosMesa.clear();
            pedidosMesa.addAll(pedidos);

            // informa ao adaptador a necessidade de notificação de mudança de
            // dados, para que a ListView seja atualizada
            adapterPedidosMesa.notifyDataSetChanged();

            // oculta o indicador de progresso
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressIndicador.setVisibility(ProgressBar.INVISIBLE);
                }
            });
        }

    }

    /**
     * Este método é responsável por obter os dados de pedidos pendentes de uma
     * mesa, armazenados no servidor remoto. Para isto, faz uso do método
     * OpenHttpConnection, que fornece o objeto InputStream da conexão com o
     * recurso de obtenção de pedidos do servidor remoto.
     *
     * @param numeroMesa
     *            é o número da mesa selecionada pelo usuário.
     * @return uma coleção de pedidos pendentes registrados para a mesa
     *         selecionada.
     */
    public ArrayList<PedidoMesa> carregaPedidosMesa(int numeroMesa) {
        ArrayList<PedidoMesa> pedidos = null;
        InputStream in;
        try {
            in = OpenHttpConnection(URL_PEDIDOS_MESA + "?mesa=" + numeroMesa
                    + "&estado=pendente");
            pedidos = obtemPedidosXML(in);
            in.close();
        } catch (final IOException e1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AtividadeAtendimentoMesa.this, e1.getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            });
        }
        return pedidos;
    }

    /**
     * Este método faz a análise de um arquivo XML recebido do servidor remoto.
     * São extraídos deste arquivo os dados da coleção de pedidos pendentes para
     * a mesa selecionada.
     *
     * @param in
     *            é o fluxo de dados por onde é recebido o arquivo XML com os
     *            dados solicitados.
     * @return uma coleção de pedidos pendentes registrados para a mesa
     *         selecionada, obtidos do arquivo XML.
     */
    private ArrayList<PedidoMesa> obtemPedidosXML(InputStream in) {
        ArrayList<PedidoMesa> pedidos = new ArrayList<PedidoMesa>();

        try {
            // prepara a classe analisadora de código xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            // obtém o documento xml estruturado (fornecido pelo analisador de
            // xml)
            Document doc = db.parse(in);

            doc.getDocumentElement().normalize();

            // obtém a listagem de elementos com a tag "prato"
            NodeList itens = doc.getElementsByTagName("prato");

            // percorre cada elemento com a tag "prato" que for encontrado
            for (int i = 0; i < itens.getLength(); i++) {
                // obtém uma referência para um elemento "prato"
                Node nodeItem = itens.item(i);

                // converte a referência de um tipo node para um tipo element
                Element elementItem = (Element) nodeItem;

                // obtém o campo "id"
                NodeList idItem = elementItem.getElementsByTagName("id");
                String strIdItem = idItem.item(0).getFirstChild()
                        .getNodeValue();

                // obtém o campo "nome"
                NodeList nomeItem = elementItem.getElementsByTagName("nome");
                String strNomeItem = nomeItem.item(0).getFirstChild()
                        .getNodeValue();

                // obtém o campo "descricao"
                NodeList descricaoItem = elementItem
                        .getElementsByTagName("descricao");
                String strDescricaoItem = descricaoItem.item(0)
                        .getFirstChild().getNodeValue();

                // obtém o campo "rendimento"
                NodeList rendimentoItem = elementItem
                        .getElementsByTagName("porcoes");
                String strRendimentoItem = rendimentoItem.item(0)
                        .getFirstChild().getNodeValue();

                // obtém o campo "preco"
                NodeList precoItem = elementItem.getElementsByTagName("preco");
                String strPrecoItem = precoItem.item(0)
                        .getFirstChild().getNodeValue();

                // obtém o campo "quantidade"
                NodeList quantidadePedido = elementItem
                        .getElementsByTagName("quantidade");
                String strQuantidadePedido = quantidadePedido.item(0)
                        .getFirstChild().getNodeValue();

                // obtém o campo "idpedido"
                NodeList idPedido = elementItem
                        .getElementsByTagName("idpedido");
                String strIdPedido = idPedido.item(0).getFirstChild()
                        .getNodeValue();

                // inclui na lista de pedidos na memória um novo registro de
                // pedido, com os dados obtidos
                pedidos.add(new PedidoMesa(obtemNumeroMesa(), Integer
                        .parseInt(strIdItem), ItemPedido.TIPO_ITEM_PRATO,
                        strNomeItem, strDescricaoItem, Integer
                        .parseInt(strRendimentoItem), Double
                        .parseDouble(strPrecoItem), Integer
                        .parseInt(strQuantidadePedido), Integer
                        .parseInt(strIdPedido)));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return pedidos;
    }

    /**
     * Este método faz a abertura de uma conexão HTTP com um servidor remoto. É
     * usada para isto uma mensagem HTTP GET, enviada para a URL recebida.
     *
     * @param urlString
     *            é a URL que identifica o recurso remoto a ser acionado.
     * @return o fluxo de dados para que se possa receber a resposta do
     *         servidor.
     * @throws IOException
     *             é lançada uma exceção em caso de falha na conexão.
     */
    private InputStream OpenHttpConnection(String urlString) throws IOException {
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
            if (HttpURLConnection.HTTP_OK == response) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }


}
