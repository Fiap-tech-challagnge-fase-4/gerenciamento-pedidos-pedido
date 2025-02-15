package br.com.fiap.pedido.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import br.com.fiap.pedido.enums.StatusPedido;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public record PedidoRequestDTO(
    Integer clienteid,
    List<ItemPedidoRequestDTO> itens,
    BigDecimal valortotal,
    @Enumerated(EnumType.STRING)
    StatusPedido status,
    LocalDateTime datacriacao,
    LocalDateTime dataconclusao
){}