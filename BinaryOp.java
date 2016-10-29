
class BinaryOp extends Operator {

	public BinaryOp(String strName) {
		super(strName);
		// TODO Auto-generated constructor stub
	}

	public STO checkOperands(STO l, Operator o, ErrorPrinter m_errors){return l;}
	public STO checkOperands(STO l, STO r, Operator o, ErrorPrinter m_errors){return l;} 

}
