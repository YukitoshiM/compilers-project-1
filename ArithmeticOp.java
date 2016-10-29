
class ArithmeticOp extends BinaryOp {

	public ArithmeticOp(String strName) {
		super(strName);
		// TODO Auto-generated constructor stub
	}
    
	public STO checkOperands(STO l, STO r, Operator o,ErrorPrinter m_errors){
		/*checks if arguement is already error*/
		if(l.isError()){return l;}
		if(r.isError()){return r;}
		
		/*get types of args*/
        Type lType = l.getType();
    	Type rType = r.getType();
    	
    	if(l.getType() instanceof ArrayType) {
    		lType = ((ArrayType)l.getType()).getSubType();
    	}
    	if(r.getType() instanceof ArrayType) {
    		lType = ((ArrayType)r.getType()).getSubType();
    	}
    	//double constVal;
    	/*checks args are NumericTypes or sub class the NumericTypes*/
    	if (!(lType instanceof NumericType) || !(rType instanceof NumericType)) {
    		// error
    	  if(!(l.getType() instanceof NumericType))
  			  m_errors.print (Formatter.toString(ErrorMsg.error1n_Expr, l.getType().getName(), o.getName()));
  		  else
  			  m_errors.print (Formatter.toString(ErrorMsg.error1n_Expr, r.getType().getName(), o.getName()));
    	  return new ErrorSTO("Numeric");
    	} 
    	else if (lType instanceof IntType && rType instanceof IntType) { 
    		//I am not sure what string input supposed to be
    		if((l instanceof ConstSTO) && (r instanceof ConstSTO)) {
    			if (o instanceof AddOp) {
    				int constVal = ((ConstSTO)l).getIntValue() + ((ConstSTO)r).getIntValue();
    				return new ConstSTO ("const int",l.getType(),(double)constVal);
    			}
    			else if(o instanceof MinusOp) {
    				int constVal = ((ConstSTO)l).getIntValue() - ((ConstSTO)r).getIntValue();
    				return new ConstSTO ("const int",l.getType(),(double)constVal);
    			}
    			else if(o instanceof MulOp) {
    				int constVal = ((ConstSTO)l).getIntValue() * ((ConstSTO)r).getIntValue();
    				return new ConstSTO ("const int",l.getType(),(double)constVal);
    			}
    			else if(o instanceof DivOp) {
    				if(((ConstSTO)r).getIntValue()==0){
    					m_errors.print (ErrorMsg.error8_Arithmetic);
    					return new ErrorSTO("divBy0");
    				}
    				int constVal = ((ConstSTO)l).getIntValue() / ((ConstSTO)r).getIntValue();
    				return new ConstSTO ("const int",l.getType(),(double)constVal);
    			}
    			else if(o instanceof ModOp) {
    				if(((ConstSTO)r).getIntValue()==0){
    					m_errors.print (Formatter.toString(ErrorMsg.error8b_CompileTime,r.getName()));
    					return new ErrorSTO("divBy0");
    				}
    				int constVal = ((ConstSTO)l).getIntValue() % ((ConstSTO)r).getIntValue();
    				return new ConstSTO ("const int",l.getType(),(double)constVal);
    			}
    		}
    		return new ExprSTO( "int", new IntType("int",1));
    	} 
    	else {
    		FloatType ftyp = new FloatType("float",4); 
    		if((l instanceof ConstSTO) && (r instanceof ConstSTO)) {
    			if (o instanceof AddOp) {
    				float constVal = ((ConstSTO)l).getFloatValue() + ((ConstSTO)r).getFloatValue();
    				return new ConstSTO ("const int",ftyp,(double)constVal);
    			}
    			else if(o instanceof MinusOp) {
    				float constVal = ((ConstSTO)l).getFloatValue() - ((ConstSTO)r).getFloatValue();
    				return new ConstSTO ("const int",ftyp,(double)constVal);
    			}
    			else if(o instanceof MulOp) {
    				float constVal = ((ConstSTO)l).getFloatValue() * ((ConstSTO)r).getFloatValue();
    				return new ConstSTO ("const int",ftyp,(double)constVal);
    			}
    			else if(o instanceof DivOp) {
    				if(((ConstSTO)r).getIntValue()==0){
    					m_errors.print (ErrorMsg.error8_Arithmetic);
    					return new ErrorSTO("divBy0");
    				}
    				float constVal = ((ConstSTO)l).getFloatValue() / ((ConstSTO)r).getFloatValue();
    				return new ConstSTO ("const int",ftyp,(double)constVal);
    			}
    			else if(o instanceof ModOp) {
    				if(((ConstSTO)r).getIntValue()==0){
    					m_errors.print (Formatter.toString(ErrorMsg.error8b_CompileTime,r.getName()));
    					return new ErrorSTO("divBy0");
    				}
    				float constVal = ((ConstSTO)l).getFloatValue() % ((ConstSTO)r).getFloatValue();
    				return new ConstSTO ("const int",ftyp,(double)constVal);
    			}
    		}
    		return new ExprSTO("float",new FloatType("float",1));
    	}
    }
	
}
