package com.example.smartairportsystem.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class parkingspace {
    @Id
    private Integer parkingspaceid;

    private String location;
    private Double price;

    public parkingspace(Integer parkingspaceid,String location,Double price){
        this.parkingspaceid = parkingspaceid;
        this.location = location;
        this.price = price;
    }

    public Integer getParkingspaceid() {
        return parkingspaceid;
    }

    public void setParkingspaceid(Integer parkingspaceid) {
        this.parkingspaceid = parkingspaceid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
