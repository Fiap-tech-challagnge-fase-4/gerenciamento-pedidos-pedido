package br.com.fiap.pedido.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.pedido.model.ItemPedido;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
}