
package br.com.alexromanelli.chuchuajato.client;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import br.com.alexromanelli.chuchuajato.client.services.PedidoService;
import br.com.alexromanelli.chuchuajato.client.services.PedidoServiceAsync;
import br.com.alexromanelli.chuchuajato.dados.Expediente;
import br.com.alexromanelli.chuchuajato.dados.ItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.Mesa;
import br.com.alexromanelli.chuchuajato.dados.PedidoItemCardapio;
import br.com.alexromanelli.chuchuajato.dados.ViewPedidoItemCardapio;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

public class PainelControlePedidos extends VerticalPanel {

	// widgets para filtros
	private HorizontalPanel hpanelFiltros;
	private Label labelFiltroExpediente;
	private ListBox listboxFiltroExpediente;
	private Label labelFiltroMesa;
	private ListBox listboxFiltroMesa;
	private Label labelFiltroPedido;
	private ListBox listboxFiltroPedido;
	private Button buttonAtualizarFiltro;
	private Image[] imgLoading;
	
	// tabela de pedidos
	private ScrollPanel scrollTabelaPedidos;
	private CellTable<ViewPedidoItemCardapio> tablePedidos;
    private TextColumn<ViewPedidoItemCardapio> colunaHorarioPedido;
    private TextColumn<ViewPedidoItemCardapio> colunaNumeroMesa;
    private TextColumn<ViewPedidoItemCardapio> colunaNomeItem;
    private TextColumn<ViewPedidoItemCardapio> colunaQuantidade;
    private TextColumn<ViewPedidoItemCardapio> colunaValorPedido;
    private TextColumn<ViewPedidoItemCardapio> colunaEstadoPedido;
    private TextColumn<ViewPedidoItemCardapio> colunaHorarioConclusao;
	
    // objetos para vinculação dos dados ao grid
    private AsyncDataProvider<ViewPedidoItemCardapio> provedorDadosPedido;
    private PedidoServiceAsync pedidoService = GWT.create(PedidoService.class);
    private SimplePager pager;
    
    // filtros
    private boolean filtrarExpediente;
    private long idExpediente;
    private boolean filtrarMesa;
    private long idMesa;
    private boolean filtrarEstado;
    private int estado;
    
