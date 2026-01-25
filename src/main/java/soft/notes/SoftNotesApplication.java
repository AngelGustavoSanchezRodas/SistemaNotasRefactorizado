package soft.notes;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import soft.notes.IU.inicio.InicioFrame;

import java.awt.*;

@SpringBootApplication
public class SoftNotesApplication {

	public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(SoftNotesApplication.class)
                .headless(false)
                .run(args);

        EventQueue.invokeLater(() -> {

            InicioFrame inicio = context.getBean(InicioFrame.class);
            inicio.setVisible(true);
        });

	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
