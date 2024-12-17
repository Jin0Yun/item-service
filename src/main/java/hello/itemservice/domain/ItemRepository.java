package hello.itemservice.domain;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepository {
    private static final Map<Long, Item> store = new ConcurrentHashMap<>();
    private static final AtomicLong sequence = new AtomicLong(0);

    public Item save(Item item) {
        item.setId(sequence.incrementAndGet());
        store.put(item.getId(), item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long id, Item updatedItem) {
        Item findItem = store.get(id);
        if (findItem != null) {
            findItem.setItemName(updatedItem.getItemName());
            findItem.setPrice(updatedItem.getPrice());
            findItem.setQuantity(updatedItem.getQuantity());
        }
    }

    public void clearStore() {
        store.clear();
    }
}
