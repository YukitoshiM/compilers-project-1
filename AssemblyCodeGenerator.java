import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;



class AssemblyCodeGenerator {
    // 1
    private int indent_level = 0;
    
    // 2
    private static final String ERROR_IO_CLOSE = 
        "Unable to close fileWriter";
    private static final String ERROR_IO_CONSTRUCT = 
        "Unable to construct FileWriter for file %s";
    private static final String ERROR_IO_WRITE = 
        "Unable to write to fileWriter";
    private Vector<Tuple> vecPair = new Vector<Tuple>();
    private String couttmp = "";

    // 3
    private FileWriter fileWriter;
    
    // 4
    private static final String FILE_HEADER = 
        "/*\n" +
        " * Generated %s\n" + 
        " */\n\n";
    
    private static final String LOCAL = "%l";
    private static final String LOCAL0 = "%l0";
    private static final String LOCAL1 = "%l1";
    private static final String LOCAL5 = "%l5";
    
    private static final String LOCAL7 = "%l7";
    private static final String LOCAL2 = "%l2";
    private static final String FLOAT0 = "%f0";
    private static final String FLOAT1 = "%f1";
    private static final String FLOAT2 = "%f2";
    private static final String GLOBAL = "%g";
    private static final String GLOBAL0 = "%g0";
    private static final String GLOBAL1 = "%g1";
    private static final String SP = "%sp";
    private static final String FP = "%fp";
    private static final String OUT0 = "%o0";
    private static final String OUT1 = "%o1";
    private static final String IN0 = "%i0";
    private static final String IN1 = "%i1";
    // 5
    private static final String SEPARATOR = "\t";
    private static final String NOP = "nop";
    
    // 6
    private static final String ANY_3 = "%s\t %s, %s, %s";
    private static final String SET_OP = "set";
    private static final String SET = SET_OP + "\t%s, %s";
    private static final String LOAD_OP = "ld";
    private static final String LOAD = LOAD_OP + "\t[%s], %s";
    private static final String SAVE_OP = "save";
    private static final String STORE_OP = "st";
    private static final String STORE = STORE_OP + "\t%s, [%s]";
    private static final String ADD_OP = "add";
    private static final String SUB_OP = "sub";
    private static final String AND_OP = "and";
    private static final String OR_OP = "or";
    private static final String XOR_OP = "xor";
    private static final String ADD = ADD_OP + "\t%s, %s, %s";
    private static final String SUB = SUB_OP + "\t%s, %s, %s";
    private static final String AND = AND_OP + "\t%s, %s, %s";
    private static final String OR = OR_OP + "\t%s, %s, %s";
    private static final String XOR = XOR_OP + "\t%s, %s, %s";
    private static final String CMP = "cmp\t%s, %s";
    private static final String BE = "be\t%s\n"+SEPARATOR+NOP;
    private static final String BNE = "bne\t%s\n"+SEPARATOR+NOP;
    private static final String BA = "ba\t%s\n"+SEPARATOR+NOP;
    private static final String STRLIT = "%s:\t.asciz\t\"%s\"";
    
    private static final String FITOS = "fitos\t%s, %s";
    private static final String FADDS = "fadds\t%s, %s, %s";
    private static final String FSUBS = "fsubs\t%s, %s, %s";
    private static final String FMULS = "fmuls\t%s, %s, %s";
    private static final String FDIVS = "fdivs\t%s, %s, %s";
    
    private static final String NEG = "neg\t%s, %s";
    private static final String FNEGS = "fnegs\t%s, %s";
    
    private static final String WORD = ".word";
    private static final String SINGLE = ".single";
    private static final String TWO_PARAM = "%s" + SEPARATOR + "%s, %s\n";
    private static final String DATA = ".section" + SEPARATOR + '"'+".data"+'"'+"\n";
    private static final String TEXT = ".section" + SEPARATOR + '"'+".text"+'"'+"\n";
    private static final String ALIGN = ".align\t4\n";
    private static final String GDECL = ".global\t%s";
    private static final String GFUNC = ".global\t%s\n";
    private static final String FUNCNAME = "%s:\n";
    private static final String SAVE_SP = SET_OP + SEPARATOR + "SAVE.%s, %s\n" + "\t"+SAVE_OP + " %s, %s, %s\n";
    private static final String PADGDECL = ", %s";
    private static final String GINIT = "\n%s:\t%s %s";
    
    private static final String NEW_LINE = "\n";
    private static final String TWO_NEW_LINE = "\n\n";
    private static final String RETRESTORE = "ret" + NEW_LINE + SEPARATOR + "restore";
    
    private static final String MUL = "mov %s, %%o0" + NEW_LINE + "\tmov %s, %%o1" + NEW_LINE+"\tcall .mul" + NEW_LINE + "\tnop";
    private static final String DIV = "mov %s, %%o0" + NEW_LINE + "\tmov %s, %%o1" + NEW_LINE+"\tcall .div" + NEW_LINE + "\tnop";
    private static final String REM = "mov %s, %%o0" + NEW_LINE + "\tmov %s, %%o1" + NEW_LINE+"\tcall .rem" + NEW_LINE + "\tnop";

    private static final String RODATA = ".section" + SEPARATOR + '"'+".rodata"+'"'+"\n";
    
    private static final String RD_INTFMT = "_intFmt:" + SEPARATOR + ".asciz \"%%d\"\n";
    private static final String RD_STRFMT = "_strFmt:" + SEPARATOR + ".asciz \"%%s\"\n";
    private static final String RD_BTRUE = "_boolT:" + SEPARATOR + SEPARATOR + ".asciz \"true\"\n";
    private static final String RD_BFALSE = "_boolF:" + SEPARATOR + SEPARATOR + ".asciz \"false\"\n";
    private static final String RD_ENDL = "_endl:" + SEPARATOR + SEPARATOR + ".asciz \"\\n\"\n";
    
    private static final String INTFMT = "_intFmt";
    private static final String STRFMT = "_strFmt";
    private static final String BTRUE = "_boolT";
    private static final String BFALSE = "_boolF";
    private static final String ENDL = "_endl";

    private static final String PRINTF = "call\tprintf\n" + SEPARATOR + "nop\n";
    private static final String PRINTFLOAT = "call\tprintFloat\n" + SEPARATOR + "nop\n";
    private static final String INPUTINT = "call\tinputInt\n" + SEPARATOR + "nop\n";
    private static final String INPUTFLOAT = "call\tinputFloat\n" + SEPARATOR + "nop\n";
    
    private static final String SAVE_MEMORY_SPACE = "SAVE.%s = -(92 + %s + 8) & -8\n";
    private static final String COMP = "mov\t1, %%l0"+NEW_LINE+ "\tcmp %s, %s" + NEW_LINE+"\t%s\t%s\n\tnop"+ NEW_LINE+"\tmov 0, %%l0"+NEW_LINE+"%s";
    private static final String FLOATCOMP = "mov\t1, %%l0"+NEW_LINE+ "\tfcmps %s, %s\n\tnop" + NEW_LINE+"\t%s\t%s\n\tnop"+ NEW_LINE+"\tmov 0, %%l0"+NEW_LINE+"%s";
    private static final String ANDAND = "cmp\t%s, %%g0"+NEW_LINE+ "\tbe\t%s\n\tnop" + NEW_LINE+"\tcmp\t%s, %%g0"+
    									NEW_LINE+ "\tbe\t%s\n\tnop"+ NEW_LINE+"\tmov 1, %%l5"+NEW_LINE+"\tba\t%s\n\tnop"
    									+NEW_LINE+"%s\n\tmov\t\t0, %%l5"+NEW_LINE+"%s";
    private static final String OROR = "cmp\t%s, %%g0"+NEW_LINE+ "\tbne\t%s\n\tnop" + NEW_LINE+"\tcmp\t%s, %%g0"+
											NEW_LINE+ "\tbne\t%s\n\tnop"+ NEW_LINE+"\tmov 0, %%l5"+NEW_LINE+"\tba\t%s\n\tnop"
											+NEW_LINE+"%s\n\tmov\t\t1, %%l5"+NEW_LINE+"%s";
    
    
    public AssemblyCodeGenerator(String fileToWrite) {
        try {
            fileWriter = new FileWriter(fileToWrite);
            writeAssembly(FILE_HEADER, (new Date()).toString());
        } 
        catch (IOException e) {
            System.err.printf(ERROR_IO_CONSTRUCT, fileToWrite);
            e.printStackTrace();
            System.exit(1);
        }
    }
    

    // 8
    public void decreaseIndent() {
        indent_level--;
    }
    
