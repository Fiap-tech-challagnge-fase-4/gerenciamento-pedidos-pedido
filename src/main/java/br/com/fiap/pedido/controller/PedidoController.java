package br.com.fiap.pedido.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import br.com.fiap.pedido.model.PedidoModel;
import br.com.fiap.pedido.model.dto.PedidoRequestDTO;
import br.com.fiap.pedido.model.dto.PedidoResponseDTO;
import br.com.fiap.pedido.service.PedidoService;
import br.com.fiap.pedido.utils.Mapper;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
	
	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}
    
    private final PedidoService pedidoService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PedidoResponseDTO> listarPedido() {
        var pedidos = pedidoService.listarPedido();
        return pedidos.stream().map(Mapper::mapPedidoModelParaPedidoResponseDTO).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponseDTO criarPedido(@RequestBody PedidoRequestDTO pedidoRequestDTO) {
        PedidoModel pedidoModel = Mapper.mapPedidoRequestDtoParaPedidoModel(pedidoRequestDTO);
        pedidoModel = pedidoService.criarPedido(pedidoModel);
        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoModel);
    }

    @PutMapping("/finalizar/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PedidoResponseDTO finalizarPedido(@PathVariable Integer id) {
        PedidoModel pedidoModel = pedidoService.finalizarPedido(id);
        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoModel);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PedidoResponseDTO obterPedido(@PathVariable Integer id) {
        PedidoModel pedidoModel = pedidoService.obterPedido(id);

        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoModel);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PedidoResponseDTO atualizarPedido(@PathVariable Integer id, @RequestBody PedidoRequestDTO pedidoRequestDTO) {
        PedidoModel pedidoModel = Mapper.mapPedidoRequestDtoParaPedidoModel(pedidoRequestDTO);
        PedidoModel pedidoAtualizado = pedidoService.atualizarPedido(id, pedidoModel);
        return Mapper.mapPedidoModelParaPedidoResponseDTO(pedidoAtualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluirPedido(@PathVariable Integer id) {        
        pedidoService.excluirPedido(id);
    }
}