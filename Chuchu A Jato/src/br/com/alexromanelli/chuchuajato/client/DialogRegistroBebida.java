
package br.com.alexromanelli.chuchuajato.client;

import br.com.alexromanelli.chuchuajato.client.Opcoes.OpcoesDialogoRegistro;
import br.com.alexromanelli.chuchuajato.client.services.BebidaService;
import br.com.alexromanelli.chuchuajato.client.services.BebidaServiceAsync;
import br.com.alexromanelli.chuchuajato.dados.Bebida;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DialogRegistroBebida extends DialogBox {

    private Bebida registro;
    private BebidaServiceAsync bebidaService = GWT.create(BebidaService.class);
    private Opcoes.OpcoesDialogoRegistro opcaoDialogoRegistro;

    private VerticalPanel vpanelConteudo;

    private HorizontalPanel hpanelLinhaNome;
    private Label labelNome;
    private TextBox textNome;

    private HorizontalPanel hpanelLinhaDescricao;
    private Label labelDescricao;
    private TextArea textareaDescricao;

    private HorizontalPanel hpanelLinhaRendimento;
    private Label labelRendimento;
    private TextBox textRendimento;

    private HorizontalPanel hpanelLinhaPreco;
    private Label labelPreco;
    private TextBox textPreco;

    private HTML htmlSeparador;

    private HorizontalPanel hpanelToolbox;
    private Button buttonSalvar;
    private Button buttonFechar;
    private Button buttonExcluir;

    public DialogRegistroBebida(Opcoes.OpcoesDialogoRegistro opcaoDialogo,
            long idRegistro) {
        super();

        opcaoDialogoRegistro = opcaoDialogo;

        this.setText(TextConstants.TITULO_DIALOGO_REGISTRO_BEBIDA);

        labelNome = new Label(TextConstants.LABEL_NOME_BEBIDA);
        textNome = new TextBox();
        textNome.setWidth("400px");
        hpanelLinhaNome = new HorizontalPanel();
        hpanelLinhaNome.add(labelNome);
        hpanelLinhaNome.add(textNome);
        hpanelLinhaNome.setCellWidth(labelNome, "150px");

        labelDescricao = new Label(TextConstants.LABEL_DESCRICAO_BEBIDA);
        textareaDescricao = new TextArea();
        textareaDescricao.setWidth("400px");
        textareaDescricao.setHeight("60px");
        hpanelLinhaDescricao = new HorizontalPanel();
        hpanelLinhaDescricao.add(labelDescricao);
        hpanelLinhaDescricao.add(textareaDescricao);
        hpanelLinhaDescricao.setCellWidth(labelDescricao, "150px");

        labelRendimento = new Label(TextConstants.LABEL_RENDIMENTO_BEBIDA);
        textRendimento = new TextBox();
        textRendimento.setWidth("50px");
        hpanelLinhaRendimento = new HorizontalPanel();
        hpanelLinhaRendimento.add(labelRendimento);
        hpanelLinhaRendimento.add(textRendimento);
        hpanelLinhaRendimento.setCellWidth(labelRendimento, "150px");

        labelPreco = new Label(TextConstants.LABEL_PRECO_BEBIDA);
        textPreco = new TextBox();
        textPreco.setWidth("50px");
        hpanelLinhaPreco = new HorizontalPanel();
        hpanelLinhaPreco.add(labelPreco);
        hpanelLinhaPreco.add(textPreco);
        hpanelLinhaPreco.setCellWidth(labelPreco, "150px");

        buttonSalvar = new Button(TextConstants.LABEL_BOTAO_SALVAR);
        buttonSalvar.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Window
                        .confirm(TextConstants.MENSAGEM_CONFIRMACAO_SALVAR_REGISTRO))
                    salvarRegistroAtual();
            }
        });
        buttonFechar = new Button(TextConstants.LABEL_BOTAO_FECHAR);
        buttonFechar.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fecharDialogoRegistro();
            }
        });

        hpanelToolbox = new HorizontalPanel();
        hpanelToolbox.setSpacing(6);
        hpanelToolbox.add(buttonSalvar);
        hpanelToolbox.add(buttonFechar);

        if (opcaoDialogo == OpcoesDialogoRegistro.EDITAR) {
            buttonExcluir = new Button(TextConstants.LABEL_BOTAO_EXCLUIR);
            buttonExcluir.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (Window
                            .confirm(TextConstants.MENSAGEM_CONFIRMACAO_EXCLUIR_REGISTRO))
                        excluirRegistroAtual();
                }
            });
            hpanelToolbox.add(buttonExcluir);
            hpanelToolbox.setCellHorizontalAlignment(buttonExcluir,
                    HorizontalPanel.ALIGN_RIGHT);
        }

        htmlSeparador = new HTML();
        htmlSeparador
                .setHeight(TextConstants.ALTURA_SEPARADOR_FORMULARIO_TOOLBOX_DIALOGO_REGISTRO);

        vpanelConteudo = new VerticalPanel();
        vpanelConteudo.setSpacing(6);
        vpanelConteudo.addStyleName("conteudo-dialogo-registro");
        vpanelConteudo.add(hpanelLinhaNome);
        vpanelConteudo.add(hpanelLinhaDescricao);
        vpanelConteudo.add(hpanelLinhaRendimento);
        vpanelConteudo.add(hpanelLinhaPreco);
        vpanelConteudo.add(htmlSeparador);
        vpanelConteudo.add(hpanelToolbox);

        vpanelConteudo.setCellVerticalAlignment(hpanelToolbox,
                VerticalPanel.ALIGN_BOTTOM);
        vpanelConteudo.setWidth(TextConstants.LARGURA_DIALOGO_REGISTRO);

        this.setWidget(vpanelConteudo);
        this.addStyleName("dialogo-registro");

        this.setGlassEnabled(true);
        this.setAnimationEnabled(true);

        if (opcaoDialogo == OpcoesDialogoRegistro.EDITAR)
            carregarDadosRegistro(idRegistro);
        else if (opcaoDialogo == OpcoesDialogoRegistro.NOVO)
            registro = new Bebida();
    }

    private void carregarDadosRegistro(long idRegistro) {
        if (bebidaService == null) {
            bebidaService = GWT.create(BebidaService.class);
        }

        AsyncCallback<Bebida> callback = new AsyncCallback<Bebida>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Bebida result) {
                registro = result;
                transfereDadosRegistroParaFormulario();
            }
        };

        bebidaService.getBebida(idRegistro, callback);
    }

    protected void salvarRegistroAtual() {
        transfereDadosFormularioParaRegistro();

        if (bebidaService == null) {
            bebidaService = GWT.create(BebidaService.class);
        }

        if (opcaoDialogoRegistro == OpcoesDialogoRegistro.NOVO) {
            AsyncCallback<Long> callback = new AsyncCallback<Long>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(Long result) {
                    Window.alert(TextConstants.MENSAGEM_CONCLUSAO_SALVAR_REGISTRO);
                    registro = new Bebida(result.intValue(),
                            registro.getNome(), registro.getDescricao(),
                            registro.getRendimentoDoses(), registro.getPreco());
                    atualizarDadosPainelCadastro();
                }
            };

            bebidaService.insertBebida(registro, callback);
        } else if (opcaoDialogoRegistro == OpcoesDialogoRegistro.EDITAR) {
            AsyncCallback<Void> callback = new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(Void result) {
                    Window.alert(TextConstants.MENSAGEM_CONCLUSAO_SALVAR_REGISTRO);
                    atualizarDadosPainelCadastro();
                }
            };

            bebidaService.updateBebida(registro, callback);
        }
    }

    protected void transfereDadosRegistroParaFormulario() {
        textNome.setText(registro.getNome());
        textareaDescricao.setText(registro.getDescricao());
        textRendimento.setText(Integer.toString(registro.getRendimentoDoses()));

        NumberFormat nf = NumberFormat.getFormat("0.00");
        textPreco.setText(nf.format(registro.getPreco()));
    }

    private void transfereDadosFormularioParaRegistro() {
        String nome = textNome.getValue();
        String descricao = textareaDescricao.getValue();
        int rendimento = Integer.parseInt(textRendimento.getValue());
        double preco = Double.parseDouble(textPreco.getValue());

        registro.setNome(nome);
        registro.setDescricao(descricao);
        registro.setRendimentoDoses(rendimento);
        registro.setPreco(preco);
    }

    protected void excluirRegistroAtual() {
        if (bebidaService == null) {
            bebidaService = GWT.create(BebidaService.class);
        }

        AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(Boolean result) {
            	if (result) {
	                Window.alert(TextConstants.MENSAGEM_CONCLUSAO_EXCLUIR_REGISTRO);
	                executaAcaoPosExclusao();
            	}
            	else {
	                Window.alert(TextConstants.MENSAGEM_EXCLUSAO_NAO_PERMITIDA);
            	}
            }
        };

        bebidaService.deleteBebida(registro.getId(), callback);
    }

    protected void executaAcaoPosExclusao() {
        atualizarDadosPainelCadastro();
        fecharDialogoRegistro();
    }

    private void atualizarDadosPainelCadastro() {
        PainelCadastroBebidas.getInstance().recarregaDados();
    }

    protected void fecharDialogoRegistro() {
        this.hide();
    }
}
