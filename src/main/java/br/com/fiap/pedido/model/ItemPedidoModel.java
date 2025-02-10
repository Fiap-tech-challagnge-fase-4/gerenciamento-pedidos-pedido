package br.com.fiap.pedido.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemPedidoModel{
    private Integer id;
    private Integer produtoid;
    private Integer quantidade;
    private BigDecimal precounitario;

    public ItemPedidoModel(
        Integer produtoid,
        Integer quantidade,
        BigDecimal precounitario
    ){
            this.produtoid = produtoid;
            this.quantidade = quantidade;
            this.precounitario = precounitario;
    }

    public void setUpdatePrecounitario(BigDecimal precounitario){
        if(precounitario != BigDecimal.valueOf(0)){
            this.precounitario = precounitario;
        }
    }
}

