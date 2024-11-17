package com.haskar.onetoonechat;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.bind.annotation.CrossOrigin;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(

				title = "Chat App API",
				version = "1.0",
				description = "This is a sample API",
				contact = @Contact(name = "Hassan Askar", email = "hassan.askar@gmail.com"),
				license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
		)
)
@CrossOrigin("*")
public class OneToOneChatApplication {
	public static void main(String[] args) {
		SpringApplication.run(OneToOneChatApplication.class, args);
	}

}
