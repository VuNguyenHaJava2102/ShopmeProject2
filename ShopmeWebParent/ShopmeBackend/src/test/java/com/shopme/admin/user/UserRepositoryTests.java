package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserWithOneRole() {
        Role roleAdmin = entityManager.find(Role.class, 1);
        User userNamHM = new User("nam@codejava.net", "nam2020", "Nam", "Ha Minh");
        userNamHM.addRole(roleAdmin);

        userRepository.save(userNamHM);
    }

    @Test
    public void testCreateUserWithTwoRole() {
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);
        User userBenzema = new User("benzema@gmail.com", "benzema9", "Karim", "Benzema");

        userBenzema.addRole(roleEditor);
        userBenzema.addRole(roleAssistant);

        userRepository.save(userBenzema);
    }

    @Test
    public void testGetAllUsers() {
        Iterable<User> users = userRepository.findAll();
        users.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        Optional<User> user1 = userRepository.findById(1);
        if(user1.isPresent()) {
            System.err.println(user1.get());
        }
    }

    @Test
    public void testUpdateUser() {
        Optional<User> userOptional1 = userRepository.findById(1);
        if(userOptional1.isPresent()) {
            User user = userOptional1.get();
            user.setEnabled(true);
            user.setEmail("namjava@gmail.com");
        }
    }

    @Test
    public void testUpdateUserRole() {
        User user = userRepository.findById(2).get();

        Role salesperson = new Role(4);

        user.getRoles().remove(salesperson);

        userRepository.save(user);
    }

    @Test
    public void testGetUserByEmail() {
//        User user = userRepository.getUserByFirstName("Karim");
//        System.out.println(user.toString());
    }

    @Test
    public void testCheckUniqueEmail() {
        Optional<User> optionalUser = userRepository.findByEmail("benzema@gmail.com");

        if(optionalUser.isEmpty()) {
            System.err.println("TRUE");
        }

        if(optionalUser.isPresent() && optionalUser.get().getId() == 2) {
            System.err.println("TRUE");
        }
        System.err.println("FALSE");
    }

    @Test
    public void test() {
        DateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
        String timestamp = dateFormatter.format(new Date());
        System.err.println(timestamp);
        System.out.println(new Date());
    }

}

