package learn.DeVitoStyles.controller.checkcout;

import com.stripe.exception.ApiConnectionException;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.ResultType;
import learn.DeVitoStyles.domain.checkout.CheckoutService;
import learn.DeVitoStyles.dto.CheckoutRequestDto;
import learn.DeVitoStyles.dto.CheckoutResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new CheckoutController(checkoutService)).build();
    }

    private static <T> Result<T> failure(String message, ResultType type) {
        Result<T> result = new Result<>();
        result.addErrorMessage(message, type);
        return result;
    }

    @Test
    void checkout_whenSuccess_returns200WithCheckoutPayload() throws Exception {
        Result<CheckoutResponseDTO> result = new Result<>();
        result.setpayload(new CheckoutResponseDTO(
                "https://checkout.stripe.com/c/pay/cs_test_123", 10, "ORD-20261015-0010", 52.13));
        when(checkoutService.checkout(any(CheckoutRequestDto.class))).thenReturn(result);

        mvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.stripe.com/c/pay/cs_test_123"))
                .andExpect(jsonPath("$.orderId").value(10))
                .andExpect(jsonPath("$.orderNumber").value("ORD-20261015-0010"))
                .andExpect(jsonPath("$.amount").value(52.13));

        ArgumentCaptor<CheckoutRequestDto> captor = ArgumentCaptor.forClass(CheckoutRequestDto.class);
        verify(checkoutService).checkout(captor.capture());
        assertThat(captor.getValue().getCartId()).isEqualTo(1);
    }

    @Test
    void checkout_whenInvalid_returns400WithMessages() throws Exception {
        when(checkoutService.checkout(any(CheckoutRequestDto.class)))
                .thenReturn(failure("Cart is empty", ResultType.INVALID));

        mvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value("Cart is empty"));
    }

    @Test
    void checkout_whenCartIdMissing_passesNullCartIdToService() throws Exception {
        when(checkoutService.checkout(any(CheckoutRequestDto.class)))
                .thenReturn(failure("Cart id is required", ResultType.INVALID));

        mvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0]").value("Cart id is required"));

        ArgumentCaptor<CheckoutRequestDto> captor = ArgumentCaptor.forClass(CheckoutRequestDto.class);
        verify(checkoutService).checkout(captor.capture());
        assertThat(captor.getValue().getCartId()).isNull();
    }

    @Test
    void checkout_whenCartNotFound_returns404WithMessages() throws Exception {
        when(checkoutService.checkout(any(CheckoutRequestDto.class)))
                .thenReturn(failure("Cart not found", ResultType.NOT_FOUND));

        mvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":999}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value("Cart not found"));
    }

    @Test
    void checkout_whenOtherFailureType_returns500WithMessages() throws Exception {
        when(checkoutService.checkout(any(CheckoutRequestDto.class)))
                .thenReturn(failure("Order already exists", ResultType.CONFLICT));

        mvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$[0]").value("Order already exists"));
    }

    @Test
    void checkout_whenStripeThrows_returns502WithMessage() throws Exception {
        when(checkoutService.checkout(any(CheckoutRequestDto.class)))
                .thenThrow(new ApiConnectionException("Stripe unreachable"));

        mvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":1}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value("Unable to reach the payment provider. Please try again."));
    }
}
