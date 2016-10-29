import java.util.Vector;


class FunctionPointerType extends Type
{
	public FunctionPointerType(String strName, int size)
	{
		super(strName,size);
	}
	
	public FunctionPointerType(String strName, Type typ, boolean ref, Vector<STO> params)
	{	
		super(strName,4);
		this.setReturnType(typ);
		this.setIsRef(ref);
		this.setParams(params);
	}
	
	
	public FunctionPointerType(String strName, Type typ, boolean ref) {
		// TODO Auto-generated constructor stub
		super(strName,4);
		this.setReturnType(typ);
		this.setIsRef(ref);
	}

	public void setReturnType( Type typ ) {
		this.m_returnType = typ;
	}
	public Type getReturnType() {
		return m_returnType;
	}
	public void setIsRef( boolean isref ) {
		this.m_isRef = isref;
	}
	public boolean getIsRef() {
		return m_isRef;
	}
	public void setParams( Vector<STO> params ) {
		this.m_params = params;
	}
	public Vector<STO> getParams() {
		return m_params;
	}
	public void setFunc(STO sto){
		func = sto;
	}
	public STO getFunc(){
		return func;
	}
	
	
	
	
	public boolean isFuncPtr() {return true;}
	
	public boolean isAddressable(Type t) {
		
		return true;
	}
	public boolean isAssignable(Type t) {
		Vector<STO> tVec;
		if(t instanceof FunctionPointerType){
			if(this.m_returnType.isEquivalent(((FunctionPointerType)t).getReturnType())){
				//if(m_isRef == ((FunctionPointerType)t).getIsRef()){
					tVec = ((FunctionPointerType)t).getParams();
					if(m_params.size() == tVec.size()){
						for(int i=0;i<m_params.size();i++){
							//System.out.println(m_params.elementAt(i).getType()+" "+tVec.elementAt(i).getType());
							if(m_params.elementAt(i).getType().isEquivalent(tVec.elementAt(i).getType())){
								
								if(m_params.elementAt(i).getType() instanceof PointerType &&
									tVec.elementAt(i).getType()	instanceof PointerType){
									
									if(((PointerType)m_params.elementAt(i).getType()).getPtrLvl() != ((PointerType)tVec.elementAt(i).getType()).getPtrLvl()){
										
										return false;
									}
									if(!((PointerType)m_params.elementAt(i).getType()).getBase().isEquivalent((((PointerType)tVec.elementAt(i).getType()).getBase()))){
										
										return false;
									}
								}
								else{
									return false;
								}
							}
						}
						return true;
					//}
				}
			}
		}
		return false;
	}
	public boolean isEquivalent(Type t) {
		Vector<STO> tVec;
		if(t instanceof FunctionPointerType){
			if(this.m_returnType.isEquivalent(((FunctionPointerType)t).getReturnType())){
				if(m_isRef == ((FunctionPointerType)t).getIsRef()){
					tVec = ((FunctionPointerType)t).getParams();
					if(m_params.size() == tVec.size()){
						for(int i=0;i<m_params.size();i++){
							if(!m_params.elementAt(i).getType().isEquivalent(tVec.elementAt(i).getType())){
								return false;
							}
						}
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private Type m_returnType;
	private boolean m_isRef;
	private Vector<STO> m_params;
	private STO func;
}
