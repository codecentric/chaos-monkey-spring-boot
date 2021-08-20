package de.codecentric.spring.boot.chaos.monkey.component;

import de.codecentric.spring.boot.chaos.monkey.assaults.ChaosMonkeyRuntimeAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.KillAppAssault;
import de.codecentric.spring.boot.chaos.monkey.assaults.MemoryAssault;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/** @author Maxime Bouchenoire */
public class ChaosMonkeyScheduler {

  private static final Logger Logger = LoggerFactory.getLogger(ChaosMonkeyScheduler.class);

  @Nullable private final ScheduledTaskRegistrar scheduler;

  private final AssaultProperties config;

  private final KillAppAssault killAppAssault;

  private final MemoryAssault memoryAssault;

  private List<ScheduledTask> currentTasks = new ArrayList<>(2);

  public ChaosMonkeyScheduler(
      ScheduledTaskRegistrar scheduler,
      AssaultProperties config,
      KillAppAssault killAppAssault,
      MemoryAssault memoryAssault) {
    this.scheduler = scheduler;
    this.config = config;
    this.killAppAssault = killAppAssault;
    this.memoryAssault = memoryAssault;

    reloadConfig();
  }

  public void reloadConfig() {
    if (!currentTasks.isEmpty()) {
      Logger.info("Cancelling previous tasks");
      currentTasks.forEach(ScheduledTask::cancel);
      currentTasks = new ArrayList<>(2);
    }

    final CronExpression globalCronExpression =
        new CronExpression(config.getRuntimeAssaultCronExpression());

    new CronExpression(config.getKillApplicationCronExpression())
        .fallbackOn(globalCronExpression)
        .ifActive(killAppCron -> scheduleRuntimeAssault(scheduler, killAppAssault, killAppCron));

    new CronExpression(config.getMemoryCronExpression())
        .fallbackOn(globalCronExpression)
        .ifActive(memoryCron -> scheduleRuntimeAssault(scheduler, memoryAssault, memoryCron));
  }

  private void scheduleRuntimeAssault(
      ScheduledTaskRegistrar scheduler, ChaosMonkeyRuntimeAssault assault, String cron) {

    if (scheduler == null) {
      // We might consider an exception here, since the user intent could
      // clearly not be serviced
      Logger.error(
          "No scheduler available in application context, will not process schedule of {}",
          assault.getClass().getSimpleName());
    } else {
      final CronTask cronTask = new CronTask(() -> {
        if (assault.isActive()) {
          assault.attack();
        }
      }, cron);

      final ScheduledTask scheduledTask = scheduler.scheduleCronTask(cronTask);
      currentTasks.add(scheduledTask);
    }
  }

  private static class CronExpression {

    private final String expression;

    private CronExpression(String expression) {
      this.expression = expression;
    }

    boolean isActive() {
      return !"OFF".equals(expression) && expression != null;
    }

    CronExpression fallbackOn(CronExpression other) {
      if (this.isActive() || !other.isActive()) {
        return this;
      }

      return other;
    }

    public void ifActive(Consumer<String> cronConsumer) {
      if (isActive()) {
        cronConsumer.accept(expression);
      }
    }
  }
}
