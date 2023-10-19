package server.utils;

import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AdminPasswordGenerator {

    private RandomKeyGenerator randomKeyGenerator = new RandomKeyGenerator();
    private String adminPassword;

    @PostConstruct
    public void generateAdminPassword() {
        // Generate the admin password here
        adminPassword = randomKeyGenerator.generateKey();

        // Print the admin password to the console
        System.out.println("ADMIN PASSWORD: " + adminPassword);
    }

    public String getAdminPassword() {
        return adminPassword;
    }
}
