package com.food.orderservicemanage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.orderservicemanage.dao.OrderDetailDao;
import com.food.orderservicemanage.dto.OrderMessageDTO;
import com.food.orderservicemanage.enummeration.OrderStatus;
import com.food.orderservicemanage.po.OrderDetailPO;
import com.food.orderservicemanage.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    RabbitTemplate rabbitTemplate;

 /*   @Value("${rabbitmq.exchange}")
    public String exchangeName;
    @Value("${rabbitmq.restaurant-routing-key}")
    public String restaurantRoutingKey;
    @Value("${rabbitmq.deliveryman-routing-key}")
    public String deliverymanRoutingKey;*/

    ObjectMapper objectMapper = new ObjectMapper();


    public void createOrder(OrderCreateVO orderCreateVO) throws IOException, TimeoutException, InterruptedException {
        log.info("createOrder:orderCreateVO:{}", orderCreateVO);
        OrderDetailPO orderPO = new OrderDetailPO();
        orderPO.setAddress(orderCreateVO.getAddress());
        orderPO.setAccountId(orderCreateVO.getAccountId());
        orderPO.setProductId(orderCreateVO.getProductId());
        orderPO.setStatus(OrderStatus.ORDER_CREATING);
        orderPO.setDate(new Date());
        orderDetailDao.insert(orderPO);

        //通知店铺下单商品
        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();
        orderMessageDTO.setOrderId(orderPO.getId());
        orderMessageDTO.setProductId(orderPO.getProductId());
        orderMessageDTO.setAccountId(orderCreateVO.getAccountId());

        final String messagePayLoad = objectMapper.writeValueAsString(orderMessageDTO);
/*
        final MessageProperties messageProperties = new MessageProperties();
        Message                 message           = new Message(messagePayLoad.getBytes(), messageProperties);
        rabbitTemplate.send(
                "exchange.order.restaurant",
                "key.restaurant",
                message
        );*/

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(orderMessageDTO.getOrderId().toString());
        rabbitTemplate.convertAndSend("exchange.order.restaurant",
                "key.restaurant",
                messagePayLoad,
                correlationData);

        log.info("message sent");


       /* ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try (final Connection connection = connectionFactory.newConnection();
             final Channel channel = connection.createChannel()) {

            //开启消息发送确认模式
            channel.confirmSelect();

            //异步确认要在发送消息前设置好.
            // 异步确认可能单条确认一次，也可能多条确认一次
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    log.info("异步消息发送确认 deliveryTag={}", deliveryTag);
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    log.info("异步消息发送失败确认 deliveryTag={}", deliveryTag);
                }
            });

            //设置消息过期时间
            final AMQP.BasicProperties properties =new AMQP.BasicProperties().builder().expiration(String.valueOf(15 * 1000)).build();


                final String messagePayLoad = objectMapper.writeValueAsString(orderMessageDTO);
                channel.basicPublish("exchange.order.restaurant",
                        "key.restaurant",
                        properties,
                        messagePayLoad.getBytes()
                );
                log.info("message sent");
        }*/

    }

}
