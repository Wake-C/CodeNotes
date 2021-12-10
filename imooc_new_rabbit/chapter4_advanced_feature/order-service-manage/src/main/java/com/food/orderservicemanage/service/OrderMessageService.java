package com.food.orderservicemanage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.orderservicemanage.dao.OrderDetailDao;
import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.enummeration.OrderStatus;
import com.food.orderservicemanage.po.OrderDetailPO;
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
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private OrderDetailDao orderDetailDao;
    ObjectMapper objectMapper = new ObjectMapper();


    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        log.info("start linstening message");
        final ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");


        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            //店铺 mq
            channel.exchangeDeclare("exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            channel.queueDeclare("queue.order",
                    true,
                    false,
                    false,
                    null);

            channel.queueBind("queue.order",
                    "exchange.order.restaurant",
                    "key.order");

            // mq 快递员
            channel.exchangeDeclare("exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);

            channel.queueBind("queue.order",
                    "exchange.order.deliveryman",
                    "key.order");


            // mq 支付
            channel.exchangeDeclare("exchange.settlement.order",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null);


            channel.queueBind("queue.order",
                    "exchange.settlement.order",
                    "key.order");

            // mq 积分
            channel.exchangeDeclare("exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null);


            channel.queueBind("queue.order",
                    "exchange.order.reward",
                    "key.order");


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

            final OrderDetailPO orderDetailPO = orderDetailDao.selectOrder(orderMessageDTO.getOrderId());
            switch (orderDetailPO.getStatus()) {
                case ORDER_CREATING:
                    //订单创建中 则是店铺返回通知
                    if (orderMessageDTO.getConfirmed() && orderMessageDTO.getPrice() != null) {
                        orderDetailPO.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderDetailPO.setPrice(orderMessageDTO.getPrice());
                        orderDetailDao.update(orderDetailPO);

                        //可以购买通知快递员  是否有空闲的
                        Connection   connection = connectionFactory.newConnection();
                        Channel      channel    = connection.createChannel();
                        final String payLoad    = objectMapper.writeValueAsString(orderMessageDTO);
                        channel.basicPublish("exchange.order.deliveryman",
                                "key.deliveryman",
                                null,
                                payLoad.getBytes());
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;

                case RESTAURANT_CONFIRMED:
                    //快递员通知
                    if (orderMessageDTO.getDeliverymanId() != null) {
                        orderDetailPO.setDeliverymanId(orderMessageDTO.getDeliverymanId());
                        orderDetailPO.setStatus(OrderStatus.DELIVERYMAN_CONFIRMED);
                        orderDetailDao.update(orderDetailPO);
                        //可以配送通知支付
                        Connection   connection = connectionFactory.newConnection();
                        Channel      channel    = connection.createChannel();
                        final String payLoad    = objectMapper.writeValueAsString(orderMessageDTO);
                        channel.basicPublish("exchange.order.settlement",
                                "key.settlement",
                                null,
                                payLoad.getBytes());
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case DELIVERYMAN_CONFIRMED:
                    //支付完成通知
                    if (null != orderMessageDTO.getSettlementId()) {
                        orderDetailPO.setSettlementId(orderMessageDTO.getSettlementId());
                        orderDetailPO.setStatus(OrderStatus.SETTLEMENT_CONFIRMED);
                        orderDetailDao.update(orderDetailPO);
                        //支付完成 通知增加积分
                        Connection   connection = connectionFactory.newConnection();
                        Channel      channel    = connection.createChannel();
                        final String payLoad    = objectMapper.writeValueAsString(orderMessageDTO);
                        channel.basicPublish("exchange.order.reward",
                                "key.reward",
                                null,
                                payLoad.getBytes());
                    }else  {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case SETTLEMENT_CONFIRMED:
                    if (orderMessageDTO.getRewardId() != null) {
                        orderDetailPO.setStatus(OrderStatus.ORDER_CREATED);
                        orderDetailPO.setRewardId(orderMessageDTO.getRewardId());
                        orderDetailDao.update(orderDetailPO);
                    }else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    };

}
