package io.iqube.pomogite.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Slider extends RealmObject {

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    /**
     * No args constructor for use in serialization
     */
    public Slider() {
    }

    /**
     * @param updatedAt
     * @param id
     * @param text
     * @param createdAt
     * @param image
     */
    public Slider(Long id, String createdAt, String text, String image, String updatedAt) {
        super();
        this.id = id;
        this.createdAt = createdAt;
        this.text = text;
        this.image = image;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Slider withId(Long id) {
        this.id = id;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Slider withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Slider withText(String text) {
        this.text = text;
        return this;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Slider withImage(String image) {
        this.image = image;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Slider withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

}