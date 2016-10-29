import java.util.Vector;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class FuncSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	FuncSTO (String strName)
	{
		super (strName);
		setReturnType (null);
		setIsReturned (false);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	FuncSTO (String strName, Vector<STO> paramList)
	{
		super (strName);
		setParamList(paramList);
		setReturnType (null);
		setIsReturned (false);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	FuncSTO (String strName, Vector<STO> paramList, int numOfParams)
	{
		super (strName);
		setParamList(paramList);
		setNumOfParams(numOfParams);
		setReturnType (null);
		setIsReturned (false);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	isFunc () 
	{ 
		return true;
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}

	

	//----------------------------------------------------------------
	// This is the return type of the function. This is different from 
	// the function's type (for function pointers).
	//----------------------------------------------------------------
	public void
	setIsReturned (boolean isreturned)
	{
		m_isReturned = isreturned;
	}

	public boolean
	getIsReturned ()
	{
		return m_isReturned;
	}
	
	public void
	setReturnType (Type typ)
	{
		m_returnType = typ;
	}

	public Type
	getReturnType ()
	{
		return m_returnType;
	}
	
	public void
	setRef (boolean ref)
	{
		m_ref = ref;
	}

	public boolean
	getRef ()
	{
		return m_ref;
	}
	
	public void setParamList (Vector<STO> params)
	{
		m_paramList = params;
	}
	
	public Vector<STO> getParamList ()
	{
		return m_paramList;
	}
	
	public void setNumOfParams (int numParams)
	{
		m_numOfParams = numParams;
	}
	
	public int getNumOfParams ()
	{
		return m_numOfParams;
	}

	public void setRetPtrLvl(int i){
		m_RetPtrLvl = i;
	}
	
	public int getRetPtrLvl(){
		return m_RetPtrLvl;
	}

//----------------------------------------------------------------
//	Instance variables.
//----------------------------------------------------------------
	private boolean		m_isReturned;
	private int			m_RetPtrLvl;
	private Type 		m_returnType;
	private Vector<STO> m_paramList;
	private int			m_numOfParams;
	private boolean m_ref;
}
