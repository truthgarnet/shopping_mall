package com.truthgarnet.shopping.order;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

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
                .andExpect(result -> {
                        Exception exception = result.getResolvedException();

                        assertInstanceOf(MethodArgumentNotValidException.class, exception);
                });
    }


    @Test
    void zero_quantity_is_400() throws Exception {
        String body = """
                {"items": [{"productSeq": 1, "quantity": 0}]}
                """;
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                        Exception exception = result.getResolvedException();

                        assertInstanceOf(MethodArgumentNotValidException.class, exception);
                });
    }

    @Test
    void is_200() throws Exception {
        String body = """
                {"items": [{"productSeq": 1, "quantity": 5}]}
                """;
        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is2xxSuccessful());
    }
}
