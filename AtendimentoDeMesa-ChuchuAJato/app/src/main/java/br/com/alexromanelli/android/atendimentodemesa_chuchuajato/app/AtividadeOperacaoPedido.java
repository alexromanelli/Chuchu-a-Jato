package br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.dados.ItemPedido;
import br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.dados.PedidoMesa;
import br.com.alexromanelli.android.atendimentodemesa_chuchuajato.app.sistema.Sistema;

/**
 * Esta atividade é responsável por apresentar um formulário de registro, e
 * possibilitar a execução das seguintes operações:<br/>
 * <ul>
 * <li>Registro de novo pedido;</li>
 * <li>Registro de entrega de pedido;</li>
 * <li>Cancelamento de pedido.</li>
 * </ul>
 *
 * @author Alexandre Romanelli
 *
 */
public class AtividadeOperacaoPedido extends ActionBarActivity {

    // endereços dos recursos web usados nesta atividade
    private static final String URL_ITENS_PRATOS = "http://chuchuajato.alexromanelli.com.br/operacao/pratos.jsp";
    private static final String URL_ITENS_BEBIDAS = "http://chuchuajato.alexromanelli.com.br/operacao/bebidas.jsp";
    private static final String URL_REGISTRAR_PEDIDO = "http://chuchuajato.alexromanelli.com.br/operacao/registrarpedido.jsp";
    // os seguintes endereços ainda não são usados, mas devem ser
    private static final String URL_REGISTRAR_ENTREGA = "http://chuchuajato.alexromanelli.com.br/operacao/registrarentrega.jsp";
    private static final String URL_CANCELAR_PEDIDO = "http://chuchuajato.alexromanelli.com.br/operacao/cancelarpedido.jsp";

    // constantes para identificar itens de menu
    private static final int MENU_ITEM_REGISTRAR_ENTREGA = Menu.FIRST;
    private static final int MENU_ITEM_CANCELAR_PEDIDO = Menu.FIRST + 1;

    // constantes usadas para controlar instruções de abertura e encerramento da
    // atividade
    public static final int REQUEST_CODE_REGISTRAR_NOVO_PEDIDO = 1;
    public static final int REQUEST_CODE_PROCESSAR_PEDIDO_REGISTRADO = 2;
    public static final int RESULT_CODE_PEDIDO_REGISTRADO = 0;
    public static final int RESULT_CODE_PEDIDO_ENTREGUE = 1;
    public static final int RESULT_CODE_PEDIDO_CANCELADO = 2;
    public static final String KEY_OPERACAO = "operacao";

    private int numeroMesa;

    private Spinner spinnerItemPedido;
    private EditText editDescricaoItem;
    private EditText editRendimento;
    private EditText editPrecoItem;
    private EditText editQuantidade;

    private ProgressBar progressIndicador;

    /*
     * O objeto handler é usado para possibilitar que a thread que realiza a
     * comunicação via HTTP com o servidor remoto acesse os elementos da
     * interface, que são controlados pela thread principal da atividade.
     */
    private Handler handler = new Handler();

    // variável usada para armazenar um objeto de pedido referente aos dados do
    // formulário
    private PedidoMesa pedido;

    // variáveis para armazenamento de dados do spinner de itens
    private ArrayList<ItemPedido> listItemPedido;
    private ArrayAdapter<ItemPedido> adapterItemPedido;

