package com.example.Commerce;

import com.example.Commerce.Entities.CategoryEntity;
import com.example.Commerce.Entities.UserEntity;
import com.example.Commerce.Enums.UserRole;
import com.example.Commerce.Repositories.CategoryRepository;
import com.example.Commerce.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SeedData implements CommandLineRunner {


    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public SeedData(UserRepository userRepository, CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() <= 10) {
            log.info("Seeding users...");
            
            // Admin user
            UserEntity admin = new UserEntity();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmail("admin@commerce.com");
            admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt()));
            admin.setRole(UserRole.ADMIN);
            userRepository.save(admin);

            
            // Seller user
            UserEntity seller = new UserEntity();
            seller.setFirstName("John");
            seller.setLastName("Seller");
            seller.setEmail("seller@commerce.com");
            seller.setPassword(BCrypt.hashpw("seller123", BCrypt.gensalt()));
            seller.setRole(UserRole.SELLER);
            userRepository.save(seller);
            
            // Customer users
            UserEntity customer1 = new UserEntity();
            customer1.setFirstName("Jane");
            customer1.setLastName("Doe");
            customer1.setEmail("jane.doe@example.com");
            customer1.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer1.setRole(UserRole.CUSTOMER);
            userRepository.save(customer1);
            
            UserEntity customer2 = new UserEntity();
            customer2.setFirstName("Mike");
            customer2.setLastName("Smith");
            customer2.setEmail("mike.smith@example.com");
            customer2.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer2.setRole(UserRole.CUSTOMER);
            userRepository.save(customer2);
            
            UserEntity customer3 = new UserEntity();
            customer3.setFirstName("Sarah");
            customer3.setLastName("Johnson");
            customer3.setEmail("sarah.johnson@example.com");
            customer3.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer3.setRole(UserRole.CUSTOMER);
            userRepository.save(customer3);
            
            UserEntity customer4 = new UserEntity();
            customer4.setFirstName("David");
            customer4.setLastName("Brown");
            customer4.setEmail("david.brown@example.com");
            customer4.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer4.setRole(UserRole.CUSTOMER);
            userRepository.save(customer4);
            
            UserEntity customer5 = new UserEntity();
            customer5.setFirstName("Emily");
            customer5.setLastName("Davis");
            customer5.setEmail("emily.davis@example.com");
            customer5.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer5.setRole(UserRole.CUSTOMER);
            userRepository.save(customer5);
            
            UserEntity seller2 = new UserEntity();
            seller2.setFirstName("Robert");
            seller2.setLastName("Wilson");
            seller2.setEmail("robert.wilson@commerce.com");
            seller2.setPassword(BCrypt.hashpw("seller123", BCrypt.gensalt()));
            seller2.setRole(UserRole.SELLER);
            userRepository.save(seller2);
            
            UserEntity customer6 = new UserEntity();
            customer6.setFirstName("Lisa");
            customer6.setLastName("Martinez");
            customer6.setEmail("lisa.martinez@example.com");
            customer6.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer6.setRole(UserRole.CUSTOMER);
            userRepository.save(customer6);
            
            UserEntity customer7 = new UserEntity();
            customer7.setFirstName("James");
            customer7.setLastName("Taylor");
            customer7.setEmail("james.taylor@example.com");
            customer7.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer7.setRole(UserRole.CUSTOMER);
            userRepository.save(customer7);
            
            UserEntity customer8 = new UserEntity();
            customer8.setFirstName("Maria");
            customer8.setLastName("Garcia");
            customer8.setEmail("maria.garcia@example.com");
            customer8.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer8.setRole(UserRole.CUSTOMER);
            userRepository.save(customer8);
            
            UserEntity customer9 = new UserEntity();
            customer9.setFirstName("Chris");
            customer9.setLastName("Anderson");
            customer9.setEmail("chris.anderson@example.com");
            customer9.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer9.setRole(UserRole.CUSTOMER);
            userRepository.save(customer9);
            
            UserEntity customer10 = new UserEntity();
            customer10.setFirstName("Amanda");
            customer10.setLastName("Thomas");
            customer10.setEmail("amanda.thomas@example.com");
            customer10.setPassword(BCrypt.hashpw("customer123", BCrypt.gensalt()));
            customer10.setRole(UserRole.CUSTOMER);
            userRepository.save(customer10);
            
            log.info("Seeded {} users successfully", userRepository.count());
        } else {
            log.info("Users already exist. Skipping seed data.");
        }

        if(categoryRepository.count() == 0){
            log.info("Seeding categories...");
            CategoryEntity electronics = new CategoryEntity();
            electronics.setName("Electronics");
            electronics.setDescription("Devices and gadgets including phones, laptops, and accessories.");
            categoryRepository.save(electronics);

            CategoryEntity fashion = new CategoryEntity();
            fashion.setName("Fashion");
            fashion.setDescription("Clothing, shoes, and accessories for men and women.");
            categoryRepository.save(fashion);

            log.info("Seeded {} categories successfully", categoryRepository.count());
        }else{
            log.info("Categories already exist. Skipping seed data.");
        }
    }
}