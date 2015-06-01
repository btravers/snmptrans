package com.zenika.snmptrans.utils;

import org.springframework.context.ApplicationContext;

public class AppContext {
    private static ApplicationContext CTX;

    /**
     * Injected from the class "ApplicationContextProvider" which is automatically
     * loaded during Spring-Initialization.
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        CTX = applicationContext;
    }

    /**
     * Get access to the Spring ApplicationContext from everywhere in your Application.
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return CTX;
    }
}
