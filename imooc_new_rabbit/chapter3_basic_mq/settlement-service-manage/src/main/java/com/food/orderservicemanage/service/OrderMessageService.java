package com.food.orderservicemanage.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.orderservicemanage.dao.SettlementDao;
import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.enummeration.SettlementStatus;
import com.food.orderservicemanage.po.SettlementPO;
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
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private SettlementDao     settlementDao;
    @Autowired
    private SettlementService settlementService;
    ObjectMapper objectMapper = new ObjectMapper();


    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        log.info("start linstening message");
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            //订单通知 mq
            channel.exchangeDeclare("exchange.order.settlement",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null);

            channel.queueDeclare("queue.settlement",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind("queue.settlement",
                    "exchange.order.settlement",
                    "key.settlement");


            channel.basicConsume("queue.settlement", true, deliverCallback, consumerTag -> {
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
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody,
                    OrderMessageDTO.class);
            SettlementPO settlementPO = new SettlementPO();
            settlementPO.setAmount(orderMessageDTO.getPrice());
            settlementPO.setDate(new Date());
            settlementPO.setOrderId(orderMessageDTO.getOrderId());
            settlementPO.setStatus(SettlementStatus.SUCCESS);
            settlementPO.setTransactionId(settlementService.settlement(orderMessageDTO.getAccountId(),
                    orderMessageDTO.getPrice()));
            settlementDao.insert(settlementPO);

            orderMessageDTO.setSettlementId(settlementPO.getId());
            //通知订单 支付完成
            try (Connection connection = connectionFactory.newConnection();
                 Channel channel = connection.createChannel()) {
                String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish("exchange.settlement.order", "key.order", null, messageToSend.getBytes());
            }

        } catch (JsonProcessingException | TimeoutException e) {
            e.printStackTrace();
        }
    };
}
