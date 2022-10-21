package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(long userId, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and current_timestamp between b.start and b.ending " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsByBookerId(long userId, PageRequest pageRequest);

    List<Booking> findByBookerIdAndEndingBefore(long userId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findByBookerIdAndStartAfter(long userId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findByBookerIdAndStatusIs(long userId, Status waiting, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "where b.item.id in ?1 " +
            "order by b.start desc")
    List<Booking> findByOwnerId(List<Long> itemIdsByOwnerId, PageRequest pageRequest);

    @Query("select b from Booking b " +
            "where b.item.id in ?1 " +
            "and current_timestamp between b.start and b.ending " +
            "order by b.start desc")
    List<Booking> findCurrentBookingsByOwnerId(List<Long> itemIdsByOwnerId, PageRequest pageRequest);

    List<Booking> findByItemIdInAndEndingBefore(List<Long> itemIdsByOwnerId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findByItemIdInAndStartAfter(List<Long> itemIdsByOwnerId, LocalDateTime now, PageRequest pageRequest);

    List<Booking> findByItemIdInAndStatusIs(List<Long> itemIdsByOwnerId, Status waiting, PageRequest pageRequest);

    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatus(long itemId, LocalDateTime now, Sort sort, Status status);

    @Query("select b from Booking b " +
            "where b.item.id = ?1 " +
            "and b.start > ?2 " +
            "and b.status = 'APPROVED' " +
            "order by b.start desc ")
    Optional<Booking> findFirstNextBooking(long itemId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item.id = ?2 " +
            "and b.booker.id = ?1 " +
            "and  b.ending < current_timestamp ")
    Optional<Booking> findEndBookingOfItemByUser(long userId, long itemId);

    @Query("select count (b.id) from Booking b " +
            "where b.status = ?1 " +
            "and b.item.id = ?2 " +
            "and ((?3 between b.start and b.ending) or (?4 between b.start and b.ending) " +
            "or (?3 < b.start and ?4 > b.ending))")
    Integer findIntersectedBookings(Status approved, long itemId, LocalDateTime start, LocalDateTime ending);
}
