//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class ConstSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	ConstSTO (String strName)
	{
		super (strName);
		//super.setValue(-1.0); // fix this
		this.setIsModifiable(false);
		this.setLit(false);
		this.setNeg(false);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}

	public 
	ConstSTO (String strName, Type typ)
	{
		super (strName, typ);
		//super.setValue(-1.0); // fix this
		this.setIsModifiable(false);
		this.setLit(false);
		this.setNeg(false);
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	ConstSTO (String strName, Type typ, double value)
	{
		
		super (strName, typ);

		this.setIsModifiable(false);
		super.setValue(value); 
		this.setLit(false);
		this.setNeg(false);// fix this
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	ConstSTO (String strName, Type typ, boolean isStatic)
	{
		
		super (strName, typ);

		this.setIsModifiable(false);
		super.setIsStatic(isStatic);
		this.setLit(false);
		this.setNeg(false);// fix this
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	ConstSTO (String strName, Type typ, boolean isStatic,double value)
	{
		
		super (strName, typ);

		this.setIsModifiable(false);
		super.setValue(value); 
		super.setIsStatic(isStatic);
		this.setLit(false);
		this.setNeg(false);// fix this
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	ConstSTO (String strName, Type typ, double value, boolean b)
	{
		super (strName, typ);
		this.setIsModifiable(false);
		super.setValue(value);
		this.setLit(b);
		this.setNeg(false);
		// fix this
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}
	
	public 
	ConstSTO (String strName, Type typ, double value, boolean b, boolean neg)
	{
		super (strName, typ);
		this.setIsModifiable(false);
		super.setValue(value);
		this.setLit(b);
		this.setNeg(neg);
		// fix this
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}

	public 
	ConstSTO (String strName, boolean isstrlt)
	{
		super (strName);
		this.setIsModifiable(false);
		this.setIsAddressable(false);
		this.setLit(true);
		this.setisStrLit(true);
		//super.setValue(-1.0);
	}
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	isConst () 
	{
		return true;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	




//----------------------------------------------------------------
//	Constants have a value, so you should store them here.
//	Note: We suggest using Java's Double class, which can hold
//	floats and ints. You can then do .floatValue() or 
//	.intValue() to get the corresponding value based on the
//	type. Booleans/Ptrs can easily be handled by ints.
//	Feel free to change this if you don't like it!
//----------------------------------------------------------------
        //private Double		m_value
}
