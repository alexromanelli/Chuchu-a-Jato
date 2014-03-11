
package br.com.alexromanelli.chuchuajato.client;

import java.util.Arrays;

import br.com.alexromanelli.chuchuajato.client.services.PratoService;
import br.com.alexromanelli.chuchuajato.client.services.PratoServiceAsync;
import br.com.alexromanelli.chuchuajato.dados.Prato;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
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

public class PainelCadastroPratos extends VerticalPanel {
    private static PainelCadastroPratos INSTANCE;

    public static PainelCadastroPratos getInstance() {
        if (INSTANCE == null)
            INSTANCE = new PainelCadastroPratos();
        return INSTANCE;
    }

    // itens da tab de cadastro de pratos
    private HorizontalPanel hpanelToolbarCadastroPratos;
    private Button buttonNovo;
    private Label labelInstrucaoUso;
    private CellTable<Prato> tablePrato;
    private TextColumn<Prato> colunaNome;
    private TextColumn<Prato> colunaDescricao;
    private TextColumn<Prato> colunaRendimentoPorcoes;
    private TextColumn<Prato> colunaPreco;

    private DialogRegistroPrato dialogRegistroPrato;

    // objetos para vinculação dos dados ao grid
    private AsyncDataProvider<Prato> provedorDadosPrato;
    private PratoServiceAsync pratoService = GWT.create(PratoService.class);
    private SimplePager pager;

