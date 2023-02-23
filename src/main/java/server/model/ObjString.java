package server.model;

public class ObjString {
    private final String s;
    public ObjString(String st){
        this.s=st;
    }

    public String getString(){
        return s;
    }

    @Override
    public boolean equals(Object o){
        if(o.getClass()==this.getClass())
            return s.equals(((ObjString)o).getString());
        else if(o.getClass()==String.class)
            return s.equals(o);
        else return false;

    }
    @Override
    public String toString(){
        return s;
    }
}
