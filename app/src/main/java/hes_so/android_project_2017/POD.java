package hes_so.android_project_2017;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lionel on 20/11/2017.
 */

public class POD {
    private int id;
    private int track_Id;
    private String name;
    private String description;
    private List<Difiiculties> difficultiesList;
    private String filePath;

    public POD(){
        difficultiesList = new ArrayList<Difiiculties>();
    }

    public POD(int track_Id, String name, String description, String filePath){
        this.track_Id = track_Id;
        this.name = name;
        this.description = description;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId(){
        return this.id;
    }

    public int getTrack_Id(){
        return this.track_Id;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setTrack_Id(int track_id){
        this.track_Id = track_id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void addDifficulty(Difiiculties d)
    {
        difficultiesList.add(d);
    }

    public List<Difiiculties> getDifficultiesList() {
        return difficultiesList;
    }

    public class Difiiculties
    {
        private int level;
        private String id;

        public Difiiculties(int level, String id) {
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
}

