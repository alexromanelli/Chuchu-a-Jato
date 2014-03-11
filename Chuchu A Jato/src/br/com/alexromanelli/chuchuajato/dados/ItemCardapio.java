/**
 * 
 */
package br.com.alexromanelli.chuchuajato.dados;

import java.io.Serializable;

/**
 * @author alexandre
 *
 */
@SuppressWarnings("serial")
public abstract class ItemCardapio implements Serializable {
	
	public static String[] TEXTO_TIPO_ITEM_CARDAPIO = {
		"p", "b"
	};
	public static int TIPO_ITEM_PRATO = 0;
	public static int TIPO_ITEM_BEBIDA = 1;

    public static String KEY_ID = "id";
    public static String KEY_TIPO = "tipo";
    public static String KEY_NOME = "nome";
    public static String KEY_DESCRICAO = "descricao";
    public static String KEY_RENDIMENTO = "rendimento";
    public static String KEY_PRECO = "preco";

    private long id;
    private int tipo;
    private String nome;
    private String descricao;
    private int rendimento;
    private double preco;

    public ItemCardapio() {
        this.id = -1;
        this.nome = "";
        this.descricao = "";
        this.rendimento = 0;
        this.preco = 0.0;
    }

    public ItemCardapio(long id, int tipo, String nome, String descricao, int rendimento,
            double preco) {
        super();
        this.id = id;
        this.tipo = tipo; 
        this.nome = nome;
        this.descricao = descricao;
        this.rendimento = rendimento;
        this.preco = preco;
    }
    
    public int getTipo() {
    	return tipo;
    }
    
    public String getTextoTipo() {
    	return TEXTO_TIPO_ITEM_CARDAPIO[tipo];
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getRendimento() {
        return rendimento;
    }

    public void setRendimento(int rendimento) {
        this.rendimento = rendimento;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public long getId() {
        return id;
    }

}
