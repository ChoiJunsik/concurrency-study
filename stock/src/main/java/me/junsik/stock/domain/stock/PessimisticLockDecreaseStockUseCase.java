package me.junsik.stock.domain.stock;

import lombok.RequiredArgsConstructor;
import me.junsik.stock.persistence.stock.StockEntity;
import me.junsik.stock.persistence.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PessimisticLockDecreaseStockUseCase {

	private final StockRepository repository;

	public void execute(final Long id, final Long quantity) {
		StockEntity stock = repository.findByIdWithPessimisticLock(id);
		stock.decrease(quantity);
		repository.saveAndFlush(stock);
	}
}
