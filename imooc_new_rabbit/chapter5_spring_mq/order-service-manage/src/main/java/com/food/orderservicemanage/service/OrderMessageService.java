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
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class OrderMessageService {
    @Autowired
    private OrderDetailDao orderDetailDao;
    ObjectMapper objectMapper = new ObjectMapper();


    @RabbitListener(
            //bingdings的 queue 都监听到了,不填写 bindings参数才需要填写
            // queues = {"queue.order"},
            //如果配置文件配置 mq 不需要填写 admin containerFactory .会使用默认的
            // admin = "rabbitAdmin",
            // containerFactory = "rabbitListenerContainerFactory",
            bindings = {
                    @QueueBinding(
                            value = @Queue(
                                    name = "queue.order",
                                    arguments = {
                                            @Argument(
                                                    name = "x-message-ttl",
                                                    value = "10000",
                                                    type = "java.lang.Integer"
                                            ),
                                            @Argument(
                                                    name = "x-dead-letter-exchange",
                                                    value = "exchange.dlx",
                                                    type = "java.lang.String"
                                            )
                                    }

                            ),
                            exchange = @Exchange(value = "exchange.order.restaurant", type = ExchangeTypes.DIRECT),
                            key = "key.order"
                    ),
                    @QueueBinding(
                                value = @Queue(
                                        name = "queue.dlx"
                                ),
                                exchange = @Exchange(value = "exchange.dlx", type = ExchangeTypes.TOPIC),
                                key = "#"
                    ),
                    @QueueBinding(
                            value = @Queue(
                                    name = "queue.order"
                            ),
                            exchange = @Exchange(value = "exchange.order.deliveryman", type = ExchangeTypes.DIRECT),
                            key = "key.order"
                    ),
                    @QueueBinding(
                            value = @Queue(
                                    name = "queue.order"
                            ),
                            exchange = @Exchange(value = "exchange.settlement.settlement", type = ExchangeTypes.FANOUT),
                            key = "key.order"
                    ),
                    @QueueBinding(
                            value = @Queue(
                                    name = "queue.order"
                            ),
                            exchange = @Exchange(value = "exchange.order.reward", type = ExchangeTypes.TOPIC),
                            key = "key.order"
                    )
            }
    )
    public void handleMessage2(byte[] orderMessageDTO2) throws IOException, TimeoutException, InterruptedException {

        log.info("handleMessage:messageBody:{}", orderMessageDTO2);
        final String messageBody = new String(orderMessageDTO2);
        log.info("deliverCallback:messageBody:{}", orderMessageDTO2);

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
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
                case SETTLEMENT_CONFIRMED:
                    if (orderMessageDTO.getRewardId() != null) {
                        orderDetailPO.setStatus(OrderStatus.ORDER_CREATED);
                        orderDetailPO.setRewardId(orderMessageDTO.getRewardId());
                        orderDetailDao.update(orderDetailPO);
                    } else {
                        orderDetailPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderDetailPO);
                    }
                    break;
            }


        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    ;

}
