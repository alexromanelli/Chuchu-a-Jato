
package br.com.alexromanelli.chuchuajato.dados;

@SuppressWarnings("serial")
public class Prato extends ItemCardapio {

    public Prato(long id, String nome, String descricao,
			int rendimento, double preco) {
		super(id, ItemCardapio.TIPO_ITEM_PRATO, nome, descricao, rendimento, preco);
	}

	public Prato() {
		super();
	}

    public int getRendimentoPorcoes() {
        return super.getRendimento();
    }

    public void setRendimentoPorcoes(int rendimentoPorcoes) {
        super.setRendimento(rendimentoPorcoes);
    }

}
