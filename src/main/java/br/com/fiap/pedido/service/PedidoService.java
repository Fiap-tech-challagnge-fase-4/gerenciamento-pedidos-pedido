package br.com.fiap.pedido.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.pedido.model.PedidoModel;

@Service
public interface PedidoService {

    public List<PedidoModel> listarPedido();

    public PedidoModel finalizarPedido(Integer idpedido);

    public PedidoModel criarPedido(PedidoModel pedido);

    public PedidoModel obterPedido(Integer id);

    public PedidoModel atualizarPedido(Integer id, PedidoModel pedido);

    public void excluirPedido(Integer id);
}
