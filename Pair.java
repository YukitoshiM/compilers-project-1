import java.util.Vector;
public class Pair {

    private Vector<Pair> vec; 
    private Type typ;
    private String name;
    private String print;

    
    public Pair(Type typ, Vector<Pair> vec)
    {
        this.typ = typ;
        this.vec = vec;
    }
    
    public Pair(Type typ, String name)
    {
        this.typ = typ;
        this.name = name;
    }
    
    public Pair(String name, String print)
    {
        this.print = print;
        this.name = name;
    }
    
    public String getPrint()
    {
        return this.print;
    }

    public Type getType()
    {
        return this.typ;
    }

    public Vector<Pair> getVector()
    {
        return vec;
    }
    
    public String getName(){
    	return name;
    }
}
