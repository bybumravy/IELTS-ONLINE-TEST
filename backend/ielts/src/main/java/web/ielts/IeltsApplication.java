package web.ielts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IeltsApplication {

	public static void main(String[] args) {
		SpringApplication.run(IeltsApplication.class, args);
	}

}
