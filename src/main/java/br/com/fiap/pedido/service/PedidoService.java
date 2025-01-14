package br.com.fiap.pedido.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.fiap.pedido.enums.StatusPedido;
import br.com.fiap.pedido.model.ItemPedido;
import br.com.fiap.pedido.model.Pedido;
import br.com.fiap.pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;

@Service
@RequiredArgsConstructor
public class PedidoService {

    @Value("${produto.url}")
    private String produtoURL;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private final StreamBridge streamBridge;

    private static final String PEDIDO_OUTPUT = "pedidoEntregaQueue-in-0";

    // Método para listar todos os pedidos
    public List<Pedido> listarPedido() {
        return pedidoRepository.findAll();
    }

    // Método para finalizar um pedido criado
    public Pedido finalizarPedido(Integer idpedido) {

        Pedido pedido = pedidoRepository.findById(idpedido).orElse(null);
        
        //Verifica se o pedido existe
        if(pedido == null)
        {
            throw new NoSuchElementException("O pedido de id " + idpedido +  " não foi encontrado.");
        }

        if(pedido.getStatus() == StatusPedido.FINALIZADO )
        {
            throw new NoSuchElementException("O pedido de id " + idpedido +  " já foi finalizado.");
        }
        
        pedido.setDataconclusao(LocalDateTime.now());
        pedido.setStatus(StatusPedido.FINALIZADO);

        return pedidoRepository.save(pedido);
    }

    // Método para criar um novo pedido
    @Transactional
    public Pedido criarPedido(Pedido pedido) {

         // Valida se os itens do pedido existem no estoque
         boolean produtosDisponiveis = verificarDisponibilidadeProdutos(pedido.getItens());
         if (!produtosDisponiveis) {
             throw new NoSuchElementException("Um ou mais produtos não estão disponíveis");
         }
 
         // TODO: Adicionar outras validações, exemplo existencia de cliente, se o
         // produto existe, dentre outras...
 
         BigDecimal valortotal = calcularValorTotal(pedido.getItens());
         pedido.setValortotal(valortotal);
 
         pedido.setDatacriacao(LocalDateTime.now());
         pedido.setStatus(StatusPedido.CRIADO);
 
         //Controla o sequencial do pedido de forma lógica
         if (pedido.getId() == null) 
         { 
             Integer maxId = pedidoRepository.getMaxId(); 
             pedido.setId((maxId != null && maxId > 0) ? maxId + 1 : 1); 
         }
         
         // Salva o pedido
         Pedido savedPedido = pedidoRepository.save(pedido);
 
         //Enviar evento de criação do pedido para a fila 
         //MessageBuilder<String> messageBuilder = MessageBuilder.withPayload("teste");//savedPedido.toString() 
         //Message<String> message = MessageBuilder.withPayload(pedido) .setHeader("contentType", "application/json") .build();
         streamBridge.send(PEDIDO_OUTPUT, MessageBuilder.withPayload(pedido).build());
         //pedidoOutput.send(messageBuilder.build());
         
         return savedPedido;
    }

    // Método para obter um pedido pelo ID
    public Pedido obterPedido(Integer id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElse(null);
    }

    // Método para atualizar um pedido existente
    public Pedido atualizarPedido(Integer id, Pedido pedido) {
        
        // if (pedidoRepository.existsById(id)) {
        //     pedido.setId(id);
        //     return pedidoRepository.save(pedido);
        // }
        // return null;


        //Por conta das regras de estoque, um novo o pedido é recriado ao invez de atualizar
        Pedido p = pedidoRepository.findById(id).orElse(null);
        
        //Verifica se o pedido existe
        if(p == null)
        {
            throw new NoSuchElementException("O pedido de id " + id +  " não foi encontrado.");
        }

        if(p.getStatus() == StatusPedido.FINALIZADO )
        {
            throw new NoSuchElementException("O pedido de id " + id +  " já foi finalizado.");
        }
        
        //Efetua a exclusão do pedido
        excluirPedido(id);

        //Cria um novo com as atualizações e mantendo o mesmo id
        pedido.setId(id);
        return criarPedido(pedido);
    }

    // Método para excluir um pedido pelo ID
    public void excluirPedido(Integer id) {

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        // Atualiza o estoque dos produtos
        if (pedido != null) {
            atualizaEstoqueProdutos(pedido.getItens(), false);
        }

        pedidoRepository.deleteById(id);
    }

    // Verifica a disponibilidade no microservico de produtos
    private boolean verificarDisponibilidadeProdutos(List<ItemPedido> itensPedido) {
        for (ItemPedido itemPedido : itensPedido) {
            Integer idProduto = itemPedido.getProdutoid();
            Integer quantidade = itemPedido.getQuantidade();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    produtoURL + "{id}",
                    String.class,
                    idProduto);

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoSuchElementException("Produto não encontrado");
            } else {
                try {
                    JsonNode produtoJson = objectMapper.readTree((response.getBody()));
                    int quantidadeEstoque = produtoJson.get("quantidadeestoque").asInt();

                    if (quantidadeEstoque < quantidade)
                        return false;

                } catch (IOException e) {
                    // Tratar erro
                }

            }
        }

        return true;

    }

    // Calcula o valor total
    private BigDecimal calcularValorTotal(List<ItemPedido> itensPedido) {
        BigDecimal valortotal = BigDecimal.ZERO;

        for (ItemPedido itemPedido : itensPedido) {
            Integer idProduto = itemPedido.getProdutoid();
            Integer quantidade = itemPedido.getQuantidade();

            ResponseEntity<String> response = restTemplate.getForEntity(
                    produtoURL + "{id}",
                    String.class,
                    idProduto);

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode produtoJson = objectMapper.readTree((response.getBody()));
                    BigDecimal preco = BigDecimal.valueOf(produtoJson.get("preco").asDouble());
                    valortotal = valortotal.add(preco.multiply(BigDecimal.valueOf(quantidade))); // usando add e
                                                                                                 // multiply

                    itemPedido.setPrecounitario(preco);
                } catch (IOException ex) {
                    // Tratar exception
                }
            }

        }
        return valortotal;
    }

    // Atualiza o estoque de produtos, somando ou subtraindo a quantidade
    private boolean atualizaEstoqueProdutos(List<ItemPedido> itensPedido, boolean removerEstoque) {
        for (ItemPedido itemPedido : itensPedido) {
            Integer idProduto = itemPedido.getProdutoid();
            Integer quantidade = itemPedido.getQuantidade();

            restTemplate.put(
                    produtoURL + "/atualizarEstoque/{id}/{quantidade}",
                    null,
                    idProduto,
                    quantidade);
        }

        return true;
    }
}
