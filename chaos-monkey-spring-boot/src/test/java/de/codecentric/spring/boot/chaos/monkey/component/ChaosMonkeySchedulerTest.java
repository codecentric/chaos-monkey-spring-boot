package de.codecentric.spring.boot.chaos.monkey.component;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import de.codecentric.spring.boot.chaos.monkey.assaults.CpuAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.MemoryAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import java.util.Arrays;
import java.util.Collections;
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

  @Mock private KillAppAssault killAppAssault;

  @Mock private MemoryAssault memoryAssault;

  @Mock private CpuAssault cpuAssault;

  @Test
  void shouldRespectTheOffSetting() {
    when(memoryAssault.getCronExpression(any())).thenReturn("OFF");
    when(killAppAssault.getCronExpression(any())).thenReturn("OFF");
    when(cpuAssault.getCronExpression(any())).thenReturn("OFF");

    new ChaosMonkeyScheduler(
        registrar, config, Arrays.asList(memoryAssault, killAppAssault, cpuAssault));
    verify(killAppAssault, never()).attack();
    verify(memoryAssault, never()).attack();
    verify(cpuAssault, never()).attack();
    verify(registrar, never()).scheduleCronTask(any());
  }

  @Test
  void shouldScheduleATask() {
    String schedule = "*/1 * * * * ?";
    ScheduledTask scheduledTask = mock(ScheduledTask.class);
    when(memoryAssault.getCronExpression(any())).thenReturn(schedule);
    when(registrar.scheduleCronTask(any())).thenReturn(scheduledTask);

    new ChaosMonkeyScheduler(registrar, config, Collections.singletonList(memoryAssault));

    verify(registrar).scheduleCronTask(argThat(hasScheduleLike(schedule)));
  }

  @Test
  void shouldNotScheduleNewTasksAfterUnrelatedUpdate() {
    String schedule = "*/1 * * * * ?";
    ScheduledTask oldTask = mockScheduledTask("memory", schedule);
    when(memoryAssault.getCronExpression(any())).thenReturn(schedule);
    when(registrar.scheduleCronTask(any())).thenReturn(oldTask);

    ChaosMonkeyScheduler cms =
        new ChaosMonkeyScheduler(registrar, config, Collections.singletonList(memoryAssault));
    verify(registrar, times(1)).scheduleCronTask(argThat(hasScheduleLike(schedule)));

    reset(registrar);

    cms.reloadConfig();

    verify(registrar, never()).scheduleCronTask(argThat(hasScheduleLike(schedule)));
  }

  @Test
  void shouldTriggerRuntimeScopeRunAttack() {
    String schedule = "*/1 * * * * ?";
    when(memoryAssault.isActive()).thenReturn(true);
    when(memoryAssault.getCronExpression(any())).thenReturn(schedule);
    when(registrar.scheduleCronTask(any()))
        .thenAnswer(
            iom -> {
              iom.getArgument(0, CronTask.class).getRunnable().run();
              return null;
            });

    new ChaosMonkeyScheduler(registrar, config, Collections.singletonList(memoryAssault));
    verify(memoryAssault).attack();
  }

  @Test
  void shouldRescheduleOnlyChangedTasks() {
    String memorySchedule = "*/1 * * * * ?";
    String killAppSchedule = "*/2 * * * * ?";
    ScheduledTask memoryTask = mockScheduledTask("memory", memorySchedule);
    ScheduledTask oldTask = mockScheduledTask("killApp", killAppSchedule);
    ScheduledTask newTask = mock(ScheduledTask.class);
    when(memoryAssault.getCronExpression(any())).thenReturn(memorySchedule);
    when(killAppAssault.getCronExpression(any())).thenReturn(killAppSchedule);
    when(registrar.scheduleCronTask(argThat(hasScheduleLike(memorySchedule))))
        .thenReturn(memoryTask);
    when(registrar.scheduleCronTask(argThat(hasScheduleLike(killAppSchedule))))
        .thenReturn(oldTask, newTask);

    ChaosMonkeyScheduler cms =
        new ChaosMonkeyScheduler(registrar, config, Arrays.asList(memoryAssault, killAppAssault));
    verify(registrar).scheduleCronTask(argThat(hasScheduleLike(memorySchedule)));
    verify(registrar).scheduleCronTask(argThat(hasScheduleLike(killAppSchedule)));

    reset(registrar);
    String killAppSchedule2 = "*/3 * * * * ?";
    when(killAppAssault.getCronExpression(any())).thenReturn(killAppSchedule2);

    cms.reloadConfig();
    verify(registrar).scheduleCronTask(argThat(hasScheduleLike(killAppSchedule2)));
    verify(memoryTask, never()).cancel();
    verify(oldTask).cancel();
  }

  private ArgumentMatcher<CronTask> hasScheduleLike(String schedule) {
    return cronTask -> cronTask != null && cronTask.getExpression().equals(schedule);
  }

  private static ScheduledTask mockScheduledTask(String name, String schedule) {
    ScheduledTask scheduledTask = mock(ScheduledTask.class, name);
    when(scheduledTask.getTask()).thenReturn(new CronTask(() -> {}, schedule));
    return scheduledTask;
  }
}
