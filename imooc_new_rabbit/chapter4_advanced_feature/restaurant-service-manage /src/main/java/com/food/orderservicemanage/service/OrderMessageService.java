package com.food.orderservicemanage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.orderservicemanage.dao.ProductDao;
import com.food.orderservicemanage.dao.RestaurantDao;
import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.enummeration.ProductStatus;
import com.food.orderservicemanage.enummeration.RestaurantStatus;
import com.food.orderservicemanage.po.ProductPO;
import com.food.orderservicemanage.po.RestaurantPO;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private RestaurantDao restaurantDao;
    @Autowired
    private ProductDao    productDao;
    ObjectMapper objectMapper = new ObjectMapper();
    Channel      channel;

    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        log.info("start linstening message");
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            this.channel = channel;

            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            //死信exchange
            channel.exchangeDeclare("exchange.dlx",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null
                );

            channel.queueDeclare("queue.dlx",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind("queue.dlx",
                    "exchange.dlx",
                    "#");

            //设置队列过期时间
            Map<String, Object> arguments = new HashMap<>(16);
            arguments.put("x-message-ttl", 10000);
            //消息超过5条
            arguments.put("x-max-length", 5);
            arguments.put("x-dead-letter-exchange", "exchange.dlx");

            channel.queueDeclare("queue.restaurant",
                    true,
                    false,
                    false,
                    arguments);

            channel.queueBind("queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant");

            //qos   prefetchSize  global 还未支持
            // channel.basicQos(2048444,2,true);
            // channel.basicQos(2);


            channel.basicConsume("queue.restaurant", false, deliverCallback, consumerTag -> {
            });
            //防止线程退出 不监听消费
            while (true) {
                Thread.sleep(1000000000);
            }
        }
    }

    DeliverCallback deliverCallback = (consumerTag, message) -> {
        final String messageBody = new String(message.getBody());
        log.info("deliverCallback:messageBody:{}", messageBody);

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            final OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody, OrderMessageDTO.class);
            final ProductPO       productPO       = productDao.selsctProduct(orderMessageDTO.getProductId());

            final RestaurantPO restaurantPO = restaurantDao.selsctRestaurant(productPO.getRestaurantId());
            if (ProductStatus.AVALIABIE == productPO.getStatus() && RestaurantStatus.OPEN == restaurantPO.getStatus()) {
                orderMessageDTO.setConfirmed(true);
                orderMessageDTO.setPrice(productPO.getPrice());
            } else {
                orderMessageDTO.setConfirmed(false);
            }

            //测试 QoS
            // Thread.sleep(1000);

            // //消息确认
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            //拒收重回队列
            // channel.basicNack(message.getEnvelope().getDeliveryTag(), false, true);


            channel.addReturnListener(returnCallback -> {
                log.info("消息返回 getReplyText={}", returnCallback.getReplyText());
            });

            //通知订单已经是否可以购买
            Connection   connection = connectionFactory.newConnection();
            Channel      channel    = connection.createChannel();
            final String payLoad    = objectMapper.writeValueAsString(orderMessageDTO);
            channel.basicPublish("exchange.order.restaurant", "key.order", null, payLoad.getBytes());
            Thread.sleep(1000);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

}
