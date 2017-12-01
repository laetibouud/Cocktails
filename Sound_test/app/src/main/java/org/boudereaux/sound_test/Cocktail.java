package org.boudereaux.sound_test;

import java.util.List;

/**
 * Created by Laëtitia on 29/11/2017.
 */

public class Cocktail {
    private String id;
    private String name;
    private String category;
    private String alcohol;
    private String instructions;
    private String picture;
    private List<String> ingredients;
    private List<String> measures;

    public Cocktail(String id, String name, String category, String alcohol, String instructions, String picture, List<String> ingredients, List<String> measures) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.alcohol = alcohol;
        this.instructions = instructions;
        this.picture = picture;
        this.ingredients = ingredients;
        this.measures = measures;
    }

    public Cocktail() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getPicture() {
        return picture;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public List<String> getMeasures() {
        return measures;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setMeasures(List<String> measures) {
        this.measures = measures;
    }
}
