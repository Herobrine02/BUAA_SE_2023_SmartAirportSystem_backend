package com.example.smartairportsystem.service;

import com.example.smartairportsystem.entity.airlinecompany;

public interface companyservice {
    public void logupNewCompany(airlinecompany newcompany);
    public airlinecompany getCompanyByEmail(String email);
    public airlinecompany getCompanyByID(Integer companyid);
}