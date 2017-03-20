package de.heidelberg.pvs.diego.collections_online_adapter.instrumenters.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.heidelberg.pvs.diego.collections_online_adapter.context.MapAllocationContext;

public class MapSizeMonitor<K, V> implements Map<K, V> {
	
	private MapAllocationContext<K, V> context;
	
	private Map<K, V> map;
	
	public MapSizeMonitor(Map<K, V> map, MapAllocationContext<K, V> context) {
		super();
		this.context = context;
		this.map = map;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		context.updateSize(size());
	}
	
	
	/* --------------------------------------------------------- */
	/* -------------------- DELEGATE METHODS --------------------*/
	/* --------------------------------------------------------- */

	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public V get(Object key) {
		return map.get(key);
	}

	public V put(K key, V value) {
		return map.put(key, value);
	}

	public V remove(Object key) {
		return map.remove(key);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	public void clear() {
		map.clear();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Collection<V> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public boolean equals(Object o) {
		return map.equals(o);
	}

	public int hashCode() {
		return map.hashCode();
	}

}