    public PainelControlePedidos() {
		super();
		this.setSize("100%","100%");
		
		configuraTabela();
		
		imgLoading = new Image[2];
		imgLoading[0] = new Image(Resources.IMAGES.loader1_40());
		imgLoading[1] = new Image(Resources.IMAGES.loader2_40());
		
		labelFiltroExpediente = new Label(TextConstants.LABEL_FILTRO_EXPEDIENTE);
		labelFiltroMesa = new Label(TextConstants.LABEL_FILTRO_MESA);
		labelFiltroPedido = new Label(TextConstants.LABEL_FILTRO_PEDIDO);
		
		listboxFiltroExpediente = new ListBox();
		listboxFiltroExpediente.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				idExpediente = Long.parseLong(listboxFiltroExpediente
						.getValue(listboxFiltroExpediente.getSelectedIndex()));
				if (idExpediente == -1)
					limpaListBoxFiltroMesa();
				else
					carregaDadosMesa();
			}
		});

		listboxFiltroMesa = new ListBox();

		listboxFiltroPedido = new ListBox();
		listboxFiltroPedido.addItem("[nenhum filtro]", "-1");
		int v = 0;
		for (String item : PedidoItemCardapio.EstadoPedido.TEXTO_ESTADO_PEDIDO)
			listboxFiltroPedido.addItem(item, Integer.toString(v++));
		
		buttonAtualizarFiltro = new Button(TextConstants.LABEL_BOTAO_FILTRO_PEDIDO);
		buttonAtualizarFiltro.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				atualizaFiltroPedido();
			}
		});

		labelFiltroExpediente.setWidth("80px");
		labelFiltroMesa.setWidth("50px");
		labelFiltroPedido.setWidth("50px");
		labelFiltroExpediente.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		labelFiltroMesa.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		labelFiltroPedido.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		listboxFiltroExpediente.setWidth("150px");
		listboxFiltroMesa.setWidth("150px");
		listboxFiltroPedido.setWidth("120px");

		HorizontalPanel hpanelOpFiltros = new HorizontalPanel();
		hpanelOpFiltros.setSpacing(2);
		hpanelOpFiltros.add(labelFiltroExpediente);
		hpanelOpFiltros.add(listboxFiltroExpediente);
		hpanelOpFiltros.add(labelFiltroMesa);
		hpanelOpFiltros.add(listboxFiltroMesa);
		hpanelOpFiltros.add(labelFiltroPedido);
		hpanelOpFiltros.add(listboxFiltroPedido);

		hpanelFiltros = new HorizontalPanel();
		hpanelFiltros.add(hpanelOpFiltros);
		hpanelFiltros.add(imgLoading[0]);
		hpanelFiltros.add(imgLoading[1]);
		imgLoading[0].setVisible(false);
		imgLoading[1].setVisible(false);
		hpanelFiltros.add(buttonAtualizarFiltro);

		hpanelFiltros.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		hpanelFiltros.setCellHorizontalAlignment(buttonAtualizarFiltro,
                HorizontalPanel.ALIGN_RIGHT);

        hpanelFiltros.setCellVerticalAlignment(buttonAtualizarFiltro,
                VerticalPanel.ALIGN_MIDDLE);
        hpanelFiltros.setCellVerticalAlignment(hpanelOpFiltros,
                VerticalPanel.ALIGN_MIDDLE);

        hpanelOpFiltros.setCellVerticalAlignment(labelFiltroExpediente,
                VerticalPanel.ALIGN_MIDDLE);
        hpanelOpFiltros.setCellVerticalAlignment(labelFiltroMesa,
                VerticalPanel.ALIGN_MIDDLE);
        hpanelOpFiltros.setCellVerticalAlignment(labelFiltroPedido,
                VerticalPanel.ALIGN_MIDDLE);
        hpanelOpFiltros.setCellVerticalAlignment(listboxFiltroExpediente,
                VerticalPanel.ALIGN_MIDDLE);
        hpanelOpFiltros.setCellVerticalAlignment(listboxFiltroMesa,
                VerticalPanel.ALIGN_MIDDLE);
        hpanelOpFiltros.setCellVerticalAlignment(listboxFiltroPedido,
                VerticalPanel.ALIGN_MIDDLE);
		
        hpanelFiltros.setSize("100%",
                TextConstants.ALTURA_TOOLBAR_FILTRO);
        hpanelFiltros.setSpacing(0);

        scrollTabelaPedidos = new ScrollPanel(tablePedidos);
		
		this.add(hpanelFiltros);
		this.add(scrollTabelaPedidos);
		this.add(pager);
        this.setCellVerticalAlignment(pager, VerticalPanel.ALIGN_BOTTOM);
        this.setCellHeight(hpanelFiltros,
                TextConstants.ALTURA_TOOLBAR_FILTRO);
        this.setCellHeight(scrollTabelaPedidos,
                TextConstants.ALTURA_TABELA_PEDIDOS);
		
		configuracaoInicialFiltros();
		atualizaFiltroPedido();
    }
    
    private void limpaListBoxFiltroMesa() {
    	listboxFiltroMesa.clear();
		listboxFiltroMesa.addItem("[nenhum filtro]", "-1");
    }

	private void configuracaoInicialFiltros() {
		listboxFiltroExpediente.clear();
		listboxFiltroExpediente.addItem("[nenhum filtro]", "-1");
		limpaListBoxFiltroMesa();
		
		carregaDadosExpediente();
	}

	private void carregaDadosMesa() {
        if (pedidoService == null) {
            pedidoService = GWT.create(PedidoService.class);
        }

        AsyncCallback<Mesa[]> callback = new AsyncCallback<Mesa[]>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Mesa[] result) {
                exibeMesas(result);
            }
        };

        pedidoService.getColecaoMesasExpediente(idExpediente, callback);
	}

	protected void exibeMesas(Mesa[] result) {
		listboxFiltroMesa.clear();
		listboxFiltroMesa.addItem("[nenhum filtro]", "-1");
		for (Mesa m : result) {
			listboxFiltroMesa.addItem("mesa " + m.getNumeroMesa(), 
					Long.toString(m.getId()));
		}
	}

	private void carregaDadosExpediente() {
        if (pedidoService == null) {
            pedidoService = GWT.create(PedidoService.class);
        }

        AsyncCallback<Expediente[]> callback = new AsyncCallback<Expediente[]>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Expediente[] result) {
                exibeExpedientes(result);
            }
        };

        pedidoService.getColecaoExpedientes(callback);
	}

	protected void exibeExpedientes(Expediente[] result) {
		listboxFiltroExpediente.clear();
		listboxFiltroExpediente.addItem("[nenhum filtro]", "-1");
		DateTimeFormat dtf = DateTimeFormat.getFormat("dd/MM/yyyy HH:mm");
		for (Expediente e : result) {
			listboxFiltroExpediente.addItem(dtf.format(e.getDataAbertura()),
					Long.toString(e.getId()));
		}
	}

	protected void atualizaFiltroPedido() {
		// exibe indicador de operação em andamento
		exibeIndicadorAndamento();
		
		filtrarExpediente = listboxFiltroExpediente.getSelectedIndex() > 0;
		filtrarMesa = listboxFiltroMesa.getSelectedIndex() > 0;
		filtrarEstado = listboxFiltroPedido.getSelectedIndex() > 0;
		
		idExpediente = Long.parseLong(listboxFiltroExpediente
				.getValue(listboxFiltroExpediente.getSelectedIndex()));
		idMesa = Long.parseLong(listboxFiltroMesa
				.getValue(listboxFiltroMesa.getSelectedIndex()));
		estado = Integer.parseInt(listboxFiltroPedido
				.getValue(listboxFiltroPedido.getSelectedIndex()));
		
		// obter contagem de registros a exibir com o filtro
        if (pedidoService == null) {
            pedidoService = GWT.create(PedidoService.class);
        }

        AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Integer result) {
                defineNumeroLinhasPedidos(result.intValue());
            }
        };

        pedidoService.getTamanhoColecaoPedidos(filtrarExpediente, idExpediente, 
        		filtrarMesa, idMesa, filtrarEstado, estado, callback);
	}
	
	private void exibeIndicadorAndamento() {
		Random r = new Random((new Date()).getTime());
		int i = r.nextInt(2);
		imgLoading[i].setVisible(true);
		imgLoading[1 - i].setVisible(false);
	}

	private void ocultaIndicadorAndamento() {
		imgLoading[0].setVisible(false);
		imgLoading[1].setVisible(false);
	}

	private void defineNumeroLinhasPedidos(int numeroLinhas) {
        tablePedidos.setRowCount(numeroLinhas, true);
        recarregaDados();
	}

	private void configuraTabela() {
        tablePedidos = new CellTable<ViewPedidoItemCardapio>();

        colunaHorarioPedido = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
            	DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM");
                return dtf.format(object.getHorarioPedido());
            }
        };

        colunaNumeroMesa = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
                return Integer.toString(object.getNumeroMesa());
            }
        };

        colunaNomeItem = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
                return object.getNomeItem();
            }
        };

        colunaQuantidade = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
                return Integer.toString(object.getQuantidade());
            }
        };

        colunaValorPedido = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
            	NumberFormat nf = NumberFormat.getFormat("0.00");
                return nf.format(object.getValorPedido());
            }
        };

        colunaEstadoPedido = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
                return object.getEstadoPedido();
            }
        };

        colunaHorarioConclusao = new TextColumn<ViewPedidoItemCardapio>() {
            @Override
            public String getValue(ViewPedidoItemCardapio object) {
            	Date horarioConclusao = object.getHorarioConclusao();
            	if (horarioConclusao == null)
            		return "---";
            	DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm dd/MM");
                return dtf.format(object.getHorarioConclusao());
            }
        };
        
        colunaHorarioPedido.setDataStoreName(PedidoItemCardapio.KEY_HORARIO_PEDIDO);
        colunaNumeroMesa.setDataStoreName(Mesa.KEY_NUMERO_MESA);
        colunaNomeItem.setDataStoreName(ItemCardapio.KEY_NOME);
        colunaQuantidade.setDataStoreName(PedidoItemCardapio.KEY_QUANTIDADE);
        colunaValorPedido.setDataStoreName(ItemCardapio.KEY_PRECO);
        colunaEstadoPedido.setDataStoreName(PedidoItemCardapio.KEY_ESTADO_PEDIDO);
        colunaHorarioConclusao.setDataStoreName(PedidoItemCardapio.KEY_HORARIO_CONCLUSAO);

        colunaHorarioPedido.setSortable(true);
        colunaNumeroMesa.setSortable(false);
        colunaNomeItem.setSortable(false);
        colunaQuantidade.setSortable(true);
        colunaValorPedido.setSortable(false);
        colunaEstadoPedido.setSortable(true);
        colunaHorarioConclusao.setSortable(true);

        tablePedidos.addColumn(colunaHorarioPedido, 
        		TextConstants.TITULO_COLUNA_HORARIO_PEDIDO);
        tablePedidos.addColumn(colunaNumeroMesa,
                TextConstants.TITULO_COLUNA_NUMERO_MESA);
        tablePedidos.addColumn(colunaNomeItem,
                TextConstants.TITULO_COLUNA_NOME_ITEM);
        tablePedidos.addColumn(colunaQuantidade, 
        		TextConstants.TITULO_COLUNA_QUANTIDADE);
        tablePedidos.addColumn(colunaValorPedido,
                TextConstants.TITULO_COLUNA_VALOR_PEDIDO);
        tablePedidos.addColumn(colunaEstadoPedido, 
        		TextConstants.TITULO_COLUNA_ESTADO_PEDIDO);
        tablePedidos.addColumn(colunaHorarioConclusao,
                TextConstants.TITULO_COLUNA_HORARIO_CONCLUSAO);

        tablePedidos.setWidth("100%");
        tablePedidos.setColumnWidth(colunaHorarioPedido, 15, Unit.PCT);
        tablePedidos.setColumnWidth(colunaNumeroMesa, 10, Unit.PCT);
        tablePedidos.setColumnWidth(colunaNomeItem, 30, Unit.PCT);
        tablePedidos.setColumnWidth(colunaQuantidade, 10, Unit.PCT);
        tablePedidos.setColumnWidth(colunaValorPedido, 10, Unit.PCT);
        tablePedidos.setColumnWidth(colunaEstadoPedido, 10, Unit.PCT);
        tablePedidos.setColumnWidth(colunaHorarioConclusao, 15, Unit.PCT);

        tablePedidos.setVisibleRange(0, 20);

        provedorDadosPedido = new AsyncDataProvider<ViewPedidoItemCardapio>() {
            @Override
            protected void onRangeChanged(HasData<ViewPedidoItemCardapio> display) {
                final Range range = display.getVisibleRange();

                // obtém a coleção de dados para atender ao novo intervalo de
                // exibição
                if (pedidoService == null) {
                    pedidoService = GWT.create(PedidoService.class);
                }
                AsyncCallback<ViewPedidoItemCardapio[]> callbackColecaoPedidos = new AsyncCallback<ViewPedidoItemCardapio[]>() {
                    @Override
                    public void onSuccess(ViewPedidoItemCardapio[] result) {
                        tablePedidos.setRowData(range.getStart(),
                                Arrays.asList(result));
                        
                        ocultaIndicadorAndamento();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                    }
                };
                String sortField = PedidoItemCardapio.KEY_HORARIO_PEDIDO;
                boolean sortAscending = true;
                if (tablePedidos.getColumnSortList().size() > 0) {
                    ColumnSortInfo sortInfo = tablePedidos.getColumnSortList()
                            .get(0);
                    sortField = sortInfo.getColumn().getDataStoreName();
                    sortAscending = sortInfo.isAscending();
                }
                pedidoService.getColecaoPedidos(filtrarExpediente,
                		idExpediente, filtrarMesa, idMesa, filtrarEstado,
                		estado, sortField, sortAscending,
                        range.getStart(), range.getLength(),
                        callbackColecaoPedidos);
            }
        };

        provedorDadosPedido.addDataDisplay(tablePedidos);

        SimplePager.Resources pagerResources = GWT
                .create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0,
                true);
        pager.setDisplay(tablePedidos);

        AsyncHandler columnSortHandler = new AsyncHandler(tablePedidos);
        tablePedidos.addColumnSortHandler(columnSortHandler);

        tablePedidos.getColumnSortList().push(colunaHorarioPedido);
	}
	
    public void recarregaDados() {
        RangeChangeEvent.fire(tablePedidos, provedorDadosPedido.getRanges()[0]);
    }
}
