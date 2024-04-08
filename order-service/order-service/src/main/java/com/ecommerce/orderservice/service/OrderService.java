package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.dto.OrderLineItemsDto;
import com.ecommerce.orderservice.dto.OrderRequest;
import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderLineItems;
import com.ecommerce.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient webClient;
    public void placeOrder(OrderRequest orderRequest){
        Order order=new Order();
        order.setOrderNo(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems= orderRequest.getOrderLineItemsList()
                .stream()
                .map(orderLineItemsDto -> mapToDto(orderLineItemsDto) )
                .toList();
        order.setOrderLineItemsList(orderLineItems);
        // call inventory srvice to check if the product is in stock
        Boolean result=webClient.get()
                .uri("http://localhost:8082/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if(result) {
            orderRepository.save(order);
        } else{
            throw new IllegalArgumentException("Product is not in stock, try later");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems=new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItems;
    }
}
