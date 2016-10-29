//---------------------------------------------------------------------
//
//---------------------------------------------------------------------
import java.util.Vector;
abstract class STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	STO (String strName)
	{
		this(strName, null);
		setOffset(-1);
		setIsStatic(false);
		setGlobal(true);
		setIsFuncReturn(false);
		//m_value = -1.0;
	}

	public 
	STO (String strName, Type typ)
	{
		setName(strName);
		setType(typ);
		setIsAddressable(true);
		setIsModifiable(false);
		setOffset(-1);
		setIsStatic(false);
		setGlobal(true);
		setIsFuncReturn(false);
		//m_value = -1.0;
	}
	
	public 
	STO (String strName, Type typ, int offset, boolean isSatic)
	{
		setName(strName);
		setType(typ);
		setOffset(offset);
		setIsAddressable(true);
		setIsModifiable(false);
		setIsStatic(isSatic);
		setGlobal(true);
		setIsFuncReturn(false);
		//m_value = -1.0;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String
	getName ()
	{
		return m_strName;
	}

	public void
	setName (String str)
	{
		m_strName = str;
	}
	
	public String
	getId ()
	{
		return m_id;
	}

	public void
	setId (String str)
	{
		m_id = str;
	}
	
	public int
	getOffset ()
	{
		return m_offset;
	}

	public void
	setOffset (int offset)
	{
		m_offset = offset;
	}

	public void setisStrLit(boolean b){
		m_isstrlit = b;
	}
	
	public boolean isStrLit(){
		return m_isstrlit;
	}
	
	public void setIsStatic(boolean b){
		m_isStatic = b;
	}
	
	public boolean getIsStatic(){
		return m_isStatic;
	}
	
	public void
	setValue (double val) 
	{
		m_value = new Double(val);
	}

	public Double
	getValue () 
	{
		return m_value;
	}

	public int
	getIntValue () 
	{
		return m_value.intValue();
	}

	public float
	getFloatValue () 
	{
		return m_value.floatValue();
	}

	public boolean
	getBoolValue () 
	{
		return (m_value.intValue() != 0);
	}
	public boolean
	getGlobal ()
	{
		return	m_global;
	}

	public void
	setGlobal (boolean x)
	{
		m_global = x;
	}
	
	public boolean
	getIsFuncReturn ()
	{
		return	m_isFuncReturn;
	}

	public void
	setIsFuncReturn (boolean x)
	{
		m_isFuncReturn = x;
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Type
	getType ()
	{
		return	m_type;
	}

	public void
	setType (Type type)
	{
		m_type = type;
	}
	
	public STO getSTO(){
		return this.sto;
	}

	
	public void setNeg(boolean neg){
		//this.setName("-"+this.getName());
		m_isneg = neg;
		if(m_isneg == true && this.getValue() != null) {
			this.setValue(-this.getValue());
		}
	}
	
	public boolean getNeg(){
		return m_isneg;
	}
	
	public void setLit(boolean b){
		m_isLit = b;
	}
	
	public boolean getLit(){
		return m_isLit;
	}
	
	//----------------------------------------------------------------
	// Addressable refers to if the object has an address. Variables
	// and declared constants have an address, whereas results from 
	// expression like (x + y) and literal constants like 77 do not 
	// have an address.
	//----------------------------------------------------------------
	public boolean
	getIsAddressable ()
	{
		return	m_isAddressable;
	}

	public void
	setIsAddressable (boolean addressable)
	{
		m_isAddressable = addressable;
	}

	//----------------------------------------------------------------
	// You shouldn't need to use these two routines directly
	//----------------------------------------------------------------
	public boolean
	getIsModifiable ()
	{
		return	m_isModifiable;
	}

	public void
	setIsModifiable (boolean modifiable)
	{
		m_isModifiable = modifiable;
	}


	//----------------------------------------------------------------
	// A modifiable L-value is an object that is both addressable and
	// modifiable. Objects like constants are not modifiable, so they 
	// are not modifiable L-values.
	//----------------------------------------------------------------
	public boolean
	isModLValue ()
	{
		return	getIsModifiable() && getIsAddressable();
	}

	private void
	setIsModLValue (boolean m)
	{
		setIsModifiable(m);
		setIsAddressable(m);
	}
	
	

    //public double getValue(){return (Double)null;};
	//----------------------------------------------------------------
	//	It will be helpful to ask a STO what specific STO it is.
	//	The Java operator instanceof will do this, but these methods 
	//	will allow more flexibility (ErrorSTO is an example of the
	//	flexibility needed).
	//----------------------------------------------------------------
	public boolean	isVar () 	{ return false; }
	public boolean	isConst ()	{ return false; }
	public boolean	isExpr ()	{ return false; }
	public boolean	isFunc () 	{ return false; }
	public boolean	isTypedef () 	{ return false; }
	public boolean	isError () 	{ return false; }


	//----------------------------------------------------------------
	// 
	//----------------------------------------------------------------
	private String  	m_strName;
	private Type		m_type;
	private boolean		m_isAddressable;
	private boolean		m_isModifiable;
	private String		m_id;
	private int			m_offset;
	private boolean		m_isstrlit;
	private boolean		m_isStatic;
	private Double		m_value;
	public int m_ifCounter = -1;
	public Vector<Integer> vecIfCount = new Vector<Integer>();
	private boolean		m_global;
	private boolean		m_isFuncReturn;
	public STO sto;
    private boolean 	m_isneg;
    private boolean     m_isLit;
}
