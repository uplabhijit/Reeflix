package com.underscoretec.reeflix.models;

import org.json.JSONArray;

import java.io.Serializable;
public class Category implements Serializable {
    String name;
    JSONArray contentArray;
    String categoryId;

    public JSONArray getContentArray() {
        return contentArray;
    }

    public void setContentArray(JSONArray contentArray) {
        this.contentArray = contentArray;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Category(String name, JSONArray contentArray, String categoryId) {
        this.name = name;
        this.contentArray = contentArray;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
