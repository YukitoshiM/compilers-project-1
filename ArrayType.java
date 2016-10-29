import java.util.Vector;


class ArrayType extends Type
{
	public ArrayType(String strName, int size)
	{
		super(strName,size);
		super.push(size);
	}
	
	public ArrayType(String strName, int size, Type subtype)
	{
		super(strName,size, subtype);
		super.setSubType(subtype);
		super.push(size);
	}
	
	
	/*public void push(int i){
		vec.add(0,i);
	}
	
	public int pop(){
		return vec.lastElement();
	}
	
	public Vector<Integer> getVec(){
		return vec;
	}
	
	public void makeClone(){
		Vector<Integer> nvec = ((ArrayType)this.getSubType()).getVec();
		this.push(nvec.lastElement());
	}*/
	
	
	/*public void setSubType(Type subtype)
	{
		m_subType = subtype;
		if(subtype != null && !(subtype instanceof StructType))
			this.setSize(this.getVec().lastElement()*subtype.getSize());		
	}
	
	public Type getSubType()
	{
		return m_subType;
	}*/
	
	public boolean isComposite() {return true;}
	public boolean isArray()	{return true;}
	
	public boolean isAddressible()	{return true;}
	public boolean isEquivalent(Type typ)	{
		if(typ instanceof ArrayType)
			if(this.getSubType().isEquivalent(typ.getSubType()))
				return true;
		return false;
	}
	
	private Type m_subType;
	private Vector<Integer> vec = new Vector<Integer>();
}
