package com.food.orderservicemanage.config;

import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.service.OrderMessageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Slf4j
@Configuration
public class RabbitConfig {

    @Autowired
    OrderMessageService orderMessageService;
    /*

    @Autowired
    public void startListenMessage() throws IOException, TimeoutException, InterruptedException {
        orderMessageService.handleMessage();
    }
    */


   /*

   @Autowired
    public void initRabbit() {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);

        //店铺 mq
        final DirectExchange restaurantExchange = new DirectExchange("exchange.order.restaurant");
        Queue                queue              = new Queue("queue.order");
        Binding binding = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.restaurant",
                "key.order",
                null);


        rabbitAdmin.declareExchange(restaurantExchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);


        // mq 快递员
        final DirectExchange deliverymanExchange = new DirectExchange("exchange.order.deliveryman");
        Binding binding1 = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.deliveryman",
                "key.order",
                null
        );

        rabbitAdmin.declareExchange(deliverymanExchange);
        rabbitAdmin.declareBinding(binding1);


        // mq 支付
        final DirectExchange settlementExchange = new DirectExchange("exchange.settlement.order");
        Binding binding2 = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.settlement.order",
                "key.order",
                null
        );
        rabbitAdmin.declareExchange(settlementExchange);
        rabbitAdmin.declareBinding(binding2);

        // mq 积分
        final DirectExchange rewardExchange = new DirectExchange("exchange.order.reward");
        Binding binding3 = new Binding("queue.order",
                Binding.DestinationType.QUEUE,
                "exchange.order.reward",
                "key.order",
                null
        );
        rabbitAdmin.declareExchange(rewardExchange);
        rabbitAdmin.declareBinding(binding3);
    }
   */
    /*

    @Bean
    ConnectionFactory connectionFactory() {
        final CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");

        //懒汉式单例  必须使用一下才会创建其他的 exchange  binding  queue
        connectionFactory.createConnection();

        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.SIMPLE);
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }
    */

    /*
    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        final RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //新版本默认 true,旧版本默认 false.不开启无法通过 Admin 来进行queue  exchange 管理
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public Exchange restaurantExchange() {
        return new DirectExchange("exchange.order.restaurant");
    }

    @Bean
    public Queue queueOrder() {
        return new Queue("queue.order");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queueOrder()).to(restaurantExchange()).with("key.order").and(null);
    }


    @Bean
    public Exchange deliveryExchange() {
        return new DirectExchange("exchange.order.deliveryman");
    }

    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(queueOrder()).to(deliveryExchange()).with("key.order").and(null);
    }

    @Bean
    public Exchange settlementExchange() {
        return new DirectExchange("exchange.settlement.order");
    }

    @Bean
    public Binding binding3() {
        return BindingBuilder.bind(queueOrder()).to(settlementExchange()).with("key.order").and(null);
    }


    @Bean
    public Exchange rewardExchange() {
        return new DirectExchange("exchange.order.reward");
    }

    @Bean
    public Binding binding4() {
        return BindingBuilder.bind(queueOrder()).to(rewardExchange()).with("key.order").and(null);
    }
    */

    /*

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("消息发送成功:correlationData={},ack={},cause={}", correlationData, ack, cause);
        });

        rabbitTemplate.setReturnsCallback(returnCallback -> {
            log.info("消息发送失败:returnCallback={}", returnCallback);
        });
        return rabbitTemplate;
    }
    */

/*
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        final SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer(connectionFactory);
        //可变参数 监听多个队列
        simpleMessageListenerContainer.setQueues(queueOrder());

        //最大并发消费者数量
        simpleMessageListenerContainer.setConcurrentConsumers(3);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(5);

        //QoS
        simpleMessageListenerContainer.setPrefetchCount(2);

        //重回队列
        simpleMessageListenerContainer.setDefaultRequeueRejected(true);

        //消息确认方式
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);

      *//*
        //手动确认消息
        simpleMessageListenerContainer.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                log.info("手动确认消息队列监听接收到消息:{}", message);
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            }

            @Override
            public void onMessage(Message message) {
                ChannelAwareMessageListener.super.onMessage(message);
            }
        });
        *//*
        *//*

        //没有channel
        simpleMessageListenerContainer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                log.info("简单监听接收到消息:{}", message);
            }
        });
        *//*


        //适配器消息监听  默认调用 handleMessage 方法名
        // simpleMessageListenerContainer.setMessageListener(new MessageListenerAdapter(orderMessageService));

        //适配器消息监听-指定队列的处理方法
        final MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(orderMessageService);
        final HashMap<String, String> queueOrTagToMethodName = new HashMap<>();
        queueOrTagToMethodName.put("queue.order", "handleMessage2");

        messageListenerAdapter.setQueueOrTagToMethodName(queueOrTagToMethodName);


        final Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        messageConverter.setClassMapper(new ClassMapper(){
            @Override
            public void fromClass(Class<?> clazz, MessageProperties properties) {
            }

            @Override
            public Class<?> toClass(MessageProperties properties) {
                return OrderMessageDTO.class;
            }
        });
        messageListenerAdapter.setMessageConverter(messageConverter);
        simpleMessageListenerContainer.setMessageListener(messageListenerAdapter);

        return simpleMessageListenerContainer;
    }

    */

/*
    @Bean
    public RabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
    */
}
