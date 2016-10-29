class PointerType extends Type{
	public PointerType(String strName, int size)
	{
		super(strName,size);
		this.setPtrLvl(1);
	}
	public boolean	isPtr()		{ return true; }
	
    public boolean isAssignable(Type t) {
    	
    	boolean flag = true;
    	
    	if(t.isPtr()){
    		if( this.getBase().isEquivalent(((PointerType)t).getBase())){
    			return true;
    		}
    		return false;
    	}
    	flag = true;
    	Type thisType = this;
    	int countThis = 0;
    	while(flag){
			thisType = ((PointerType)thisType).getSubType();
			countThis++;
			if(!(thisType.isPtr())){
				flag = false;
			}
		}
		
    	int countT = 0;
    	if (t.isPtr()){
			while(flag){
				t = ((PointerType)t).getSubType();
				
				countT++;
				if( !(t.isPtr())){
					flag = false;
				}
			}
		}
		else if (t.isArray()){
			countT--;
			while(flag){
				t = ((ArrayType)t).getSubType();
				countT++;
				if(!(t.isArray())){
					flag = false;
				}
			}
			
			if(countT != countThis)
				return false;
		}
		else if(t.isNulPtr()){
			return true;
		}
		else{

			return false;
		}
    	
    	
		
    	
    	
		if(!(thisType.isEquivalent(t)) && !t.isNulPtr() )
			return false;
		
		
		
		return true;
	}
    
    public boolean isEquivalent(Type t) {
		if (t.isPtr() && this.isPtr()) {
			return true;
		}
		return false;
	}
	
	public void setSubType(Type subtype)
	{
		m_subType = subtype;
	}
	
	public Type getSubType()
	{
		return m_subType;
	}
	
	public void setBase(Type ptr)
	{  
		m_base = ptr;
		if(m_subType instanceof PointerType){
			((PointerType) m_subType).setBase(ptr);
		}
	}
	
	public Type getBase()
	{
		return m_base;
	}
	
	public void setPtrLvl(int lvl) {
		m_ptrlvl = lvl;
	}
	
	public int getPtrLvl() {
		return m_ptrlvl;
	}
	
	private int m_ptrlvl;

	private Type m_base;
	
	private Type m_subType;
}
