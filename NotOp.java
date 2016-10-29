
class NotOp extends UnaryOp {

	public NotOp(String strName) {
		super(strName);
		// TODO Auto-generated constructor stub
	}
	
		public STO checkOperands(STO r, Operator o,ErrorPrinter m_errors){
			/*checks if arguement is already error*/
			if(r.isError()){return r;}
			
			/*get types of args*/
	    	Type rType = r.getType();
	    	
	    	/*checks args are NumericTypes or sub class the NumericTypes*/ 
	    	if((rType instanceof BoolType)){
	    		//
	    		double d;
    			if (r instanceof ConstSTO) {
    				boolean constVal = (r).getBoolValue();
    				if(constVal)
    					d = 0;
    				else
    					d = 1;
    				return new ConstSTO ("const not",r.getType(),d);
    			}
	    		return new ExprSTO("",new BoolType("bool",1));
	    	}
	    	else {
	    		// error
	    		STO boolClass = new ConstSTO("bool",new BoolType("bool", 3));
	  			m_errors.print (Formatter.toString(ErrorMsg.error1u_Expr, r.getType().getName(), o.getName(), boolClass.getType().getName()));
	    		return new ErrorSTO("Boolean");
	    	}
	    }

}
