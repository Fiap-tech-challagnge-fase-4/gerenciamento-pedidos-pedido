package br.com.fiap.pedido.repository.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
@AllArgsConstructor
public class ItemPedido implements Serializable{
    public ItemPedido(){
        
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne 
    @JoinColumn(name = "pedidoid") 
    @JsonBackReference
    private Pedido pedido;
    private Integer produtoid;
    private Integer quantidade;
    private BigDecimal precounitario;

    public ItemPedido(
        Integer id,
        Integer produtoid,
        Integer quantidade,
        BigDecimal precounitario
    ){
        this.id = id;
        this.produtoid = produtoid;
        this.quantidade = quantidade;
        this.precounitario = precounitario;
    }

    public void setPedido(Pedido pedido){
        this.pedido = pedido;
    }
}