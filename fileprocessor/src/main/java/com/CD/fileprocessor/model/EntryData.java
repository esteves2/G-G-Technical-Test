package com.CD.fileprocessor.model;

public class EntryData {

    private String uuid;
    private String id;
    private String name;
    private String likes;
    private String transport;
    private Double avgSpeed;
    private Double topSpeed;

    public EntryData() {}

    public EntryData(String uuid, String id, String name, String likes, String transport, Double avgSpeed, Double topSpeed) {
        this.uuid = uuid;
        this.id = id;
        this.name = name;
        this.likes = likes;
        this.transport = transport;
        this.avgSpeed = avgSpeed;
        this.topSpeed = topSpeed;
    }

    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLikes() {
        return likes;
    }
    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTransport() {
        return transport;
    }
    public void setTransport(String transport) {
        this.transport = transport;
    }

    public Double getAvgSpeed() {
        return avgSpeed;
    }
    public void setAvgSpeed(Double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public Double getTopSpeed() {
        return topSpeed;
    }
    public void setTopSpeed(Double topSpeed) {
        this.topSpeed = topSpeed;
    }
}