    public PainelCadastroPratos() {
        super();

        configuraTabela();

        buttonNovo = new Button(TextConstants.LABEL_BOTAO_NOVO_REGISTRO);
        buttonNovo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                dialogRegistroPrato = new DialogRegistroPrato(
                        Opcoes.OpcoesDialogoRegistro.NOVO, 0);
                dialogRegistroPrato.show();
                dialogRegistroPrato.center();
            }
        });
        buttonNovo.setSize(TextConstants.LARGURA_BOTAO_NOVO_REGISTRO,
                TextConstants.ALTURA_BOTAO_NOVO_REGISTRO);

        labelInstrucaoUso = new Label(
                TextConstants.LABEL_INSTRUCAO_USO_PAINEL_CADASTRO);

        hpanelToolbarCadastroPratos = new HorizontalPanel();
        hpanelToolbarCadastroPratos.setSize("100%",
                TextConstants.ALTURA_TOOLBAR_CADASTRO);
        hpanelToolbarCadastroPratos.setSpacing(2);

        hpanelToolbarCadastroPratos.add(labelInstrucaoUso);
        hpanelToolbarCadastroPratos.add(buttonNovo);
        hpanelToolbarCadastroPratos.setCellHorizontalAlignment(buttonNovo,
                HorizontalPanel.ALIGN_RIGHT);

        this.setWidth("100%");

        this.add(hpanelToolbarCadastroPratos);
        this.add(tablePrato);
        this.add(pager);
        this.setCellVerticalAlignment(pager, VerticalPanel.ALIGN_BOTTOM);

        this.setCellHeight(hpanelToolbarCadastroPratos,
                TextConstants.ALTURA_TOOLBAR_CADASTRO);
        // this.setCellHeight(tablePrato,
        // TextConstants.ALTURA_TABELA_REGISTROS);

        this.setSpacing(6);
    }

    private void configuraTabela() {
        tablePrato = new CellTable<Prato>();

        colunaNome = new TextColumn<Prato>() {
            @Override
            public String getValue(Prato object) {
                return object.getNome();
            }
        };

        colunaDescricao = new TextColumn<Prato>() {
            @Override
            public String getValue(Prato object) {
                return object.getDescricao();
            }
        };

        colunaRendimentoPorcoes = new TextColumn<Prato>() {
            @Override
            public String getValue(Prato object) {
                return Integer.toString(object.getRendimentoPorcoes());
            }
        };

        colunaPreco = new TextColumn<Prato>() {
            @Override
            public String getValue(Prato object) {
                NumberFormat nf = NumberFormat.getFormat("0.00");
                return nf.format(object.getPreco());
            }
        };

        colunaNome.setDataStoreName(Prato.KEY_NOME);
        colunaDescricao.setDataStoreName(Prato.KEY_DESCRICAO);
        colunaRendimentoPorcoes.setDataStoreName(Prato.KEY_RENDIMENTO);
        colunaPreco.setDataStoreName(Prato.KEY_PRECO);

        colunaNome.setSortable(true);
        colunaDescricao.setSortable(false);
        colunaRendimentoPorcoes.setSortable(true);
        colunaPreco.setSortable(true);

        tablePrato
                .addColumn(colunaNome, TextConstants.TITULO_COLUNA_NOME_PRATO);
        tablePrato.addColumn(colunaDescricao,
                TextConstants.TITULO_COLUNA_DESCRICAO_PRATO);
        tablePrato.addColumn(colunaRendimentoPorcoes,
                TextConstants.TITULO_COLUNA_RENDIMENTO_PRATO);
        tablePrato.addColumn(colunaPreco,
                TextConstants.TITULO_COLUNA_PRECO_PRATO);

        tablePrato.setWidth("100%");
        tablePrato.setColumnWidth(colunaNome, 25, Unit.PCT);
        tablePrato.setColumnWidth(colunaDescricao, 39, Unit.PCT);
        tablePrato.setColumnWidth(colunaRendimentoPorcoes, 23, Unit.PCT);
        tablePrato.setColumnWidth(colunaPreco, 13, Unit.PCT);

        if (pratoService == null) {
            pratoService = GWT.create(PratoService.class);
        }

        AsyncCallback<Integer> callbackNumeroLinhas = new AsyncCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Integer result) {
                tablePrato.setRowCount(result.intValue(), true);
            }
        };
        pratoService.getQuantidadePratosRegistrados(callbackNumeroLinhas);

        tablePrato.setVisibleRange(0, 10);

        provedorDadosPrato = new AsyncDataProvider<Prato>() {
            @Override
            protected void onRangeChanged(HasData<Prato> display) {
                final Range range = display.getVisibleRange();

                // obtém a coleção de dados para atender ao novo intervalo de
                // exibição
                if (pratoService == null) {
                    pratoService = GWT.create(PratoService.class);
                }
                AsyncCallback<Prato[]> callbackColecaoPratos = new AsyncCallback<Prato[]>() {
                    @Override
                    public void onSuccess(Prato[] result) {
                        tablePrato.setRowData(range.getStart(),
                                Arrays.asList(result));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                };
                String sortField = Prato.KEY_NOME;
                boolean sortAscending = true;
                if (tablePrato.getColumnSortList().size() > 0) {
                    ColumnSortInfo sortInfo = tablePrato.getColumnSortList()
                            .get(0);
                    sortField = sortInfo.getColumn().getDataStoreName();
                    sortAscending = sortInfo.isAscending();
                }
                pratoService.getColecaoPrato(sortField, sortAscending,
                        range.getStart(), range.getLength(),
                        callbackColecaoPratos);
            }
        };

        provedorDadosPrato.addDataDisplay(tablePrato);

        SimplePager.Resources pagerResources = GWT
                .create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,
                true);
        pager.setDisplay(tablePrato);

        AsyncHandler columnSortHandler = new AsyncHandler(tablePrato);
        tablePrato.addColumnSortHandler(columnSortHandler);

        tablePrato.getColumnSortList().push(colunaNome);

        // adiciona manipulador para clique sobre uma linha da tabela
        tablePrato.addCellPreviewHandler(new CellPreviewEvent.Handler<Prato>() {
            @Override
            public void onCellPreview(CellPreviewEvent<Prato> event) {
                boolean isClick = "click".equals(event.getNativeEvent()
                        .getType()); // verifica se o evento é de clique
                if (isClick) {
                    dialogRegistroPrato = new DialogRegistroPrato(
                            Opcoes.OpcoesDialogoRegistro.EDITAR, event
                                    .getValue().getId());
                    dialogRegistroPrato.show();
                    dialogRegistroPrato.center();
                }
            }
        });
    }

    public void recarregaDados() {
        RangeChangeEvent.fire(tablePrato, provedorDadosPrato.getRanges()[0]);
    }
}
