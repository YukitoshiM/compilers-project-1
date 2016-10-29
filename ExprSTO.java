//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class ExprSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	ExprSTO (String strName)
	{
		super (strName);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
		this.setIsAddressable(false);
		this.setIsModifiable(false);
	}

	public 
	ExprSTO (String strName, Type typ)
	{
		super (strName, typ);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
		this.setIsAddressable(false);
		this.setIsModifiable(false);
	}
	
	ExprSTO (String strName, Type typ, STO sto)
	{
		super (strName, typ);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
		this.sto = sto;
		this.setIsAddressable(false);
		this.setIsModifiable(false);
	}
	
	

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	isExpr ()
	{
		return	true;
	}
}
