package com.reeflix;

import org.json.JSONArray;
/**
 * Created by ravi on 21/12/17.
 */

public class Movie {
    String title;
    String thumbnail1;
    String maturity;

    public Movie(String title, String thumbnail1, String maturity) {
        this.title = title;
        this.thumbnail1 = thumbnail1;
        this.maturity = maturity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMaturity() {
        return maturity;
    }

    public void setMaturity(String maturity) {
        this.maturity = maturity;
    }

    public String getThumbnail1() {
        return thumbnail1;
    }

    public void setThumbnail1(String thumbnail1) {
        this.thumbnail1 = thumbnail1;
    }
}
