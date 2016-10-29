
class BoolType extends BasicType {
	
	public BoolType(String strName, int size)
	{
		super(strName,size);
	}
	
	public boolean isBool()	{return true;}
	
	public boolean isAssignable(Type t) {
		if (t.isBool()) {
			return true;
		}
		return false;
	}
	
	public boolean isEquivalent(Type t) {
		if (t.isBool()) {
			return true;
		}
		return false;
	}

}
