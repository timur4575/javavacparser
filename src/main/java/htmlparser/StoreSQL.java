package htmlparser;


import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Класс отвечает за работу с БД - записывает и читает вакансии.
 */
public class StoreSQL {
    private Config config;
    private Connection conn;
    private Logger logger = Logger.getLogger(StoreSQL.class);

    public StoreSQL(Config config) {
        this.config = config;
    }

    public void init() {
        try {
            Class.forName(config.get("driver-class-name"));
            conn = DriverManager.getConnection(
                    config.get("url"),
                    config.get("username"),
                    config.get("password"));
            creatTableIfNotExist();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Создает таблицу, если таковая отсутсвует.
     */
    private void creatTableIfNotExist() throws SQLException {
        PreparedStatement st = conn.prepareStatement(
                "CREATE TABLE IF NOT EXISTS vacancies("
                        + "id SERIAL PRIMARY KEY,"
                        + "name VARCHAR (256) UNIQUE,"
                        + "text VARCHAR (10000),"
                        + "link VARCHAR (1000),"
                        + "create_date DATE)");
        st.execute();
        st.close();
    }

    /**
     * Метод добавляет вакансии из мапы в БД.
     *
     * @throws SQLException
     */
    public void addVacancies(List<Vacancy> vacancies) {
        try {
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO vacancies (name, text, link, create_date) "
                            + "VALUES (?,?,?,?)");
            for (Vacancy curVacancy : vacancies) {
                st.setString(1, curVacancy.getName());
                st.setString(2, curVacancy.getText());
                st.setString(3, curVacancy.getUrl());
                st.setDate(4, Date.valueOf(curVacancy.getCreateDate()));
                try {
                    st.execute();

                    logger.info(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                            + " Вакансия добавлена в бд");
                } catch (SQLException e) {
                    logger.error(e.getMessage());
                }
            }
            st.close();
            logger.info(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    + " Все вакансии были сохранены в таблицу vacancies.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод считывает из БД самую позднюю дату создания вакансии.
     *
     * @return - vacancyMap - мапа вакансий.
     * @throws SQLException
     */
    public String readDateOfLastVacancy() {
        String returnDate = null;
        try {
            PreparedStatement st = conn.prepareStatement("SELECT MAX(create_date) FROM vacancies");
            ResultSet resultSet = st.executeQuery();
            while (resultSet.next()) {
                returnDate = resultSet.getString(1);
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return returnDate;
    }


    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
