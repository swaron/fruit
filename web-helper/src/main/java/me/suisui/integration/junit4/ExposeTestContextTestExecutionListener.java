package me.suisui.integration.junit4;

import org.springframework.core.NamedThreadLocal;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class ExposeTestContextTestExecutionListener extends AbstractTestExecutionListener {
    private static final ThreadLocal<TestContext> testContextHolder = new NamedThreadLocal<TestContext>(
            "Current exeuting test context");

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        testContextHolder.set(testContext);
        super.beforeTestMethod(testContext);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        testContextHolder.set(null);
        super.afterTestMethod(testContext);
    }
    public static TestContext currentTestContext() {
        return testContextHolder.get();
    }

}
