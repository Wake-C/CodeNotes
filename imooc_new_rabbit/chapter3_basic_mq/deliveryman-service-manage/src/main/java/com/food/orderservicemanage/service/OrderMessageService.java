package com.food.orderservicemanage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.orderservicemanage.dao.DeliverymanDao;
import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.enummeration.DeliverymanStatus;
import com.food.orderservicemanage.po.DeliverymanPO;
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
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private DeliverymanDao deliverymanDao;
    ObjectMapper objectMapper = new ObjectMapper();


    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        log.info("start linstening message");
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            //快递员 mq
            channel.exchangeDeclare("exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            channel.queueDeclare("queue.deliveryman",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind("queue.deliveryman",
                    "exchange.order.deliveryman",
                    "key.deliveryman");


            channel.basicConsume("queue.order", true, deliverCallback, consumerTag -> {
            });
            while (true) {
                Thread.sleep(1000000);
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
            final List<DeliverymanPO> deliverymanPO = deliverymanDao.selectAvaliableDeliveryman(DeliverymanStatus.AVALIABIE);
            orderMessageDTO.setDeliverymanId(deliverymanPO.get(0).getId());

            //通知订单
            Connection   connection = connectionFactory.newConnection();
            Channel      channel    = connection.createChannel();
            final String messagePayLoad          = objectMapper.writeValueAsString(orderMessageDTO);
            channel.basicPublish("exchange.order.deliveryman",
                    "key.order",
                    null,
                    messagePayLoad.getBytes());
        }catch (TimeoutException e) {
            e.printStackTrace();
        }
    };
}
