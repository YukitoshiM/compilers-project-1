//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class VarSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	VarSTO (String strName)
	{
		super (strName);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
		this.setIsAddressable(true);
		this.setIsModifiable (true);
		this.setPtrLvl(0);
	}

	public 
	VarSTO (String strName, Type typ)
	{
		
		super (strName, typ);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
		
		this.setIsAddressable(true);
		this.setIsModifiable (true);
		this.setPtrLvl(0);
	}
	public 
	VarSTO (String strName, boolean b, Type typ)
	{
		
		super (strName, typ);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
		this.setIsStatic(b);
		this.setIsAddressable(true);
		this.setIsModifiable (true);
		this.setPtrLvl(0);
	}
	
	public 
	VarSTO (String strName, Type typ, boolean b)
	{
		
		super (strName, typ);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
		this.setRef(b);
		this.setIsAddressable(true);
		this.setIsModifiable (true);
		this.setPtrLvl(0);
	}
	
	public 
	VarSTO (String strName, Type typ, int ptrlvl)
	{
		
		super (strName, typ);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
		this.setRef(false);
		this.setIsAddressable(true);
		this.setIsModifiable (true);
		this.setPtrLvl(ptrlvl);
	}
	
	
	public 
	VarSTO (String strName, Type typ, int ptrlvl, int offset, boolean isStatic)
	{
		
		super (strName, typ, offset, isStatic);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
		this.setRef(false);
		this.setIsAddressable(true);
		this.setIsModifiable (true);
		this.setPtrLvl(ptrlvl);
	}
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean   
	isVar () 
	{
		return true;
	}
	
	public void setRef(boolean b){
		m_ref = b;
	}
	
	public boolean getRef(){
		return m_ref;
	}
	
	public void setPtrLvl(int lvl){
		m_ptrlvl = lvl;
	}
	
	public int getPtrLvl(){
		return m_ptrlvl;
	}
	
	private boolean m_ref;
	private int m_ptrlvl;
}