    // indicador do tipo de item do pedido
    private char tipoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atividade_operacao_pedido);

        // obtém referências de elementos do layout
        EditText editNumeroMesa = (EditText) findViewById(R.id.edittextPedidoMesa);
        spinnerItemPedido = (Spinner) findViewById(R.id.spinnerPedidoItem);
        editDescricaoItem = (EditText) findViewById(R.id.edittextPedidoDescricaoItem);
        editRendimento = (EditText) findViewById(R.id.edittextPedidoRendimento);
        editPrecoItem = (EditText) findViewById(R.id.edittextPedidoPreco);
        editQuantidade = (EditText) findViewById(R.id.edittextPedidoQuantidade);
        Button buttonRegistrar = (Button) findViewById(R.id.buttonPedidoSalvar);
        progressIndicador = (ProgressBar) findViewById(R.id.progressOperacaoPedido);

        buttonRegistrar.setEnabled(true);

        // configura spinner de itens
        listItemPedido = new ArrayList<ItemPedido>();
        adapterItemPedido = new ArrayAdapter<ItemPedido>(this,
                android.R.layout.simple_spinner_item, listItemPedido);
        spinnerItemPedido.setAdapter(adapterItemPedido);

        // carregar dados de itens para o spinner
        obtemDadosItens();

        // obtém os dados do pedido que estão no bundle
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int operacao = extras.getInt(KEY_OPERACAO);
            switch (operacao) {

                // se a operação for sobre um pedido já registrado, os dados deste
                // pedido são apresentados
                case REQUEST_CODE_PROCESSAR_PEDIDO_REGISTRADO:
                    pedido = new PedidoMesa(
                            (HashMap<String, String>) extras
                                    .get(PedidoMesa.KEY_PEDIDO_MESA));

                    numeroMesa = pedido.getNumeroMesa();
                    editNumeroMesa.setText(Integer.toString(numeroMesa));
                    editDescricaoItem.setText(pedido.getDescricaoItem());
                    editRendimento.setText(Integer.toString(pedido
                            .getRendimentoItem()));
                    editPrecoItem.setText(Double.toString(pedido.getPrecoItem()));
                    editQuantidade.setText(Integer.toString(pedido
                            .getQuantidadePedido()));

                    // neste caso, não deve ser disponibilizado o botão de registrar
                    // o pedido
                    buttonRegistrar.setEnabled(false);

                    break;

                // se a operação for de registro de um novo pedido, apenas o número
                // da mesa é conhecido
                case REQUEST_CODE_REGISTRAR_NOVO_PEDIDO:
                    numeroMesa = extras.getInt(PedidoMesa.KEY_NUMERO_MESA);
                    editNumeroMesa.setText(Integer.toString(numeroMesa));
                    break;
            }
        }

        // define evento de clique no botão "registrar"
        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executaOperacaoRegistrarPedido();
            }
        });

        // define evento de mudança de seleção de item do spinner
        spinnerItemPedido
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapter,
                                               View comp, int pos, long id) {
                        exibeDetalhesItem(listItemPedido.get(pos));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapter) {
                    }
                });
    }

    /**
     * Este método exibe no formulário os dados de um item. Normalmente, este
     * método será executado juntamente com o evento de seleção do spinner que
     * apresenta os itens do cardápio.
     *
     * @param itemPedido
     *            é o item que será apresentado em detalhes.
     */
    protected void exibeDetalhesItem(ItemPedido itemPedido) {
        editDescricaoItem.setText(itemPedido.getDescricao());
        editRendimento.setText(Integer.toString(itemPedido.getRendimento()));
        editPrecoItem.setText(Double.toString(itemPedido.getPreco()));
    }

    /**
     * Este método resume a execução da operação de registro de pedido. Consiste
     * em atualizar o objeto de pedido com os dados do formulário e enviar este
     * objeto para a thread que faz a devida comunicação via HTTP com o servidor
     * que armazena os dados.
     */
    protected void executaOperacaoRegistrarPedido() {
        atualizaPedidoFormulario();
        new RegistroPedidoTask().execute(pedido);
    }

    protected void executaOperacaoRegistrarEntregaPedido() {
        new RegistraEntregaTask().execute(pedido);
    }

    protected void executaOperacaoCancelarPedido() {
        new CancelaPedidoTask().execute(pedido);
    }

    /**
     * Este método resume a execução do download de itens de cardápio. Isto é
     * feito obtendo o tipo de item que deve ser exibido (prato ou bebida) e
     * aciona a thread que faz a comunicação para este fim, via HTTP, com o
     * servidor que armazena os dados.
     */
    private void obtemDadosItens() {
        tipoItem = ItemPedido.TIPO_ITEM_PRATO;
        new DownloadListagemItensTask().execute(Character.toString(tipoItem));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.atividade_operacao_pedido, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_registrar_atendimento:
                showDialog(MENU_ITEM_REGISTRAR_ENTREGA);
                return true;
            case R.id.action_cancelar:
                showDialog(MENU_ITEM_CANCELAR_PEDIDO);
                return true;
        }

        return false;
    }

    /**
     * Este método faz a exibição de janelas de diálogo com o usuário. No caso
     * desta atividade, as possibilidades são:
     * <ul>
     * <li>Confirmação de entrega de pedido;</li>
     * <li>Confirmação de cancelamento de pedido.</li>
     * </ul>
     * Ao confirmar uma operação, é chamado o método responsável por executar a
     * operação confirmada.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {

            case MENU_ITEM_REGISTRAR_ENTREGA:
                return new AlertDialog.Builder(this)
                        .setTitle("Confirmação")
                        .setMessage("Confirma o registro da entrega do pedido?")
                        .setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        executaOperacaoRegistrarEntregaPedido();
                                    }
                                })
                        .setNegativeButton("Não",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();

            case MENU_ITEM_CANCELAR_PEDIDO:
                return new AlertDialog.Builder(this)
                        .setTitle("Confirmação")
                        .setMessage("Confirma o cancelamento do pedido?")
                        .setPositiveButton("Sim",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        executaOperacaoCancelarPedido();
                                    }
                                })
                        .setNegativeButton("Não",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }).create();
        }
        return super.onCreateDialog(id);
    }

    /**
     * A classe interna DownloadListagemItensTask é destinada a executar uma
     * thread separada para obter os dados de itens de cardápio do servidor, via
     * HTTP.
     *
     * @author Alexandre Romanelli
     *
     */
    private class DownloadListagemItensTask extends
            AsyncTask<String, Integer, ArrayList<ItemPedido>> {

        /**
         * Este método será executado automaticamente quando o objeto desta
         * classe for executado.
         */
        protected ArrayList<ItemPedido> doInBackground(String... tipoItem) {
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

            // executa método que obtém os dados via HTTP
            ArrayList<ItemPedido> itensPedido = carregaItensPedido(tipoItem[0]
                    .charAt(0));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return itensPedido;
        }

        protected void onProgressUpdate(Integer... progresso) {
            //setProgressoDownloadListagemItens(progresso[0]);
        }

        /**
         * Este método será executado quando o resultado da execução do método
         * doInBackground for obtido.
         */
        protected void onPostExecute(ArrayList<ItemPedido> itensPedido) {
            // atualiza dados de itens de cardápio
            listItemPedido.clear();
            listItemPedido.addAll(itensPedido);

            // informa ao adaptador a necessidade de notificação de mudança de
            // dados, para que a ListView seja atualizada
            adapterItemPedido.notifyDataSetChanged();

            // ajusta seleção do spinner para a posição do item do pedido
            // exibido (se houver pedido exibido)
            if (pedido != null) {
                int pos = 0;
                for (ItemPedido i : listItemPedido)
                    if (((int) i.getId()) == pedido.getIdItem())
                        break;
                    else
                        pos++;

                if (pos < listItemPedido.size())
                    spinnerItemPedido.setSelection(pos, true);
            }

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
     * Este método simplesmente exibe uma mensagem de aviso para o usuário, com
     * Toast.
     *
     * @param mensagem
     *            é a mensagem a ser exibida na tela.
     */
    private void exibeMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }

    /**
     * A classe RegistraEntregaTask é destinada a executar uma thread separada
     * para acionar o recurso do servidor de dados, via HTTP, para registrar a
     * entrega de um pedido.
     *
     * @author Alexandre Romanelli
     *
     */
    private class RegistraEntregaTask extends
            AsyncTask<PedidoMesa, Void, Integer> {

        /**
         * Este método será executado automaticamente quando o objeto desta
         * classe for executado.
         */
        @Override
        protected Integer doInBackground(PedidoMesa... params) {
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

            // executa o método que faz a comunicação via HTTP para registrar a
            // entrega do pedido
            Integer resposta = registraEntregaPedido();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return resposta;
        }

        /**
         * Este método será executado quando o resultado da execução do método
         * doInBackground for obtido.
         */
        @Override
        protected void onPostExecute(Integer result) {
            switch (result.intValue()) {
                // se a resposta for positiva, encerra a atividade e exibe mensagem
                // de sucesso
                case 1:
                    encerraAtividade(RESULT_CODE_PEDIDO_ENTREGUE);
                    break;

                // se a resposta for negativa, exibe mensagem de falha
                case 0:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            exibeMensagem("entrega de pedido não foi registrada. tente novamente.");
                        }
                    });
                    break;
            }
        }

    }

    /**
     * A classe CancelaPedidoTask é destinada a executar uma thread separada
     * para acionar o recurso do servidor de dados, via HTTP, para cancelar um
     * pedido.
     *
     * @author Alexandre Romanelli
     *
     */
    private class CancelaPedidoTask extends
            AsyncTask<PedidoMesa, Void, Integer> {

        /**
         * Este método será executado automaticamente quando o objeto desta
         * classe for executado.
         */
        @Override
        protected Integer doInBackground(PedidoMesa... params) {
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

            // executa o método que faz a comunicação via HTTP para cancelar o
            // pedido
            Integer resposta = cancelaPedido();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return resposta;
        }

        /**
         * Este método será executado quando o resultado da execução do método
         * doInBackground for obtido.
         */
        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                // se a resposta for positiva, encerra a atividade e exibe mensagem
                // de sucesso
                case 1:
                    encerraAtividade(RESULT_CODE_PEDIDO_CANCELADO);
                    break;

                // se a resposta for negativa, exibe mensagem de falha
                case 0:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            exibeMensagem("pedido não foi cancelado. tente novamente.");
                        }
                    });
                    break;
            }
        }

    }

    /**
     * A classe RegistroPedidoTask é destinada a executar uma thread separada
     * para acionar o recurso do servidor de dados, via HTTP, para registrar um
     * pedido.
     *
     * @author Alexandre Romanelli
     *
     */
    private class RegistroPedidoTask extends
            AsyncTask<PedidoMesa, Void, Integer> {

        /**
         * Este método será executado automaticamente quando o objeto desta
         * classe for executado.
         */
        @Override
        protected Integer doInBackground(PedidoMesa... params) {
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

            // executa o método que faz a comunicação via HTTP para registrar o
            // pedido
            Integer resposta = registraPedido(params[0]);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return resposta;
        }

        /**
         * Este método será executado quando o resultado da execução do método
         * doInBackground for obtido.
         */
        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                // se a resposta for positiva, encerra a atividade e exibe mensagem
                // de sucesso
                case 1:
                    encerraAtividade(RESULT_CODE_PEDIDO_REGISTRADO);
                    break;

                // se a resposta for negativa, exibe mensagem de falha
                case 0:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            exibeMensagem("pedido não foi registrado. tente novamente.");
                        }
                    });
                    break;
            }
        }

    }

    /**
     * Este método é responsável por obter os dados de itens de cardápio do
     * servidor remoto. Para isto, faz uso do método OpenHttpConnection, que
     * fornece o objeto InputStream da conexão com o recurso de obtenção de
     * itens do servidor remoto.
     *
     * @param tipoItem
     *            é um caractere que identifica se os itens a carregar são
     *            pratos ou bebidas.
     * @return Uma coleção de itens de cardápio, extraídos do arquivo XML
     *         fornecido pelo servidor remoto pelo método obtemItensPedidoXML.
     */
    public ArrayList<ItemPedido> carregaItensPedido(char tipoItem) {
        ArrayList<ItemPedido> itensPedido = null;
        InputStream in = null;
        try {
            switch (tipoItem) {
                case ItemPedido.TIPO_ITEM_PRATO:
                    in = Sistema.OpenHttpConnection(URL_ITENS_PRATOS);
                    break;
                case ItemPedido.TIPO_ITEM_BEBIDA:
                    in = Sistema.OpenHttpConnection(URL_ITENS_BEBIDAS);
                    break;
            }
            itensPedido = obtemItensPedidoXML(in);
            if (in != null) {
                in.close();
            }
        } catch (final IOException e1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AtividadeOperacaoPedido.this, e1.getLocalizedMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            });
            e1.printStackTrace();
        }
        return itensPedido;
    }

    /**
     * Procede o encerramento da atividade atual, e exibe uma mensagem escolhida
     * de acordo com o código de informação recebido. Este código também é
     * repassado como resultado da execução desta atividade.
     *
     * @param codigo
     *            é o código de informação que define a mensagem a ser exibida.
     */
    public void encerraAtividade(int codigo) {
        switch (codigo) {
            case RESULT_CODE_PEDIDO_REGISTRADO:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        exibeMensagem("pedido registrado com sucesso.");
                    }
                });
                break;
            case RESULT_CODE_PEDIDO_CANCELADO:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        exibeMensagem("pedido cancelado com sucesso.");
                    }
                });
                break;
            case RESULT_CODE_PEDIDO_ENTREGUE:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        exibeMensagem("entrega de pedido registrada com sucesso.");
                    }
                });
                break;
        }
        setResult(codigo);
        finish();
    }

    /**
     * O método registraEntregaPedido é responsável por fazer uma chamada ao recurso do
     * servidor remoto que é disponibilizado para fazer registrar as entregas de
     * pedidos. O resultado informado pelo servidor remoto é analisado com uma
     * chamada ao método obtemConfirmacaoXML.
     *
     * @return o valor informado pelo servidor como resposta à requisição de
     *         registro de entrega de pedido.
     */
    public Integer registraEntregaPedido() {
        Integer resultado = 0;

        try {
			/*
			 * faz a requisição de registro ao servidor, enviando uma query
			 * string com os parâmetros necessários
			 */
            InputStream resposta = Sistema.OpenHttpConnection(URL_REGISTRAR_ENTREGA
                    + "?pedido="
                    + Integer.toString(pedido.getIdPedido()));

            // faz a análise do resultado
            resultado = obtemConfirmacaoXML(resposta);
            resposta.close();
        } catch (IOException e1) {
            Toast.makeText(this, e1.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            e1.printStackTrace();
        }

        return resultado;
    }

    /**
     * O método cancelaPedido é responsável por fazer uma chamada ao recurso do
     * servidor remoto que é disponibilizado para fazer o cancelamento de
     * pedidos. O resultado informado pelo servidor remoto é analisado com uma
     * chamada ao método obtemConfirmacaoXML.
     *
     * @return o valor informado pelo servidor como resposta à requisição de
     *         cancelamento de pedido.
     */
    public Integer cancelaPedido() {
        Integer resultado = 0;

        try {
			/*
			 * faz a requisição de registro ao servidor, enviando uma query
			 * string com os parâmetros necessários
			 */
            InputStream resposta = Sistema.OpenHttpConnection(URL_CANCELAR_PEDIDO
                    + "?pedido="
                    + Integer.toString(pedido.getIdPedido()));

            // faz a análise do resultado
            resultado = obtemConfirmacaoXML(resposta);
            resposta.close();
        } catch (IOException e1) {
            Toast.makeText(this, e1.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            e1.printStackTrace();
        }

        return resultado;
    }

    /**
     * O método registraPedido é responsável por fazer uma chamada ao recurso do
     * servidor remoto que é disponibilizado para fazer o registro de novos
     * pedidos. O resultado informado pelo servidor remoto é analisado com uma
     * chamada ao método obtemConfirmacaoXML.
     *
     * @param pedidoMesa
     *            é o objeto de pedido que contém os dados necessários para
     *            fazer o registro.
     * @return o valor informado pelo servidor como resposta à requisição de
     *         registro de pedido.
     */
    public Integer registraPedido(PedidoMesa pedidoMesa) {
        Integer resultado = 0;

        try {
            String parametroItem = "";
            // configura o parâmetro que identifica o item (pode ser prato ou
            // bebida)
            switch (pedidoMesa.getTipoItem()) {
                case ItemPedido.TIPO_ITEM_PRATO:
                    parametroItem = "prato=";
                    break;
                case ItemPedido.TIPO_ITEM_BEBIDA:
                    parametroItem = "bebida=";
                    break;
            }
			/*
			 * faz a requisição de registro ao servidor, enviando uma query
			 * string com os parâmetros necessários
			 */
            InputStream resposta = Sistema.OpenHttpConnection(URL_REGISTRAR_PEDIDO
                    + "?mesa=" + Integer.toString(numeroMesa) + "&"
                    + parametroItem + Integer.toString(pedido.getIdItem())
                    + "&quantidade="
                    + Integer.toString(pedido.getQuantidadePedido()));

            // faz a análise do resultado
            resultado = obtemConfirmacaoXML(resposta);
            resposta.close();
        } catch (IOException e1) {
            Toast.makeText(this, e1.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            e1.printStackTrace();
        }

        return resultado;
    }

    /**
     * Este método faz a análise de um arquivo XML de confirmação de operação do
     * servidor remoto, e informa o resultado obtido.
     *
     * @param in
     *            é a referência para o fluxo de dados por onde é recebida a
     *            resposta do servidor remoto.
     * @return o valor de resultado da operação informado pelo servidor.
     */
    private int obtemConfirmacaoXML(InputStream in) {
        int resultado = 0;
        try {
            // prepara a classe analisadora de código xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            // obtém o documento xml estruturado (fornecido pelo analisador de
            // xml)
            Document doc = db.parse(in);

            doc.getDocumentElement().normalize();

            // obtém a listagem de elementos com a tag "resultado"
            NodeList itens = doc.getElementsByTagName("resultado");
            String strResultado = itens.item(0).getFirstChild()
                    .getNodeValue();
            resultado = Integer.parseInt(strResultado);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return resultado;
    }

    /**
     * Este método faz a análise do arquivo XML retornado pelo servidor remoto
     * como resposta a uma requisição de itens de cardápio.
     *
     * @param in
     *            é a referência para o fluxo de dados por onde são recebidos os
     *            dados fornecidos pelo servidor remoto.
     * @return uma coleção de itens de cardápio.
     */
    private ArrayList<ItemPedido> obtemItensPedidoXML(InputStream in) {
        ArrayList<ItemPedido> itensPedido = new ArrayList<ItemPedido>();

        try {
            // prepara a classe analisadora de código xml
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            // obtém o documento xml estruturado (fornecido pelo analisador de
            // xml)
            Document doc = db.parse(in);

            doc.getDocumentElement().normalize();

            // obtém a listagem de elementos com a tag do item
            NodeList itens = doc.getElementsByTagName(ItemPedido
                    .getKeyTagItemPedido(tipoItem));

            // percorre cada elemento com a tag "prato" que for encontrado
            for (int i = 0; i < itens.getLength(); i++) {
                // obtém uma referência para um elemento "prato"
                Node nodeItem = itens.item(i);

                // converte a referência de um tipo node para um tipo element
                Element elementItem = (Element) nodeItem;

                // obtém o campo "id"
                NodeList idItem = elementItem
                        .getElementsByTagName(ItemPedido.KEY_ID);
                String strIdItem = idItem.item(0).getFirstChild()
                        .getNodeValue();

                // obtém o campo "nome"
                NodeList nomeItem = elementItem
                        .getElementsByTagName(ItemPedido.KEY_NOME);
                String strNomeItem = nomeItem.item(0).getFirstChild()
                        .getNodeValue();

                // obtém o campo "descricao"
                NodeList descricaoItem = elementItem
                        .getElementsByTagName(ItemPedido.KEY_DESCRICAO);
                String strDescricaoItem = descricaoItem.item(0)
                        .getFirstChild().getNodeValue();

                // obtém o campo "rendimento"
                NodeList rendimentoItem = elementItem
                        .getElementsByTagName(ItemPedido
                                .getKeyTagRendimento(tipoItem));
                String strRendimentoItem = rendimentoItem.item(0)
                        .getFirstChild().getNodeValue();

                // obtém o campo "preco"
                NodeList precoItem = elementItem
                        .getElementsByTagName(ItemPedido.KEY_PRECO);
                String strPrecoItem = precoItem.item(0)
                        .getFirstChild().getNodeValue();

                // inclui na lista de itens na memória um novo registro de item,
                // com os dados obtidos
                itensPedido.add(new ItemPedido(Integer.parseInt(strIdItem),
                        ItemPedido.TIPO_ITEM_PRATO, strNomeItem,
                        strDescricaoItem, Integer.parseInt(strRendimentoItem),
                        Double.parseDouble(strPrecoItem)));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return itensPedido;
    }

    /**
     * Este método reconstrói o objeto de pedido com os dados presentes no
     * formulário da atividade.
     */
    private void atualizaPedidoFormulario() {
        ItemPedido itemSelecionado = listItemPedido.get(spinnerItemPedido
                .getSelectedItemPosition());
        long idItem = itemSelecionado.getId();
        String nomeItem = itemSelecionado.getNome();
        String descricaoItem = itemSelecionado.getDescricao();
        int rendimentoItem = itemSelecionado.getRendimento();
        double precoUnidadeItem = itemSelecionado.getPreco();
        int quantidadePedido = Integer.parseInt(editQuantidade.getText()
                .toString());
        int idPedido = -1;
        pedido = new PedidoMesa(numeroMesa, (int) idItem,
                itemSelecionado.getTipoItem(), nomeItem, descricaoItem,
                rendimentoItem, precoUnidadeItem, quantidadePedido, idPedido);
    }

}
