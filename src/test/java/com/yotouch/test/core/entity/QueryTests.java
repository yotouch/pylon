package com.yotouch.test.core.entity;

import com.yotouch.core.entity.query.ff.CountField;
import com.yotouch.core.entity.query.ff.FunctionField;
import com.yotouch.core.entity.query.Query;
import com.yotouch.core.entity.query.QueryField;
import com.yotouch.core.entity.query.ff.SumField;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.base.PylonApplication;
import com.yotouch.core.entity.Entity;
import com.yotouch.core.entity.EntityManager;
import com.yotouch.core.runtime.DbSession;
import com.yotouch.core.runtime.YotouchApplication;
import com.yotouch.core.runtime.YotouchRuntime;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
@Transactional
public class QueryTests {

    private static final Logger logger = LoggerFactory.getLogger(QueryTests.class);

    @Autowired
    private EntityManager entityMgr;

    @Autowired
    private YotouchApplication ytApp;

    @Before
    public void setup() {

        YotouchRuntime rt = ytApp.getRuntime();
        DbSession ds = rt.createDbSession();

        Entity u1 = ds.newEntity("user");
        u1.setValue("age", "99");
        u1.setValue("nickname", "f99");
        ds.save(u1);

        Entity u2 = ds.newEntity("user");
        u2.setValue("age", "99");
        u2.setValue("nickname", "f99-2");
        ds.save(u2);

        Entity u3 = ds.newEntity("user");
        u3.setValue("age", "88");
        u3.setValue("nickname", "f99");
        ds.save(u3);

    }

    @Test
    public void testQuery() {

        YotouchRuntime rt = ytApp.getRuntime();
        DbSession ds = rt.createDbSession();


        QueryField qf = new CountField();

        Query q = new Query();
        q.addField(qf);
        q.rawSql("age = ?", new Object[]{99});

        Entity e = ds.queryOne("user", q);

        int count = e.v(qf.getName());

        assertEquals(2, count);


        q = new Query();
        qf = new SumField().setArg("age");
        q.addField(qf);
        q.rawSql("nickname = ?", new Object[]{"f99"});

        e = ds.queryOne("user", q);
        double sum = e.v(qf.getName());
        assertEquals(187, sum, 0);





    }

}
