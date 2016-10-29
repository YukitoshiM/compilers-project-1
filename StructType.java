import java.util.Vector;


class StructType  extends Type
{
	public StructType(String strName, int size)
	{
		super(strName,0);
		base = null;
	}
	
	public boolean isStruct() {return true;} 
	
	public void push(STO s){
		vector.addElement(s);
		this.setSize(this.getSize()+s.getType().getSize());
		
	}
	
	public boolean checker(String s){
		boolean b = false;
		for(int i = 0; i < vector.size(); i++){
			if(s.equals(vector.elementAt(i).getName())){
				b = true;
				return b;
			}
		}
		
		return b;
	}
	public STO getField(String s){
		boolean b = false;
		for(int i = 0; i < vector.size(); i++){
			if(s.equals(vector.elementAt(i).getName())){
				return vector.elementAt(i);
			}
		}
		return new ErrorSTO("Struct Error");
	}
	
	public boolean isAssignable(Type t) {
		if(t.isStruct()){
			if(this.getName().equals(t.getName()) || this.base.equals(t.getName()))
				return true;
		}
		return false;
	}
	public boolean isEquivalent(Type t) {
		if(t.isStruct()){
			if(this.getName().equals(t.getName()))
				return true;
		}
		return false;
	}
	
	
	
	public void setBase(String name) {
		base = name;
	}
	
	public String getBase() {
		return base;
	}
	
	public String base;
}
