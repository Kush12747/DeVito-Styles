package learn.DeVitoStyles.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private Integer productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal lineTotal;
}
