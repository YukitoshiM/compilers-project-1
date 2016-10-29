
abstract class Operator {
	//----------------------------------------------------------------
		//
		//----------------------------------------------------------------
		public 
		Operator (String strName)
		{
			setName(strName);
		}


		//----------------------------------------------------------------
		//
		//----------------------------------------------------------------
		public String
		getName ()
		{
			return m_typeName;
		}

		private void
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

		private void
		setSize (int size)
		{
			m_size = size;
		}

		//----------------------------------------------------------------
		//	It will be helpful to ask a Type what specific Type it is.
		//	The Java operator instanceof will do this, but you may
		//	also want to implement methods like isNumeric(), isInt(),
		//	etc. Below is an example of isInt(). Feel free to
		//	change this around.
		//----------------------------------------------------------------
		/*public boolean	unaryOp ()	{ return false; }
		public boolean	BinaryOp()	{ return false; }*/
		/*public boolean	isNumeric() { return false; }
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
		*/
		//----------------------------------------------------------------
		//	Name of the Type (e.g., int, bool, or some typedef
		//----------------------------------------------------------------
		public abstract STO checkOperands(STO l, Operator o, ErrorPrinter m_errors);
		public abstract STO checkOperands(STO l, STO r, Operator o, ErrorPrinter m_errors);
		private String  	m_typeName;
		private int		m_size;
}
