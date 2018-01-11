package hes_so.santour;


public class Difficulty {
    private int level;
    private String id;

    public Difficulty(){

    }
    public Difficulty(String id, int level) {
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
