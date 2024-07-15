package kr.or.kosa.cmsplusmain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class CmsplusMainApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmsplusMainApplication.class, args);
	}

}
