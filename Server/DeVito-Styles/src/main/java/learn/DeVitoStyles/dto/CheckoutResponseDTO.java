package learn.DeVitoStyles.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponseDTO {

    private String clientSecret;

    private Integer orderId;

    private String orderNumber;

    private Double amount;
}
