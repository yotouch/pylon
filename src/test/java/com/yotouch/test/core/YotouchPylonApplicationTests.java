package com.yotouch.test.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.yotouch.base.PylonApplication;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PylonApplication.class)
public class YotouchPylonApplicationTests {
    
    @Autowired
    ApplicationContext ctx;
    
	@Test
	public void contextLoads() {
        
	}

}
