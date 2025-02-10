package br.com.fiap.pedido.utils;

import java.util.stream.Collectors;

import br.com.fiap.pedido.model.ItemPedidoModel;
import br.com.fiap.pedido.model.PedidoModel;
import br.com.fiap.pedido.model.dto.ItemPedidoRequestDTO;
import br.com.fiap.pedido.model.dto.ItemPedidoResponseDTO;
import br.com.fiap.pedido.model.dto.PedidoRequestDTO;
import br.com.fiap.pedido.model.dto.PedidoResponseDTO;
import br.com.fiap.pedido.repository.entities.ItemPedido;
import br.com.fiap.pedido.repository.entities.Pedido;

public class Mapper {

    public static PedidoResponseDTO mapPedidoModelParaPedidoResponseDTO(PedidoModel pedidoModel){

        PedidoResponseDTO pedidoResponseDTO = new PedidoResponseDTO(
            pedidoModel.getId(),
            pedidoModel.getClienteid(),
            pedidoModel.getItens().stream().map(a -> mapItemPedidoModelParaItemPedidoResponseDTO(a)).collect(Collectors.toList()),
            pedidoModel.getValortotal(),
            pedidoModel.getStatus(),
            pedidoModel.getDatacriacao(),
            pedidoModel.getDataconclusao()
        );

        return pedidoResponseDTO;
    }

    public static ItemPedidoResponseDTO mapItemPedidoModelParaItemPedidoResponseDTO(ItemPedidoModel itemPedidoModel){

        ItemPedidoResponseDTO itemPedidoResponseDTO = new ItemPedidoResponseDTO(
            itemPedidoModel.getId(),
            itemPedidoModel.getProdutoid(),
            itemPedidoModel.getQuantidade(),
            itemPedidoModel.getPrecounitario()
        );
        return itemPedidoResponseDTO;
    }

    public static PedidoModel mapPedidoRequestDtoParaPedidoModel(PedidoRequestDTO pedidoRequestDTO){

        PedidoModel pedido = new PedidoModel(
            pedidoRequestDTO.clienteid(),
            pedidoRequestDTO.itens().stream().map(item -> mapItensPedidoRequestDtoParaPedidoModel(item)).collect(Collectors.toList()),
            pedidoRequestDTO.valortotal(),
            pedidoRequestDTO.status(),
            pedidoRequestDTO.datacriacao(),
            pedidoRequestDTO.dataconclusao()
        );
        return pedido;
    }

    public static ItemPedidoModel mapItensPedidoRequestDtoParaPedidoModel(ItemPedidoRequestDTO itemPedidoRequestDTO){

        ItemPedidoModel item = new ItemPedidoModel(itemPedidoRequestDTO.produtoid(), itemPedidoRequestDTO.quantidade(), itemPedidoRequestDTO.precounitario());
        return item;
    }

    public static ItemPedido mapItemPedidoModelParaItemPedido(ItemPedidoModel itemPedidoModel){
        ItemPedido item = new ItemPedido(itemPedidoModel.getId(), itemPedidoModel.getProdutoid(), itemPedidoModel.getQuantidade(), itemPedidoModel.getPrecounitario());
        return item;
    }

    public static PedidoModel mapPedidoParaPedidoModel(Pedido pedido){

        PedidoModel pedidoModel = new PedidoModel(
            pedido.getId(),
            pedido.getClienteid(),
            pedido.getItens().stream().map(item -> mapPedidoModelParaPedido(item)).collect(Collectors.toList()),
            pedido.getValortotal(),
            pedido.getStatus(),
            pedido.getDatacriacao(),
            pedido.getDataconclusao()
        );
        return pedidoModel;
    }

    public static ItemPedidoModel mapPedidoModelParaPedido(ItemPedido itemPedido){

        ItemPedidoModel item = new ItemPedidoModel(itemPedido.getId(), itemPedido.getProdutoid(), itemPedido.getQuantidade(), itemPedido.getPrecounitario());
        return item;
    }

    public static Pedido mapPedidoModelParaPedido(PedidoModel pedidoModel){
        
        Pedido pedido = new Pedido(
            pedidoModel.getId(),
            pedidoModel.getClienteid(),
            pedidoModel.getItens().stream().map(item -> mapItemPedidoModelParaItemPedido(item)).collect(Collectors.toList()),
            pedidoModel.getValortotal(),
            pedidoModel.getStatus(),
            pedidoModel.getDatacriacao(),
            pedidoModel.getDataconclusao()
        );
        return pedido;
    }
}
