package com.yotouch.test.core.entity;

import com.yotouch.core.entity.mf.IntMetaFieldImpl;
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

import java.util.List;


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

        if (ds.queryOneRawSql(
                "user",
                "age = ? and nickname = ?",
                new Object[]{99, "f99"}
        ) == null) {
            Entity u1 = ds.newEntity("user");
            u1.setValue("age", "99");
            u1.setValue("nickname", "f99");
            ds.save(u1);
        }

        if (ds.queryOneRawSql(
                "user",
                "age = ? and nickname = ?",
                new Object[]{99, "f99-2"}
        ) == null) {
            Entity u2 = ds.newEntity("user");
            u2.setValue("age", "99");
            u2.setValue("nickname", "f99-2");
            ds.save(u2);
        }

        if (ds.queryOneRawSql(
                "user",
                "age = ? and nickname = ?",
                new Object[]{88, "f99"}
        ) == null) {
            Entity u3 = ds.newEntity("user");
            u3.setValue("age", "88");
            u3.setValue("nickname", "f99");
            ds.save(u3);
        }

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


        qf = new CountField("ageCount");
        q = new Query();
        q.addField(qf);
        q.rawSql("age = ?", new Object[]{99});

        e = ds.queryOne("user", q);
        int count99_1 = e.v(qf.getName());

        assertEquals(2, count99_1);


        qf = new CountField();
        qf.setName("ageCount2");
        q = new Query();
        q.addField(qf);
        q.rawSql("age = ?", new Object[]{99});

        e = ds.queryOne("user", q);
        int count99_2 = e.v(qf.getName());

        assertEquals(2, count99_2);


        q = new Query();
        qf = new SumField("ageSum").setArg("age");

        q.addField(qf);
        q.rawSql("nickname = ?", new Object[]{"f99"});

        e = ds.queryOne("user", q);
        double sum1 = e.v(qf.getName());
        assertEquals(187, sum1, 0);


        q = new Query();
        qf = new SumField().setArg("age");
        qf.setName("ageSum2");

        q.addField(qf);
        q.rawSql("nickname = ?", new Object[]{"f99"});

        e = ds.queryOne("user", q);
        double sum2 = e.v(qf.getName());
        assertEquals(187, sum2, 0);
    }


    @Test
    public void testGroupBy() {
        YotouchRuntime rt = ytApp.getRuntime();
        DbSession ds = rt.createDbSession();

        Query q = new Query();
        QueryField qf = new CountField("ageCount");

        IntMetaFieldImpl age = new IntMetaFieldImpl();
        age.setName("age");
        q.addField(qf).addField(age);
        q.rawSql("nickname LIKE ? group by age order by age desc", new Object[]{"f99%"});

        List<Entity> el = ds.query("user", q);

        int age99Count = el.get(0).v(qf.getName());
        assertEquals(2, age99Count);
        int  age88Count = el.get(1).v(qf.getName());
        assertEquals(1, age88Count);


        q = new Query();
        qf = new CountField("ageCount2");
        q.addField(qf).addField(age);
        q.addGroupBy("age");
        q.rawSql("nickname LIKE ?", new Object[]{"f99%"});

        el = ds.query("user", q);

        assertEquals(1, (int)el.get(0).v(qf.getName()));
        assertEquals(2, (int)el.get(1).v(qf.getName()));


        q = new Query();
        qf = new CountField("ageCount2");
        q.addField(qf).addField(age);
        q.addOrderBy("age", "desc");
        q.addGroupBy("age");
        q.rawSql("nickname LIKE ?", new Object[]{"f99%"});

        el = ds.query("user", q);

        assertEquals(2, el.size());
        assertEquals(2, (int)el.get(0).v(qf.getName()));
        assertEquals(1, (int)el.get(1).v(qf.getName()));
    }

    @Test
    public void testOrderBy() {
        YotouchRuntime rt = ytApp.getRuntime();
        DbSession ds = rt.createDbSession();

        Query q = new Query();
        q.addOrderBy("age", "desc");
        q.rawSql("nickname LIKE ?", new Object[]{"f99%"});
        List<Entity> el = ds.query("user", q);

        assertEquals(99, (int)el.get(0).v("age"));
        assertEquals(99, (int)el.get(1).v("age"));
        assertEquals(88, (int)el.get(2).v("age"));


        q = new Query();
        q.addOrderBy("age");
        q.rawSql("nickname LIKE ?", new Object[]{"f99%"});
        el = ds.query("user", q);

        assertEquals(88, (int)el.get(0).v("age"));
        assertEquals(99, (int)el.get(1).v("age"));
        assertEquals(99, (int)el.get(2).v("age"));


        q = new Query();
        q.addOrderBy("nickname");
        q.addOrderBy("age");
        q.rawSql("nickname LIKE ?", new Object[]{"f99%"});
        el = ds.query("user", q);

        assertEquals(88, (int)el.get(0).v("age"));
        assertEquals("f99", (String)el.get(0).v("nickname"));
        assertEquals(99, (int)el.get(1).v("age"));
        assertEquals("f99", (String)el.get(1).v("nickname"));
        assertEquals(99, (int)el.get(2).v("age"));
        assertEquals("f99-2", (String)el.get(2).v("nickname"));


        q = new Query();
        q.addOrderBy("nickname");
        q.addOrderBy("age", "desc");
        q.rawSql("nickname LIKE ?", new Object[]{"f99%"});
        el = ds.query("user", q);

        assertEquals(99, (int)el.get(0).v("age"));
        assertEquals("f99", (String)el.get(0).v("nickname"));
        assertEquals(88, (int)el.get(1).v("age"));
        assertEquals("f99", (String)el.get(1).v("nickname"));
        assertEquals(99, (int)el.get(2).v("age"));
        assertEquals("f99-2", (String)el.get(2).v("nickname"));
    }
}
