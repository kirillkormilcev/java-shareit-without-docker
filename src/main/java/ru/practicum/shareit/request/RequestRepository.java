package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequestorId(long userId, Sort sort);

    @Query("select r from Request r " +
            "where r.requestor.id <> ?1")
    List<Request> findRequestOfOtherUsers(long userId, PageRequest pageRequest);
}
