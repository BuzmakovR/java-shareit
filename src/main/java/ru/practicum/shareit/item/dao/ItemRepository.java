package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

	List<Item> findAllByOwnerId(Long ownerId);

	Optional<Item> findByIdAndOwnerId(Long id, Long ownerId);

	@Query("select item " +
			"from Item item " +
			"where item.isAvailable = true " +
			"and ( " +
			"lower(item.name) like lower(concat('%', ?1, '%')) " +
			"or lower(item.description) like lower(concat('%', ?1, '%')) " +
			")")
	List<Item> findAllUsingSearch(String name);
}
