package com.food.orderservicemanage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.orderservicemanage.dao.RewardDao;
import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.enummeration.RewardStatus;
import com.food.orderservicemanage.po.RewardPO;
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
    private RewardDao rewardDao;
    ObjectMapper objectMapper = new ObjectMapper();


    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        log.info("start linstening message");
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");


        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            //店铺 mq
            channel.exchangeDeclare("exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null);

            channel.queueDeclare("queue.reward",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind("queue.reward",
                    "exchange.order.reward",
                    "key.reward");

            channel.basicConsume("queue.reward", true, deliverCallback, consumerTag -> {
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
            RewardPO              rewardPO        = new RewardPO();
            rewardPO.setOrderId(orderMessageDTO.getOrderId());
            rewardPO.setStatus(RewardStatus.SUCCESS);
            rewardPO.setAmount(orderMessageDTO.getPrice());
            rewardPO.setDate(new Date());
            rewardDao.insert(rewardPO);
            orderMessageDTO.setRewardId(rewardPO.getId());

            //积分增加完毕 通知订单
            try (Connection connection = connectionFactory.newConnection();
                 Channel channel = connection.createChannel()) {
                String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish("exchange.order.reward", "key.order", null, messageToSend.getBytes());
            }

        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    };
}