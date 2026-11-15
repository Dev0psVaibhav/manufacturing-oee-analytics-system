package com.manufacturing.oee.common.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

/**
 * Standardized pagination wrapper for query result sets.
 *
 * @param <T> Item element type
 */
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    @Schema(description = "List of items in the current page")
    private final List<T> content;

    @Schema(description = "Zero-based current page number", example = "0")
    private final int page;

    @Schema(description = "Number of items per page", example = "20")
    private final int size;

    @Schema(description = "Total number of elements across all pages", example = "100")
    private final long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private final int totalPages;

    @Schema(description = "Indicates whether this is the final page", example = "false")
    private final boolean last;

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        this.content = content != null ? content : Collections.emptyList();
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLast() {
        return last;
    }
}
