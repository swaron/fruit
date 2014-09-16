package me.suisui.integration.junit4;


import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.Ordered;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionRollbackAdvice extends TransactionalTestExecutionListener implements Ordered {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    private int order = 2;
    private TransactionTemplate transactionTemplate;
    private boolean newTransactionOnNoTransaction = false;
    
    @Required
    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }
    
    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Object adviceRollbackInfo(final ProceedingJoinPoint point) throws Throwable {
        TestContext testContext = ExposeTestContextTestExecutionListener.currentTestContext();
        if (testContext == null) {
            logger.debug("no test context,probably ExposeTestContextTestExecutionListener not configurated.");
            return point.proceed();
        }
        final boolean rollback = isRollback(testContext);
        TransactionStatus currentTransactionStatus = null;
        try {
            currentTransactionStatus = TransactionAspectSupport.currentTransactionStatus();
        } catch (NoTransactionException e) {
        }
        if(currentTransactionStatus == null){
            if(newTransactionOnNoTransaction){
                logger.debug("not in a transaction, start new transaciton.");
                return transactionTemplate.execute(new TransactionCallback<Object>() {
                    @Override
                    public Object doInTransaction(TransactionStatus status){
                        if(rollback){
                            status.setRollbackOnly();
                        }
                        try {
                            return point.proceed();
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }else{
                return point.proceed();
            }
        }else{
            if (rollback) {
                currentTransactionStatus.setRollbackOnly();
            }
            return point.proceed();
        }

    }
}
