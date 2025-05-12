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
    public int submodules; 
    public String gitignore; 
    public String gitattributes; 
    public String tag; 

    public Repository(String name, String language, int stars, int forks) {
        this.name = name;
        this.language = language;
        this.stars = stars;
        this.forks = forks;
        this.submodules = submodules; 
        this.gitignore = gitignore; 
        this.gitattributes = gitattributes; 
        this.tag = tag; 
    }
}

