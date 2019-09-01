package htmlparser;

/**
 * Класс-бин, описывает вакансию с сайта SQL.ru
 */
public class Vacancy {
    private int id;
    private String name;
    private String text;
    private String url;
    private String createDate;

    public Vacancy(String name, String text, String url, String createDate) {
        this.name = name;
        this.text = text;
        this.url = url;
        this.createDate = createDate;
    }

    public Vacancy() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return String.format("* %s,\n  %s,\n  %s\n  %s", getName(), getText(), getUrl(), getCreateDate());
    }
}
