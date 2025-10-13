package ru.practicum.users;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.ResourceConflictException;
import ru.practicum.exceptions.UserAlreadyExistsException;
import ru.practicum.validations.EmailValidator;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(int from, int size) {
        int page = from / size; // вычисляем номер страницы
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable).getContent();
    }

    public List<User> getUsersByIds(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size); // Вычисляем страницу из from и size
        return userRepository.findByIdIn(ids, pageable);
    }

    @Transactional
    public User addUser(User user) {
        if (!EmailValidator.isEmailPartLengthValid(user.getEmail())) {
            throw new ResourceConflictException("Email is incorrect");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email существует");
        }
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
