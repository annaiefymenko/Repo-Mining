package hsd.crawler;
import java.util.List;

public class Repository {
    public String name;
    public String language;
    public int stars;
    public int forks;
    public int commits;
    public int branches;
    public int files;
    public String lastModified;
    public List<String> authors;

    public Repository(String name, String language, int stars, int forks) {
        this.name = name;
        this.language = language;
        this.stars = stars;
        this.forks = forks;
    }
}

