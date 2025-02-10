package br.com.fiap.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.fiap.pedido.enums.StatusPedido;
import br.com.fiap.pedido.repository.entities.ItemPedido;
import br.com.fiap.pedido.repository.entities.Pedido;
import br.com.fiap.pedido.repository.PedidoRepository;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PedidoServiceImplIT {
	
    @Autowired
    private PedidoRepository pedidoRepository;
	
	@Test
    void deveListarPedidosComSucesso() throws Exception {
        //Arrange
        inserirPedido(gerarPedido());

        //Act
        List<Pedido> listaPedidos = pedidoRepository.findAll();

		// Assert
		assertThat(listaPedidos).isNotEmpty().hasSizeGreaterThan(0);
		assertThat(listaPedidos).allSatisfy(pedido -> {
			assertThat(pedido).isNotNull();
		});
    }

	@Test
    void deveCriarPedidoComSucesso() throws Exception {

        //Arrange
        Pedido pedido = gerarPedido();
        
        //Act
		var pedidoCriado = inserirPedido(pedido);

		// Assert
		assertThat(pedidoCriado).isNotNull();
		assertThat(pedidoCriado.getId()).isGreaterThan(0);
		assertThat(pedidoCriado.getItens().size()).isGreaterThan(0);
    }
	
	@Test
    void deveFinalizarPedidoComSucesso() throws Exception {
        
		//Arrange
        Pedido pedido = gerarPedido();
        
        //Act
		var pedidoCriado = inserirPedido(pedido);
		pedidoCriado.setDataconclusao(LocalDateTime.now());
		pedidoCriado.setStatus(StatusPedido.FINALIZADO);
		var pedidoFinalizado = pedidoRepository.save(pedidoCriado);

		//Assert
		assertThat(pedidoFinalizado).isNotNull();
		assertThat(pedidoFinalizado).isInstanceOf(Pedido.class).isNotNull();
		assertThat(pedidoFinalizado.getDataconclusao()).isEqualTo(pedidoCriado.getDataconclusao());
		assertThat(pedidoFinalizado.getStatus().name()).isEqualTo(StatusPedido.FINALIZADO.name());
    }
	
	@Test
    void deveObterPedidoComSucesso() throws Exception {
        //Act
        Pedido pedido = gerarPedido();
    	        
        //Act
		var pedidoInserido = inserirPedido(pedido);
		var pedidoObtidoOptional = pedidoRepository.findById(pedidoInserido.getId());

		var pedidoObtido = pedidoObtidoOptional.get();
		//Assert
        assertThat(pedidoObtido).isNotNull();
		assertThat(pedidoObtido).isInstanceOf(Pedido.class).isNotNull();
		assertThat(pedidoObtido.getId()).isEqualTo(pedido.getId());
    }
	
	@Test
    void deveAtualizarPedidoComSucesso() throws Exception {
        
        //Arrange
        Pedido pedido = gerarPedido();    	

		//Act
        var pedidoCriado = inserirPedido(pedido);
        pedidoCriado.setDataconclusao(LocalDateTime.now());
        pedidoCriado.setStatus(StatusPedido.CRIADO);
		var pedidoAtualizado = pedidoRepository.save(pedidoCriado);
        
		//Assert
        assertThat(pedidoAtualizado).isNotNull();
		assertThat(pedidoAtualizado).isInstanceOf(Pedido.class).isNotNull();
		assertThat(pedidoAtualizado.getDataconclusao()).isEqualTo(pedidoCriado.getDataconclusao());
		assertThat(pedidoAtualizado.getStatus().name()).isEqualTo(StatusPedido.CRIADO.name());
    }
	
	@Test
    void deveExcluirPedidoComSucesso() throws Exception {
        
         //Arrange
         Pedido pedido = gerarPedido();    	
         
		 //Act
		 var pedidoCriado = inserirPedido(pedido);
		 pedidoRepository.deleteById(pedidoCriado.getId());
		 Optional<Pedido> pedidoExcluido = pedidoRepository.findById(pedido.getId());
		 
		 //Assert
         assertThat(pedidoExcluido).isEmpty();
    }

	private Pedido gerarPedido() {
		Integer maxId = pedidoRepository.getMaxId();
		if(maxId == 0){
			maxId = 1;
		}
		var pedido = new Pedido();
        pedido.setId(maxId);
        pedido.setClienteid(1);
        pedido.setValortotal(BigDecimal.valueOf(59.90));
        pedido.setDataconclusao(LocalDateTime.now());
        pedido.setDatacriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);

		var itemPedido = new ItemPedido();
		itemPedido.setPedido(pedido);
		itemPedido.setPrecounitario(BigDecimal.valueOf(10.40));
		itemPedido.setProdutoid(1);
		itemPedido.setQuantidade(10);

		var itens = Arrays.asList(itemPedido);
		pedido.setItens(itens);
        return pedido;
	}

	private Pedido inserirPedido(Pedido pedido){        
        return pedidoRepository.save(pedido);
    }
}
