package br.com.fiap.pedido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.pedido.model.Pedido;

@Service
public interface PedidoService {

    public List<Pedido> listarPedido();

    public Pedido finalizarPedido(Integer idpedido);

    public Pedido criarPedido(Pedido pedido);

    public Pedido obterPedido(Integer id);

    public Pedido atualizarPedido(Integer id, Pedido pedido);

    public void excluirPedido(Integer id);
}
