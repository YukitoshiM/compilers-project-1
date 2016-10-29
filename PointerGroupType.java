
class PointerGroupType extends Type {
	PointerGroupType(String strName, int size)
	{
		super(strName,size);
	}
	
	PointerGroupType(String strName, Type typ, int size)
	{
		super(strName,size);
		this.setPtr(typ);

	}
	
	
	public boolean	isPtrGrp()	{ return true; }
	
	public boolean isAssignable(Type t) {
    	if(t instanceof PointerGroupType){
    		if(this.m_base.isEquivalent(((PointerGroupType)t).getBase())){
    			return true;
    		}
    	}
		return false;
	}
	public void setPtr(Type ptr)
	{  
		
		m_ptr = ptr;
	}
	
	public Type getPtr()
	{
		return m_ptr;
	}
	
	public void setBase(Type ptr)
	{  
		Type t = ((PointerType)m_ptr).getSubType();
		if((t) == null){
			
			((PointerType)m_ptr).setSubType(ptr);
		}
		
		while(t instanceof PointerType){
			
			if(((PointerType)t).getSubType() == null){
				
				((PointerType)t).setSubType(ptr);
			}
			t = ((PointerType)t).getSubType();
		}
		m_base = ptr;
	}
	
	public Type getBase()
	{	
		return m_base;
	}
	private Type m_ptr;
	private Type m_base;
}
