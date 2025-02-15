package br.com.fiap.pedido.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.fiap.pedido.repository.entities.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    @Query("SELECT COALESCE(MAX(p.id), 0) FROM Pedido p") 
    Integer getMaxId();
}