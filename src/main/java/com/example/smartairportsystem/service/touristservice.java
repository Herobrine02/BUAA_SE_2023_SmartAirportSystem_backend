package com.example.smartairportsystem.service;

import com.example.smartairportsystem.entity.tourist;

public interface touristservice {
    public void logupNewTourist(tourist newtourist);
    public tourist getTouristByNickname(String nickname);
    public tourist getTouristByID(Integer touristid);
}
