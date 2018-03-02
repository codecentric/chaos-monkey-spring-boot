package de.mrbwilms.spring.boot.chaos.monkey.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import de.mrbwilms.spring.boot.chaos.monkey.configuration.AssaultProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class ChaosMonkeyTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor captorLoggingEvent;

    @Before
    public void setup() {
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        when(mockAppender.getName()).thenReturn("MOCK");
        root.addAppender(mockAppender);

    }


    private ChaosMonkey chaosMonkey;

    @Mock
    private AssaultProperties assaultProperties;

    @Before
    public void setUp() throws Exception {
        given(this.assaultProperties.getLevel()).willReturn(0);
        given(this.assaultProperties.getTroubleRandom()).willReturn(10);
        chaosMonkey = new ChaosMonkey(assaultProperties);

    }


    @Test
    public void isKillAppAssaultActive() {

        given(this.assaultProperties.isExceptionsActive()).willReturn(false);
        given(this.assaultProperties.isLatencyActive()).willReturn(false);
        given(this.assaultProperties.isKillApplicationActive()).willReturn(true);

        chaosMonkey.callChaosMonkey();

        ArgumentCaptor<LoggingEvent> argument = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender,times(2)).doAppend(argument.capture());

        assertEquals(Level.INFO, argument.getAllValues().get(0).getLevel());
        assertEquals(Level.INFO, argument.getAllValues().get(1).getLevel());
        assertEquals("Chaos Monkey - I am killing your Application!", argument.getAllValues().get(0).getMessage());
        assertEquals("Chaos Monkey - Unable to kill the App, I am not the BOSS!", argument.getAllValues().get(1).getMessage());

    }

    @Test
    public void isLatencyAssaultActive() {

        given(this.assaultProperties.isExceptionsActive()).willReturn(false);
        given(this.assaultProperties.isLatencyActive()).willReturn(true);
        given(this.assaultProperties.isKillApplicationActive()).willReturn(false);

        chaosMonkey.callChaosMonkey();

        ArgumentCaptor<LoggingEvent> argument = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender,times(1)).doAppend(argument.capture());

        assertEquals(Level.INFO, argument.getValue().getLevel());
        assertEquals("Chaos Monkey - timeout", argument.getValue().getMessage());


    }

    @Test
    public void isExceptionAssaultActive() {

        exception.expect(RuntimeException.class);
        exception.expectMessage("Chaos Monkey - RuntimeException");

        given(this.assaultProperties.isExceptionsActive()).willReturn(true);
        given(this.assaultProperties.isLatencyActive()).willReturn(false);
        given(this.assaultProperties.isKillApplicationActive()).willReturn(false);

        chaosMonkey.callChaosMonkey();

        ArgumentCaptor<LoggingEvent> argument = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender,times(1)).doAppend(argument.capture());

        assertEquals(Level.INFO, argument.getValue().getLevel());
        assertEquals("Chaos Monkey - exception", argument.getValue().getMessage());


    }

    @Test
    public void isExceptionAndLatencyAssaultActiveExpectExceptionLogging() {

        exception.expect(RuntimeException.class);
        exception.expectMessage("Chaos Monkey - RuntimeException");

        given(this.assaultProperties.isExceptionsActive()).willReturn(true);
        given(this.assaultProperties.isLatencyActive()).willReturn(true);
        given(this.assaultProperties.isKillApplicationActive()).willReturn(false);
        given(this.assaultProperties.getExceptionRandom()).willReturn(8);


        chaosMonkey.callChaosMonkey();

        ArgumentCaptor<LoggingEvent> argument = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender,times(1)).doAppend(argument.capture());

        assertEquals(Level.INFO, argument.getValue().getLevel());
        assertEquals("Chaos Monkey - exception", argument.getValue().getMessage());


    }

    @Test
    public void isExceptionAndLatencyAssaultActiveExpectLatencyLogging() {

        given(this.assaultProperties.isExceptionsActive()).willReturn(true);
        given(this.assaultProperties.isLatencyActive()).willReturn(true);
        given(this.assaultProperties.isKillApplicationActive()).willReturn(false);
        given(this.assaultProperties.getExceptionRandom()).willReturn(5); // <7

        chaosMonkey.callChaosMonkey();

        ArgumentCaptor<LoggingEvent> argument = ArgumentCaptor.forClass(LoggingEvent.class);
        verify(mockAppender,times(1)).doAppend(argument.capture());

        assertEquals(Level.INFO, argument.getValue().getLevel());
        assertEquals("Chaos Monkey - timeout", argument.getValue().getMessage());


    }

}
