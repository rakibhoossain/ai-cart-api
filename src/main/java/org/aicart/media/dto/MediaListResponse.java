package org.aicart.media.dto;

import java.util.List;

public class MediaListResponse {
    private List<MediaFileDTO> data;
    private long total;
    private int page;
    private int size;
    private int totalPages;

    // Constructors
    public MediaListResponse() {}

    public MediaListResponse(List<MediaFileDTO> data, long total, int page, int size) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }

    // Getters and Setters
    public List<MediaFileDTO> getData() { return data; }
    public void setData(List<MediaFileDTO> data) { this.data = data; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
