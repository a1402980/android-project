package hes_so.santour;


public class PODcategory {
    private int id;
    private String name;

    public PODcategory(){

    }

    public PODcategory(String name){
        this.name = name;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

}

