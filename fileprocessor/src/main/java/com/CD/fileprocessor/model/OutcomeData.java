package com.CD.fileprocessor.model;

public class OutcomeData {

    private String name;
    private String transport;
    private Double topSpeed;

    public OutcomeData(){}

    public OutcomeData(String name, String transport, Double topSpeed) {
        this.name = name;
        this.transport = transport;
        this.topSpeed = topSpeed;
    }

    public Double getTopSpeed() {
        return topSpeed;
    }
    public void setTopSpeed(Double topSpeed) {
        this.topSpeed = topSpeed;
    }

    public String getTransport() {
        return transport;
    }
    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
