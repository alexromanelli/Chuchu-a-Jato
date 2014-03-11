
package br.com.alexromanelli.chuchuajato.client;

import java.util.Arrays;

import br.com.alexromanelli.chuchuajato.client.services.BebidaService;
import br.com.alexromanelli.chuchuajato.client.services.BebidaServiceAsync;
import br.com.alexromanelli.chuchuajato.dados.Bebida;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.CellPreviewEvent;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

public class PainelCadastroBebidas extends VerticalPanel {
    private static PainelCadastroBebidas INSTANCE;

    public static PainelCadastroBebidas getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PainelCadastroBebidas();
        return INSTANCE;
    }

    // itens da tab de cadastro de Bebidas
    private HorizontalPanel hpanelToolbarCadastroBebidas;
    private Button buttonNovo;
    private Label labelInstrucaoUso;
    private CellTable<Bebida> tableBebida;
    private TextColumn<Bebida> colunaNome;
    private TextColumn<Bebida> colunaDescricao;
    private TextColumn<Bebida> colunaRendimentoDoses;
    private TextColumn<Bebida> colunaPreco;

    private DialogRegistroBebida dialogRegistroBebida;

    // objetos para vinculação dos dados ao grid
    private AsyncDataProvider<Bebida> provedorDadosBebida;
    private BebidaServiceAsync BebidaService = GWT.create(BebidaService.class);
    private SimplePager pager;

    public PainelCadastroBebidas() {
        super();

        configuraTabela();

        buttonNovo = new Button(TextConstants.LABEL_BOTAO_NOVO_REGISTRO);
        buttonNovo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogRegistroBebida = new DialogRegistroBebida(
                        Opcoes.OpcoesDialogoRegistro.NOVO, 0);
                dialogRegistroBebida.show();
                dialogRegistroBebida.center();
            }
        });
        buttonNovo.setSize(TextConstants.LARGURA_BOTAO_NOVO_REGISTRO,
                TextConstants.ALTURA_BOTAO_NOVO_REGISTRO);

        labelInstrucaoUso = new Label(
                TextConstants.LABEL_INSTRUCAO_USO_PAINEL_CADASTRO);

        hpanelToolbarCadastroBebidas = new HorizontalPanel();
        hpanelToolbarCadastroBebidas.setSize("100%",
                TextConstants.ALTURA_TOOLBAR_CADASTRO);
        hpanelToolbarCadastroBebidas.setSpacing(2);

        hpanelToolbarCadastroBebidas.add(labelInstrucaoUso);
        hpanelToolbarCadastroBebidas.add(buttonNovo);
        hpanelToolbarCadastroBebidas.setCellHorizontalAlignment(buttonNovo,
                HorizontalPanel.ALIGN_RIGHT);

        this.setWidth("100%");

        this.add(hpanelToolbarCadastroBebidas);
        this.add(tableBebida);
        this.add(pager);
        this.setCellVerticalAlignment(pager, VerticalPanel.ALIGN_BOTTOM);

        this.setCellHeight(hpanelToolbarCadastroBebidas,
                TextConstants.ALTURA_TOOLBAR_CADASTRO);
        // this.setCellHeight(tableBebida,
        // TextConstants.ALTURA_TABELA_REGISTROS);

        this.setSpacing(6);
    }

    private void configuraTabela() {
        tableBebida = new CellTable<Bebida>();

        colunaNome = new TextColumn<Bebida>() {
            @Override
            public String getValue(Bebida object) {
                return object.getNome();
            }
        };

        colunaDescricao = new TextColumn<Bebida>() {
            @Override
            public String getValue(Bebida object) {
                return object.getDescricao();
            }
        };

        colunaRendimentoDoses = new TextColumn<Bebida>() {
            @Override
            public String getValue(Bebida object) {
                return Integer.toString(object.getRendimentoDoses());
            }
        };

        colunaPreco = new TextColumn<Bebida>() {
            @Override
            public String getValue(Bebida object) {
                NumberFormat nf = NumberFormat.getFormat("0.00");
                return nf.format(object.getPreco());
            }
        };

        colunaNome.setDataStoreName(Bebida.KEY_NOME);
        colunaDescricao.setDataStoreName(Bebida.KEY_DESCRICAO);
        colunaRendimentoDoses.setDataStoreName(Bebida.KEY_RENDIMENTO);
        colunaPreco.setDataStoreName(Bebida.KEY_PRECO);

        colunaNome.setSortable(true);
        colunaDescricao.setSortable(false);
        colunaRendimentoDoses.setSortable(true);
        colunaPreco.setSortable(true);

        tableBebida.addColumn(colunaNome,
                TextConstants.TITULO_COLUNA_NOME_BEBIDA);
        tableBebida.addColumn(colunaDescricao,
                TextConstants.TITULO_COLUNA_DESCRICAO_BEBIDA);
        tableBebida.addColumn(colunaRendimentoDoses,
                TextConstants.TITULO_COLUNA_RENDIMENTO_BEBIDA);
        tableBebida.addColumn(colunaPreco,
                TextConstants.TITULO_COLUNA_PRECO_BEBIDA);

        tableBebida.setWidth("100%");
        tableBebida.setColumnWidth(colunaNome, 25, Unit.PCT);
        tableBebida.setColumnWidth(colunaDescricao, 39, Unit.PCT);
        tableBebida.setColumnWidth(colunaRendimentoDoses, 23, Unit.PCT);
        tableBebida.setColumnWidth(colunaPreco, 13, Unit.PCT);

        if (BebidaService == null) {
            BebidaService = GWT.create(BebidaService.class);
        }

        AsyncCallback<Integer> callbackNumeroLinhas = new AsyncCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Integer result) {
                tableBebida.setRowCount(result.intValue(), true);
            }
        };
        BebidaService.getQuantidadeBebidasRegistrados(callbackNumeroLinhas);

        tableBebida.setVisibleRange(0, 10);

        provedorDadosBebida = new AsyncDataProvider<Bebida>() {
            @Override
            protected void onRangeChanged(HasData<Bebida> display) {
                final Range range = display.getVisibleRange();

                // obtém a coleção de dados para atender ao novo intervalo de
                // exibição
                if (BebidaService == null) {
                    BebidaService = GWT.create(BebidaService.class);
                }
                AsyncCallback<Bebida[]> callbackColecaoBebidas = new AsyncCallback<Bebida[]>() {
                    @Override
                    public void onSuccess(Bebida[] result) {
                        tableBebida.setRowData(range.getStart(),
                                Arrays.asList(result));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                };
                String sortField = Bebida.KEY_NOME;
                boolean sortAscending = true;
                if (tableBebida.getColumnSortList().size() > 0) {
                    ColumnSortInfo sortInfo = tableBebida.getColumnSortList()
                            .get(0);
                    sortField = sortInfo.getColumn().getDataStoreName();
                    sortAscending = sortInfo.isAscending();
                }
                BebidaService.getColecaoBebida(sortField, sortAscending,
                        range.getStart(), range.getLength(),
                        callbackColecaoBebidas);
            }
        };

        provedorDadosBebida.addDataDisplay(tableBebida);

        SimplePager.Resources pagerResources = GWT
                .create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,
                true);
        pager.setDisplay(tableBebida);

        AsyncHandler columnSortHandler = new AsyncHandler(tableBebida);
        tableBebida.addColumnSortHandler(columnSortHandler);

        tableBebida.getColumnSortList().push(colunaNome);

        // adiciona manipulador para clique sobre uma linha da tabela
        tableBebida
                .addCellPreviewHandler(new CellPreviewEvent.Handler<Bebida>() {
                    @Override
                    public void onCellPreview(CellPreviewEvent<Bebida> event) {
                        boolean isClick = "click".equals(event.getNativeEvent()
                                .getType()); // verifica se o evento é de clique
                        if (isClick) {
                            dialogRegistroBebida = new DialogRegistroBebida(
                                    Opcoes.OpcoesDialogoRegistro.EDITAR, event
                                            .getValue().getId());
                            dialogRegistroBebida.show();
                            dialogRegistroBebida.center();
                        }
                    }
                });
    }

    public void recarregaDados() {
        RangeChangeEvent.fire(tableBebida, provedorDadosBebida.getRanges()[0]);
    }

}
