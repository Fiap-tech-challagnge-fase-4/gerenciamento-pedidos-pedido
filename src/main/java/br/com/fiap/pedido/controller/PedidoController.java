package br.com.fiap.pedido.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.pedido.model.Pedido;
import br.com.fiap.pedido.service.PedidoService;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<Pedido> listarPedido() {
        return pedidoService.listarPedido();
    }

    @PostMapping
    public Pedido criarPedido(@RequestBody Pedido pedido) {
        return pedidoService.criarPedido(pedido);
    }

    @PutMapping("/finalizar/{id}")
    public Pedido finalizarPedido(@PathVariable Integer id) {
        return pedidoService.finalizarPedido(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obterPedido(@PathVariable Integer id) {
        Pedido pedido = pedidoService.obterPedido(id);
        return pedido != null ? ResponseEntity.ok(pedido) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Integer id, @RequestBody Pedido pedido) {
        Pedido pedidoAtualizado = pedidoService.atualizarPedido(id, pedido);
        return pedidoAtualizado != null ? ResponseEntity.ok(pedidoAtualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPedido(@PathVariable Integer id) {
        pedidoService.excluirPedido(id);
        return ResponseEntity.noContent().build();
    }
}