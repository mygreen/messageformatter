package com.github.mygreen.messageformatter.expression;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.Getter;


/**
 * EL式のパース結果のオブジェクトをキャッシュです。
 * <p>このキャッシュはGCにより削除対象とすることができます。</p>
 *
 *
 * @author T.TSUCHIE
 *
 * @param <K> キャッシュのキー
 * @param <V> キャッシュの値
 */
public class ObjectCache<K, V> {

    /**
     * キャッシュの実態
     */
    private final Map<K, SoftReference<V>> map = new HashMap<>();

    /**
     * 最後にアクセスしたキャッシュの値。
     * {@link #objectsToKeepCount} の値により保持するサイズが変わります。
     */
    private final LinkedList<V> objectsLastAccessed = new LinkedList<>();

    /**
     * GCによってメモリを開放する時に{@link SoftReference} が削除されるときに、
     * メモリ内に保持しておくキャッシュオブジェクトの最大個数を指定します。
     * 値が{@literal -1}のときはGC実行時でも全てのキャッシュを保持します。
     */
    @Getter
    private final int objectsToKeepCount;

    /**
     * インスタンスを作成します。
     * 設定としてキャッシュは全て保持するようにします。
     *
     */
    public ObjectCache() {
        this.objectsToKeepCount = -1;
    }

    /**
     * キャッシュサイズを指定してインスタンスを作成します。
     *
     * @param maxObjectsToKeep GCによってメモリを開放する時に{@link SoftReference} が削除されるときに、
     *         メモリ内に保持しておくキャッシュオブジェクトの最大個数を指定します。
     *         値が{@literal -1}のときはGC実行時でも全てのキャッシュを保持します。
     */
    public ObjectCache(final int maxObjectsToKeep) {
        this.objectsToKeepCount = maxObjectsToKeep;
    }

    /**
     * キャッシュの値がnullの情報を削除し、キャッシュサイズを削減します。
     */
    public void compact() {
        for (final Map.Entry<K, SoftReference<V>> entry : map.entrySet()) {
            final SoftReference<V> ref = entry.getValue();
            if (ref.get() == null) map.remove(entry.getKey());
        }
    }

    /**
     * キーがキャッシュに含まれているかどうか判定します。
     * @param key キャッシュのキー。
     * @return {@literal true}のときキャッシュに含まれています。
     */
    public boolean contains(final K key) {
        return map.containsKey(key);
    }

    /**
     * キーを元にキャッシュから値を取り出します。
     * @param key キャッシュのキー。
     * @return キャッシュの値。キャッシュに存在しなければ、{@literal null}を返します。
     */
    public V get(final K key) {
        final SoftReference<V> softReference = map.get(key);
        if (softReference != null) {
            final V value = softReference.get();
            if (value == null) {
                map.remove(key);
            } else if (objectsToKeepCount > 0 && value != objectsLastAccessed.getFirst()) {
                objectsLastAccessed.remove(value);
                objectsLastAccessed.addFirst(value);
                if (objectsLastAccessed.size() > objectsToKeepCount) objectsLastAccessed.removeLast();
            }
            return softReference.get();
        }
        return null;
    }

    /**
     * キャッシュに追加します。
     * @param key キー
     * @param value 値
     */
    public void put(final K key, final V value) {
        map.remove(key);
        map.put(key, new SoftReference<>(value));
    }
}
