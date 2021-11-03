package com.ecommerce.order.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RequestOrderCancellation {
	@NotNull(message="Payment Method Should Not be Null")
	@NotEmpty(message="Payment Method Should Not be Emply")
	@NotBlank(message="Payment Method Should Not be Blank")
	private String orderId;
	private String reason;
}
