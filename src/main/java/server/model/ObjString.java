package server.model;

public class ObjString {
    private String s;
    public ObjString(String st){
        this.s=st;
    }

    public String getString(){
        return s;
    }

    @Override
    public boolean equals(Object s2){
        if(s2.getClass()==this.getClass())
            return s.equals(((ObjString) s2).getString());
        else if(s2.getClass()==s.getClass())
            return s.equals(s2);
        else return false;

    }
    @Override
    public String toString(){
        return s;
    }
}
