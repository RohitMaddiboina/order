package com.ecommerce.order.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestOrderCancellation
{
	@NotNull(message="Payment Method Should Not be Null")
	@NotEmpty(message="Payment Method Should Not be Emply")
	@NotBlank(message="Payment Method Should Not be Blank")
	private String orderId;
	
	@NotNull(message="Quantity Should Not be Null")
	@NotEmpty(message="Quantity Should Not be Emply")
	@NotBlank(message="Quantity Should Not be Blank")
	private int quantity;
	
	private String reason;
}
