
public class Tuple {

    private String fname;
    private String instr;

    public Tuple(String name, String instr)
    {
        this.fname = name;
        this.instr = instr;
    }
    
    public String getfname(){
    	return fname;
    }
    public void setfname(String name){
    	this.fname = name;
    }
    public String getinstr(){
    	return instr;
    }
    public void setinstr(String instr){
    	this.instr = instr;
    }
}