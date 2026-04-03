package paulodev.sentinel_api.config;

import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import paulodev.sentinel_api.modules.apartment.entity.Apartment;
import paulodev.sentinel_api.modules.apartment.repository.ApartmentRepository;
import paulodev.sentinel_api.modules.condominium.entity.Condominium;
import paulodev.sentinel_api.modules.condominium.repository.CondominiumRepository;
import paulodev.sentinel_api.modules.user.entity.User;
import paulodev.sentinel_api.modules.user.entity.UserRole;
import paulodev.sentinel_api.modules.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   CondominiumRepository condominiumRepository,
                                   ApartmentRepository apartmentRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                System.out.println("Iniciando o Seeder Completo do Banco de Dados");

                Faker faker = new Faker(new Locale("pt", "BR"));
                String defaultPassword = passwordEncoder.encode("123456");

                /// CRIANDO USUÁRIOS

                List<User> users = new ArrayList<>();

                // User admin fixo
                User pauloAdmin = new User("Paulo", "paulo@test.com", defaultPassword, UserRole.ADMIN);
                users.add(pauloAdmin);

                // Criando 9 fake users
                for (int i = 0; i < 9; i++) {
                    String name = faker.name().fullName();
                    String email = faker.internet().emailAddress();
                    User fakeUser = new User(name, email, defaultPassword, UserRole.USER);
                    users.add(fakeUser);
                }
                userRepository.saveAll(users);
                System.out.println("10 Usuários criados!");


                /// CRIANDO CONDOMÍNIOS (Aleatório por Usuário)

                List<Condominium> allCondominiums = new ArrayList<>();

                for (User user : users) {
                    // Sorteia de 1 a 3 condomínios para este usuário
                    int qtdCondominios = faker.random().nextInt(1, 4);

                    for (int i = 0; i < qtdCondominios; i++) {
                        String condoName = "Condomínio " + faker.company().name();
                        String address = faker.address().streetAddress();

                        Condominium condo = new Condominium(condoName, address, user);
                        allCondominiums.add(condo);
                    }
                }

                condominiumRepository.saveAll(allCondominiums);
                System.out.println(allCondominiums.size() + " Condomínios criados!");


                /// CRIANDO APARTAMENTOS (Aleatório por Condomínio)

                List<Apartment> allUnits = new ArrayList<>();

                for (Condominium condo : allCondominiums) {
                    // Sorteia de 10 a 40 apartamentos para este prédio
                    int qtdApartaments = faker.random().nextInt(3, 11);

                    for (int i = 1; i <= qtdApartaments; i++) {
                        // Gera números realistas como "101", "405", "1202"
                        int andar = (faker.random().nextInt(1, 15)) * 100;
                        int numeroPorta = faker.random().nextInt(1, 8);
                        String unitNumber = String.valueOf(andar + numeroPorta);

                        Apartment unit = new Apartment(unitNumber, condo);
                        allUnits.add(unit);
                    }
                }

                apartmentRepository.saveAll(allUnits);
                System.out.println(allUnits.size() + " Apartamentos criados");
                System.out.println("Seeder finalizado com sucesso! O banco está pronto para uso.");

            } else {
                System.out.println("O banco já possui dados. Processo de Seeder ignorado.");
            }
        };
    }
}