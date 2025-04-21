package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	Optional<Booking> findByIdAndItemOwnerId(Long id, Long ownerId);

	List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

	List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime endDate);

	List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime startDate);

	List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);

	List<Booking> findAllByBookerIdAndItemIdAndEndBefore(Long userId, Long itemId, LocalDateTime endDate);

	Optional<Booking> findByItemIdAndStartBeforeAndEndAfter(Long id, LocalDateTime startDate, LocalDateTime endDate);

	List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long userId);

	List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime endDate);

	List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long userId, LocalDateTime startDate, LocalDateTime endDate);

	List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime startDate);

	List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long userId, BookingStatus bookingStatus);
}
