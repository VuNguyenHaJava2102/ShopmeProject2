package com.shopme.admin.user;

import com.shopme.admin.file.FileUploadUtils;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    public static final int NUMBER_OF_USERS_PER_PAGE = 5;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // PUBLIC METHOD

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<User> getAllUsers() {
        Sort sort = Sort.by("firstName");
        return userRepository.findAll(sort);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    public Page<User> getUsersByPage(int pageNum,
                                     String sortField,
                                     String sortDir,
                                     String keyword) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, NUMBER_OF_USERS_PER_PAGE, sort);

        if(keyword != "") {
            Page<User> userPage = userRepository.searchUserByKeyword(pageable, keyword);
            return userPage;
        }

        Page<User> userPage = userRepository.findAll(pageable);

        return userPage;
    }

    public User saveUser(User user) {
        if(user.getId() == null) {
            encodePassword(user);
        } else {
            User userInDB = userRepository.findById(user.getId()).get();
            if(!user.getPassword().equals(userInDB.getPassword())) {
                encodePassword(user);
            }
        }
        return userRepository.save(user);
    }

    // // Use for save or edit user
    public boolean checkUniqueEmail(User user, String email) {
        /*
        - We pass ID and E-mail to this method for 2 case: Create and Edit
        */
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isEmpty()) return true;

        if(user.getId() != null) {
            if(optionalUser.isPresent() && optionalUser.get().getId() == user.getId()) return true;
        }

        return false;
    }

    public User updateAccountDetails(User user) {
        User userInDb = userRepository.findById(user.getId()).get();
        if(!user.getPassword().equals(userInDb.getPassword())) {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    // Use for update account details
    public boolean checkPasswordMatch(User user) {
        User userInDb = userRepository.findById(user.getId()).get();

        // User doesn't change the password
        if(user.getPassword().equals(userInDb.getPassword())) {
            return true;
        }

        // User change the password
        if(user.getPassword().equals(user.getConfirmPassword())) {
            return true;
        }
        return false;
    }

    public User getUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).get();
        if(user == null) {
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }
        return user;
    }

    public void deleteUserById(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).get();
        if(user == null) {
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }
        userRepository.delete(user);
        FileUploadUtils.deleteUploadDir("user-photos/" + id);
    }

    public boolean updateUserStatus(int id) throws UserNotFoundException {
        User user = userRepository.findById(id).get();
        if(user == null) {
            throw new UserNotFoundException("Could not find any user with ID: " + id);
        }
        user.setEnabled(!user.isEnabled());
        return userRepository.save(user).isEnabled();
    }

    // PRIVATE METHOD

    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }
}
