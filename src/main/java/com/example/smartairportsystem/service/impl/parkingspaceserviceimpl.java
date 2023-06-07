package com.example.smartairportsystem.service.impl;

import com.example.smartairportsystem.entity.parkingspace;
import com.example.smartairportsystem.mapper.parkingspacemapper;
import com.example.smartairportsystem.service.parkingspaceservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("parkingspaceservice")
public class parkingspaceserviceimpl implements parkingspaceservice {
    @Autowired
    private parkingspacemapper parkingspaceMapper;

    public List<parkingspace> listEmptyParkingspace(String starttime,String endtime){return null;}
    public List<parkingspace> listAllParkingspace(){return parkingspaceMapper.listAllParkingspace();}
    public void addNewParkingspace(parkingspace newps){parkingspaceMapper.addNewParkingspace(newps);}
    public void updateOldParkingspace(parkingspace newps){parkingspaceMapper.updateOldParkingspace(newps);}
    public void removeOldParkingspace(Integer parkingspaceid){parkingspaceMapper.removeOldParkingspace(parkingspaceid);}
    public parkingspace getParkingspaceByID(Integer parkingspaceid){return parkingspaceMapper.getParkingspaceByID(parkingspaceid);}
    public parkingspace getParkingspaceByLocation(String location,Integer exceptid){return parkingspaceMapper.getParkingspaceByLocation(location,exceptid);}
}
