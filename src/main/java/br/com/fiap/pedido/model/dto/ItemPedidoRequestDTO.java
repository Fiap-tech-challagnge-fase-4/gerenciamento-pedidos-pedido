package br.com.fiap.pedido.model.dto;

import java.math.BigDecimal;

public record ItemPedidoRequestDTO(
    Integer produtoid,
    Integer quantidade,
    BigDecimal precounitario
){}
