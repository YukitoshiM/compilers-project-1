import java.util.Vector;


class EqualOp extends ComparisonOp {

	public EqualOp(String strName) {
		super(strName);
		// TODO Auto-generated constructor stub
	}
	
	public STO checkOperands(STO l, STO r, Operator o,ErrorPrinter m_errors){
		/*checks if arguement is already error*/
		if(l.isError()){return l;}
		if(r.isError()){return r;}
		System.out.println(l.getType());
		/*get types of args*/
        Type lType = l.getType();
    	Type rType = r.getType();
    	
    	if(l.getType() instanceof NullPointerType && l.getType() instanceof NullPointerType){
    		return new ConstSTO ("const Eq",new BoolType("bool",1),1.0);
    	}
    	else if(l.getType() instanceof NullPointerType || l.getType() instanceof NullPointerType){
    		return new ConstSTO ("const Eq",new BoolType("bool",1),0.0);
    	}
    	
    	
    		if((l instanceof ConstSTO) && (r instanceof ConstSTO)) {
    			double d;
    			BoolType b = new BoolType("bool",1); 
    			if(o instanceof EqualOp) {
    				
    				boolean constVal = ((ConstSTO)l).getFloatValue() == ((ConstSTO)r).getFloatValue();
    				if(constVal)
    					d = 1;
    				else
    					d = 0;
    				return new ConstSTO ("const Eq",b,d);
    			}
    		}
    		
    	
    	
    	int numStar = 0;
		String strL = "";
		Vector<STO> lVec;
		String strR = "";
		Vector<STO> rVec;
		
    	//Start writing error message on rho
    	if(lType.isFuncPtr() && !l.isFunc()){
			if(!((FunctionPointerType)lType).getReturnType().isPtr())
				strL = "funcptr : " + (((FunctionPointerType)lType).getReturnType()).getName();
			else if(((FunctionPointerType)lType).getReturnType().isPtr()){
				strL = "funcptr : " + ((PointerType)((FunctionPointerType)lType).getReturnType()).getBase().getName();
				numStar = ((PointerType)(((FunctionPointerType)lType).getReturnType())).getPtrLvl();
				while(numStar != 0){
					strL += "*";
					--numStar;
				}
			}

			if(((FunctionPointerType)l.getType()).getIsRef()){
				strL+=" &";
			}
			
			strL += " (" ;
				
			lVec = ((FunctionPointerType)lType).getParams();
			
			for(int j = 0; j < lVec.size();++j){
				if(!(lVec.elementAt(j).getType() instanceof PointerType))
					strL += lVec.elementAt(j).getType().getName();
				else{
					strL += ((PointerType)lVec.elementAt(j).getType()).getBase().getName();
					
					numStar = ((PointerType) (lVec.elementAt(j).getType())).getPtrLvl();
					
					while(numStar != 0){
						strL += "*";
						--numStar;
					}
					
				}
				if(((VarSTO) lVec.elementAt(j)).getRef()) 
					strL += " &" + lVec.elementAt(j).getName();
				else
					strL += " " + lVec.elementAt(j).getName();
				
				
				if((j + 1 ) != lVec.size()){
					strL += ", ";
				}
			}
			strL += ")";
			
			if(!lType.getName().equals("funcptr"))
				strR = lType.getName();
    	}
    	if(rType.isFuncPtr() && !r.isFunc()){
			if(!((FunctionPointerType)rType).getReturnType().isPtr())
				strR = "funcptr : " + (((FunctionPointerType)rType).getReturnType()).getName();
			else if(((FunctionPointerType)rType).getReturnType().isPtr()){
				strR = "funcptr : " + ((PointerType)((FunctionPointerType)rType).getReturnType()).getBase().getName();
				numStar = ((PointerType)(((FunctionPointerType)rType).getReturnType())).getPtrLvl();
				while(numStar != 0){
					strR += "*";
					--numStar;
				}
			}

			if(((FunctionPointerType)r.getType()).getIsRef()){
				strR+=" &";
			}
			
			strR += " (" ;
				
			rVec = ((FunctionPointerType)rType).getParams();
			
			for(int j = 0; j < rVec.size();++j){
				if(!(rVec.elementAt(j).getType() instanceof PointerType))
					strR += rVec.elementAt(j).getType().getName();
				else{
					strR += ((PointerType)rVec.elementAt(j).getType()).getBase().getName();
					
					numStar = ((PointerType) (rVec.elementAt(j).getType())).getPtrLvl();
					
					while(numStar != 0){
						strR += "*";
						--numStar;
					}
					
				}
				if(((VarSTO) rVec.elementAt(j)).getRef()) 
					strR += " &" + rVec.elementAt(j).getName();
				else
					strR += " " + rVec.elementAt(j).getName();
				
				
				if((j + 1 ) != rVec.size()){
					strR += ", ";
				}
			}
			strR += ")";
			if(!rType.getName().equals("funcptr"))
				strR = rType.getName();
    	}
    	
    	/*checks args are NumericTypes or sub class the NumericTypes*/ 
    	if( ((lType instanceof NumericType) && (rType instanceof NumericType)) || 
    			((lType instanceof BoolType) && (rType instanceof BoolType)) ||
    				((lType instanceof PointerType) && (rType instanceof PointerType)) || 
    					((lType instanceof PointerType) && (rType instanceof NullPointerType)) || 
    						((lType instanceof NullPointerType) && (rType instanceof PointerType)) ||
    						((lType instanceof FunctionPointerType) && (rType instanceof NullPointerType))||
    						((lType instanceof NullPointerType) && (rType instanceof FunctionPointerType)) ||
    						((lType instanceof FunctionPointerType) && (rType instanceof FunctionPointerType)) ||
    						((l instanceof FuncSTO) && (rType instanceof FunctionPointerType))||
    						((r instanceof FuncSTO) && (lType instanceof FunctionPointerType)) ||
    						((r instanceof FuncSTO) && (l instanceof FuncSTO))
    						) {
    		return new ExprSTO("Equal Op",new BoolType("bool",1));
    	} 
    	else if (((lType instanceof NullPointerType) && (rType instanceof NullPointerType)) ) {
    		return new ExprSTO("Equal Op",new BoolType("bool",1));
    	}
    	/*There was bug here which was fixed!!!!!*/
    	else if( ((lType instanceof PointerType) && !(rType instanceof PointerType)) &&
    			   !((lType instanceof FunctionPointerType)) && !((rType instanceof FunctionPointerType)))  {	
    		m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, o.getName(), l.getType().getName(),r.getType().getName()));
      	  	return new ErrorSTO("operands not of pointer types");
    	}
    	else if( (!(lType instanceof PointerType) && (rType instanceof PointerType)) &&
    			   !((lType instanceof FunctionPointerType)) && !((rType instanceof FunctionPointerType)))  {
    		m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, o.getName(), l.getType().getName(),r.getType().getName()));
  	  		return new ErrorSTO("operands not of pointer types");
    	}
    	else if( ((lType instanceof FunctionPointerType))){// || (rType instanceof FunctionPointerType)) ){
    		if(!l.isFunc())
    			m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, o.getName(), strL, r.getType().getName()));
    		else if(l.isFunc())
    			m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, o.getName(), l.getName(), r.getType().getName()));
  	  		return new ErrorSTO("operands not of functionptr types");
    	}
    	else if( ((rType instanceof FunctionPointerType))){// || (rType instanceof FunctionPointerType)) ){
    		if(!r.isFunc())
    			m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, o.getName(), l.getType().getName(),strR));
    		else if(r.isFunc())
    			m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, o.getName(), l.getType().getName(),r.getName()));
    		
  	  		return new ErrorSTO("operands not of functionptr types");
    	}
    	else {
    		m_errors.print (Formatter.toString(ErrorMsg.error1b_Expr, l.getType().getName(), o.getName(),r.getType().getName()));
    		return new ErrorSTO("Numeric");
    	}
    }

}
