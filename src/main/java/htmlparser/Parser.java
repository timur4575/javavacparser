package htmlparser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private List<Vacancy> vacancies = new ArrayList<>();

    private boolean continParse = true;
    private int curPage = 1;
    private String lastDate;

    public Parser(String lastDate) {
        this.lastDate = lastDate;
    }

    public List<Vacancy> getVacancies() {
        return vacancies;
    }

    public void startParse() throws IOException {
        while (continParse) {
            Document page = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + curPage).get();
            Elements allTopicsElements = page.getElementsByAttributeValue("class", "postslisttopic");
            findJavaVac(allTopicsElements);
            curPage++;
        }
    }

    /**
     * Метод ищет вакансии, связанные с Java.
     * Он проверяет полученные элементы на соответсвтие паттерну.
     * Если элемент соответсвтует требованиям, то вызывается метод создания заявки.
     *
     * @param allTopicsElements - список элеметов (топиков) с текущей страницы.
     */
    private void findJavaVac(Elements allTopicsElements) {
        //Создаём паттерн для сравнение тем вакансий с нужным нам образцом.
        String regexName = ".*[Jj][Aa][Vv][Aa][^Ss].*";
        Pattern pattern = Pattern.compile(regexName);

        allTopicsElements.forEach(el -> {
            Matcher matcher = pattern.matcher(el.child(0).text());
            if (matcher.matches()) {
                try {
                    //Вызов метода, который отпарсит нужные данные и вызовет метод создания заявки.
                    parseJavaVac(el.child(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Метод получает элемент, соответствующий паттерну. И парсит данные для будущей вакансии.
     * Если дата создания вакансии будет null, или будет совпадать с последней датой вакансии, считанной из БД,
     * то метод, создающий заявку, не будет вызван, а поле continParse будет задано, как false.
     *
     * @param el - Элемент (дочерний элемент топика), который содержит вакансию связанную с Java.
     */
    private void parseJavaVac(Element el) throws IOException {
        String name = el.text();
        String link = el.attr("href");
        String createDate = null;
        String text = null;

        Document topicPage = Jsoup.connect(link).get();
        Element dateEl = topicPage.getElementsByAttributeValue("class", "msgFooter").get(0);
        createDate = getFormattedDateString(dateEl);

        if (createDate == null || (lastDate != null && createDate.equals(lastDate))) {
            continParse = false; //прекратит дальнейшие итерации цикла в методе startParse.
        }

        Element textEl = topicPage.getElementsByAttributeValue("class", "msgBody").get(1);
        text = textEl.text();

        if (createDate != null) {
            createVacancy(name, link, text, createDate);
        }
    }

    /**
     * Метод создает объект-вакансию и добавляет её в список вакансий.
     *
     * @param name       - имя вакансии.
     * @param link       - ссылка на топик.
     * @param text       - текст топика.
     * @param createDate - дата создания топика.
     */
    private void createVacancy(String name, String link, String text, String createDate) {
        Vacancy newVacancy = new Vacancy(name, text, link, createDate);
        vacancies.add(newVacancy);
    }

    /**
     * Метод принимает елемент с датой создания топика,
     * парсит дату создания и приводит её к виду yyyy-MM-dd.
     * Это делается для будущего использования даты в БД postgres.
     *
     * @param dateEl - Елемент, содержаий дату написания поста.
     * @return - возвращается отформанированная строка. Если год записи 2018, то вернет null.
     */
    private String getFormattedDateString(Element dateEl) {
        String str = dateEl.text();
        String dateStr = str.substring(0, str.indexOf(","));
        List<String> listOfMonth = Arrays.asList("", "янв", "фев", "мар", "апр", "май", "июн", "июл",
                "авг", "сен", "окт", "ноя", "дек");

        String[] dateArr = dateStr.split(" ");
        if (!dateArr[2].equals("19")) {
            return null;
        }

        //С помощью стринг-билдера собираем дату в нужный нам формат.
        //добавили год
        StringBuilder dateStrBuilder = new StringBuilder();
        dateStrBuilder.append("20");
        dateStrBuilder.append(dateArr[2]);
        dateStrBuilder.append("-");

        //добавили месяц
        String month = listOfMonth.indexOf(dateArr[1]) + "";
        if (month.length() == 1) {
            dateStrBuilder.append("0");
        }
        dateStrBuilder.append(month);
        dateStrBuilder.append("-");

        //добавили день
        String day = dateArr[0];
        if (day.length() == 1) {
            dateStrBuilder.append("0");
        }
        dateStrBuilder.append(day);
        return dateStrBuilder.toString();
    }
}
