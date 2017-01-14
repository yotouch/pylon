package com.yotouch.base.service;

import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;

public interface WalletService {

    Entity getCustomerWallet(DbSession dbSession, String customerUuid);

    Entity getShopWallet(DbSession dbSession, String shopUuid);
    
    void addToShopWallet(DbSession dbSession, String shopUuid, int amount);

    void addToCustomerWallet(DbSession dbSession, String customerUuid, int amount);

}
