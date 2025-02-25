package br.com.fiap.pedido.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.function.StreamBridge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.pedido.enums.StatusPedido;
import br.com.fiap.pedido.model.PedidoModel;
import br.com.fiap.pedido.repository.entities.ItemPedido;
import br.com.fiap.pedido.repository.entities.Pedido;
import br.com.fiap.pedido.repository.PedidoRepository;
import br.com.fiap.pedido.service.impl.PedidoServiceImpl;
import br.com.fiap.pedido.utils.Mapper;

@SpringBootTest
class PedidoServiceImplTest {
	
    @Mock
    private PedidoRepository pedidoRepository;
    
	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
    private StreamBridge streamBridge;
	
    private PedidoServiceImpl pedidoServiceImpl;
    
    private AutoCloseable openMocks;
    
	@BeforeEach
	void setup(){
		openMocks = MockitoAnnotations.openMocks(this);
		pedidoServiceImpl = new PedidoServiceImpl(pedidoRepository, restTemplate, streamBridge, objectMapper);
	}
	@AfterEach
	void teardown() throws Exception {
		openMocks.close();
	}
	
	@Test
    void deveListarPedidosComSucesso() {
        //Arrange
        List<Pedido> listaPedidos = Arrays.asList(gerarPedido(1), gerarPedido(2));
		when(pedidoRepository.findAll()).thenReturn(listaPedidos);

        //Act
        List<PedidoModel> listaPedidosObtida = pedidoServiceImpl.listarPedido();

		// Assert
		verify(pedidoRepository, times(1)).findAll();		
		assertThat(listaPedidosObtida).isNotEmpty().hasSize(2)
			.allSatisfy(pedido -> {
			assertThat(pedido).isNotNull().isInstanceOf(PedidoModel.class);
		});
    }
	
	@Test
    void deveCriarPedidoComSucesso() throws Exception {

        //Arrange
		String produto = gerarProduto();
		JsonNode jsonNode = gerarProdutoJsonNode(produto);
        Pedido pedido = gerarPedido(2);
		ResponseEntity<String> responseEntity = ResponseEntity.ok(produto);

		//Act
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
		when(restTemplate.getForEntity(anyString(), eq(String.class), anyInt())).thenReturn(responseEntity);
		when(objectMapper.readTree(anyString())).thenReturn(jsonNode);

		PedidoModel pedidoCriado = pedidoServiceImpl.criarPedido(Mapper.mapPedidoParaPedidoModel(pedido));

		// Assert
		verify(pedidoRepository, times(1)).save(any(Pedido.class));
		assertThat(pedidoCriado).isNotNull();
		assertThat(pedidoCriado.getId()).isEqualTo(2);
    }

	@Test
    void deveFinalizarPedidoComSucesso() {
        
		//Arrange
        Pedido pedido = gerarPedido(2);
		pedido.setDataconclusao(LocalDateTime.now());		
		when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
        when(pedidoRepository.findById(anyInt())).thenReturn(Optional.of(pedido));
        //Act
		PedidoModel pedidoFinalizado = pedidoServiceImpl.finalizarPedido(pedido.getId());
		
		//Assert
		verify(pedidoRepository, times(1)).save(any(Pedido.class));
		assertThat(pedidoFinalizado).isNotNull().isInstanceOf(PedidoModel.class).isNotNull();
		assertThat(pedidoFinalizado.getDataconclusao()).isEqualTo(pedido.getDataconclusao());
		assertThat(pedidoFinalizado.getStatus().name()).isEqualTo(StatusPedido.FINALIZADO.name());
    }

	@Test
    void deveObterPedidoComSucesso() {
        //Act
        Pedido pedido = gerarPedido(4);
		when(pedidoRepository.findById(anyInt())).thenReturn(Optional.of(pedido));

        //Act
		PedidoModel pedidoObtido = pedidoServiceImpl.obterPedido(pedido.getId());

		//Assert
		verify(pedidoRepository, times(1)).findById(anyInt());
        assertThat(pedidoObtido).isInstanceOf(PedidoModel.class).isNotNull();
		assertThat(pedidoObtido.getId()).isEqualTo(pedido.getId());
    }

	@Test
    void deveAtualizarPedidoComSucesso() throws Exception {
        
        //Arrange
		String produto = gerarProduto();
		JsonNode jsonNode = gerarProdutoJsonNode(produto);        
		ResponseEntity<String> responseEntity = ResponseEntity.ok(produto);
        Pedido pedido = gerarPedido(5);
		pedido.setDataconclusao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);
		when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
		when(pedidoRepository.findById(anyInt())).thenReturn(Optional.of(pedido));
		when(restTemplate.getForEntity(anyString(), eq(String.class), anyInt())).thenReturn(responseEntity);
		when(objectMapper.readTree(anyString())).thenReturn(jsonNode);

		//Act
		PedidoModel pedidoAtualizado = pedidoServiceImpl.atualizarPedido(pedido.getId(), Mapper.mapPedidoParaPedidoModel(pedido));
        
		//Assert
		verify(pedidoRepository, times(1)).save(any(Pedido.class));
        assertThat(pedidoAtualizado).isNotNull().isInstanceOf(PedidoModel.class).isNotNull();
		assertThat(pedidoAtualizado.getDataconclusao()).isEqualTo(pedido.getDataconclusao());
		assertThat(pedidoAtualizado.getStatus().name()).isEqualTo(StatusPedido.CRIADO.name());
    }

	@Test
    void deveExcluirPedidoComSucesso() {
        
         //Arrange
		 int idPedido = 6;
		 Pedido pedido = gerarPedido(idPedido);
		 when(pedidoRepository.findById(anyInt())).thenReturn(Optional.of(pedido));
		 doNothing().when(pedidoRepository).deleteById(anyInt());

		 //Act
		 pedidoServiceImpl.excluirPedido(idPedido);         
		 
		 
		 //Assert
		verify(pedidoRepository, times(1)).deleteById(anyInt());
    }
		
	private Pedido gerarPedido(int id) {		
		Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setClienteid(1);
        pedido.setValortotal(BigDecimal.valueOf(59.90));
        pedido.setDataconclusao(LocalDateTime.now());
        pedido.setDatacriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);

		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setId(1);
		itemPedido.setPedido(pedido);
		itemPedido.setPrecounitario(BigDecimal.valueOf(10.40));
		itemPedido.setProdutoid(1);
		itemPedido.setQuantidade(10);

		List<ItemPedido> itens = Arrays.asList(itemPedido);
		pedido.setItens(itens);
        return pedido;
	}

	public static JsonNode gerarProdutoJsonNode(String produtoJson) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readTree(produtoJson);
	}

	public static String gerarProduto(){
		return "{\"id\": 2,\"nome\": \"Camiseta Masculina\",\"descricao\": \"Camiseta de corrida\",\"preco\": 7.15,\"quantidadeEstoque\": 80,\"categoria\": \"Vestuário\",\"imagemurl\": \"\",\"codigobarras\": \"102030\",\"status\": \"Ativo\"}";
	}
 }
