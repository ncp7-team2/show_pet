package bitcamp.show_pet.util;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TransactionProxyBuilder {
    TransactionTemplate txTemplate;

    public TransactionProxyBuilder(PlatformTransactionManager txManager) {
        this.txTemplate = new TransactionTemplate(txManager);
    }

    public Object build(Object originalWorker) {
        Class<?> clazz = originalWorker.getClass().getInterfaces()[0];

        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method originalMethod = getOriginalMethod(originalWorker, method);

                Transactional transactional = originalMethod.getAnnotation(Transactional.class);

                if (transactional != null) {
                    return txTemplate.execute(status -> {
                        try {
                            return originalMethod.invoke(originalWorker, args);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    return originalMethod.invoke(originalWorker, args);
                }
            }
        });
    }

    public Method getOriginalMethod(Object originalWorker, Method method) throws Exception {
        Method[] methods = originalWorker.getClass().getDeclaredMethods();
        for (Method originalmethod : methods) {
            if (originalmethod.getName().equals(method.getName()) && originalmethod.getParameterCount() == method.getParameterCount()) {
                return originalmethod;
            }
        }
        return null;
    }
}
