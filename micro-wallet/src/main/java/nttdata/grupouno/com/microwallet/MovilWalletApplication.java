package nttdata.grupouno.com.microwallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class MovilWalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovilWalletApplication.class, args);
	}

}
