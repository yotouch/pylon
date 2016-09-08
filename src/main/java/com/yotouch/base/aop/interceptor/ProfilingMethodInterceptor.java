package com.yotouch.base.aop.interceptor;

import com.google.common.base.Joiner;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class ProfilingMethodInterceptor implements MethodInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ProfilingMethodInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        final StopWatch stopWatch = new StopWatch("Thread-" + Thread.currentThread().getId());
        stopWatch.start("invocation.proceed()");

        try {
            logger.info("~~~~~~~~ START METHOD {} - {}~~~~~~~~", invocation.getMethod().toGenericString(), Joiner.on(",").useForNull("").join(invocation.getArguments()));
            return invocation.proceed();
        } finally {
            stopWatch.stop();
            logger.info(stopWatch.prettyPrint());
            logger.info("~~~~~~~~ END METHOD {} - {} ~~~~~~~~ {} ", invocation.getMethod().toGenericString(), Joiner.on(",").useForNull("").join(invocation.getArguments()), stopWatch.getTotalTimeMillis());
        }
    }

}
