import java.util.Vector;

//---------------------------------------------------------------------
// This is the top of the Type hierarchy. You most likely will need to
// create sub-classes (since this one is abstract) that handle specific
// types, such as IntType, FloatType, ArrayType, etc.
//---------------------------------------------------------------------


abstract class Type
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	Type (String strName, int size)
	{
		setName(strName);
		setSize(size);
		m_subType = null;
	}
	
	public 
	Type (String strName, int size, Type type)
	{
		setName(strName);
		setSize(size);
		m_subType = type;
	}
	


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String
	getName ()
	{
		return m_typeName;
	}

	public void
	setName (String str)
	{
		m_typeName = str;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public int
	getSize ()
	{
		return m_size;
	}

	public void
	setSize (int size)
	{
		m_size = size;
	}
	
	public void push(int i){
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
	}

	public void setSubType(Type subtype)
	{
		m_subType = subtype;
		if(subtype != null && !(subtype instanceof StructType))
			this.setSize(this.getVec().lastElement()*subtype.getSize());		
	}
	
	public Type getSubType()
	{
		return m_subType;
	}
	
	public void setStruct(Vector<STO> fields) {
		vector = fields;
	}
	
	public Vector<STO> getStruct() {
		return vector;
	}
	
	public boolean isAssignable(Type t) {return false;}
	public boolean isEquivalent(Type t) {return false;}
	//public boolean isAddressable() {return false;}
	//public boolean isModifiable() {return false;}
	//----------------------------------------------------------------
	//	It will be helpful to ask a Type what specific Type it is.
	//	The Java operator instanceof will do this, but you may
	//	also want to implement methods like isNumeric(), isInt(),
	//	etc. Below is an example of isInt(). Feel free to
	//	change this around.
	//----------------------------------------------------------------
	public boolean	isInt ()	{ return false; }
	public boolean	isFloat()	{ return false; }
	public boolean	isNumeric() { return false; }
	public boolean	isBool()	{ return false; }
	public boolean	isBasic()	{ return false; }
	public boolean	isVoid()	{ return false; }
	public boolean	isComposite()	{ return false; }
	public boolean	isArray()	{ return false; }
	public boolean	isStruct()	{ return false; }
	public boolean	isPtrGrp()	{ return false; }
	public boolean	isPtr()		{ return false; }
	public boolean	isFuncPtr()	{ return false; }
	public boolean	isNulPtr()	{ return false; }
	public boolean	isDref()	{ return false; }
	
	//----------------------------------------------------------------
	//	Name of the Type (e.g., int, bool, or some typedef
	//----------------------------------------------------------------
	private String  	m_typeName;
	private int		m_size;
	private Type m_subType;
	private Vector<Integer> vec = new Vector<Integer>();
	public Vector<STO> vector = new Vector<STO>();
}
