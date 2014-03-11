
package br.com.alexromanelli.chuchuajato.dados;

@SuppressWarnings("serial")
public class Bebida extends ItemCardapio {

    public Bebida(long id, String nome, String descricao,
			int rendimento, double preco) {
		super(id, ItemCardapio.TIPO_ITEM_BEBIDA, nome, descricao, rendimento, preco);
	}

	public Bebida() {
		super();
	}

	public int getRendimentoDoses() {
        return super.getRendimento();
    }

    public void setRendimentoDoses(int rendimentoDoses) {
        super.setRendimento(rendimentoDoses);
    }

}
