package br.com.fiap.pedido.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Data
@Entity
public class ItemPedido implements Serializable{
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
}

