package br.com.fiap.pedido.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.com.fiap.pedido.model.PedidoModel;
import br.com.fiap.pedido.model.dto.PedidoRequestDTO;
import br.com.fiap.pedido.model.dto.PedidoResponseDTO;
import br.com.fiap.pedido.service.PedidoService;
import br.com.fiap.pedido.utils.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping
    public List<PedidoResponseDTO> listarPedido() {
        var pedidos = pedidoService.listarPedido();
        return pedidos.stream().map(p -> Mapper.mapPedidoModelParaPedidoResponseDTO(p)).collect(Collectors.toList());
    }

    @PostMapping
    public PedidoResponseDTO criarPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        PedidoModel pedidoModel = Mapper.mapPedidoRequestDtoParaPedidoModel(pedidoRequestDTO);
        pedidoModel = pedidoService.criarPedido(pedidoModel);
        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoModel);
    }

    @PutMapping("/finalizar/{id}")
    public PedidoResponseDTO finalizarPedido(@PathVariable Integer id) {
        PedidoModel pedidoModel = pedidoService.finalizarPedido(id);
        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoModel);
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO obterPedido(@PathVariable Integer id) {
        PedidoModel pedidoModel = pedidoService.obterPedido(id);

        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoModel);
    }

    @PutMapping("/{id}")
    public PedidoResponseDTO atualizarPedido(@PathVariable Integer id, @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        PedidoModel pedidoModel = Mapper.mapPedidoRequestDtoParaPedidoModel(pedidoRequestDTO);
        PedidoModel pedidoAtualizado = pedidoService.atualizarPedido(id, pedidoModel);
        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoAtualizado);
    }

    @DeleteMapping("/{id}")
    public void excluirPedido(@PathVariable Integer id) {        
        pedidoService.excluirPedido(id);
    }
}