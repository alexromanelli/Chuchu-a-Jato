
package br.com.alexromanelli.chuchuajato.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * O painel central da aplicação "Chuchu a Jato" é destinado ao controle das
 * seguintes operações:<br/>
 * <ul>
 * <li>Definir o número de mesas disponíveis;</li>
 * <li>Definir o início e o término do atendimento;</li>
 * <li>Exibir o instantâneo dos estados das mesas;</li>
 * <li>Apresentar o cadastro de pratos;</li>
 * <li>Apresentar o cadastro de bebidas;</li>
 * </ul>
 * 
 * @author alexandre
 * 
 */
public class Chuchu_A_Jato implements EntryPoint {

    private VerticalPanel vpanelPainelCentral;
    private HorizontalPanel hpanelLogo;
    private Image imageLogo;
    private Image imageUnderConstruction;
    private TabLayoutPanel tpanelControles;

    private PainelControleExpediente painelControleExpediente;
    private PainelCadastroPratos painelCadastroPratos;
    private PainelCadastroBebidas painelCadastroBebidas;
    private PainelControlePedidos painelControlePedidos;

    @Override
    public void onModuleLoad() {
        vpanelPainelCentral = new VerticalPanel();
        vpanelPainelCentral.setSize("100%", "600px");

        hpanelLogo = new HorizontalPanel();
        hpanelLogo.setWidth("100%");

        imageLogo = new Image(Resources.IMAGES.chuchuajatoLogo());
        imageLogo.setSize(TextConstants.LARGURA_IMAGEM_LOGO,
                TextConstants.ALTURA_LOGO);
        imageLogo.setAltText(TextConstants.LABEL_LOGO);

        imageUnderConstruction = new Image(Resources.IMAGES.underConstruction());
        imageUnderConstruction.setSize(
                TextConstants.LARGURA_IMAGEM_UNDER_CONSTRUCTION,
                TextConstants.ALTURA_UNDER_CONSTRUCTION);
        imageUnderConstruction
                .setAltText(TextConstants.LABEL_UNDER_CONSTRUCTION);

        hpanelLogo.add(imageLogo);
        hpanelLogo.add(imageUnderConstruction);
        hpanelLogo.setCellHorizontalAlignment(imageUnderConstruction,
                HorizontalPanel.ALIGN_RIGHT);

        tpanelControles = new TabLayoutPanel(30, Unit.PX);
        tpanelControles
                .setSize("100%", TextConstants.ALTURA_TABPANEL_CONTROLES);
        tpanelControles.setAnimationDuration(1000);
        tpanelControles.setAnimationVertical(false);

        painelControleExpediente = new PainelControleExpediente();
        // painelControleExpediente.setSize("100%",
        // TextConstants.ALTURA_PAINEL_CONTROLE);
        VerticalPanel vp = new VerticalPanel();
        vp.setSize("100%", "100%");
        vp.add(painelControleExpediente);
        vp.setCellHorizontalAlignment(painelControleExpediente,
                HorizontalPanel.ALIGN_CENTER);
        vp.setCellVerticalAlignment(painelControleExpediente,
                VerticalPanel.ALIGN_MIDDLE);
        tpanelControles.add(vp, TextConstants.TITULO_TAB_EXPEDIENTE);

        painelCadastroPratos = PainelCadastroPratos.getInstance();
        painelCadastroPratos.setSize("100%",
                TextConstants.ALTURA_PAINEL_CONTROLE);
        tpanelControles.add(painelCadastroPratos,
                TextConstants.TITULO_TAB_PRATOS);

        painelCadastroBebidas = PainelCadastroBebidas.getInstance();
        painelCadastroBebidas.setSize("100%",
                TextConstants.ALTURA_PAINEL_CONTROLE);
        tpanelControles.add(painelCadastroBebidas,
                TextConstants.TITULO_TAB_BEBIDAS);

        painelControlePedidos = new PainelControlePedidos();
        painelControlePedidos.setSize("100%",
                TextConstants.ALTURA_PAINEL_CONTROLE);
        tpanelControles.add(painelControlePedidos,
                TextConstants.TITULO_TAB_PEDIDOS);

        vpanelPainelCentral.add(hpanelLogo);
        vpanelPainelCentral
                .setCellHeight(hpanelLogo, TextConstants.ALTURA_LOGO);
        vpanelPainelCentral.add(tpanelControles);
        vpanelPainelCentral.setCellHeight(tpanelControles,
                TextConstants.ALTURA_TABPANEL_CONTROLES);

        RootPanel.get("painelCentral").add(vpanelPainelCentral);
    }

}
