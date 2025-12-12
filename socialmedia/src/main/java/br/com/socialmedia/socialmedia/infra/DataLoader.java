package br.com.socialmedia.socialmedia.infra;

import br.com.socialmedia.socialmedia.entity.User;
import br.com.socialmedia.socialmedia.repository.UserRepository;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import org.slf4j.Logger;
@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger =  LoggerFactory.getLogger(DataLoader.class);
    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if(userRepository.count() == 0){

            User buyer1 = new User("Comprador 1", false);
            User seller1 = new User("Vendedor 1", true);
            User seller2 = new User("Vendedor 2", true);

            userRepository.saveAll(List.of(buyer1, seller1, seller2));
            logger.info("Initial users loaded successfully.");
        } else {
            logger.info("Users already exist in the database. Skipping initial load.");
        }

    }
}
