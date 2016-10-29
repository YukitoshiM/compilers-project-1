
class UnaryOp extends Operator {

	public UnaryOp(String strName) {
		super(strName);
	}

	public STO checkOperands(STO l, STO r, Operator o, ErrorPrinter m_errors) {return l;}
	public STO checkOperands(STO l, Operator o, ErrorPrinter m_errors) {
		
		//System.out.println("In checkOperands of UnaryOp");
		if(!(l.getType() instanceof NumericType) && !(l.getType() instanceof PointerType)) {
			m_errors.print (Formatter.toString(ErrorMsg.error2_Type, l.getType().getName(), o.getName()));
			return new ErrorSTO("Numeric");
		}
		else if (!(l.isModLValue())) {
			m_errors.print (Formatter.toString(ErrorMsg.error2_Lval, o.getName()));
			return new ErrorSTO("not a modifiable l-val");
		}
		else {
			if(l.getType() instanceof IntType)
				return new ExprSTO( "int unary op", new IntType("",4));
			else if (l.getType() instanceof FloatType)
				return new ExprSTO( "float unary op", new FloatType("",4));
			else 
				return new ExprSTO( "pointer unary op", new PointerType("",4));
		}
	}
}