    public void dispose() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(ERROR_IO_CLOSE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void increaseIndent() {
        indent_level++;
    }
    
    public void do_data(Vector<STO> vecId, Vector<Type> vecType, Vector<Boolean> vecBool, Vector<String> vecFunc, Vector<Integer> vecArraySize){
    	STO ids;
    	boolean global_Exist_Flag = false;
    	String id_Name, id_Name2;
    	int arraySize;
    	
    	for (int i = 0; i < vecId.size (); i++)
		{
    		int pad;
			ids = vecId.elementAt (i);
			arraySize = vecArraySize.elementAt(i);
	    	if(i == 0){
	    		increaseIndent();
	        	writeAssembly(DATA);
	        	writeAssembly(ALIGN);
	        	if(!vecBool.elementAt(i)){
	        		//this if-else stmt checks array type or not and iterate for arraytype 
	        		if(!(ids.getType() instanceof ArrayType))
	        			writeAssembly(GDECL, ids.getId());
	        		else
		        		for(pad = 0; pad <= arraySize; pad++)
			        		if(pad == 0 && ids.getType() instanceof ArrayType)
								writeAssembly(GDECL, ids.getId() + pad );
			        		else if(pad < arraySize && ids.getType() instanceof ArrayType )
			        			writeAssembly(PADGDECL, ids.getId() + pad );
	        		global_Exist_Flag = true;
	        	}
				decreaseIndent();
	    	}
			else{
				if(!vecBool.elementAt(i) && global_Exist_Flag){
					if(!(ids.getType() instanceof ArrayType))
						writeAssembly(PADGDECL, ids.getId());
					for(pad = 0; pad <= arraySize; pad++)
		        		if(pad < arraySize && ids.getType() instanceof ArrayType )
		        			writeAssembly(PADGDECL, ids.getId() + pad );
				}
				else if(!vecBool.elementAt(i)){
					if(!(ids.getType() instanceof ArrayType))
	        			writeAssembly(GDECL, ids.getId());
	        		else
		        		for(pad = 0; pad <= arraySize; pad++)
			        		if(pad == 0 && ids.getType() instanceof ArrayType)
								writeAssembly(GDECL, ids.getId() + pad );
			        		else if(pad < arraySize && ids.getType() instanceof ArrayType )
			        			writeAssembly(PADGDECL, ids.getId() + pad);
					global_Exist_Flag = true;
				}
			}
		}
    	
    	for (int i = 0; i < vecId.size (); i++)
		{
			ids = vecId.elementAt (i);
			id_Name = ids.getId();
			arraySize = vecArraySize.elementAt(i);
			
			if(vecBool.elementAt(i) && !vecFunc.elementAt(i).equals(""))
				id_Name = "."+id_Name+"_"+vecFunc.elementAt(i);
			
			if(vecType.elementAt(i) instanceof IntType){
				for(int j = 0; j <= arraySize; j++){
					if(j < arraySize && ids.getType() instanceof ArrayType){
						id_Name2 = id_Name + j ;
						writeAssembly(GINIT, id_Name2, WORD, String.valueOf(0));
					}
					if(ids instanceof ConstSTO && (ids).getValue() == null)
						writeAssembly(GINIT, id_Name, WORD, String.valueOf(0));
					else if(ids instanceof ConstSTO)
						writeAssembly(GINIT, id_Name, WORD, String.valueOf(((ConstSTO)ids).getIntValue()));
					else
						writeAssembly(GINIT, id_Name, WORD, String.valueOf(0));
				}
			}
			else if(vecType.elementAt(i) instanceof FloatType){
				for(int j = 0; j <= arraySize; j++){
					if(j < arraySize && ids.getType() instanceof ArrayType){
						id_Name2 = id_Name + j;
						writeAssembly(GINIT, id_Name2, SINGLE, "0r0");
					}
					if((ids).getValue() == null)
						writeAssembly(GINIT, id_Name, SINGLE, "0r0");
					else
						writeAssembly(GINIT, id_Name, SINGLE, "0r"+String.valueOf(((ConstSTO)ids).getFloatValue()));
				}
			}
			else if(vecType.elementAt(i) instanceof BoolType){
				for(int j = 0; j <= arraySize; j++){
					if(j < arraySize && ids.getType() instanceof ArrayType){
						id_Name2 = id_Name + j;
						writeAssembly(GINIT, id_Name2, WORD, String.valueOf(0));
					}
					if((ids).getValue() == null)
						writeAssembly(GINIT, id_Name, WORD, String.valueOf(0));
					else
						writeAssembly(GINIT, id_Name, WORD, String.valueOf((((ConstSTO)ids).getValue()).intValue()));
				}
			}
			
		}
    	writeAssembly(NEW_LINE);
    	writeAssembly(NEW_LINE);
    }
    
    public void do_func(Vector<String> vecFuncId, Vector<Type> vecRetType, Vector<Boolean> vecRetRef){
    	String ids;
    	for (int i = 0; i < vecFuncId.size (); i++)
		{   
    		writeAssembly(NEW_LINE);
			ids = vecFuncId.elementAt (i);
			writeAssembly("!start "+ids+NEW_LINE);
	    	increaseIndent();
	    	writeAssembly(TEXT);
	        writeAssembly(ALIGN);
			writeAssembly(GFUNC, ids);
			decreaseIndent();
			writeAssembly(FUNCNAME,ids);
			increaseIndent();
			writeAssembly(SAVE_SP,ids,GLOBAL1,SP,GLOBAL1,SP);
			writeAssembly(NEW_LINE);
			decreaseIndent();
			for(int index = 0; index < vecPair.size(); index++)
				writePair(vecPair.elementAt(index),ids);
			writeAssembly(SAVE_MEMORY_SPACE, ids, String.valueOf(vecHighestOffsets.elementAt(i)*4));
		}
    }
    
    public void add_rodata() {
    	increaseIndent();
    	writeAssembly(RODATA);
    	decreaseIndent();
    	writeAssembly(RD_ENDL);
    	writeAssembly(RD_INTFMT);
    	writeAssembly(RD_STRFMT);
    	writeAssembly(RD_BTRUE);
    	writeAssembly(RD_BFALSE);
    }
    
    int stringCount = 0;
    public String strlitheader(String str){
    	String string = new String();
    	String name = ".str"+String.valueOf(stringCount);
    	string += NEW_LINE + "! cout << \"" + str + "\"" + NEW_LINE;
		string += SEPARATOR + DATA;
    	string += SEPARATOR + ALIGN;
		string += createString(STRLIT,name,str)+TWO_NEW_LINE;
		string += SEPARATOR + TEXT;
		string += SEPARATOR + ALIGN + NEW_LINE;
		string += SEPARATOR + createString(SET,name,OUT0)+NEW_LINE;
		string += SEPARATOR + PRINTF+NEW_LINE;
		stringCount++;
    	return string;
	}
    
    int tmpCounter = 0;
    int boolPrintCounter = 0;
    public void do_cout_expr(STO sto,String fname) {
    	couttmp=new String();
    	// cout << (constant variable,literal)
    	// formating cout instructions for constant variables/literals
    	if(sto instanceof ConstSTO) {
    		//formating for a string literal
    		if(sto.isStrLit()) {
    			couttmp += strlitheader(sto.getName());
    		}
    		// all other constants including literal int/float/bool
    		else {
    			// formating for constant int
    			if(sto.getType() instanceof IntType) {
    				couttmp += "! cout << "+((ConstSTO) sto).getIntValue()+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,INTFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,String.valueOf((((ConstSTO)sto).getIntValue())),OUT1)+NEW_LINE;
    				couttmp += SEPARATOR +PRINTF + NEW_LINE;
    			}
    			// formating for constant float
    			else if(sto.getType() instanceof FloatType) {
    				couttmp += "! cout << " + ((ConstSTO) sto).getFloatValue() + NEW_LINE;
    				couttmp += SEPARATOR + DATA;
    				couttmp += SEPARATOR + ALIGN;
    				couttmp += "tmp_"+ tmpCounter +":\t.single 0r" +((ConstSTO) sto).getFloatValue() + TWO_NEW_LINE;
    				couttmp += SEPARATOR + TEXT;
    				couttmp += SEPARATOR + ALIGN;
    				couttmp += SEPARATOR + createString(SET,"tmp_"+ tmpCounter,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,FLOAT0)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTFLOAT + NEW_LINE;
    				++tmpCounter;
    			}
    			// formating for constant bool
    			else if(sto.getType() instanceof BoolType) {
    				//true values
    				if(( sto).getBoolValue()) {
    					couttmp += "! cout << true\n";
    					couttmp += SEPARATOR + createString(SET,STRFMT,OUT0)+NEW_LINE;
    					couttmp += SEPARATOR + createString(SET,BTRUE,OUT1)+NEW_LINE;
    					couttmp += SEPARATOR + PRINTF + NEW_LINE;
    				}
    				// false values
    				else {
    					couttmp += "! cout << false\n";
    					couttmp += SEPARATOR + createString(SET,STRFMT,OUT0)+NEW_LINE;
    					couttmp += SEPARATOR + createString(SET,BFALSE,OUT1)+NEW_LINE;
    					couttmp += SEPARATOR + PRINTF + NEW_LINE;
    				}
    			}
    			
    		}
    	} // end constant formatting
    	// formatting variables global/static/local
    	else if(sto instanceof VarSTO) {
    		// format static variables
    		if(sto.getIsStatic()) {
    			// static int
    			String name = new String();
    			if(!(sto.getGlobal())) 
    				name = "."+sto.getName()+"_"+funcName;
    			else
    				name = sto.getName();
    			
    			if(sto.getType() instanceof IntType) {
    				couttmp += NEW_LINE + "! cout << " + name + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,INTFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,name,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF + NEW_LINE;
    			}
    			// static float
    			else if(sto.getType() instanceof FloatType) {
    				couttmp += NEW_LINE + "! cout << " + name + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,name,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,FLOAT0)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTFLOAT + NEW_LINE;
    			}
    			// static bool
    			else if(sto.getType() instanceof BoolType) {
    				couttmp += NEW_LINE + "! cout << " + name + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,STRFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,name,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(CMP,LOCAL0,String.valueOf(0))+NEW_LINE;
    				couttmp += SEPARATOR + createString(BNE,"boolTrue" + boolPrintCounter)+TWO_NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,BFALSE,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF+NEW_LINE;
    				couttmp += SEPARATOR + createString(BA,"printBEnd"+ boolPrintCounter)+TWO_NEW_LINE;
    				couttmp += "boolTrue"+boolPrintCounter +":" + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,BTRUE,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF + NEW_LINE;
  					couttmp += "printBEnd"+boolPrintCounter +":" + TWO_NEW_LINE;
  					++boolPrintCounter;
    			}
    		}
    		// formating local variables
    		else if(sto.getOffset() > 0) {
    			if(sto.getType() instanceof IntType) {
    				couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,INTFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,String.valueOf((sto.getOffset()*(-4))),LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(ADD,FP,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF + NEW_LINE;
    			}
    			else if(sto.getType() instanceof FloatType) {
    				couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,String.valueOf((sto.getOffset()*(-4))),LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(ADD,FP,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,FLOAT0)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTFLOAT + NEW_LINE;
    			}
    			else if(sto.getType() instanceof BoolType) {
    				couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,STRFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,String.valueOf((sto.getOffset()*(-4))),LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(ADD,FP,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(CMP,LOCAL0,String.valueOf(0))+NEW_LINE;
    				couttmp += SEPARATOR + createString(BNE,"boolTrue"+ boolPrintCounter)+TWO_NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,BFALSE,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF+NEW_LINE;
    				couttmp += SEPARATOR + createString(BA,"printBEnd"+ boolPrintCounter)+TWO_NEW_LINE;
    				couttmp += "boolTrue"+boolPrintCounter +":" + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,BTRUE,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF + NEW_LINE;
  					couttmp += "printBEnd"+boolPrintCounter +":" + TWO_NEW_LINE;
  					++boolPrintCounter;
    			}
    		}
    		// global variables
    		else{
    			if(sto.getType() instanceof IntType) {
    				couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,INTFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,sto.getName(),LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(ADD,GLOBAL0,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF + NEW_LINE;
    			}
    			// global flat
    			else if(sto.getType() instanceof FloatType) {
    				couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,sto.getName(),LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(ADD,GLOBAL0,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,FLOAT0)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTFLOAT + NEW_LINE;
    			}
    			// global bool
    			else if(sto.getType() instanceof BoolType) {
    				couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,STRFMT,OUT0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,sto.getName(),LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(ADD,GLOBAL0,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(LOAD,LOCAL0,LOCAL0)+NEW_LINE;
    				couttmp += SEPARATOR + createString(CMP,LOCAL0,String.valueOf(0))+NEW_LINE;
    				couttmp += SEPARATOR + createString(BNE,"boolTrue"+ boolPrintCounter)+TWO_NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,BFALSE,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF+NEW_LINE;
    				couttmp += SEPARATOR + createString(BA,"printBEnd"+ boolPrintCounter)+TWO_NEW_LINE;
    				couttmp += "boolTrue"+boolPrintCounter +":" + NEW_LINE;
    				couttmp += SEPARATOR + createString(SET,BTRUE,OUT1)+NEW_LINE;
    				couttmp += SEPARATOR + PRINTF + NEW_LINE;
  					couttmp += "printBEnd"+boolPrintCounter +":" + TWO_NEW_LINE;
  					++boolPrintCounter;
    			}
    		}
    		
    	} // end globals/static/local
    	else if (sto instanceof ExprSTO) {

    			if(sto.getType() instanceof IntType) {
					couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
					couttmp += SEPARATOR + createString(SET,INTFMT,OUT0)+NEW_LINE;
					couttmp += SEPARATOR + createString(SET,String.valueOf(sto.getOffset()*(-4)),LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(ADD,FP,LOCAL0,LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(LOAD,LOCAL0,OUT1)+NEW_LINE;
					couttmp += SEPARATOR + PRINTF + NEW_LINE;
				}
				// global flat
				else if(sto.getType() instanceof FloatType) {
					couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
					couttmp += SEPARATOR + createString(SET,String.valueOf(sto.getOffset()*(-4)),LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(ADD,FP,LOCAL0,LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(LOAD,LOCAL0,FLOAT0)+NEW_LINE;
					couttmp += SEPARATOR + PRINTFLOAT + NEW_LINE;
				}
				// global bool
				else if(sto.getType() instanceof BoolType) {
					couttmp += NEW_LINE + "! cout << " + sto.getName() + NEW_LINE;
					couttmp += SEPARATOR + createString(SET,STRFMT,OUT0)+NEW_LINE;
					couttmp += SEPARATOR + createString(SET,String.valueOf(sto.getOffset()*(-4)),LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(ADD,FP,LOCAL0,LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(LOAD,LOCAL0,LOCAL0)+NEW_LINE;
					couttmp += SEPARATOR + createString(CMP,LOCAL0,String.valueOf(0))+NEW_LINE;
					couttmp += SEPARATOR + createString(BNE,"boolTrue"+ boolPrintCounter)+TWO_NEW_LINE;
					couttmp += SEPARATOR + createString(SET,BFALSE,OUT1)+NEW_LINE;
					couttmp += SEPARATOR + PRINTF+NEW_LINE;
					couttmp += SEPARATOR + createString(BA,"printBEnd"+ boolPrintCounter)+TWO_NEW_LINE;
					couttmp += "boolTrue"+boolPrintCounter +":" + NEW_LINE;
					couttmp += SEPARATOR + createString(SET,BTRUE,OUT1)+NEW_LINE;
					couttmp += SEPARATOR + PRINTF + NEW_LINE;
					couttmp += "printBEnd"+boolPrintCounter +":" + TWO_NEW_LINE;
					++boolPrintCounter;
				}
    		
    		//--highestOffset;
    	}
    	vecPair.addElement(new Tuple(fname,couttmp));
    }
    
    // handles cout << endl instruction
    public void do_cout_endl(String name) {
    	couttmp = new String();
    	
    	couttmp += NEW_LINE + "! cout << endl\n";
    	couttmp += SEPARATOR + createString(SET,ENDL,OUT0);
    	couttmp += NEW_LINE+SEPARATOR + PRINTF + NEW_LINE;
    	
    	vecPair.addElement(new Tuple(name,couttmp));
    }
    
    // insert formatted string into vector for printing
    // resets format string for cout
    public void dump_cout(String name) {
    	vecPair.addElement(new Tuple(name,couttmp));
    	couttmp = new String();
    }
    String unarytmp;
    public int UnaryFmt(String name,STO sto,boolean minus) {
    	unarytmp=new String();
    	String opname=new String();
    		if(minus)
    			opname+="! -";
    		else
    			opname+="! +";
    	unarytmp+=NEW_LINE+opname+sto.getName()+TWO_NEW_LINE;
    	if(sto instanceof ConstSTO) {
    		if(sto.getLit()) {
    			if(sto.getType() instanceof IntType) {
    				
    				unarytmp += NEW_LINE+SEPARATOR+createString(SET,String.valueOf(sto.getIntValue()),LOCAL1)+NEW_LINE;
    				if(minus)
    					unarytmp += SEPARATOR+createString(NEG,LOCAL1,LOCAL1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,LOCAL1)+NEW_LINE;
    			}
    			else if(sto.getType() instanceof FloatType) {
    				unarytmp += NEW_LINE+SEPARATOR + DATA;
    				unarytmp += SEPARATOR + ALIGN;
    				unarytmp += "tmp_"+ tmpCounter +":\t.single 0r" +((ConstSTO) sto).getFloatValue() + TWO_NEW_LINE;
    				unarytmp += SEPARATOR + TEXT;
    				unarytmp += SEPARATOR + ALIGN;
    				unarytmp += SEPARATOR+createString(SET,"tmp_"+String.valueOf(tmpCounter),LOCAL0)+NEW_LINE;
    				unarytmp += SEPARATOR+createString(LOAD,LOCAL0,FLOAT1)+NEW_LINE;
    				if(minus)
    					unarytmp += SEPARATOR+createString(FNEGS,FLOAT1,FLOAT1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,FLOAT1)+NEW_LINE;
    				tmpCounter++;
    			}
    		}
    		else {
    			if(sto.getType() instanceof IntType) {
    				unarytmp += NEW_LINE+SEPARATOR+createString(SET,String.valueOf(sto.getIntValue()),LOCAL1)+NEW_LINE;
    				if(minus)
    					unarytmp += SEPARATOR+createString(NEG,LOCAL1,LOCAL1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,LOCAL1)+NEW_LINE;
    			}
    			else if(sto.getType() instanceof FloatType) {
    				unarytmp+=NEW_LINE+loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,FLOAT1);
    				if(minus)
    					unarytmp += SEPARATOR+createString(FNEGS,FLOAT1,FLOAT1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,FLOAT1)+NEW_LINE;
    			}
    		}
    	}
    	else if(sto instanceof VarSTO) {
    		if(sto.getOffset() > 0) {
    			if(sto.getType() instanceof IntType) {
    				unarytmp+=NEW_LINE+loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,LOCAL1);
    				if(minus)
    					unarytmp += SEPARATOR+createString(NEG,LOCAL1,LOCAL1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,LOCAL1)+NEW_LINE;
    			}
    			else {
        			unarytmp+=NEW_LINE+loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,FLOAT1);
        			if(minus)
        				unarytmp += SEPARATOR+createString(FNEGS,FLOAT1,FLOAT1)+NEW_LINE;
        			unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,FLOAT1)+NEW_LINE;
    			}
    		}
    		else if(!sto.getGlobal()) {
    			if(sto.getType() instanceof IntType) {
    				unarytmp+=NEW_LINE+loadGlobalOrStaticVar("."+sto.getName()+"_"+name,LOCAL0,LOCAL1);
    				if(minus)
    					unarytmp += SEPARATOR+createString(NEG,LOCAL1,LOCAL1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,LOCAL1)+NEW_LINE;
    			}
    			else {
    				unarytmp+=NEW_LINE+loadGlobalOrStaticVar("."+sto.getName()+"_"+name,LOCAL0,FLOAT1);
        			if(minus)
        				unarytmp += SEPARATOR+createString(FNEGS,FLOAT1,FLOAT1)+NEW_LINE;
        			unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,FLOAT1)+NEW_LINE;
    			}
    		}
    		else {
    			if(sto.getType() instanceof IntType) {
    				unarytmp+=NEW_LINE+loadGlobalOrStaticVar(sto.getName(),LOCAL0,LOCAL1);
    				if(minus)
    					unarytmp += SEPARATOR+createString(NEG,LOCAL1,LOCAL1)+NEW_LINE;
    				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,LOCAL1)+NEW_LINE;
    			}
    			else {
    				unarytmp+=NEW_LINE+loadGlobalOrStaticVar(sto.getName(),LOCAL0,FLOAT1);
        			if(minus)
        				unarytmp += SEPARATOR+createString(FNEGS,FLOAT1,FLOAT1)+NEW_LINE;
        			unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,FLOAT1)+NEW_LINE;
    			}
    		}
    	}
    	else if(sto instanceof ExprSTO){
			if(sto.getType() instanceof IntType) {
				unarytmp+=NEW_LINE+loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,LOCAL1);
				if(minus)
					unarytmp += SEPARATOR+createString(NEG,LOCAL1,LOCAL1)+NEW_LINE;
				unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,LOCAL1)+NEW_LINE;
			}
			else {
				unarytmp+=NEW_LINE+loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,FLOAT1);
    			if(minus)
    				unarytmp += SEPARATOR+createString(FNEGS,FLOAT1,FLOAT1)+NEW_LINE;
    			unarytmp+=storeLocalVar(String.valueOf((highestOffset+1)*(-4)),LOCAL0,FLOAT1)+NEW_LINE;
			}
    	}
    	vecPair.addElement(new Tuple(name,unarytmp));
    	return ++highestOffset;
    }
    
    int notba = 0;
    public String unarybranching(int offset) {
    	String string = new String();
		string+=SEPARATOR+createString(CMP,LOCAL1,GLOBAL0)+NEW_LINE;
		string+=SEPARATOR+createString(BNE,"not"+String.valueOf(notba))+TWO_NEW_LINE;
		string+=SEPARATOR+createString(SET,String.valueOf(1),LOCAL1)+TWO_NEW_LINE;
		string+=SEPARATOR+createString(BA,"endnot"+String.valueOf(notba))+NEW_LINE;
		string+="not"+notba+":"+NEW_LINE;
		string+=SEPARATOR+createString(SET,String.valueOf(0),LOCAL1)+NEW_LINE;
		string+="endnot"+notba+":"+NEW_LINE;
		string+=storeLocalVar(String.valueOf((offset+1)*(-4)),LOCAL0,LOCAL1);
		notba++;
		return string;
    }
    
    String nottmp;
    public int UnaryNotFmt(String name,STO sto) {
    	nottmp=new String();
    	int tempHighestOffset = highestOffset + 1;
    	
    	if(sto instanceof ConstSTO) {
    		nottmp+="! !"+sto.getName()+NEW_LINE;
    		nottmp+=SEPARATOR+createString(SET,String.valueOf(sto.getIntValue()),LOCAL1)+NEW_LINE;
    		nottmp+=unarybranching(highestOffset);
    	}
    	else if(sto instanceof VarSTO) {
    		if(sto.getOffset() > 0) {
    			nottmp+="! !"+sto.getName()+NEW_LINE;
        		nottmp+=loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,LOCAL1);
        		nottmp+=unarybranching(highestOffset);
    		}
    		else if(!(sto.getGlobal())) {
    			nottmp+="! !"+sto.getName()+NEW_LINE;
        		nottmp+=loadGlobalOrStaticVar("."+sto.getName()+"_"+name,LOCAL0,LOCAL1);
        		nottmp+=unarybranching(highestOffset);
    		}
    		else {
    			nottmp+="! !"+sto.getName()+NEW_LINE;
        		nottmp+=loadGlobalOrStaticVar(sto.getName(),LOCAL0,LOCAL1);
        		nottmp+=unarybranching(highestOffset);
    		}
    	}
    	else {
    	}
	
    	vecPair.addElement(new Tuple(name,nottmp));
    	++highestOffset;
    	return tempHighestOffset;
    }
    public void writePair(Tuple p, String funcName){
    	if(p.getfname().equals(funcName)){
    		writeString(p.getinstr());
    	}
    }
    
    String cintmp;
    public void dump_cin(String name,STO sto) {
    	
    	cintmp = new String();
    	cintmp += NEW_LINE + "! cin >> " + sto.getName() + NEW_LINE;
    	if(sto.getType() instanceof IntType) {
    		
    		cintmp += SEPARATOR + INPUTINT + NEW_LINE;
    		
    		if(sto.getOffset() > 0)
    			cintmp += storeLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,OUT0);
    		else if(!sto.getGlobal())
    			cintmp += storeGlobalOrStaticVar("."+sto.getName()+"_"+name,LOCAL0,OUT0) + NEW_LINE;
    		else
    			cintmp += storeGlobalOrStaticVar(sto.getName(),LOCAL0,OUT0) + NEW_LINE;
    		
    		
    	}
    	else {
    		
    		cintmp += SEPARATOR + INPUTFLOAT + NEW_LINE;
    		
    		if(sto.getOffset() > 0)
    			cintmp += storeLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,FLOAT0);
    		else if(!sto.getGlobal())
    			cintmp += storeGlobalOrStaticVar("."+sto.getName()+"_"+name,LOCAL0,FLOAT0) + NEW_LINE;
    		else
    			cintmp += storeGlobalOrStaticVar(sto.getName(),LOCAL0,FLOAT0) + NEW_LINE;
    		
    	}
    	
    	vecPair.addElement(new Tuple(name,cintmp));
    }
   
    int floatCount = 0;
    int highestOffset = 1;
    String funcName;
    Vector<Integer> vecHighestOffsets = new Vector<Integer>();
    public void localInit( STO id, Type type, int offset, String funcName, String varName, boolean isStatic){
    	//csck last func name and current are same if not update the last to current and highest offset to 0 for new function
    	if(this.funcName == null)
    		this.funcName = funcName;
    	else if(!this.funcName.equals(funcName))
    		this.funcName = funcName;
    	
    	String string = new String(),value = new String(),name = "",floatValue = "";
    	Tuple p;
    	int offsetValue = id.getOffset();;
    	//remember the name for print out in assembly file
    	if(id.getId() != null)
    		name = id.getId();
    	else if(varName != null)
    		name = varName;
    	if(isStatic)
    		name = "."+name+"_"+funcName;
    	
    	//update highest offset for local array declaration
    	if(id.getType() instanceof ArrayType)
    		if(highestOffset < offset + id.getType().getSize())
    			highestOffset = offset + id.getType().getSize();
    	
    	//update highest offset for temp
    	if(highestOffset < offset)
    		highestOffset = offset;
    	//Constant value assignment
    	if(id instanceof ConstSTO){
	    	if((id).getValue() != null){
	    		if(type instanceof IntType){
	    			value = String.valueOf(((ConstSTO)id).getIntValue());
	    			string += NEW_LINE +"!"+value;
	    		}
	    		else if(type instanceof FloatType){
	    			floatValue = String.valueOf(((ConstSTO)id).getFloatValue());
	    			value  = "_t" + String.valueOf(floatCount) ;
	    			string += NEW_LINE +"!"+floatValue + defineFloatVar(value, floatValue);
	    			floatCount++;
	    		}
	    		else if(type instanceof BoolType){
	    			value  = String.valueOf((((ConstSTO)id).getValue()).intValue());
	    			string += NEW_LINE +"!"+(((ConstSTO)id).getBoolValue());
	    		}

		    	if(!(type instanceof FloatType)){
			    	string += NEW_LINE + SEPARATOR + createString(SET, String.valueOf(value), LOCAL1)+NEW_LINE;
			    	string += storeLocalVar(String.valueOf((-highestOffset*4-4)), LOCAL0, LOCAL1);
			    	string += NEW_LINE;
			    	string += "!"+name+ " = " + value+NEW_LINE;
			    	string += loadLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, LOCAL1);
			    	if(offset > 0)
			    		string += storeLocalVar(String.valueOf((-offset*4)), LOCAL0, LOCAL1);
			    	else
			    		string +=storeGlobalOrStaticVar(name, LOCAL0, LOCAL1);
		    	}
		    	else{
			    	string += NEW_LINE + SEPARATOR + createString(SET, String.valueOf(value), LOCAL1)+NEW_LINE;
			    	string += SEPARATOR + createString(LOAD, LOCAL1, FLOAT1)+NEW_LINE;
			    	string += storeLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, FLOAT1);
			    	string += NEW_LINE;
			    	string += "!"+name+ " = " + floatValue+NEW_LINE;
			    	string += loadLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, FLOAT1);
			    	
			    	if(offset > 0)
			    		string += storeLocalVar(String.valueOf(-offset*4), LOCAL0, FLOAT1);
			    	else
			    		string +=storeGlobalOrStaticVar(name, LOCAL0, FLOAT1);
		    	}
	    	}
    	}
    	else if(id instanceof VarSTO){
    		string += NEW_LINE +"!"+name +" = "+id.getName()+NEW_LINE;
	    	if(type instanceof FloatType){
		    	//checks if rOperand is global, static, or local
		    	if(id.getIsStatic())
		    		string += loadGlobalOrStaticVar("."+id.getName()+"_"+funcName, LOCAL0, FLOAT1);
	    		else if(id.getOffset() != -1)
	    			string +=loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0, FLOAT1);
	    		else
	    			string += loadGlobalOrStaticVar(id.getName(), LOCAL0, FLOAT1);
		    	
		    	if(id.getType() instanceof IntType)
	    			string += SEPARATOR + createString(FITOS, FLOAT1, FLOAT1)+NEW_LINE;
	    		//checks if rOperand is global, static, or local
	    		if(offset != -1)
	    			string += storeLocalVar(String.valueOf(-offset*4), LOCAL1, FLOAT1);
	    		else
	    			string += storeGlobalOrStaticVar(name, LOCAL1, FLOAT1);  	
	    	}
	    	else {
	    		//checks if rOperand is global, static, or local
	    		 if(id.getIsStatic())
	    			 string += loadGlobalOrStaticVar("."+id.getName()+"_"+funcName, LOCAL0, LOCAL0);
	    		 else if(id.getOffset() != -1)
	    			 loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0, LOCAL0);
	    		else
	    			string += loadGlobalOrStaticVar(id.getName(), LOCAL0, LOCAL0);
	    		 
	    		//checks if lOperand is global, Static,  or local
		    	if(offset != -1 && !isStatic)
		    		string += storeLocalVar(String.valueOf((-offset*4)), LOCAL1, LOCAL0);
		    	else
		    		string += storeGlobalOrStaticVar(name, LOCAL1, LOCAL0);
		    }
    	}
    	else if(id instanceof ExprSTO){
    		--highestOffset;
    		if(!(id.getType() instanceof ArrayType) && (type instanceof FloatType)){
    			//checks if rOperand is always ExprSTO
	    		string +=loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0, FLOAT1);
	    		if(id.getType() instanceof IntType)
	    			string += SEPARATOR + createString(FITOS, FLOAT1, FLOAT1)+NEW_LINE;
	    		//checks if rOperand is global, static, or local
	    		if(offset > 0)
		    		string += storeLocalVar(String.valueOf(-offset*4), LOCAL1, FLOAT1); 
	    		else
	    			string += storeGlobalOrStaticVar(name, LOCAL1, FLOAT1);
	    	}	
    		else if(!(id.getType() instanceof ArrayType)){
	    		//checks if rOperand is always ExprSTO
	    		string += loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0,LOCAL0);
	    		
	    		//checks if rOperand is global, Static,  or local
		    	if(offset != -1 && !isStatic)
		    		string += storeLocalVar(String.valueOf((-offset*4)), LOCAL1, LOCAL0);
		    	else
		    		string += storeGlobalOrStaticVar(name, LOCAL1, LOCAL0);
	    	}
    	}
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    }
    
    public void assingInit( STO id, Type type, Type subType,int offset, String funcName, String varName, boolean isStatic, boolean localStatic){
    	if(this.funcName == null)
    		this.funcName = funcName;
    	else if(!this.funcName.equals(funcName))
    		this.funcName = funcName;
    	String string = new String(),value = new String(),floatValue = "",name = "";
    	Tuple p;
    	int offsetValue = id.getOffset();

    	if(id.getId() != null)
    		name = id.getId();
    	else if(varName != null)
    		name = varName;
    	
    	//checks if lOperand is a static
    	if(!localStatic)
    		varName = "." + varName+"_"+funcName;
    	//update highest offset for temp
    	if(highestOffset < offset)
    		highestOffset = offset;
    	
    	if(id instanceof ConstSTO){
	    	if((id).getValue() != null){
	    		if(type instanceof IntType){
	    			value = String.valueOf(((ConstSTO)id).getIntValue());
	    			string += NEW_LINE +"!"+value;
	    		}
	    		else if(type instanceof FloatType){
	    			floatValue = String.valueOf(((ConstSTO)id).getFloatValue());
	    			value  = "_t" + String.valueOf(floatCount) ;
	    			string += NEW_LINE +"!"+floatValue + defineFloatVar(value, floatValue);
	    			floatCount++;
	    		}
	    		else if(type instanceof BoolType){
	    			value  = String.valueOf((((ConstSTO)id).getValue()).intValue());
	    			string += NEW_LINE +"!"+(((ConstSTO)id).getBoolValue());
	    		}
	    		
		    	if(!(type instanceof FloatType)){
			    	string += NEW_LINE + SEPARATOR + createString(SET, String.valueOf(value), LOCAL1)+NEW_LINE;
			    	string += storeLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, LOCAL1);
			    	string += NEW_LINE;
			    	string += "!"+name+ " = " + value+NEW_LINE;
			    	string += loadLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, LOCAL1);
			    	if(offset > 0)
			    		string += storeLocalVar(String.valueOf(-offset*4), LOCAL0, LOCAL1);
			    	else
		    			string += storeGlobalOrStaticVar(varName, LOCAL0, LOCAL1);
			    	
		    	}
		    	else{
			    	string += NEW_LINE + SEPARATOR + createString(SET, String.valueOf(value), LOCAL1)+NEW_LINE;
			    	string += SEPARATOR + createString(LOAD, LOCAL1, FLOAT1)+NEW_LINE;
			    	string += storeLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, FLOAT1);
			    	string += NEW_LINE;
			    	string += "!"+name+ " = " + floatValue+NEW_LINE;
			    	string += loadLocalVar(String.valueOf(-highestOffset*4-4), LOCAL0, FLOAT1);
			    	if(offset > 0)
			    		string += storeLocalVar(String.valueOf(-offset*4), LOCAL0, FLOAT1);
			    	else
			    		string += storeGlobalOrStaticVar(varName, LOCAL0, FLOAT1);
		    	}
	    	}
    	}
    	else if(id instanceof VarSTO){
    		
	    	string += NEW_LINE +"!"+ varName +" = "+id.getName() + " & offset is: "+offset+ varName+ NEW_LINE;
	    	if((type instanceof FloatType)){
	    		//checks if rOperand is global, static, or local
	    		if(id.getIsStatic()){
	    			string += loadGlobalOrStaticVar("."+id.getName()+"_"+funcName, LOCAL0, FLOAT1);
	    		}
	    		else if(id.getOffset() > 0)
	    			string +=loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0, FLOAT1);
	    		else{
		    		string += loadGlobalOrStaticVar(id.getName(), LOCAL0, FLOAT1);
	    		}
	    		//convert int to float
	    		if(id.getType() instanceof IntType)
	    			string += SEPARATOR + createString(FITOS, FLOAT1, FLOAT1)+NEW_LINE;
	    		//checks if rOperand is global, static, or local
	    		if(offset > 0)
		    		string += storeLocalVar(String.valueOf(-offset*4), LOCAL1, FLOAT1); 
	    		else
	    			string += storeGlobalOrStaticVar(varName, LOCAL1, FLOAT1);
	    	}
	    	else{
	    		//checks if rOperand is global, static, or local
	    		 if(id.getIsStatic())
	    			 string += loadGlobalOrStaticVar("."+id.getName()+"_"+funcName, LOCAL0, LOCAL0);
	    		 else if(id.getOffset() > 0)
	    			 string += loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0,LOCAL0);
	    		else
	    			string += loadGlobalOrStaticVar(id.getName(), LOCAL0, LOCAL0);
	    		 
	    		//checks if rOperand is global, Static,  or local
		    	if(offset > 0 && !isStatic)
		    		string += storeLocalVar(String.valueOf((-offset*4)), LOCAL1, LOCAL0);
		    	else
		    		string += storeGlobalOrStaticVar(varName, LOCAL1, LOCAL0);
	    	}
	    }
    	else if(id instanceof ExprSTO){
    		--highestOffset;
    		if((type instanceof FloatType)){
    			//checks if rOperand is always ExprSTO
	    		string +=loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0, FLOAT1);
	    		if(id.getType() instanceof IntType)
	    			string += SEPARATOR + createString(FITOS, FLOAT1, FLOAT1)+NEW_LINE;
	    		//checks if rOperand is global, static, or local
	    		if(offset > 0)
		    		string += storeLocalVar(String.valueOf(-offset*4), LOCAL1, FLOAT1); 
	    		else
	    			string += storeGlobalOrStaticVar(varName, LOCAL1, FLOAT1);
	    	}	
    		else{
	    		//checks if rOperand is always ExprSTO
	    		string += loadLocalVar(String.valueOf(-offsetValue*4), LOCAL0,LOCAL0);
	    		
	    		//checks if rOperand is global, Static,  or local
		    	if(offset > 0 && !localStatic)
		    		string += storeLocalVar(String.valueOf((-offset*4)), LOCAL1, LOCAL0);
		    	else
		    		string += storeGlobalOrStaticVar(varName, LOCAL1, LOCAL0);
	    	}
    	}
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    }
    
    public int writeBinary(STO l, Operator o, STO r, String funcName) {
    	
    	String string = NEW_LINE + "!" + l.getName() + o.getName() + r.getName() + NEW_LINE, op = o.getName();
    	Tuple p;
    	
    	if((l.getType() instanceof IntType && r.getType()instanceof IntType) || (l.getType() instanceof BoolType && r.getType()instanceof BoolType)){
    		if(l instanceof ConstSTO)
    			string += NEW_LINE + SEPARATOR + createString(SET,String.valueOf((((ConstSTO)l).getIntValue())),LOCAL1)+NEW_LINE;
    		else if(l instanceof VarSTO){
    			if(l.getOffset() > 0)
    				string += loadLocalVar(String.valueOf(-l.getOffset()*4), LOCAL0, LOCAL1);
    			else if(!l.getGlobal())
            		string += loadGlobalOrStaticVar("."+l.getName()+"_" + funcName, LOCAL0, LOCAL1);
    			else
    				string += loadGlobalOrStaticVar(l.getName(), LOCAL0, LOCAL1);
    		}
    		else if(l instanceof ExprSTO){
    			--highestOffset;
    			string += loadLocalVar(String.valueOf(-l.getOffset()*4), LOCAL0, LOCAL1);
    		}
    		
    		string += NEW_LINE;
    		
    		if(r instanceof ConstSTO) 
    			string += SEPARATOR + createString(SET,String.valueOf((((ConstSTO)r).getIntValue())),LOCAL2)+NEW_LINE;
    		else if(r instanceof VarSTO){
    			if(r.getOffset() > 0)
    				string += loadLocalVar(String.valueOf(-r.getOffset()*4), LOCAL0, LOCAL2);
    			else if(!r.getGlobal())
            		string += loadGlobalOrStaticVar("."+r.getName()+"_" + funcName, LOCAL0, LOCAL2);
    			else
    				string += loadGlobalOrStaticVar(r.getName(), LOCAL0, LOCAL2);
    		}
    		else if(r instanceof ExprSTO){
    			--highestOffset;
    			string += loadLocalVar(String.valueOf(-r.getOffset()*4), LOCAL0, LOCAL2);
    		}
    	}
    	// float arithmetic expressions
    	else if(l.getType() instanceof FloatType || r.getType()instanceof FloatType){
    		
    		// left operand not a float
    		if(!(l.getType() instanceof FloatType)) {
    			// left operand is a const int
    			if(l instanceof ConstSTO) {
    				string += SEPARATOR + createString(SET,String.valueOf(l.getIntValue()),LOCAL0)+NEW_LINE;
    				string += storeLocalVar(String.valueOf((highestOffset)*(-4)-4),LOCAL1,LOCAL0);
    				string += SEPARATOR + createString(LOAD,LOCAL1,FLOAT1) + NEW_LINE;
    			}
    			// left operand is a variable int
    			else if(l instanceof VarSTO) {
    				if(l.getOffset() > 0)
    					string += loadLocalVar(String.valueOf(-l.getOffset()*4), LOCAL0, FLOAT1) + NEW_LINE;
    				else if(!(l.getGlobal()))
    					string += loadGlobalOrStaticVar("."+l.getName()+"_" + funcName, LOCAL0, FLOAT1) + NEW_LINE;
    				else
    					string += loadGlobalOrStaticVar(l.getName(), LOCAL0, FLOAT1) + NEW_LINE;
    			}
    			// left operand is an expression int
    			else if(l instanceof ExprSTO) {
    				//to be added
    				--highestOffset;
    				string += loadLocalVar(String.valueOf(-l.getOffset()*4), LOCAL0, FLOAT1) + NEW_LINE;
    			}
    			// do int to float conversion
    			string += SEPARATOR + createString(FITOS,FLOAT1,FLOAT1) + TWO_NEW_LINE;
    		}
    		// left operand is a float
    		else {
    			String floatValue, name;
    			// left operand is a constant float
    			if(l instanceof ConstSTO) {
	    			floatValue = String.valueOf(((ConstSTO)l).getFloatValue());
	    			name  = "_t" + String.valueOf(floatCount) ;
	    			string += NEW_LINE +"!"+floatValue + defineFloatVar(name, floatValue) + NEW_LINE;
	    			floatCount++;
	    			
	    			string += loadGlobalOrStaticVar(name, LOCAL0, FLOAT1) + NEW_LINE;
    			}
    			// left operand is a variable float
    			else if(l instanceof VarSTO) {
        			if(l.getOffset() > 0)
        				string += loadLocalVar(String.valueOf(-l.getOffset()*4), LOCAL0, FLOAT1) + NEW_LINE;
        			else if(!l.getGlobal())
                		string += loadGlobalOrStaticVar("."+l.getName()+"_" + funcName, LOCAL0, FLOAT1) + NEW_LINE;
        			else
        				string += loadGlobalOrStaticVar(l.getName(), LOCAL0, FLOAT1) + NEW_LINE;
    			}
    			// left operand is an expression float
    			else if(l instanceof ExprSTO) {
    				--highestOffset;
        			string += loadLocalVar(String.valueOf(-l.getOffset()*4), LOCAL0, FLOAT1);
    			}
    		}
    		// code for right operand
    		// right operand is not a float
    		if(!(r.getType() instanceof FloatType)){
    			// right operand is constant int
    			if(r instanceof ConstSTO) {
    				string += SEPARATOR + createString(SET,String.valueOf((((ConstSTO)r).getIntValue())),LOCAL0)+NEW_LINE;
    				string += storeLocalVar(String.valueOf((highestOffset)*(-4)-4),LOCAL1,LOCAL0);
    				string += SEPARATOR + createString(LOAD,LOCAL1,FLOAT2) + NEW_LINE;
    			}
    			// right operand is a variable int
    			else if(r instanceof VarSTO) {
    				if(r.getOffset() > 0)
    					string += loadLocalVar(String.valueOf(-r.getOffset()*4), LOCAL0, FLOAT2) + NEW_LINE;
    				else if(!(r.getGlobal()))
    					string += loadGlobalOrStaticVar("."+r.getName()+"_" + funcName, LOCAL0, FLOAT2) + NEW_LINE;
    				else
    					string += loadGlobalOrStaticVar(r.getName(), LOCAL0, FLOAT2) + NEW_LINE;
    			}
    			//right operand is an expression int
    			else if(r instanceof ExprSTO) {
    				--highestOffset;
        			string += loadLocalVar(String.valueOf(-r.getOffset()*4), LOCAL0, FLOAT2);
    			}
    			string += SEPARATOR + createString(FITOS,FLOAT2,FLOAT2) + TWO_NEW_LINE;
    			 
    		}
    		// right operand is a float
    		else {
    			String floatValue, name;
    			// left operand is a constant float
    			if(r instanceof ConstSTO) {
	    			floatValue = String.valueOf(((ConstSTO)r).getFloatValue());
	    			name  = "_t" + String.valueOf(floatCount) ;
	    			string += NEW_LINE +"!"+floatValue + defineFloatVar(name, floatValue) + NEW_LINE;
	    			floatCount++;
	    			
	    			string += loadGlobalOrStaticVar(name, LOCAL0, FLOAT2) + NEW_LINE;
    			}
    			// left operand is a variable float
    			else if(r instanceof VarSTO) {
        			if(r.getOffset() > 0)
        				string += loadLocalVar(String.valueOf(-r.getOffset()*4), LOCAL0, FLOAT2);
        			else if(!r.getGlobal())
                		string += loadGlobalOrStaticVar("."+r.getName()+"_" + funcName, LOCAL0, FLOAT2);
        			else
        				string += loadGlobalOrStaticVar(r.getName(), LOCAL0, FLOAT2);
    			}
    			// left operand is an expression float
    			else if(r instanceof ExprSTO) {
    				string += loadLocalVar(String.valueOf(-r.getOffset()*4), LOCAL0, FLOAT2);
    				--highestOffset;
    			}
    		}
    	}
    	
    	int tempHighestOffset = highestOffset+1;
    	
    	if(r instanceof ExprSTO)
    		tempHighestOffset = highestOffset + 1;
    	
    	
    	if(l.getType() instanceof IntType && r.getType()instanceof IntType|| (l.getType() instanceof BoolType && r.getType()instanceof BoolType))
    		string += findOp(op, LOCAL1, LOCAL2, String.valueOf(-tempHighestOffset*4));
    	else if(l.getType() instanceof FloatType || r.getType()instanceof FloatType)
    		string += findOpfloat(op, FLOAT1, FLOAT2, String.valueOf(-tempHighestOffset*4));
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    	++highestOffset;
    	return tempHighestOffset;
	}
    
    public void writeIf(STO sto){
    	String string = NEW_LINE + "!Starting if_" + sto.m_ifCounter +NEW_LINE;
    	Tuple p;
    	
    	if(sto instanceof ConstSTO)
    		string += SEPARATOR + createString(SET,String.valueOf(sto.getIntValue()),LOCAL0)+NEW_LINE;
    	else if(sto instanceof VarSTO){
    		if(!sto.getGlobal())
        		string += loadGlobalOrStaticVar("."+sto.getName()+"_" + funcName, LOCAL0, LOCAL0);
    		else if(sto.getOffset() > 0)
				string += loadLocalVar(String.valueOf(-sto.getOffset()*4), LOCAL0, LOCAL0);
			else
				string += loadGlobalOrStaticVar(sto.getName(), LOCAL0, LOCAL0);
    	}
    	else if(sto instanceof ExprSTO)
    		string += loadLocalVar(String.valueOf(-sto.getOffset()*4), LOCAL0, LOCAL0);
    	
    	string += SEPARATOR + createString(CMP, LOCAL0, GLOBAL0)+NEW_LINE;
    	string += SEPARATOR + createString(BE, "if_" + sto.m_ifCounter)+NEW_LINE;
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    }
    
    public void endIf(STO sto){
    	String string = NEW_LINE + "!Ending if_" + sto.vecIfCount.elementAt(sto.vecIfCount.size()-1)+ NEW_LINE;
    	Tuple p;
    	string += "if_"+sto.vecIfCount.elementAt(sto.vecIfCount.size()-1)+":";
    	sto.vecIfCount.remove(sto.vecIfCount.size()-1);
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    }
    
    public void rememberHighestOffset(){
    	vecHighestOffsets.addElement(highestOffset);
    	highestOffset = 1;
    }
    
    public void writeReturn(STO sto, Type type,String fname){
    	String string = NEW_LINE + "!Return " + NEW_LINE;
    	Tuple p;
    	
    	// return value is constant
    	if(sto instanceof ConstSTO){
    		if(type instanceof IntType)
    			string += SEPARATOR + createString(SET, String.valueOf(sto.getIntValue()), IN0)+NEW_LINE;
    		else if(type instanceof FloatType){
    			string += defineFloatVar("_t"+String.valueOf(floatCount),String.valueOf(sto.getFloatValue()));
    			string += SEPARATOR + createString(SET, "_t"+String.valueOf(floatCount),LOCAL0)+NEW_LINE;
    			string += SEPARATOR + createString(LOAD,LOCAL0,FLOAT0)+NEW_LINE;
    			floatCount++;
    		}
    		else if(type instanceof BoolType)
    			if(sto.getBoolValue())
    				string += SEPARATOR + createString(SET, String.valueOf(1), IN0)+NEW_LINE;
    			else
    				string += SEPARATOR + createString(SET, GLOBAL0, IN0)+NEW_LINE;
    	}
    	// return value is from variable
    	else if(sto instanceof VarSTO) {
    		// return value is from local variable
    		if(sto.getIsStatic()) {
    			String name= new String();
    			if(!(sto.getGlobal()))
    				name = "."+sto.getName()+"_"+fname;
    			else
    				name = sto.getName();
    				
    			if(type instanceof IntType) {
    				string += loadGlobalOrStaticVar(name,LOCAL0,IN0);
    			}
    			else if(type instanceof FloatType) {
    				string += loadGlobalOrStaticVar(name,LOCAL0,FLOAT0);
        			if(sto.getType() instanceof IntType)
        				string += SEPARATOR + createString(FITOS,FLOAT0,FLOAT0) + NEW_LINE;
    			}
    			else if(type instanceof BoolType) {
    				string += loadGlobalOrStaticVar(name,LOCAL0,IN0);
    			}
    		}
    		else if(sto.getOffset() > 0) {
    			if(type instanceof IntType)
    				string += loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,IN0);
    			else if(type instanceof FloatType) {
    				string += loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,FLOAT0);
    				if(sto.getType() instanceof IntType)
    					string += SEPARATOR + createString(FITOS,FLOAT0,FLOAT0) + NEW_LINE;
    			}
    			else if(type instanceof BoolType)
        			string += loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,IN0);
    		}
    		else {
    			if(type instanceof IntType)
    				string += loadGlobalOrStaticVar(sto.getName(),LOCAL0,IN0);
    			else if(type instanceof FloatType) {
    				string += loadGlobalOrStaticVar(sto.getName(),LOCAL0,FLOAT0);
    				if(sto.getType() instanceof IntType)
    					string += SEPARATOR + createString(FITOS,FLOAT0,FLOAT0) + NEW_LINE;
    			}
    			else if(type instanceof BoolType)
        			string += loadGlobalOrStaticVar(sto.getName(),LOCAL0,IN0);
    		}
    	} 
    	// return value is an expr
    	else {
			if(type instanceof IntType) {
				string += loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,IN0);
			}
			else if(type instanceof FloatType) {
				string += loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,FLOAT0);
    			if(sto.getType() instanceof IntType)
    				string += SEPARATOR + createString(FITOS,FLOAT0,FLOAT0) + NEW_LINE;
			}
			else if(type instanceof BoolType) {
				string += loadLocalVar(String.valueOf(sto.getOffset()*(-4)),LOCAL0,IN0);
			}
    	}
    	string += NEW_LINE+"\t"+RETRESTORE+NEW_LINE;
    	p = new Tuple(fname, string);
    	vecPair.addElement(p);
    }
    
    public void returnRestore(String fname){
    	String string = NEW_LINE + "!Return " + NEW_LINE;
    	Tuple p;
    	string = NEW_LINE+"\t"+RETRESTORE+NEW_LINE;
    	string += NEW_LINE+"\t"+RETRESTORE+NEW_LINE;
    	p = new Tuple(fname, string);
    	vecPair.addElement(p);
    }
    
    public int writeIncDec(STO sto, Operator op, boolean b, String funcName){
    	String string, opName;
    	int tempHighestOffset = highestOffset + 1;
    	
    	if(op.getName().equals("++"))
    		opName = "add";
    	else
    		opName = "sub";
    	
    	if(b)
    		string = "Pre";
    	else
    		string = "Post";
    	
    	string = NEW_LINE + "!"+ string + op.getName() + NEW_LINE;
    	Tuple p;
    	
    	if(!sto.getGlobal())
    		string += loadGlobalOrStaticVar("."+sto.getName()+"_" + funcName, LOCAL0, LOCAL1);
    	else if(sto.getOffset() < 0)
			string += loadGlobalOrStaticVar(sto.getName(), LOCAL0, LOCAL1);
		else
			string += loadLocalVar(String.valueOf(-sto.getOffset()*4), LOCAL0, LOCAL1);
    	
    	if(b){
    		string += SEPARATOR + createString(ANY_3, opName, LOCAL1, "1", LOCAL1)+NEW_LINE;
    		string += SEPARATOR + createString(STORE, LOCAL1, LOCAL0)+NEW_LINE;
    		string += storeLocalVar(String.valueOf(-tempHighestOffset*4), LOCAL0, LOCAL1);
    	}
    	else{
    		string += storeLocalVar(String.valueOf(-tempHighestOffset*4), LOCAL2, LOCAL1);
    		string += SEPARATOR + createString(ANY_3, opName, LOCAL1, "1", LOCAL1)+NEW_LINE;
    		string += SEPARATOR + createString(STORE, LOCAL1, LOCAL0)+NEW_LINE;
    	}
    	
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    	++highestOffset;
    	return tempHighestOffset;
    }
    
    public int writeIncDecFloat(STO sto, Operator op, boolean b, String funcName){
    	String string=new String(),opName;
    	int tempHighestOffset = highestOffset + 1;
    	
    	if(op.getName().equals("++"))
    		opName = "fadds";
    	else
    		opName = "fsubs";
    	
    	if(b)
    		string += "Pre";
    	else
    		string += "Post";
    	
    	string = NEW_LINE + "!"+ string +"(" + sto.getName() +")" + NEW_LINE;
    	string+=SEPARATOR+createString(SET,String.valueOf(1),FLOAT0)+NEW_LINE;
    	string+=SEPARATOR+createString(FITOS,FLOAT0,FLOAT0)+NEW_LINE;
    	
    	if(!sto.getGlobal())
    		string += loadGlobalOrStaticVar("."+sto.getName()+"_" + funcName, LOCAL0, FLOAT1);
    	else if(sto.getOffset() < 0)
			string += loadGlobalOrStaticVar(sto.getName(), LOCAL0, FLOAT1);
		else
			string += loadLocalVar(String.valueOf(-sto.getOffset()*4), LOCAL0, FLOAT1);
    	
    	if(b){
    		string += SEPARATOR + createString(ANY_3, opName, FLOAT1, FLOAT0, FLOAT0)+NEW_LINE;
    		string += storeLocalVar(String.valueOf(-tempHighestOffset*4), LOCAL0, FLOAT0);
    	}
    	else{
    		string += storeLocalVar(String.valueOf(-tempHighestOffset*4), LOCAL1, FLOAT1);
    		string += SEPARATOR + createString(ANY_3, opName, FLOAT1, FLOAT0, FLOAT0)+NEW_LINE;
    		string += SEPARATOR + createString(STORE, FLOAT0, LOCAL0)+NEW_LINE;
    	}
    	
    	vecPair.addElement(new Tuple(funcName, string));
    	++highestOffset;
    	return tempHighestOffset;
    }
    
    public void writeExit(STO sto, String funcName){
    	String string = NEW_LINE + "!Exit " + sto.getName();
    	Tuple p;
    	if(sto instanceof ConstSTO){
    		string += NEW_LINE + SEPARATOR + createString(SET, String.valueOf(sto.getIntValue()), OUT0)+NEW_LINE;
    		string += SEPARATOR + createString("call\texit"+NEW_LINE + SEPARATOR + "nop")+NEW_LINE;
    	}
    	else if(sto instanceof VarSTO){
    		if(!sto.getGlobal())
        		string += NEW_LINE + loadGlobalOrStaticVar("."+sto.getName()+"_" + funcName, LOCAL0, OUT0);
    		else if(sto.getOffset() < 0)
    			string += NEW_LINE + loadGlobalOrStaticVar(sto.getName(), LOCAL0, OUT0);
    		else
    			string += NEW_LINE + SEPARATOR +loadLocalVar(String.valueOf(-sto.getOffset()*4), LOCAL0, OUT0);
    		
    		string += SEPARATOR + createString("call\texit"+NEW_LINE + SEPARATOR + "nop")+NEW_LINE;
    	}
    	else if(sto instanceof ExprSTO){
    		string += NEW_LINE + SEPARATOR +loadLocalVar(String.valueOf(-sto.getOffset()*4), LOCAL0, OUT0);
    		string += SEPARATOR + createString("call\texit"+NEW_LINE + SEPARATOR + "nop")+NEW_LINE;
    	}
    	
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    }
    
    public int writeFuncCall(STO sto, String funcName, int offset){
    	highestOffset++;
    	if(offset > highestOffset)
    		highestOffset++;
    	String string = NEW_LINE+"!"+sto.getName()+NEW_LINE;
    	string += SEPARATOR + createString("call\t"+ sto.getName() +NEW_LINE + SEPARATOR + "nop")+NEW_LINE;
    	if(((FuncSTO)sto).getReturnType() instanceof FloatType)
			string += storeLocalVar(String.valueOf((-highestOffset*4)), LOCAL7, FLOAT0);
		else
			string += storeLocalVar(String.valueOf((-highestOffset*4)), LOCAL7, OUT0);
    	
    	Tuple p;
    	p = new Tuple(funcName, string);
    	vecPair.addElement(p);
    	return highestOffset;
    }
    

  //define a float var within a loacal
    public String defineFloatVar(String floatName, String floatValue){
		String string = new String();
		string += NEW_LINE + SEPARATOR +DATA;
		string += SEPARATOR +ALIGN;
		string += SEPARATOR + floatName + ": .single 0r" + floatValue+ TWO_NEW_LINE;
		string += SEPARATOR + ".section" +'"'+".text"+'"'+ NEW_LINE;
		string += SEPARATOR + ALIGN;
    	return string;
	}
    
    //store value into a local variable
    public String storeLocalVar(String offset, String localReg, String valueReg){
    	String string = new String();
    	string += SEPARATOR + createString(SET, offset, localReg)+NEW_LINE;
		string += SEPARATOR + createString(ADD, FP, localReg, localReg)+NEW_LINE;
		string += SEPARATOR + createString(STORE, valueReg, localReg)+NEW_LINE;
    	return string;
	}
    
    //store value into a gloabal and local variable
    public String storeGlobalOrStaticVar(String name, String localReg, String valueReg){
		String string = new String();
		string += SEPARATOR + createString(SET, name, localReg)+NEW_LINE;
		string += SEPARATOR + createString(STORE, valueReg, localReg);
    	return string;
	}
    
    //loads value from a loacal variable
    public String loadLocalVar(String offset, String localReg, String destReg){
		String string = new String();
		string += NEW_LINE + SEPARATOR + createString(SET, offset, localReg)+NEW_LINE;
    	string += SEPARATOR + createString(ADD, FP, localReg, localReg)+NEW_LINE;
    	string += SEPARATOR + createString(LOAD, localReg, destReg )+NEW_LINE;
    	return string;
	}
    
    //loads value from a global or local variable
    public String loadGlobalOrStaticVar(String name, String localReg, String destReg){
		String string = new String();
		string += SEPARATOR + createString(SET, name, localReg)+NEW_LINE;
		string += SEPARATOR + createString(LOAD, localReg, destReg )+NEW_LINE;
    	return string;
	}
    int compCounter=0;
    public String findOp(String op, String tempReg1, String tempReg2, String offset ){
    	String string = new String();
    	if(op.equals("+")){
    		string += SEPARATOR + createString(ADD, tempReg1, tempReg2,tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, LOCAL1);
    	}
    	else if(op.equals("-")){
    		string += SEPARATOR + createString(SUB, tempReg1, tempReg2,tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, LOCAL1);
    	}
    	else if(op.equals("*")){
    		string += SEPARATOR + createString(MUL, tempReg1, tempReg2)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, OUT0);
    	}
    	else if(op.equals("/")){
    		string += SEPARATOR + createString(DIV, tempReg1, tempReg2)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, OUT0);
    	}
    	else if(op.equals("%")){
    		string += SEPARATOR + createString(REM, tempReg1, tempReg2)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, OUT0);
    	}
    	else if(op.equals("&")){
    		string += SEPARATOR + createString(AND, tempReg1, tempReg2, tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, LOCAL1);
    	}
    	else if(op.equals("|")){
    		string += SEPARATOR + createString(OR, tempReg1, tempReg2, tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, LOCAL1);
    	}
    	else if(op.equals("^")){
    		string += SEPARATOR + createString(XOR, tempReg1, tempReg2, tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, LOCAL1);
    	}
    	else if(op.equals(">")){
    		string += NEW_LINE+SEPARATOR + createString(COMP, tempReg1, tempReg2, "bg", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("<")){
    		string += NEW_LINE+SEPARATOR + createString(COMP, tempReg1, tempReg2, "bl", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals(">=")){
    		string += NEW_LINE+SEPARATOR + createString(COMP, tempReg1, tempReg2, "bge", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("<=")){
    		string += NEW_LINE+SEPARATOR + createString(COMP, tempReg1, tempReg2, "ble", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("==")){
    		string += NEW_LINE+SEPARATOR + createString(COMP, tempReg1, tempReg2, "be", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;	
    		++highestOffset;
    	}
    	else if(op.equals("!=")){
    		string += NEW_LINE+SEPARATOR + createString(COMP, tempReg1, tempReg2, "bne", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("&&")){
    		string += NEW_LINE+SEPARATOR + createString(ANDAND, tempReg1,"compare" + compCounter, tempReg2,"compare" + compCounter, "endComp"+compCounter, "compare" + compCounter+":","endComp"+compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL0,LOCAL5);
    		compCounter++;
    	}
    	else if(op.equals("||")){
    		string += NEW_LINE+SEPARATOR + createString(OROR, tempReg1,"compare" + compCounter, tempReg2,"compare" + compCounter, "endComp"+compCounter, "compare" + compCounter+":","endComp"+compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL0,LOCAL5);
    		compCounter++;
    	}
    	return string;
    }
    public String findOpfloat(String op, String tempReg1, String tempReg2, String offset ){
    	String string = new String();
    	string += NEW_LINE;
    	if(op.equals("+")){
    		string += SEPARATOR + createString(FADDS, tempReg1, tempReg2,tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, FLOAT1);
    	}
    	else if(op.equals("-")){
    		string += SEPARATOR + createString(FSUBS, tempReg1, tempReg2,tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, FLOAT1);
    	}
    	else if(op.equals("*")){
    		string += SEPARATOR + createString(FMULS, tempReg1, tempReg2,tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, FLOAT1);
    	}
    	else if(op.equals("/")){
    		string += SEPARATOR + createString(FDIVS, tempReg1, tempReg2,tempReg1)+NEW_LINE;
    		string += storeLocalVar(offset , LOCAL0, FLOAT1);
    	}
    	else if(op.equals(">")){
    		string += NEW_LINE+SEPARATOR + createString(FLOATCOMP, tempReg1, tempReg2, "fbl", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("<")){
    		string += NEW_LINE+SEPARATOR + createString(FLOATCOMP, tempReg1, tempReg2, "fbg", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals(">=")){
    		string += NEW_LINE+SEPARATOR + createString(FLOATCOMP, tempReg1, tempReg2, "fble", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("<=")){
    		string += NEW_LINE+SEPARATOR + createString(FLOATCOMP, tempReg1, tempReg2, "fbge", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("==")){
    		string += NEW_LINE+SEPARATOR + createString(FLOATCOMP, tempReg1, tempReg2, "fbe", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	else if(op.equals("!=")){
    		string += NEW_LINE+SEPARATOR + createString(FLOATCOMP, tempReg1, tempReg2, "fbne", "compare" + compCounter,"compare" + compCounter+":")+NEW_LINE;
    		string += storeLocalVar(offset, LOCAL1,LOCAL0);
    		compCounter++;
    		++highestOffset;
    	}
    	return string;
    }
    public String createString(String template, String ... params) {
        return String.format(template, (Object[])params);
    }
    
    public void writeString(String template) {
    	try {
            fileWriter.write(template);
        } catch (IOException e) {
            System.err.println(ERROR_IO_WRITE);
            e.printStackTrace();
        }
    }
    
    public void writeAssembly(String template, String ... params) {
        StringBuilder asStmt = new StringBuilder();
        for (int i=0; i < indent_level; i++) {
            asStmt.append(SEPARATOR);
        }
        asStmt.append(String.format(template, (Object[])params));
        try {
            fileWriter.write(asStmt.toString());
            
        } catch (IOException e) {
            System.err.println(ERROR_IO_WRITE);
            e.printStackTrace();
        }
    }
} 