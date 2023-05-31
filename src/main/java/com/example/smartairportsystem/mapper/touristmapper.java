package com.example.smartairportsystem.mapper;

import com.example.smartairportsystem.entity.tourist;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface touristmapper {
    public void logupNewTourist(tourist newtourist);
    public tourist getTouristByEmail(String email);
    public tourist getTouristByID(Integer touristid);
}
