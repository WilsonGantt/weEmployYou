package we.employ.you;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class WeEmployYouApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeEmployYouApplication.class, args);
	}
}