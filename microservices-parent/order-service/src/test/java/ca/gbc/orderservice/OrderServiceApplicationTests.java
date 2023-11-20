package ca.gbc.orderservice;

import ca.gbc.orderservice.dto.InventoryRequest;
import ca.gbc.orderservice.dto.InventoryResponse;
import ca.gbc.orderservice.dto.OrderLineItemDto;
import ca.gbc.orderservice.dto.OrderRequest;
import ca.gbc.orderservice.model.Order;
import ca.gbc.orderservice.model.OrderLineItem;
import ca.gbc.orderservice.repository.OrderRepository;
import ca.gbc.orderservice.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceApplicationTests extends AbstractContainerBaseTest{

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testPlaceOrderWithSufficientStock() {
        // Arrange
        List<OrderLineItemDto> lineItems = Arrays.asList(
                new OrderLineItemDto(1L, "SKU1", 2, new BigDecimal("100.00")),
                new OrderLineItemDto(2L, "SKU2", 1, new BigDecimal("150.00"))
        );
        OrderRequest orderRequest = OrderRequest.builder().orderLineItemDtoList(lineItems).build();

        InventoryResponse inventoryResponse1 = new InventoryResponse("SKU1", true);
        InventoryResponse inventoryResponse2 = new InventoryResponse("SKU2", true);

        InventoryResponse[] inventoryResponses = {inventoryResponse1, inventoryResponse2};
        when(responseSpec.bodyToMono(InventoryResponse[].class))
                .thenReturn(Mono.just(inventoryResponses));

        // Act
        orderService.placeOrder(orderRequest);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
