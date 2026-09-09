package learn.DeVitoStyles.controller.checkcout;

import com.stripe.exception.StripeException;
import learn.DeVitoStyles.domain.Result;
import learn.DeVitoStyles.domain.checkout.CheckoutService;
import learn.DeVitoStyles.dto.CheckoutRequestDto;
import learn.DeVitoStyles.dto.CheckoutResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static learn.DeVitoStyles.domain.ResultType.INVALID;
import static learn.DeVitoStyles.domain.ResultType.NOT_FOUND;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "http://localhost:5173")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    /*
     * POST /api/checkout
     *
     * Starts the checkout process.
     *
     * Request:
     * {
     *     "cartId": 1
     * }
     *
     * Response:
     * {
     *     "checkoutUrl": "...",
     *     "orderId": 10,
     *     "orderNumber": "...",
     *     "amount": 52.13
     * }
     */
    @PostMapping
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequestDto request) throws StripeException {
        Result<CheckoutResponseDTO> result = checkoutService.checkout(request);

        if (!result.isSuccess()) {
            return ResponseEntity.status(mapStatus(result)).body(result);
        }

        return ResponseEntity.ok(result.getpayload());
    }

    private HttpStatus mapStatus(Result<?> result) {
        return switch (result.getResultType()) {

            case NOT_FOUND -> HttpStatus.NOT_FOUND;

            case INVALID -> HttpStatus.BAD_REQUEST;

            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
