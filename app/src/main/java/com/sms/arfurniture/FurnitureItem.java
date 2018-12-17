package com.sms.arfurniture;

public class FurnitureItem {
    private long id;
    private FurnitureType type;
    private String title;
    private String description;
    private String icon;
    private String model;
    private float scale;

    public FurnitureItem(long id, FurnitureType type, String title, String description, String icon, String model) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.icon = "file:///android_asset/" + icon;
        this.model = model;
    }

    public String getModel() {

        return model;
    }

    public long getId() {
        return id;
    }

    public FurnitureType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public float getScale() {
        return scale;
    }

    public FurnitureItem(long id, FurnitureType type, String title, String description, String icon, String model, float scale) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.icon = "file:///android_asset/" + icon;

        this.model = model;
        this.scale = scale;
    }

    public void setModel(String model) {
        this.model = model;
    }

    enum FurnitureType {
        TABLE, SOFA, CHAIR, SHELF
    }


}
