package br.com.fiap.pedido.utils;
import br.com.fiap.pedido.model.ItemPedidoModel;
import br.com.fiap.pedido.model.PedidoModel;
import br.com.fiap.pedido.model.dto.ItemPedidoRequestDTO;
import br.com.fiap.pedido.model.dto.ItemPedidoResponseDTO;
import br.com.fiap.pedido.model.dto.PedidoRequestDTO;
import br.com.fiap.pedido.model.dto.PedidoResponseDTO;
import br.com.fiap.pedido.repository.entities.ItemPedido;
import br.com.fiap.pedido.repository.entities.Pedido;

public class Mapper {
	
	private Mapper(){ }

    public static PedidoResponseDTO mapPedidoModelParaPedidoResponseDTO(PedidoModel pedidoModel){

    	return new PedidoResponseDTO(
            pedidoModel.getId(),
            pedidoModel.getClienteid(),
            pedidoModel.getItens().stream().map(Mapper::mapItemPedidoModelParaItemPedidoResponseDTO).toList(),
            pedidoModel.getValortotal(),
            pedidoModel.getStatus(),
            pedidoModel.getDatacriacao(),
            pedidoModel.getDataconclusao()
        );
    }

    public static ItemPedidoResponseDTO mapItemPedidoModelParaItemPedidoResponseDTO(ItemPedidoModel itemPedidoModel){

    	return new ItemPedidoResponseDTO(
            itemPedidoModel.getId(),
            itemPedidoModel.getProdutoid(),
            itemPedidoModel.getQuantidade(),
            itemPedidoModel.getPrecounitario()
        );
    }

    public static PedidoModel mapPedidoRequestDtoParaPedidoModel(PedidoRequestDTO pedidoRequestDTO){

        return new PedidoModel(
            pedidoRequestDTO.clienteid(),
            pedidoRequestDTO.itens().stream().map(Mapper::mapItensPedidoRequestDtoParaPedidoModel).toList(),
            pedidoRequestDTO.valortotal(),
            pedidoRequestDTO.status(),
            pedidoRequestDTO.datacriacao(),
            pedidoRequestDTO.dataconclusao()
        );
    }

    public static ItemPedidoModel mapItensPedidoRequestDtoParaPedidoModel(ItemPedidoRequestDTO itemPedidoRequestDTO){

    	return new ItemPedidoModel(itemPedidoRequestDTO.produtoid(), itemPedidoRequestDTO.quantidade(), itemPedidoRequestDTO.precounitario());
    }

    public static ItemPedido mapItemPedidoModelParaItemPedido(ItemPedidoModel itemPedidoModel){
        return new ItemPedido(itemPedidoModel.getId(), itemPedidoModel.getProdutoid(), itemPedidoModel.getQuantidade(), itemPedidoModel.getPrecounitario());
    }

    public static PedidoModel mapPedidoParaPedidoModel(Pedido pedido){

    	return new PedidoModel(
            pedido.getId(),
            pedido.getClienteid(),
            pedido.getItens().stream().map(Mapper::mapPedidoModelParaPedido).toList(),
            pedido.getValortotal(),
            pedido.getStatus(),
            pedido.getDatacriacao(),
            pedido.getDataconclusao()
        );
    }

    public static ItemPedidoModel mapPedidoModelParaPedido(ItemPedido itemPedido){

    	return new ItemPedidoModel(itemPedido.getId(), itemPedido.getProdutoid(), itemPedido.getQuantidade(), itemPedido.getPrecounitario());        
    }

    public static Pedido mapPedidoModelParaPedido(PedidoModel pedidoModel){
        
    	Pedido pedido = new Pedido(
            pedidoModel.getId(),
            pedidoModel.getClienteid(),
            pedidoModel.getItens().stream().map(Mapper::mapItemPedidoModelParaItemPedido).toList(),
            pedidoModel.getValortotal(),
            pedidoModel.getStatus(),
            pedidoModel.getDatacriacao(),
            pedidoModel.getDataconclusao()
        );

        for (ItemPedido itemPedido : pedido.getItens()) {
            itemPedido.setPedido(pedido);
        }

        return pedido;
    }
}
