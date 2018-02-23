package de.mrbw.chaos.monkey.aop;

import de.mrbw.chaos.monkey.component.ChaosMonkey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Benjamin Wilms
 */

@Aspect
public class SpringControllerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringControllerAspect.class);

    private final ChaosMonkey chaosMonkey;

    public SpringControllerAspect(ChaosMonkey chaosMonkey) {
        this.chaosMonkey = chaosMonkey;
    }

    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void classAnnotatedWithControllerPointcut() {
    }

    @Pointcut("execution(* *.*(..))")
    public void allPublicMethodPointcut() {
    }

    @Around("classAnnotatedWithControllerPointcut() && allPublicMethodPointcut()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.debug(LOGGER.isDebugEnabled() ? "Controller class and public method detected: " + pjp.getSignature() : null);

        chaosMonkey.callChaosMonkey();

        return pjp.proceed();
    }

}
