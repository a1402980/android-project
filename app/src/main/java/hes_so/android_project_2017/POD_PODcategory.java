package hes_so.android_project_2017;

/**
 * Created by Lionel on 20/11/2017.
 */

public class POD_PODcategory {
    private int POD_id;
    private int PODcateg_id;
    private int difficulty;

    public POD_PODcategory(){

    }

    public POD_PODcategory(int POD_id, int PODcateg_id, int difficulty){
        this.POD_id = POD_id;
        this.PODcateg_id = PODcateg_id;
        this.difficulty = difficulty;
    }

    public int getPOD_id(){
        return this.POD_id;
    }

    public int getPODcateg_id(){
        return this.PODcateg_id;
    }

    public int getDifficulty(){
        return this.difficulty;
    }

    public void setPOD_id(int POD_id){
        this.POD_id = POD_id;
    }

    public void setPODcateg_id(int PODcateg_id){
        this.PODcateg_id = PODcateg_id;
    }

    public void setDifficulty(int difficulty){
        this.difficulty = difficulty;
    }

}

