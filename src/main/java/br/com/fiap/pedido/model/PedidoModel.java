package br.com.fiap.pedido.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.pedido.enums.StatusPedido;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PedidoModel{
    private Integer id;
    private Integer clienteid;
    private List<ItemPedidoModel> itens;
    private BigDecimal valortotal;
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    private LocalDateTime datacriacao;
    private LocalDateTime dataconclusao;

    public PedidoModel(
        Integer clienteid,
        List<ItemPedidoModel> itens,
        BigDecimal valortotal,
        StatusPedido status,
        LocalDateTime datacriacao,
        LocalDateTime dataconclusao
    ){
        this.id = 0;
        this.clienteid = clienteid;
        this.itens = itens;
        this.valortotal = valortotal;
        this.status = status;
        this.datacriacao = datacriacao;
        this.dataconclusao = dataconclusao;   
    }

    public void setId(int id){
        this.id = id;
    }

    public void setStatus(StatusPedido statusPedido) throws Exception{
        if(this.status == StatusPedido.FINALIZADO){
            throw new Exception("Não é possível alterar o status de um pedido finalizado.");
        }

        if(this.status == statusPedido){
            throw new Exception("O status do pedido já é: " + statusPedido.toString());
        }

        this.status = statusPedido;
    }
}