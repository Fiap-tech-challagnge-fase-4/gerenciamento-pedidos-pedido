package br.com.fiap.pedido.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import br.com.fiap.pedido.enums.StatusPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Pedido")
public class Pedido implements Serializable{
    @Id
    private Integer id;
    private Integer clienteid;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pedido", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ItemPedido> itens;
    private BigDecimal valortotal;
    @Enumerated(EnumType.STRING)
    private StatusPedido status;
    private LocalDateTime datacriacao;
    private LocalDateTime dataconclusao;
}



