package com.underscoretec.reeflix.models;

import java.io.Serializable;
import java.util.ArrayList;
public class Video implements Serializable {
    String id;
    String title;
    String description;
    String cast;
    String maturity;
    String sources;
    String thumbnail1;
    String thumbnail2;
    String type;

    ArrayList<Cast> casts = new ArrayList<>();
    ArrayList<Director> directors = new ArrayList<>();
    ArrayList<Producer> producers = new ArrayList<>();
    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<Genre> genres = new ArrayList<>();

    public Video(String title, String description, String thumbnail1, String thumbnail2, String maturity, String sources, String id) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.thumbnail1 = thumbnail1;
        this.thumbnail2 = thumbnail2;
        this.sources = sources;
        this.maturity = maturity;
    }

    public Video(String id, String title, String description, String cast, String maturity,
                 String sources, String thumbnail1, String thumbnail2, String type,
                 ArrayList<Cast> casts, ArrayList<Director> directors, ArrayList<Producer> producers,
                 ArrayList<Category> categories, ArrayList<Genre> genres) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cast = cast;
        this.maturity = maturity;
        this.sources = sources;
        this.thumbnail1 = thumbnail1;
        this.thumbnail2 = thumbnail2;
        this.type = type;
        this.casts = casts;
        this.directors = directors;
        this.producers = producers;
        this.categories = categories;
        this.genres = genres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getMaturity() {
        return maturity;
    }

    public void setMaturity(String maturity) {
        this.maturity = maturity;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getThumbnail1() {
        return thumbnail1;
    }

    public void setThumbnail1(String thumbnail1) {
        this.thumbnail1 = thumbnail1;
    }

    public String getThumbnail2() {
        return thumbnail2;
    }

    public void setThumbnail2(String thumbnail2) {
        this.thumbnail2 = thumbnail2;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Cast> getCasts() {
        return casts;
    }

    public void setCasts(ArrayList<Cast> casts) {
        this.casts = casts;
    }

    public ArrayList<Director> getDirectors() {
        return directors;
    }

    public void setDirectors(ArrayList<Director> directors) {
        this.directors = directors;
    }

    public ArrayList<Producer> getProducers() {
        return producers;
    }

    public void setProducers(ArrayList<Producer> producers) {
        this.producers = producers;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public ArrayList<Genre> getGenres() {
        return genres;
    }

    public void setGenres(ArrayList<Genre> genres) {
        this.genres = genres;
    }
}
