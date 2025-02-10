package br.com.fiap.pedido.model.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
    Integer id,
    Integer produtoid,
    Integer quantidade,
    BigDecimal precounitario
){}
