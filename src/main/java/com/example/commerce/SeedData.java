package com.example.Commerce;

import com.example.Commerce.entities.CategoryEntity;
import com.example.Commerce.entities.InventoryEntity;
import com.example.Commerce.entities.ProductEntity;
import com.example.Commerce.entities.UserEntity;
import com.example.Commerce.enums.UserRole;
import com.example.Commerce.repositories.CategoryRepository;
import com.example.Commerce.repositories.InventoryRepository;
import com.example.Commerce.repositories.ProductRepository;
import com.example.Commerce.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SeedData implements CommandLineRunner {


    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    public SeedData(UserRepository userRepository, CategoryRepository categoryRepository,
                    ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
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

        // Seed Products and Inventories
        if(productRepository.count() == 0){
            log.info("Seeding products...");

            // Get categories
            CategoryEntity electronics = categoryRepository.findByNameIgnoreCase("Electronics")
                    .orElseThrow(() -> new RuntimeException("Electronics category not found"));
            CategoryEntity fashion = categoryRepository.findByNameIgnoreCase("Fashion")
                    .orElseThrow(() -> new RuntimeException("Fashion category not found"));

            // Electronics Products
            ProductEntity laptop = new ProductEntity();
            laptop.setName("Dell XPS 15 Laptop");
            laptop.setCategory(electronics);
            laptop.setSku("DELL-XPS15-001");
            laptop.setPrice(1299.99);
            laptop.setAvailable(true);
            productRepository.save(laptop);

            InventoryEntity laptopInventory = new InventoryEntity();
            laptopInventory.setProduct(laptop);
            laptopInventory.setQuantity(25);
            laptopInventory.setLocation("Warehouse A - Section 1");
            inventoryRepository.save(laptopInventory);

            ProductEntity smartphone = new ProductEntity();
            smartphone.setName("iPhone 15 Pro");
            smartphone.setCategory(electronics);
            smartphone.setSku("APPLE-IP15P-001");
            smartphone.setPrice(999.99);
            smartphone.setAvailable(true);
            productRepository.save(smartphone);

            InventoryEntity smartphoneInventory = new InventoryEntity();
            smartphoneInventory.setProduct(smartphone);
            smartphoneInventory.setQuantity(50);
            smartphoneInventory.setLocation("Warehouse A - Section 2");
            inventoryRepository.save(smartphoneInventory);

            ProductEntity headphones = new ProductEntity();
            headphones.setName("Sony WH-1000XM5 Headphones");
            headphones.setCategory(electronics);
            headphones.setSku("SONY-WH1000XM5-001");
            headphones.setPrice(399.99);
            headphones.setAvailable(true);
            productRepository.save(headphones);

            InventoryEntity headphonesInventory = new InventoryEntity();
            headphonesInventory.setProduct(headphones);
            headphonesInventory.setQuantity(100);
            headphonesInventory.setLocation("Warehouse A - Section 3");
            inventoryRepository.save(headphonesInventory);

            ProductEntity smartwatch = new ProductEntity();
            smartwatch.setName("Apple Watch Series 9");
            smartwatch.setCategory(electronics);
            smartwatch.setSku("APPLE-AWS9-001");
            smartwatch.setPrice(429.99);
            smartwatch.setAvailable(true);
            productRepository.save(smartwatch);

            InventoryEntity smartwatchInventory = new InventoryEntity();
            smartwatchInventory.setProduct(smartwatch);
            smartwatchInventory.setQuantity(75);
            smartwatchInventory.setLocation("Warehouse A - Section 2");
            inventoryRepository.save(smartwatchInventory);

            ProductEntity tablet = new ProductEntity();
            tablet.setName("Samsung Galaxy Tab S9");
            tablet.setCategory(electronics);
            tablet.setSku("SAMSUNG-TABS9-001");
            tablet.setPrice(649.99);
            tablet.setAvailable(true);
            productRepository.save(tablet);

            InventoryEntity tabletInventory = new InventoryEntity();
            tabletInventory.setProduct(tablet);
            tabletInventory.setQuantity(40);
            tabletInventory.setLocation("Warehouse A - Section 1");
            inventoryRepository.save(tabletInventory);

            ProductEntity camera = new ProductEntity();
            camera.setName("Canon EOS R6 Mark II");
            camera.setCategory(electronics);
            camera.setSku("CANON-R6M2-001");
            camera.setPrice(2499.99);
            camera.setAvailable(true);
            productRepository.save(camera);

            InventoryEntity cameraInventory = new InventoryEntity();
            cameraInventory.setProduct(camera);
            cameraInventory.setQuantity(15);
            cameraInventory.setLocation("Warehouse B - Section 1");
            inventoryRepository.save(cameraInventory);

            // Fashion Products
            ProductEntity menJeans = new ProductEntity();
            menJeans.setName("Levi's 501 Original Fit Jeans");
            menJeans.setCategory(fashion);
            menJeans.setSku("LEVIS-501-001");
            menJeans.setPrice(89.99);
            menJeans.setAvailable(true);
            productRepository.save(menJeans);

            InventoryEntity menJeansInventory = new InventoryEntity();
            menJeansInventory.setProduct(menJeans);
            menJeansInventory.setQuantity(200);
            menJeansInventory.setLocation("Warehouse C - Section 1");
            inventoryRepository.save(menJeansInventory);

            ProductEntity womenDress = new ProductEntity();
            womenDress.setName("Floral Summer Dress");
            womenDress.setCategory(fashion);
            womenDress.setSku("DRESS-FLORAL-001");
            womenDress.setPrice(79.99);
            womenDress.setAvailable(true);
            productRepository.save(womenDress);

            InventoryEntity womenDressInventory = new InventoryEntity();
            womenDressInventory.setProduct(womenDress);
            womenDressInventory.setQuantity(150);
            womenDressInventory.setLocation("Warehouse C - Section 2");
            inventoryRepository.save(womenDressInventory);

            ProductEntity sneakers = new ProductEntity();
            sneakers.setName("Nike Air Max 270");
            sneakers.setCategory(fashion);
            sneakers.setSku("NIKE-AM270-001");
            sneakers.setPrice(149.99);
            sneakers.setAvailable(true);
            productRepository.save(sneakers);

            InventoryEntity sneakersInventory = new InventoryEntity();
            sneakersInventory.setProduct(sneakers);
            sneakersInventory.setQuantity(180);
            sneakersInventory.setLocation("Warehouse C - Section 3");
            inventoryRepository.save(sneakersInventory);

            ProductEntity jacket = new ProductEntity();
            jacket.setName("North Face ThermoBall Jacket");
            jacket.setCategory(fashion);
            jacket.setSku("TNF-THERMO-001");
            jacket.setPrice(199.99);
            jacket.setAvailable(true);
            productRepository.save(jacket);

            InventoryEntity jacketInventory = new InventoryEntity();
            jacketInventory.setProduct(jacket);
            jacketInventory.setQuantity(120);
            jacketInventory.setLocation("Warehouse C - Section 1");
            inventoryRepository.save(jacketInventory);

            ProductEntity handbag = new ProductEntity();
            handbag.setName("Michael Kors Jet Set Tote");
            handbag.setCategory(fashion);
            handbag.setSku("MK-JSTOTE-001");
            handbag.setPrice(298.00);
            handbag.setAvailable(true);
            productRepository.save(handbag);

            InventoryEntity handbagInventory = new InventoryEntity();
            handbagInventory.setProduct(handbag);
            handbagInventory.setQuantity(85);
            handbagInventory.setLocation("Warehouse C - Section 4");
            inventoryRepository.save(handbagInventory);

            ProductEntity sunglasses = new ProductEntity();
            sunglasses.setName("Ray-Ban Aviator Classic");
            sunglasses.setCategory(fashion);
            sunglasses.setSku("RB-AVIATOR-001");
            sunglasses.setPrice(159.99);
            sunglasses.setAvailable(true);
            productRepository.save(sunglasses);

            InventoryEntity sunglassesInventory = new InventoryEntity();
            sunglassesInventory.setProduct(sunglasses);
            sunglassesInventory.setQuantity(250);
            sunglassesInventory.setLocation("Warehouse C - Section 5");
            inventoryRepository.save(sunglassesInventory);

            log.info("Seeded {} products with inventories successfully", productRepository.count());
        }else{
            log.info("Products already exist. Skipping seed data.");
        }
    }
}