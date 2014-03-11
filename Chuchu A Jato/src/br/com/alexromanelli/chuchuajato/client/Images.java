
package br.com.alexromanelli.chuchuajato.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {

    @Source("br/com/alexromanelli/chuchuajato/client/resources/chuchuajato.png")
    ImageResource chuchuajatoLogo();

    @Source("br/com/alexromanelli/chuchuajato/client/resources/underconstruction.png")
    ImageResource underConstruction();

    @Source("br/com/alexromanelli/chuchuajato/client/resources/loader1.gif")
    ImageResource loader1();

    @Source("br/com/alexromanelli/chuchuajato/client/resources/loader2.gif")
    ImageResource loader2();

    @Source("br/com/alexromanelli/chuchuajato/client/resources/loader1-40.gif")
    ImageResource loader1_40();

    @Source("br/com/alexromanelli/chuchuajato/client/resources/loader2-40.gif")
    ImageResource loader2_40();

}
