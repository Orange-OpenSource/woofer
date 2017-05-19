/*
 * Copyright (C) 2017 Orange
 *
 * This software is distributed under the terms and conditions of the 'Apache-2.0'
 * license which can be found in the file 'LICENSE.txt' in this package distribution
 * or at 'http://www.apache.org/licenses/LICENSE-2.0'.
 */
package com.orange.oswe.demo.woofer.webfront.mvc;

import org.springframework.hateoas.PagedResources;
import org.thymeleaf.util.NumberUtils;

/**
 * Helper class to compute table pagination in UI
 */
public class Pagination {
	private Pagination() {
	}
	public static boolean isFirst(PagedResources<?> resources) {
		return resources.getMetadata().getNumber() <= 0;
	}
	public static boolean isLast(PagedResources<?> resources) {
		return resources.getMetadata().getNumber() >= resources.getMetadata().getTotalPages() - 1;
	}
	public static boolean hasPrev(PagedResources<?> resources) {
		return !isFirst(resources);
	}
	public static boolean hasNext(PagedResources<?> resources) {
		return !isLast(resources);
	}
	public static boolean hasEllipsisBefore(PagedResources<?> resources) {
		return resources.getMetadata().getNumber() >= 4;
	}
	public static boolean hasEllipsisAfter(PagedResources<?> resources) {
		return resources.getMetadata().getNumber() <= resources.getMetadata().getTotalPages() - 5;
	}
	public static Integer[] getNearPages(PagedResources<?> resources) {
		long firstPage = Math.max(1, resources.getMetadata().getNumber() - 2);
		long lastPage = Math.min(resources.getMetadata().getTotalPages() - 2, resources.getMetadata().getNumber() + 2);
		if(firstPage > lastPage) {
			return new Integer[0];
		} else {
			return NumberUtils.sequence((int)firstPage, (int)lastPage);
		}
	}
}
