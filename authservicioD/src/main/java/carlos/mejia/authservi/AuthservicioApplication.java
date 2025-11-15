package carlos.mejia.authservi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients	
public class AuthservicioApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthservicioApplication.class, args);
		
		
	}
	
	/*
	public class GeneradorPassword {
	    public static void main(String[] args) {
	        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	        String rawPassword = "adminpass"; 
	        String hashedPassword = encoder.encode(rawPassword);
	        System.out.println("El hash BCrypt para 'adminpass' es: " + hashedPassword);
	    }
	}*/

}
