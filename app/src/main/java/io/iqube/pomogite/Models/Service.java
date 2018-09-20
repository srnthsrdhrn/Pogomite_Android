package io.iqube.pomogite.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Service extends RealmObject{

    @PrimaryKey
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("fare")
    @Expose
    private Double fare;

    /**
     * No args constructor for use in serialization
     *
     */
    public Service() {
    }

    /**
     *
     * @param id
     * @param description
     * @param name
     * @param fare
     * @param type
     * @param user
     */
    public Service(Long id, User user ,String name, String description, String type, Double fare) {
        super();
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.type = type;
        this.fare = fare;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Service withId(Long id) {
        this.id = id;
        return this;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service withUser(User user) {
        this.user = user;
        return this;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Service withName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Service withDescription(String description) {
        this.description = description;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Service withType(String type) {
        this.type = type;
        return this;
    }

    public Double getFare() {
        return fare;
    }

    public void setFare(Double fare) {
        this.fare = fare;
    }

    public Service withFare(Double fare) {
        this.fare = fare;
        return this;
    }
}