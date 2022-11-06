package me.junsik.stock.domain.stock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.junsik.stock.persistence.stock.StockEntity;
import me.junsik.stock.persistence.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DecreaseStockUseCaseTest {

	@Autowired
	private DecreaseStockUseCase useCase;

	@Autowired
	private PessimisticLockDecreaseStockUseCase pessimisticLockDecreaseStockUseCase;

	@Autowired
	private OptimisticLockDecreaseStockFacade optimisticLockDecreaseStockFacade;

	@Autowired
	private StockRepository repository;

	private StockEntity stock;

	@BeforeEach
	public void before() {
		stock = StockEntity.builder()
				.productId(1L)
				.quantity(100L)
				.build();

		repository.save(stock);
	}

	@AfterEach
	public void after() {
		repository.deleteAll();
	}

	@Test
	void test_stock_decrease() {
		useCase.execute(stock.getId(), 1L);

		StockEntity stock = repository.findById(this.stock.getId()).orElseThrow();

		assertEquals(99, stock.getQuantity());
	}

	@Test
	void test_parallel_stock_decrease() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; ++i) {
			executorService.submit(() -> {
				try {
					useCase.execute(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		StockEntity stock = repository.findById(this.stock.getId()).orElseThrow();

		assertEquals(0L, stock.getQuantity());
	}


	@Test
	void test_parallel_pessimistic_lock_stock_decrease() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; ++i) {
			executorService.submit(() -> {
				try {
					pessimisticLockDecreaseStockUseCase.execute(1L, 1L);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		StockEntity stock = repository.findById(this.stock.getId()).orElseThrow();

		assertEquals(0L, stock.getQuantity());
	}

	@Test
	void test_parallel_optimistic_lock_stock_decrease() throws InterruptedException {
		int threadCount = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; ++i) {
			executorService.submit(() -> {
				try {
					optimisticLockDecreaseStockFacade.execute(1L, 1L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} finally {
					latch.countDown();
				}
			});
		}

		latch.await();

		StockEntity stock = repository.findById(this.stock.getId()).orElseThrow();

		assertEquals(0L, stock.getQuantity());
	}
}
