package htmlparser;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;

public class SqlRuParser implements Job {
    private static Config config;

    public static void main(String[] args) {
        String propertyFile = args[0];
        config = new Config(propertyFile);
        QuartzConf quartzConf = new QuartzConf(config);
        quartzConf.quartzInit();
    }

    private void start() {
        try {
            //Создали объект StoreSql - надо считать уже имеющиеся в БД вакансии.
            StoreSQL storeSQL = new StoreSQL(config);
            storeSQL.init();
            //Создали объект Parser - в конструктор передали дату последней вакансии.
            Parser parser = new Parser(storeSQL.readDateOfLastVacancy());
            parser.startParse();
            //Записали новые вакансии в БД.
            storeSQL.addVacancies(parser.getVacancies());
            storeSQL.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        start();
    }
}
