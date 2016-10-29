
class BooleanOp extends BinaryOp {

	public BooleanOp(String strName) {
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
    	
    	/*checks args are NumericTypes or sub class the NumericTypes*/ 
    	/* Bug fixed, I for got to negate (lType instanceof BoolType)
    	 * Adn Check should be or!!!!!
    	 */
    	if(((lType instanceof BoolType) && (rType instanceof BoolType))){
    		if((l instanceof ConstSTO) && (r instanceof ConstSTO)) {
    			double d;
    			if (o instanceof AndOp) {
    				boolean constVal = (l).getBoolValue() && (r).getBoolValue();
    			
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    			
    				return new ConstSTO ("const and",l.getType(),d);
    			}
    			else if(o instanceof OrOp) {
    				boolean constVal = (l).getBoolValue() || (r).getBoolValue();
    			
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				
    				return new ConstSTO ("const or",l.getType(),d);
    			}
    			
    		return new ExprSTO("",new BoolType("bool",1));
    	}
    	/*else {
    		// error
    			
    		STO boolClass = new ConstSTO("bool",new BoolType("bool", 3));
    		if(!(l.getType() instanceof BoolType))
  			    m_errors.print (Formatter.toString(ErrorMsg.error1w_Expr, l.getType().getName(), o.getName(), boolClass.getType().getName()));   		
  			else
  			    m_errors.print (Formatter.toString(ErrorMsg.error1w_Expr, r.getType().getName(), o.getName(), boolClass.getType().getName()));
  			return new ErrorSTO("Boolean");
    	}*/
    }
    	//dummy
    	return new ExprSTO("",new BoolType("bool",1));
    	
	}
}
