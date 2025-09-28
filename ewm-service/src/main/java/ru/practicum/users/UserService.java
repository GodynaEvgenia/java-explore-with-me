package ru.practicum.users;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    // Добавить пользователя
    public User addUser(User user) {
        return userRepository.save(user);
    }

    // Удалить пользователя по id
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
