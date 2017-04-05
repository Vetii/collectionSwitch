package de.heidelberg.pvs.diego.collections_online_adapter.monitors.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import de.heidelberg.pvs.diego.collections_online_adapter.optimizers.maps.MapAllocationOptimizer;

public class MapSizeMonitor<K, V> implements Map<K, V> {
	
	private MapAllocationOptimizer context;
	
	private Map<K, V> map;

	private int index;
	
	public MapSizeMonitor(Map<K, V> map, MapAllocationOptimizer context, int index) {
		super();
		this.context = context;
		this.map = map;
		this.index = index;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		context.updateSize(index, size());
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