package me.junsik.stock.domain.stock;

import lombok.RequiredArgsConstructor;
import me.junsik.stock.persistence.stock.StockEntity;
import me.junsik.stock.persistence.stock.repository.StockRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DecreaseStockUseCase {

	private final StockRepository repository;

	public synchronized void execute(final Long id, final Long quantity) {
		StockEntity stock = repository.findById(id).orElseThrow();
		stock.decrease(quantity);
		repository.saveAndFlush(stock);
	}
}
