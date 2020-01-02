package de.codecentric.spring.boot.chaos.monkey.component;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@ExtendWith(MockitoExtension.class)
class ChaosMonkeySchedulerTest {

  @Mock private ScheduledTaskRegistrar registrar;

  @Mock private AssaultProperties config;

  @Mock private ChaosMonkeyRuntimeScope scope;

  @Test
  void shouldTolerateMissingRegistry() {
    when(config.getRuntimeAssaultCronExpression()).thenReturn("*/5 * * * * ?");
    new ChaosMonkeyScheduler(null, config, scope);
    // no exception despite null injection
  }

  @Test
  void shouldRespectTheOffSetting() {
    when(config.getRuntimeAssaultCronExpression()).thenReturn("OFF");

    new ChaosMonkeyScheduler(registrar, config, scope);
    verify(scope, never()).callChaosMonkey();
    verify(registrar, never()).scheduleCronTask(any());
  }

  @Test
  void shouldScheduleATask() {
    String schedule = "*/1 * * * * ?";
    ScheduledTask scheduledTask = mock(ScheduledTask.class);
    when(config.getRuntimeAssaultCronExpression()).thenReturn(schedule);
    when(registrar.scheduleCronTask(any())).thenReturn(scheduledTask);

    new ChaosMonkeyScheduler(registrar, config, scope);

    verify(registrar).scheduleCronTask(argThat(hasScheduleLike(schedule)));
  }

  @Test
  void shouldScheduleANewTaskAfterAnUpdate() {
    String schedule = "*/1 * * * * ?";
    ScheduledTask oldTask = mock(ScheduledTask.class);
    ScheduledTask newTask = mock(ScheduledTask.class);
    when(config.getRuntimeAssaultCronExpression()).thenReturn(schedule);
    when(registrar.scheduleCronTask(any())).thenReturn(oldTask, newTask);

    ChaosMonkeyScheduler cms = new ChaosMonkeyScheduler(registrar, config, scope);
    cms.reloadConfig();

    verify(registrar, times(2)).scheduleCronTask(argThat(hasScheduleLike(schedule)));
    verify(oldTask).cancel();
  }

  @Test
  void shouldTriggerRuntimeScopeRunAttack() {
    String schedule = "*/1 * * * * ?";
    when(config.getRuntimeAssaultCronExpression()).thenReturn(schedule);
    when(registrar.scheduleCronTask(any()))
        .thenAnswer(
            iom -> {
              iom.getArgument(0, CronTask.class).getRunnable().run();
              return null;
            });

    new ChaosMonkeyScheduler(registrar, config, scope);
    verify(scope).callChaosMonkey();
  }

  private ArgumentMatcher<CronTask> hasScheduleLike(String schedule) {
    return cronTask -> cronTask.getExpression().equals(schedule);
  }
}
