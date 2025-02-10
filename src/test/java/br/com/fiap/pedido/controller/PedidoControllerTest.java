package br.com.fiap.pedido.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.http.MediaType;

import br.com.fiap.pedido.enums.StatusPedido;
import br.com.fiap.pedido.model.PedidoModel;
import br.com.fiap.pedido.repository.entities.ItemPedido;
import br.com.fiap.pedido.repository.entities.Pedido;
import br.com.fiap.pedido.service.PedidoService;
import br.com.fiap.pedido.utils.Mapper;

public class PedidoControllerTest {

    private final String endPoint = "http://localhost:8084/api/pedidos";

    private MockMvc mockMvc;
    private AutoCloseable mock;

    @Mock
    private PedidoService pedidoService;

    @InjectMocks
    private PedidoController pedidoController;

    @BeforeEach
    void setUp() {
    	mock = MockitoAnnotations.openMocks(this);
    	mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
    		.addFilter((request, response, chain) -> {
    			response.setCharacterEncoding("UTF-8");
    			chain.doFilter(request, response);
    		})
    		.build();
    }

    @AfterEach
    void tearDown() throws Exception {
    	mock.close();
    }
    
    @Test
    void deveListarPedidosComSucesso() throws Exception {
        List<PedidoModel> pedidos = gerarListaPedidos().stream().map(p -> Mapper.mapPedidoParaPedidoModel(p)).toList();
        when(pedidoService.listarPedido()).thenReturn(pedidos);

        mockMvc.perform(get(endPoint))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].valortotal").value(59.90));
    }
    
    @Test
    void deveCriarPedidoComSucesso() throws Exception {
        
        Pedido pedido = gerarPedido(5);
    	
        when(pedidoService.criarPedido(any())).thenReturn(Mapper.mapPedidoParaPedidoModel(pedido));

        var content = asJsonString(pedido);

        mockMvc.perform(post(endPoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.valortotal").value(59.90));
    }

    @Test
    void deveFinalizarPedidoComSucesso() throws Exception {

        Pedido pedido = gerarPedido(5);
        pedido.setStatus(StatusPedido.FINALIZADO);
        
    	PedidoModel pedidoModel = Mapper.mapPedidoParaPedidoModel(pedido);
        when(pedidoService.finalizarPedido(any())).thenReturn(pedidoModel);

        var content = asJsonString(pedido);

        mockMvc.perform(put(endPoint + "/finalizar/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.status").value(StatusPedido.FINALIZADO.toString()));
    }

    @Test
    void deveObterPedidoComSucesso() throws Exception {
        
        Pedido pedido = gerarPedido(5);
    	
        pedido.setStatus(StatusPedido.FINALIZADO);

        when(pedidoService.obterPedido(anyInt())).thenReturn(Mapper.mapPedidoParaPedidoModel(pedido));

        var content = asJsonString(pedido);

        mockMvc.perform(get(endPoint + "/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.status").value(StatusPedido.FINALIZADO.toString()));
    }

    @Test
    void deveAtualizarPedidoComSucesso() throws Exception {

        Pedido pedido = gerarPedido(5);
    	
        when(pedidoService.atualizarPedido(anyInt(), any(PedidoModel.class))).thenReturn(Mapper.mapPedidoParaPedidoModel(pedido));

        var content = asJsonString(pedido);

        mockMvc.perform(put(endPoint + "/" + pedido.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.status").value(StatusPedido.CRIADO.toString()));
    }

    @Test
    void deveExcluirPedidoComSucesso() throws Exception {
        doNothing().when(pedidoService).excluirPedido(anyInt());
        mockMvc.perform(delete(endPoint + "/1")).andExpect(status().isOk());
    }

    private List<Pedido> gerarListaPedidos() {
		List<Pedido> listaReservas = Arrays.asList(
			gerarPedido(1),
			gerarPedido(2)
		);
		return listaReservas;
	}

    private Pedido gerarPedido(int id) {		
		var pedido = new Pedido();
        pedido.setId(id);
        pedido.setClienteid(1);
        pedido.setValortotal(BigDecimal.valueOf(59.90));
        pedido.setDataconclusao(LocalDateTime.now());
        pedido.setDatacriacao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);

        var itemPedido = new ItemPedido();
		itemPedido.setId(1);
		itemPedido.setPedido(pedido);
		itemPedido.setPrecounitario(BigDecimal.valueOf(10.40));
		itemPedido.setProdutoid(1);
		itemPedido.setQuantidade(10);

		var itens = Arrays.asList(itemPedido);
		pedido.setItens(itens);
        return pedido;
	}

    public static String asJsonString(final Pedido object) throws Exception{
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper.writeValueAsString(object);
	}
}
