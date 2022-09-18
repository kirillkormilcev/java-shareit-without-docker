package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    @Query(value = "select U.email from User U")
    List<String> getAllEmails();
}
