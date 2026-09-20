package com.truthgarnet.shopping.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.truthgarnet.shopping.common.CustomException;
import com.truthgarnet.shopping.common.ErrorCode;

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
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CM001"))
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
        String body = """
                {"items": [{"productSeq": 1, "quantity": 5}]}
                """;

        // given
        when(orderService.insertOrders(any()))
                .thenThrow(new CustomException(ErrorCode.OUT_OF_STOCK, "상품 A는 재고가 부족한 상품입니다."));

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.OUT_OF_STOCK.getCode()))
                .andExpect(jsonPath("$.errors").isEmpty());
    }
}