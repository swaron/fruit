package me.suisui.integration.guava;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public abstract class TableHelper {

	@SuppressWarnings("unchecked")
	public static <R, C, V> void putInList(Table<R, C, List<V>> graph, R row, C column, V value) {
		List<V> list = graph.get(row, column);
		if (list != null) {
			list.add(value);
		} else {
			graph.put(row, column, Lists.newArrayList(value));
		}
	}

}
