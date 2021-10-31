package com.ecommerce.order.config;

import org.springframework.context.annotation.Bean;

import java.util.Collections;

import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Configuration

public class SwaggerConfig {

	@Bean
	public Docket swaggerDocket()
	{
				return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.paths(
				PathSelectors.ant("/orders/")
				.or( PathSelectors.ant("/orders/isWalletAmountSufficient") )
			
			
			
				)
				.apis(RequestHandlerSelectors.basePackage("com.ecommerce.order"))
				.build()
				.apiInfo(apiDetails());
	}
	
	public ApiInfo apiDetails()
	{
				return new ApiInfo("Order-Service",
				"This service return order placed by user"
				+ "\n ->Payement method [wallet balance,Pay on delivery]"
				+ "\n ->Display Debited Amount"
				+ "\n ->Deliver done to be user location", "1.0", "Free Api",
				new Contact("Fasscio","vamsi.com","vk98@gmail.com"), "My Licence",
				"http://localhost:9898", Collections.emptyList());
	}


	
}
