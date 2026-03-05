import com.anuj.cache.core.LRUCache;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    @Test
    void testPutAndGet() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);

        cache.put(1, "A");
        cache.put(2, "B");

        assertEquals("A", cache.get(1));
        assertEquals("B", cache.get(2));
    }

    @Test
    void testEviction() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);

        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");

        assertNull(cache.get(1));
        assertEquals("B", cache.get(2));
        assertEquals("C", cache.get(3));
    }

    @Test
    void testUpdateExistingKey() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);

        cache.put(1, "A");
        cache.put(1, "Updated");

        assertEquals("Updated", cache.get(1));
        assertEquals(1, cache.size());
    }

    @Test
    void testDelete() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);

        cache.put(1, "A");
        cache.delete(1);

        assertNull(cache.get(1));
    }
    @Test
    void testTTLExpiration() throws InterruptedException {
        LRUCache<Integer, String> cache = new LRUCache<>(2);

        cache.put(1, "A", 1000);
        assertEquals("A", cache.get(1));
        Thread.sleep(2000);
        assertNull(cache.get(1));
    }
}