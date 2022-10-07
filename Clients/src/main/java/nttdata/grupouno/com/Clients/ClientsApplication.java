package nttdata.grupouno.com.Clients;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class ClientsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientsApplication.class, args);
	}

}
