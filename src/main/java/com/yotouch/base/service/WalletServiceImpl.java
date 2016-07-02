package com.yotouch.base.service;

import com.yotouch.core.Consts;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private YotouchApplication ytApp;

    @Override
    public Entity getCustomerWallet(DbSession dbSession, String customerUuid) {
        return getWallet(dbSession, Consts.WALLET_TYPE_CUSTOMER, customerUuid);
    }
    
    private Entity getWallet(DbSession dbSession, String type, String uuid) {
        synchronized (uuid) {
            Entity wallet = dbSession.queryOneRawSql(
                    "wallet",
                    "ownerUuid = ? AND ownerType = ?",
                    new Object[]{uuid, type}
            );

            if (wallet == null) {
                wallet = dbSession.newEntity("wallet");
                wallet.setValue("ownerType", type);
                wallet.setValue("ownerUuid", uuid);
                wallet.setValue("amount", 0);
                wallet = dbSession.save(wallet);
            }

            return wallet;
        }
    }
    
    @Override
    public Entity getShopWallet(DbSession dbSession, String shopUuid) {
        return getWallet(dbSession, Consts.WALLET_TYPE_SHOP, shopUuid);
    }

    @Override
    public void addToShopWallet(DbSession dbSession, String shopUuid, int amount) {
        Entity wallet = this.getShopWallet(dbSession, shopUuid);
        this.doAddWallet(dbSession, wallet, amount);
    }

    @Override
    public void addToCustomerWallet(DbSession dbSession, String customerUuid, int amount) {
        Entity wallet = this.getCustomerWallet(dbSession, customerUuid);
        this.doAddWallet(dbSession, wallet, amount);
        
    }
    
    private synchronized void doAddWallet(DbSession dbSession, Entity wallet, int newAmount) {
        int amount = wallet.v("amount");
        wallet.setValue("amount", amount + newAmount);
        wallet.setValue("lastChangedAt", new Date());
        dbSession.save(wallet);
    }

}
