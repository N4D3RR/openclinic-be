package naderdeghaili.capstoneproject.services;

import lombok.extern.slf4j.Slf4j;
import naderdeghaili.capstoneproject.entities.User;
import naderdeghaili.capstoneproject.entities.UserType;
import naderdeghaili.capstoneproject.exceptions.NotFoundException;
import naderdeghaili.capstoneproject.payloads.UserCreateDTO;
import naderdeghaili.capstoneproject.payloads.UserUpdateDTO;
import naderdeghaili.capstoneproject.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder bcrypt;


    public UserService(UserRepository userRepository, PasswordEncoder bcrypt) {
        this.userRepository = userRepository;
        this.bcrypt = bcrypt;
    }

    //GET ALL
    public Page<User> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable);
    }

    //GET BY ID
    public User findByID(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

    }

    //GET BY EMAIL
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User with email " + email + " not found"));

    }

    //GET BY ROLE
    public Page<User> findByRole(UserType role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findByRole(role, pageable);

    }

    //SAVE
    public User saveUser(UserCreateDTO payload) {
        if (userRepository.existsByEmail(payload.email()))
            throw new IllegalArgumentException("Email " + payload.email() + " already in use");

        User newUser = new User(
                payload.firstName(), payload.lastName(), payload.email(), bcrypt.encode(payload.password()), payload.role()
        );

        log.info("User " + payload.email() + " saved successfully");
        return userRepository.save(newUser);

    }

    //UPDATE
    public User findByIdAndUpdate(UUID userId, UserUpdateDTO payload) {
        User found = this.findByID(userId);

        if (payload.email() != null && !found.getEmail().equals(payload.email())
                && userRepository.existsByEmail(payload.email()))
            throw new IllegalArgumentException("Email " + payload.email() + " already in use");

        if (payload.firstName() != null) found.setFirstName(payload.firstName());
        if (payload.lastName() != null) found.setLastName(payload.lastName());
        if (payload.email() != null) found.setEmail(payload.email());
        if (payload.password() != null) found.setPassword(bcrypt.encode(payload.password()));
        if (payload.role() != null) found.setRole(payload.role());

        log.info("User with id " + userId + " updated successfully");
        return userRepository.save(found);

    }

    // DELETE
    public void findByIdAndDelete(UUID userId) {
        User found = this.findByID(userId);
        userRepository.delete(found);
        log.info("User with id " + userId + " deleted successfully");
    }


}
