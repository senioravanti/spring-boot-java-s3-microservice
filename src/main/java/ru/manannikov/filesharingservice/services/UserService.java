package ru.manannikov.filesharingservice.services;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.manannikov.filesharingservice.enums.Role;
import ru.manannikov.filesharingservice.exceptions.UserNotFoundException;
import ru.manannikov.filesharingservice.models.UserEntity;
import ru.manannikov.filesharingservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByUsername(email).orElseThrow(() ->
            new UsernameNotFoundException(String.format("Пользователь с именем %s не найден", email)));
    }

    public List<UserEntity> findAll() {
        return repository.findAll();
    }

    public UserEntity findById(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new UserNotFoundException(
                String.format("Пользователь с идентификатором %d не найден", id)
            )
        );
    }

    public UserEntity save(UserEntity user) {
        // Пароль надо зашифровать перед добавлением записи в таблицу
        user.setPassword(
            passwordEncoder.encode(user.getPassword())
        );
        return repository.save(user);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @PostConstruct
    public void init() {
        if (repository.count() == 0) {
            UserEntity senioravanti = new UserEntity(
                null, "senioravanti@vk.com", passwordEncoder.encode("12345"),
                "Антон", "Мананников",
                Role.valueOf("ADMIN")
            );
            repository.save(senioravanti);
        }
    }
}
