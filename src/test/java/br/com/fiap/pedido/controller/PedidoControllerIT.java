package br.com.fiap.pedido.controller;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;

import br.com.fiap.pedido.enums.StatusPedido;
import br.com.fiap.pedido.model.ItemPedido;
import br.com.fiap.pedido.model.Pedido;
import br.com.fiap.pedido.repository.PedidoRepository;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

@SpringBootTest(properties = "spring.main.lazy-initialization=true", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PedidoControllerIT {

    @LocalServerPort
	private int port;

    private final String endPoint = "http://localhost:8084/api/pedidos";
    
    @Autowired
    private PedidoRepository pedidoRepository;

	@BeforeEach
	public void setup() {
	    RestAssured.port = 8084;
	    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
    
    @Test
    void deveListarPedidosComSucesso() throws Exception {
        //Arrange
        pedidoRepository.save(gerarPedido(1));
        pedidoRepository.save(gerarPedido(2));
        
        //Act & Assert
        given().filter(new AllureRestAssured())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(endPoint)
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PedidosSchema.json"));
    }
    
    @Test
    void deveCriarPedidoComSucesso() throws Exception {

        //Arrange
        Pedido pedido = gerarPedido(2);
        
        //Act & Assert
        given().filter(new AllureRestAssured())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(pedido)
                .when().post(endPoint)
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PedidoSchema.json"))
                .body("$", hasKey("id"))
                .body("$", hasKey("clienteid"))
                .body("$", hasKey("valortotal"))
                .body("$", hasKey("status"))
                .body("$", hasKey("datacriacao"))
                .body("$", hasKey("dataconclusao"));
    }

    @Test
    void deveFinalizarPedidoComSucesso() throws Exception {
        //Arrange
        Pedido pedido = gerarPedido(3);    	
        pedidoRepository.save(pedido);

        //Act & Assert
        given().filter(new AllureRestAssured())
                .when().put(endPoint + "/finalizar/{id}", pedido.getId())
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    void deveObterPedidoComSucesso() throws Exception {
        //Act
        Pedido pedido = gerarPedido(4);
    	pedidoRepository.save(pedido);
        
        //Act & Assert
        given().filter(new AllureRestAssured())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get(endPoint + "/{id}", pedido.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PedidoSchema.json"));
    }

    @Test
    void deveAtualizarPedidoComSucesso() throws Exception {
        
        //Arrange
        Pedido pedido = gerarPedido(5);    	
        pedidoRepository.save(pedido);
        pedido.setDataconclusao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.CRIADO);

        //Act & Assert
        given().filter(new AllureRestAssured())
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(pedido)
                .when().put(endPoint + "/{id}", pedido.getId())
                .then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./schemas/PedidoSchema.json"));        
    }

    @Test
    void deveExcluirPedidoComSucesso() throws Exception {
        
         //Arrange
         Pedido pedido = gerarPedido(6);    	
         pedidoRepository.save(pedido);
         pedido.setDataconclusao(LocalDateTime.now());
         pedido.setStatus(StatusPedido.CRIADO);
 
         //Act & Assert
         given().filter(new AllureRestAssured())
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .when().delete(endPoint + "/{id}", pedido.getId())
                 .then().statusCode(HttpStatus.NO_CONTENT.value());
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
		itemPedido.setQuantidade(1);

		var itens = Arrays.asList(itemPedido);
		pedido.setItens(itens);
        return pedido;
	}

    public static String asJsonString(final Pedido object) {
		try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			return "{ }";
		}
	}
}
