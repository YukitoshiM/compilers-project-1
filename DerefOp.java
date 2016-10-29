
public class DerefOp extends UnaryOp {

	public DerefOp(String name) {
		super(name);
	}

	public boolean	isDref()	{ return true; }
	
	public STO checkOperands(STO r, Operator o,ErrorPrinter m_errors){
		
		/*checks if arguement is already error*/
		if(r.isError()){return r;}
		
		/*get types of args*/
    	Type rType = r.getType();
    	
    	if(rType instanceof PointerGroupType )
    		rType = ((PointerGroupType) rType).getPtr();
    	
    //	if(rType instanceof PointerType){
    	//      PointerType t = (PointerType) ((PointerType)rType).getSubType();
    	
    	//System.out.println(((PointerType)rType));
    	
    	/*checks args are NumericTypes or sub class the NumericTypes*/ 
    	if(!(rType instanceof PointerType)){
    		
    		m_errors.print (Formatter.toString(ErrorMsg.error15_Receiver, r.getType().getName()));
    		return new ErrorSTO("Pointer");  		
    	}
    	
    	
    	VarSTO sto = new VarSTO("*"+r.getName(),((PointerType)rType).getSubType());
    	//sto.setDeref(((VarSTO) r).getDeref()+1);
    	return sto;
    	
    }
}
