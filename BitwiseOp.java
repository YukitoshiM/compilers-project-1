
class BitwiseOp extends BinaryOp {

	public BitwiseOp(String strName) {
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
    	
    	//System.out.println(lType);
    	//System.out.println(rType);
    	
    	/*checks args are NumericTypes or sub class the NumericTypes*/
    	
    	if (!(lType instanceof IntType) || !(rType instanceof IntType)) {
    		// error
    		STO intClass = new ConstSTO("int",new IntType("int", 3));
    		
    	  if(!(l.getType() instanceof IntType))
  			  m_errors.print (Formatter.toString(ErrorMsg.error1w_Expr, l.getType().getName(), o.getName(), intClass.getType().getName()));
  		  else
  			  m_errors.print (Formatter.toString(ErrorMsg.error1w_Expr, r.getType().getName(), o.getName(), intClass.getType().getName()));
    	  return new ErrorSTO("Numeric");
    	} 
    	else if ((l instanceof ConstSTO) && (r instanceof ConstSTO)) {
    		if ( o instanceof BwAndOp) {
    			double value = ((ConstSTO) l).getIntValue() & ((ConstSTO) r).getIntValue();
    			return new ConstSTO("bitwise & op",new IntType("int",4),value);
    		}
    		else if ( o instanceof BwOrOp) {
    			double value = ((ConstSTO) l).getIntValue() | ((ConstSTO) r).getIntValue();
    			return new ConstSTO("bitwise | op",new IntType("int",4),value);
    		}
    		else {
    			double value = ((ConstSTO) l).getIntValue() ^ ((ConstSTO) r).getIntValue();
    			return new ConstSTO("bitwise ^ op",new IntType("int",4),value);
    		}
    	}
    	else{ 
    		//I am not sure what string input supposed to be
    		return new ExprSTO( "", new IntType("int",1));
    	} 
    	
    }
}
