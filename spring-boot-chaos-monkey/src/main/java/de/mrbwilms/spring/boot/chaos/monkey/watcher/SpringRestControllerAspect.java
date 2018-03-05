package de.mrbwilms.spring.boot.chaos.monkey.watcher;

import de.mrbwilms.spring.boot.chaos.monkey.component.ChaosMonkey;
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
public class SpringRestControllerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringRestControllerAspect.class);

    private final ChaosMonkey chaosMonkey;

    public SpringRestControllerAspect(ChaosMonkey chaosMonkey) {
        this.chaosMonkey = chaosMonkey;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void classAnnotatedWithControllerPointcut() {
    }

    @Pointcut("execution(* *.*(..))")
    public void allPublicMethodPointcut() {
    }

    @Around("classAnnotatedWithControllerPointcut() && allPublicMethodPointcut()")
    public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
        LOGGER.debug(LOGGER.isDebugEnabled() ? "RestController class and public method detected: " + pjp.getSignature() : null);

        chaosMonkey.callChaosMonkey();

        return pjp.proceed();
    }

}
