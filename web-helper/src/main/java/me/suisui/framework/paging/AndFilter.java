package me.suisui.framework.paging;

import java.util.List;

import javax.persistence.metamodel.SingularAttribute;

import com.google.common.collect.Lists;

public class AndFilter extends Filter {

	List<Filter> filters = Lists.newArrayList();

	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}

	public void addFilter(String operator, String key, Object value) {
		Filter f = new Filter(operator, key, value);
		f.setOperator(operator);
		filters.add(f);
	}

	public void addFilter(String operator, SingularAttribute<?, ?> attr, Object value) {
		Filter f = new Filter(operator, attr.getName(), value);
		f.setOperator(operator);
		filters.add(f);
	}
}
