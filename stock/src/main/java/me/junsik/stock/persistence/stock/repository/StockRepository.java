package me.junsik.stock.persistence.stock.repository;

import javax.persistence.LockModeType;
import me.junsik.stock.persistence.stock.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface StockRepository extends JpaRepository<StockEntity, Long> {

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("select s from StockEntity s where s.id = :id")
	StockEntity findByIdWithPessimisticLock(final Long id);

	@Lock(value = LockModeType.OPTIMISTIC)
	@Query("select s from StockEntity s where s.id = :id")
	StockEntity findByIdWithOptimisticLock(final Long id);
}
