package hsd.crawler;

public class Repository {
    public String name;
    public String language;
    public int stars;
    public int forks;

    public Repository(String name, String language, int stars, int forks) {
        this.name = name;
        this.language = language;
        this.stars = stars;
        this.forks = forks;
    }
}
