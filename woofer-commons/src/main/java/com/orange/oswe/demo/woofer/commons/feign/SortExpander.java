/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.commons.feign;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import feign.Param.Expander;

public class SortExpander implements Expander {
	@Override
	public String expand(Object value) {
		if(value instanceof Sort) {
			Sort sort = (Sort) value;
			return StreamSupport.stream(sort.spliterator(), false).map(order -> order.getProperty() + "," + order.getDirection())
					.collect(Collectors.joining(":"));
		} else if(value instanceof Order) {
			Order order = (Order) value;
			return order.getProperty()+","+order.getDirection();
		} else {
			return null;
		}
	}
}
