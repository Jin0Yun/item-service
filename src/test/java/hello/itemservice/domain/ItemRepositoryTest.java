package hello.itemservice.domain;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ItemRepositoryTest {
    ItemRepository itemRepository = new ItemRepository();

    @AfterEach
    void afterEach() {
        itemRepository.clearStore();
    }

    @Test
    @DisplayName("아이템을 저장할 때 고유한 ID가 생성되어야 한다.")
    void saveUniqueIdTest() {
        // given
        Item item1 = new Item("itemA", 10000, 10);
        Item item2 = new Item("itemB", 20000, 5);

        // when
        Item savedItem1 = itemRepository.save(item1);
        Item savedItem2 = itemRepository.save(item2);

        // then
        assertThat(savedItem1.getId()).isNotEqualTo(savedItem2.getId());
    }

    @Test
    @DisplayName("존재하지 않는 아이템 ID로 조회할 때 null을 반환한다.")
    void findByIdNonExistentTest() {
        // given
        Long nonExistentId = 999L;

        // when
        Item result = itemRepository.findById(nonExistentId);

        // then
        assertThat(result).isNull();
    }


    @Test
    @DisplayName("아이템을 저장한 후, 저장된 아이템을 조회하면 동일한 아이템이 반환되어야 한다.")
    void save() {
        // given
        Item item = new Item("itemA", 10000, 10);

        // when
        Item savedItem = itemRepository.save(item);

        // then
        Item findItem = itemRepository.findById(savedItem.getId());
        assertThat(findItem).isEqualTo(savedItem);
    }

    @Test
    @DisplayName("멀티스레드 환경에서 아이템을 저장하면 모든 스레드에서 저장한 아이템 개수가 일치해야 한다.")
    void saveConcurrentTest() throws InterruptedException {
        // given
        int threadCount = 100;
        List<Thread> threads = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                Item item = new Item("item" + Thread.currentThread().getId(), 10000, 10);
                itemRepository.save(item);
            }));
        }

        // 모든 스레드 시작
        threads.forEach(Thread::start);
        // 모든 스레드 종료 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // then
        assertThat(itemRepository.findAll()).hasSize(threadCount);
    }

    @Test
    @DisplayName("아이템을 여러 개 저장하고 findAll을 호출하면 모든 아이템을 반환해야 한다.")
    void findAll() {
        // given
        Item item1 = new Item("itemA", 10000, 10);
        Item item2 = new Item("itemB", 20000, 5);
        itemRepository.save(item1);
        itemRepository.save(item2);

        // when
        List<Item> items = itemRepository.findAll();

        // then
        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
    }

    @Test
    @DisplayName("아이템을 업데이트 하면, 해당 아이템의 정보가 업데이트되어야 한다.")
    void updateItemTest() {
        // given
        Item originalItem = new Item("itemA", 10000, 10);
        Item savedItem = itemRepository.save(originalItem);
        Item updatedItem = new Item("updatedItem", 20000, 5);

        // when
        itemRepository.update(savedItem.getId(), updatedItem);

        // then
        Item findItem = itemRepository.findById(savedItem.getId());
        assertThat(findItem.getItemName()).isEqualTo("updatedItem");
        assertThat(findItem.getPrice()).isEqualTo(20000);
        assertThat(findItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("존재하지 않는 아이템 ID로 업데이트 시, 아무 변화도 일어나지 않아야 한다.")
    void updateNonExistentItemTest() {
        // given
        Long nonExistentId = 999L;
        Item updatedItem = new Item("updatedItem", 20000, 5);

        // when
        itemRepository.update(nonExistentId, updatedItem);

        // then
        Item result = itemRepository.findById(nonExistentId);
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("아이템을 저장한 후 clearStore를 호출하면 모든 아이템이 삭제되어야 한다.")
    void clearStoreTest() {
        // given
        Item item1 = new Item("itemA", 10000, 10);
        Item item2 = new Item("itemB", 20000, 5);
        itemRepository.save(item1);
        itemRepository.save(item2);

        // when
        itemRepository.clearStore();

        // then
        assertThat(itemRepository.findAll()).isEmpty();
    }
}