package wordy.ast;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

abstract class Utils {
    /**
     * Utilities to create maps that preserve iteration order. We want AST node children to iterate
     * in the order the node specifies them, and while Java's Map.of() may preserve iteration order
     * in practice, it doesn’t guarantee it. (See https://stackoverflow.com/a/53590354/239816)
     */
    static <K,V> Map<K,V> orderedMap(K key0, V value0, K key1, V value1) {
        return orderedMap(List.of(
            entry(key0, value0),
            entry(key1, value1)));
    }

    static <K,V> Map<K,V> orderedMap(K key0, V value0, K key1, V value1, K key2, V value2, K key3, V value3) {
        return orderedMap(List.of(
            entry(key0, value0),
            entry(key1, value1),
            entry(key2, value2),
            entry(key3, value3)));
    }

    static <K,V> Map<K,V> orderedMap(List<Map.Entry<K,V>> entries) {
        Map<K,V> result = new LinkedHashMap<>(entries.size() * 4 / 3 + 1);
        for(var entry: entries) {
            result.put(entry.getKey(), entry.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

    private Utils() {}
}
