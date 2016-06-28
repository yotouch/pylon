package com.yotouch.base.service;

import com.yotouch.core.runtime.DbSession;

public interface  IdService {

    int nextId(DbSession dbSession);

    int nextSeqId(DbSession dbSession, String name);
    
}
