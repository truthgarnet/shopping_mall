package com.truthgarnet.shopping.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.truthgarnet.shopping.common.CustomException;
import com.truthgarnet.shopping.common.ErrorCode;
import com.truthgarnet.shopping.common.FieldErrorResponse;
import com.truthgarnet.shopping.orderItem.OrderItemRequest;

@WebMvcTest(OrderController.class)
public class OrderValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void empty_items_is_400() throws Exception {
        String body = """
                {"items": []}
                """;
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CM001"))
                .andExpect(jsonPath("$.errors[0].field").value("items"))
                .andExpect(jsonPath( "$.errors[0].message").value("주문상품을 넣어주세요."));
    }

    @Test
    void zero_quantity_is_400() throws Exception {
        String body = """
                {"items": [{"productSeq": 1, "quantity": 0}]}
                """;

        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(errorCode.getCode()))
                .andExpect(jsonPath("$.errors[0].field").value("items[0].quantity"))
                .andExpect(jsonPath( "$.errors[0].message").value("수량은 1개 이상이어야 합니다."));

    }

    @Test
    void is_200() throws Exception {
        String body = """
                {"items": [{"productSeq": 1, "quantity": 5}]}
                """;
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void lack_quantity_is_OUT_OF_STOCK() throws Exception {
        OrderItemRequest orderItemRequest = new OrderItemRequest(1L, 5);
        List<OrderItemRequest> items = new ArrayList<>();
        items.add(orderItemRequest);
        OrderRequest orderRequest = new OrderRequest(items);

        // given
        ErrorCode errorCode = ErrorCode.OUT_OF_STOCK;

        List<FieldErrorResponse> errors = List.of(new FieldErrorResponse("items[0].productSeq", errorCode.getMessage()));

        when(orderService.insertOrders(any()))
                .thenThrow(new CustomException(errorCode, errors));

        CustomException e = catchThrowableOfType(CustomException.class, () -> orderService.insertOrders(orderRequest));
        assertThat(e.getErrors()).hasSize(1)
                .extracting(FieldErrorResponse::getField)
                .containsExactly("items[0].productSeq");
        }
}
