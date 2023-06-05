package com.example.smartairportsystem.service;

import com.example.smartairportsystem.entity.merchant;

import java.util.List;

public interface merchantservice {
    public void logupNewMerchant(merchant newmerchant);
    public merchant getMerchantByEmail(String email);
    public merchant getMerchantByID(Integer merchantid);
    public merchant getMerchantByIDnumber(Integer idnumber);
    public merchant getMerchantByShopname(String shopname);
    public void updateOldMerchant(person newmerchant);
    public void removeOldMerchant(Integer merchantid);
}