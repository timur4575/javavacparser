package htmlparser;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Класс инициализирует планировщик quartz и запускает его работу.
 */
public class QuartzConf {

    private Config config;

    public QuartzConf(Config config) {
        this.config = config;
    }

    protected void quartzInit() {
        config.init(); //инициализация config.
        String cronExpression = config.get("cron.time"); //получили график запусков.
        try {
            SchedulerFactory schedFact = new StdSchedulerFactory();
            Scheduler sched = schedFact.getScheduler(); //получили планировщик
            sched.start();

            //определить Job и привязать его к нашему классу HelloJob
            JobDetail jobDetail = newJob(SqlRuParser.class)
                    .withIdentity("myJob", "group1")
                    .build();

            //Задать тригер для старта и повторного запуска каждые 40 сек.
            Trigger trigger = newTrigger()
                    .withIdentity("trigger3", "group1")
                    .withSchedule(cronSchedule(cronExpression))
                    .forJob("myJob", "group1")
                    .build();

            //Скажите кварцу, чтобы запланировать работу, используя наш триггер
            sched.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
