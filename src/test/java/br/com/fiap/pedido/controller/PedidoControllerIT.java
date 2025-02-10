package br.com.fiap.pedido.controller;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import br.com.fiap.pedido.enums.StatusPedido;
import br.com.fiap.pedido.repository.entities.ItemPedido;
import br.com.fiap.pedido.repository.entities.Pedido;
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
	    RestAssured.port = port;
	    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
    
    @Test
    void deveListarPedidosComSucesso() throws Exception {
        //Arrange
        inserirPedido(gerarPedido());
        inserirPedido(gerarPedido());
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
        Pedido pedido = gerarPedido();
        
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
        Pedido pedido = gerarPedido();    	
        inserirPedido(pedido);

        //Act & Assert
        given().filter(new AllureRestAssured())
                .when().put(endPoint + "/finalizar/{id}", pedido.getId())
                .then().statusCode(HttpStatus.OK.value());
    }

    @Test
    void deveObterPedidoComSucesso() throws Exception {
        //Act
        Pedido pedido = gerarPedido();
        inserirPedido(pedido);

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
        Pedido pedido = gerarPedido();    	
        inserirPedido(pedido);
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
         Pedido pedido = gerarPedido();    	
         inserirPedido(pedido);
         pedido.setDataconclusao(LocalDateTime.now());
         pedido.setStatus(StatusPedido.CRIADO);
 
         //Act & Assert
         given().filter(new AllureRestAssured())
                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                 .when().delete(endPoint + "/{id}", pedido.getId())
                 .then().statusCode(HttpStatus.OK.value());
    }

    private Pedido gerarPedido() {
        Integer maxId = pedidoRepository.getMaxId(); 	
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
		itemPedido.setQuantidade(1);

		var itens = Arrays.asList(itemPedido);
		pedido.setItens(itens);
        return pedido;
	}

    private void inserirPedido(Pedido pedido){
        pedidoRepository.save(pedido);
    }
}
