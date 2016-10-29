
class FloatType extends NumericType
{
	public FloatType (String strName, int size)
	{
		super(strName,size);
	}
	
	public boolean isFloat()	{return true;}
	
	public boolean isAssignable(Type t)
	{
		if (t.isNumeric()) {
			return true;
		}
		return false;
	}
	
	public boolean isEquivalent(Type t)
	{
		if(t.isFloat()) {
			return true;
		}
		return false;
	}
}
