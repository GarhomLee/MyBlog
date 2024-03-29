package com.ljh.vo;

public class BlogQuery {

    private String title;
    private Long categoryId;
    private boolean isRecommended;

    public BlogQuery() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getIsRecommended() {
        return isRecommended;
    }

    public void setIsRecommended(Boolean recommended) {
        isRecommended = recommended;
    }
}
