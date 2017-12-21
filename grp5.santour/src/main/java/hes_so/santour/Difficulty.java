package hes_so.santour;


public class Difficulty {
    private int level;
    private String id;

    public Difficulty(int level, String id) {
        this.level = level;
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (level<=10 && level>= 0) {
            this.level = level;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
