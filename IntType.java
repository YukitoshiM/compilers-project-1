
class IntType extends NumericType
{
	public IntType(String strName, int size)
	{
		super(strName,size);
	}
	
	public boolean isInt()		{return true;}
	
	public boolean isAssignable(Type t) {
		
		if (t.isInt())
			return true;
		
		return false;
	}
	
	public boolean isEquivalent(Type t) {
		if (t.isInt() && this.isInt()) {
			return true;
		}
		return false;
	}
	
	
}
