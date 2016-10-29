
abstract class ComparisonOp extends BinaryOp {

	public ComparisonOp(String strName) {
		super(strName);
		// TODO Auto-generated constructor stub
	}
	
	public STO checkOperands(STO l, STO r, Operator o,ErrorPrinter m_errors){
		/*checks if arguement is already error*/
		if(l.isError()){return l;}
		if(r.isError()){return r;}
		System.out.println(l);
		/*get types of args*/
        Type lType = l.getType();
    	Type rType = r.getType();
    	
    	/*checks args are NumericTypes or sub class the NumericTypes*/
    	if (!(lType instanceof NumericType) || !(rType instanceof NumericType)) {
    		// error
    	  if(!(l.getType() instanceof NumericType))
  			  m_errors.print (Formatter.toString(ErrorMsg.error1n_Expr, l.getType().getName(), o.getName()));
  		  else
  			  m_errors.print (Formatter.toString(ErrorMsg.error1n_Expr, r.getType().getName(), o.getName()));
    	  return new ErrorSTO("Numeric");
    	} 
    	else {
    		
    		if((l instanceof ConstSTO) && (r instanceof ConstSTO)) {
    			double d;
    			BoolType b = new BoolType("bool",1); 
    			if (o instanceof LessThanOp) {
    				boolean constVal = (l).getFloatValue() < (r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const less",b,d);
    			}
    			else if(o instanceof GreaterThanOp) {
    				boolean constVal = ((ConstSTO)l).getFloatValue() > ((ConstSTO)r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const greater",b,d);
    			}
    			else if(o instanceof LessThanEqOp) {
    				boolean constVal = ((ConstSTO)l).getFloatValue() <= ((ConstSTO)r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const lessEq",b,d);
    			}
    			else if(o instanceof GreaterThanEqOp) {
    				
    				boolean constVal = ((ConstSTO)l).getFloatValue() >= ((ConstSTO)r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const greaterEq",b,d);
    			}
    			else if(o instanceof EqualOp) {
    				
    				boolean constVal = ((ConstSTO)l).getFloatValue() == ((ConstSTO)r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const Eq",b,d);
    			}
    			else if(o instanceof NotEqualOp) {
    				
    				boolean constVal = ((ConstSTO)l).getFloatValue() != ((ConstSTO)r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const NotEq",b,d);
    			}
    		}
    		return new ExprSTO("",new BoolType("bool",1));
    	}
    }

}
