package de.codecentric.spring.boot.chaos.monkey.watcher;

import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * @author Benjamin Wilms
 */
abstract class ChaosMonkeyBaseAspect {
    @Pointcut("within(de.codecentric.spring.boot.chaos.monkey..*)")
    public void classInChaosMonkeyPackage() { }
}
