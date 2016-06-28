package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface WalletService {
    
    
    Entity getUserWallet(DbSession dbSession, String userUuid);

    Entity getShopWallet(DbSession dbSession, String shopUuid);
    
    void addToShopWallet(DbSession dbSession, String shopUuid, double amount);

    void addToUserWallet(DbSession dbSession, String userUuid, double payed);


    

}
