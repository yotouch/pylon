package com.yotouch.test.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yotouch.base.PylonApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PylonApplication.class)
public class YotouchPylonApplicationTests {
    
    @Autowired
    ApplicationContext ctx;
    
	@Test
	public void contextLoads() {
        
	}

}
