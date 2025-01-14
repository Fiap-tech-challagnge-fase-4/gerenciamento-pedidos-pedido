package br.com.fiap.pedido.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("br.com.fiap")
public class RabbitConfiguration {

    @Value("${queue.pedido.name}")
	public String pedidoQueueName;

	@Value("${queue.pedido.exchange.name}")
	private String pedidoExchangeName;

	@Value("${queue.pedido-dlx.key}")
	private String pedidoDlxKey;

    @Bean("pedidoQueue")
    public Queue pedidoQueue() {
        return new Queue(pedidoQueueName, true, false, false);
    }

    @Bean("pedidoDlx")
	TopicExchange pedidoDlx() {
		return new TopicExchange(pedidoExchangeName);
	}

	@Bean
	Binding pedidoBinding(
			@Qualifier("pedidoQueue") Queue pedidoQueue,
			@Qualifier("pedidoDlx") TopicExchange pedidoDlx) {
		return BindingBuilder.bind(pedidoQueue).to(pedidoDlx).with(pedidoDlxKey);
	}

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
