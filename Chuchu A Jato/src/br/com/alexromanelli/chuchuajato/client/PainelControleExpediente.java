package br.com.alexromanelli.chuchuajato.client;

import java.util.Date;
import java.util.Random;

import br.com.alexromanelli.chuchuajato.client.services.ExpedienteService;
import br.com.alexromanelli.chuchuajato.client.services.ExpedienteServiceAsync;
import br.com.alexromanelli.chuchuajato.dados.Expediente;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PainelControleExpediente extends VerticalPanel {

	// itens da tab de controle de expediente
	private Label labelEstadoAtual;
	private Label labelAbrirExpediente;
	private Label labelMesas;
	private TextBox textNumeroMesas;
	private Button buttonAlterarEstado;

	private Image imgLoading;

	// serviço assíncrono para chamadas aos métodos de comunicação com o
	// servidor
	private ExpedienteServiceAsync expedienteService = GWT
			.create(ExpedienteService.class);
	private String estadoAtual;

	public PainelControleExpediente() {
		super();
		this.setSize("30%", "200px");
		this.setSpacing(10);

		// brincadeira para obter aleatoriamente um dos dois gifs
		ImageResource imgRes = Resources.IMAGES.loader1();
		Random r = new Random((new Date()).getTime());
		if (r.nextDouble() >= 0.5)
			imgRes = Resources.IMAGES.loader2();

		imgLoading = new Image(imgRes);
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		this.add(imgLoading);

		labelEstadoAtual = new Label("");

		buttonAlterarEstado = new Button("---");
		buttonAlterarEstado.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (estadoAtual.equals(Expediente.EstadoExpediente.EXPEDIENTE_ABERTO)) {
					fecharExpediente();
				} else {
					abrirExpediente();
				}
			}
		});

		carregaDadosAtuais();
	}

	protected void abrirExpediente() {
		if (expedienteService == null) {
			expedienteService = GWT.create(ExpedienteService.class);
		}

		try {
			final int numeroMesas = Integer.parseInt(textNumeroMesas.getText());

			AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					if (result) {
						Window.alert("Expediente aberto!");
						exibeExpedienteAberto(numeroMesas);
					} else {
						Window.alert("Não foi possível abrir o expediente.");
					}
				}

				@Override
				public void onFailure(Throwable caught) {
				}
			};

			expedienteService.abrirExpediente(numeroMesas, callback);
		} catch (NumberFormatException e) {
			Window.alert("Número de mesas inválido.");
			textNumeroMesas.setFocus(true);
		}
	}

	protected void fecharExpediente() {
		if (expedienteService == null) {
			expedienteService = GWT.create(ExpedienteService.class);
		}

		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					Window.alert("Expediente fechado!");
					exibeExpedienteFechado();
				} else {
					Window.alert("Não foi possível fechar o expediente.\nÉ possível que haja pedido pendente.");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		};

		expedienteService.fecharExpediente(callback);
	}

	/**
	 * Este método faz a comunicação com o servidor para obter os dados de
	 * número de mesas e do estado atual do expediente (aberto ou fechado). Como
	 * a comunicação é assíncrona, o tratamento dos dados retornados é feito nos
	 * eventos do callback.
	 */
	private void carregaDadosAtuais() {
		if (expedienteService == null) {
			expedienteService = GWT.create(ExpedienteService.class);
		}

		final AsyncCallback<Integer> callbackNumeroMesas = new AsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Integer result) {
				exibeExpedienteAberto(result);
			}
		};

		AsyncCallback<String> callbackEstadoExpediente = new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				if (result.equals(Expediente.EstadoExpediente.EXPEDIENTE_ABERTO))
					expedienteService.getNumeroMesas(callbackNumeroMesas);
				else
					exibeExpedienteFechado();
			}
		};

		expedienteService.getEstadoExpediente(callbackEstadoExpediente);
	}

	protected void exibeExpedienteFechado() {
		this.clear();
		estadoAtual = Expediente.EstadoExpediente.EXPEDIENTE_FECHADO;

		labelEstadoAtual.setText("Expediente fechado.");
		labelAbrirExpediente = new Label("Abrir novo expediente com ");
		labelMesas = new Label(" mesas");
		textNumeroMesas = new TextBox();
		buttonAlterarEstado.setText("Abrir");

		this.add(labelEstadoAtual);
		HorizontalPanel hpanelNovo = new HorizontalPanel();
		hpanelNovo.add(labelAbrirExpediente);
		hpanelNovo.add(textNumeroMesas);
		hpanelNovo.add(labelMesas);
		labelAbrirExpediente.setWidth("180px");
		textNumeroMesas.setWidth("50px");
		labelMesas.setWidth("60px");
		hpanelNovo.setCellVerticalAlignment(labelAbrirExpediente, ALIGN_MIDDLE);
		hpanelNovo.setCellVerticalAlignment(textNumeroMesas, ALIGN_MIDDLE);
		hpanelNovo.setCellVerticalAlignment(labelMesas, ALIGN_MIDDLE);
		hpanelNovo.setCellHorizontalAlignment(labelMesas, ALIGN_RIGHT);
		this.add(hpanelNovo);
		this.add(buttonAlterarEstado);
	}

	protected void exibeExpedienteAberto(Integer numeroMesas) {
		this.clear();
		estadoAtual = Expediente.EstadoExpediente.EXPEDIENTE_ABERTO;

		labelEstadoAtual.setText("Expediente aberto com " + numeroMesas
				+ " mesa" + (numeroMesas > 1 ? "s" : "") + ".");
		buttonAlterarEstado.setText("Fechar");
		this.add(labelEstadoAtual);
		this.add(buttonAlterarEstado);
	}

}
