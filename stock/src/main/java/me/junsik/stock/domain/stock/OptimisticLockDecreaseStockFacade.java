package me.junsik.stock.domain.stock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.junsik.stock.persistence.stock.StockEntity;
import me.junsik.stock.persistence.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OptimisticLockDecreaseStockFacade {

	private final OptimisticLockDecreaseStockUseCase useCase;


	public void execute(final Long id, final Long quantity) throws InterruptedException {
		while (true) {
			try {
				useCase.execute(id, quantity);
				break;
			} catch (Exception e) {
				log.error("error", e);
				Thread.sleep(50);
			}
		}
	}
}
