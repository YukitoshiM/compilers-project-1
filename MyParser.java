
//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

import java_cup.runtime.*;
import java.util.*;



class MyParser extends parser
{

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	MyParser (Lexer lexer, ErrorPrinter errors)
	{
		m_lexer = lexer;
		m_asmgen = new AssemblyCodeGenerator("rc.s");
		m_symtab = new SymbolTable ();
		m_errors = errors;
		m_nNumErrors = 0;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	Ok ()
	{
		return (m_nNumErrors == 0);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Symbol
	scan ()
	{
		Token		t = m_lexer.GetToken ();

		//	We'll save the last token read for error messages.
		//	Sometimes, the token is lost reading for the next
		//	token which can be null.
		m_strLastLexeme = t.GetLexeme ();

		switch (t.GetCode ())
		{
			case sym.T_ID:
			case sym.T_ID_U:
			case sym.T_STR_LITERAL:
			case sym.T_FLOAT_LITERAL:
			case sym.T_INT_LITERAL:
			case sym.T_CHAR_LITERAL:
				return (new Symbol (t.GetCode (), t.GetLexeme ()));
			default:
				return (new Symbol (t.GetCode ()));
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	syntax_error (Symbol s)
	{
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	report_fatal_error (Symbol s)
	{
		m_nNumErrors++;
		if (m_bSyntaxError)
		{
			m_nNumErrors++;

			//	It is possible that the error was detected
			//	at the end of a line - in which case, s will
			//	be null.  Instead, we saved the last token
			//	read in to give a more meaningful error 
			//	message.
			m_errors.print (Formatter.toString (ErrorMsg.syntax_error, m_strLastLexeme));
		}
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	unrecovered_syntax_error (Symbol s)
	{
		report_fatal_error (s);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	DisableSyntaxError ()
	{
		m_bSyntaxError = false;
	}

	public void
	EnableSyntaxError ()
	{
		m_bSyntaxError = true;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String 
	GetFile ()
	{
		String filename = m_lexer.getEPFilename ();
		String returnname = new String();
		int index = filename.indexOf('.');
		returnname = filename.substring(0,index);
		return returnname;
	}

	public int
	GetLineNum ()
	{
		return (m_lexer.getLineNumber ());
	}

	public void
	SaveLineNum ()
	{
		m_nSavedLineNum = m_lexer.getLineNumber ();
	}

	public int
	GetSavedLineNum ()
	{
		return (m_nSavedLineNum);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoProgramStart()
	{
		// Opens the global scope.
		m_symtab.openScope ();
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoProgramEnd()
	{
		m_symtab.closeScope ();
		m_asmgen.do_data(vecId,  vecType, vecBool, vecFunc, vecArraySize);
		m_asmgen.add_rodata();
		m_asmgen.do_func(vecFuncId, vecRetType, vecRetRef);
		m_asmgen.dispose();
	}

	//for global and static declarelation
	Vector<STO> vecId  =new Vector<STO>();
	Vector<Type> vecType  =new Vector<Type>();
	Vector<Boolean> vecBool  =new Vector<Boolean>();
	Vector<String> vecFunc  =new Vector<String>();
	
	//for local declarelation
	Vector<STO> vecLId  =new Vector<STO>();
	Vector<Type> vecLType  =new Vector<Type>();
	Vector<Boolean> vecLBool  =new Vector<Boolean>();
	Vector<Integer> vecArraySize = new Vector<Integer>();
	int offset = 0;
	int arrayOffset = -1;
	
	//store array size for var. dec.
	int arraySize = 0;
	int arrayInd = -1;
		
	//for functions
	Vector<String> vecFuncId  =new Vector<String>();
	Vector<Type> vecRetType  =new Vector<Type>();
	Vector<Boolean> vecRetRef  =new Vector<Boolean>();
	String curFunc = null, lastFunc = null;
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoVarDecl (Vector<STO> lstIDs, Type typ, boolean isStatic)
	{
		
		for (int i = 0; i < lstIDs.size (); i++)
		{
			STO id = lstIDs.elementAt (i);
			VarSTO sto;
			
			
			if(m_symtab.getLevel() == 1 || isStatic){
				STO ids = lstIDs.elementAt (i);
				vecId.addElement(ids);
				vecType.addElement(typ);
				vecBool.addElement(isStatic);
				vecArraySize.addElement(arraySize);
				//this remembers function name for static variable name mangling
				if(m_symtab.getLevel() != 1 ){
					vecFunc.addElement(curFunc);
					m_asmgen.localInit( ids, typ, offset, curFunc, id.getId(), isStatic);
				}
				else
					vecFunc.addElement("");
			}
			else if(lastFunc == null){	
				lastFunc = curFunc;
				offset++;
			
				STO ids = lstIDs.elementAt (i);
				m_asmgen.localInit( ids, typ, offset, curFunc, id.getId(), isStatic);
			}
			else{
				if(lastFunc.equals(curFunc)){
					offset++;
					STO ids = lstIDs.elementAt (i);
					m_asmgen.localInit( ids, typ, offset, curFunc, id.getId(), isStatic);
				}
				else{
					lastFunc = curFunc;
					offset = 1;
					STO ids = lstIDs.elementAt (i);
					m_asmgen.localInit( ids, typ, offset, curFunc, id.getId(), isStatic);
				}
			}
			/*if(id.isError() || id.getType() instanceof NullPointerType){
				
			}
			else if (m_symtab.accessLocal (id.getId()) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id.getId()));
				
			}
			else if(!(id.getType() instanceof VoidType) && !(id.getType() instanceof ArrayType) 
					&& !(id.getType() instanceof PointerType) &&!(id.getType() instanceof PointerGroupType) 
					&& !(typ.isAssignable(id.getType())) &&!(id.getType() instanceof FunctionPointerType)  ){
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, id.getType().getName(), typ.getName()));
				return;
			}
			else if(!(id instanceof FuncSTO) && m_symtab.accessGlobal (id.getName()) != null){
				if(!(id.isConst())){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, id.getId()));
				}
			}		
			else if(isStatic && !(id.isConst())){
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, id.getId()));
			}
			else if(id.isVar()) {
				
				if((id.getType() instanceof ArrayType) && !(typ.isArray())) {
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, id.getType().getName(), typ.getName()));
					
				}	
			}
			else */if(id.isExpr() && (id.getType() instanceof ArrayType) ) {
				
				if((id.getType()).getSubType() instanceof StructType ) return;
				
				( id.getType()).setSubType(typ);
				
				if(typ instanceof ArrayType)
				((ArrayType) id.getType()).makeClone();
				
				String name = (typ.getName()+id.getType().getName());
				id.getType().setName(name);
				//VarSTO 		sto;// = new VarSTO (id.getName(),id.getType());
				
				//This codes added for pp2 and checks global, static, or local and setting offset
				if(m_symtab.getLevel() == 1 || isStatic)
					sto = new VarSTO (id.getName(),id.getType(), 0, -1, isStatic);
				else
					sto = new VarSTO (id.getName(),id.getType(), 0, offset, isStatic);
				//updating offset with array size
				if(!(m_symtab.getLevel() == 1) && !isStatic)
					offset = offset + arraySize - 1;
				arraySize = 0;
				
				sto.setIsModifiable(false);
				((ArrayType)sto.getType()).setSubType(typ);
				if(typ.isArray())
					((ArrayType)sto.getType()).makeClone();
				m_symtab.insert (sto);
			}
			else if(id.getType() instanceof PointerGroupType) {
				
				boolean checker = true;
				PointerType pt = (PointerType)((PointerGroupType)id.getType()).getPtr();
				pt.setBase(typ);
				
				while(checker){
					if(pt.getSubType() != null){
						pt = (PointerType) pt.getSubType();
					}
					else{
						pt.setSubType(typ);
						checker = false;
					}
				}

				STO sto1 =((ExprSTO)id).getSTO();
				
				if (sto1 != null && sto1.getType() instanceof ArrayType) {
				ArrayType arr = (ArrayType)sto1.getType();
				PointerType pt2 = (PointerType)((PointerGroupType)id.getType()).getPtr();
				boolean checker2 = true;
				while(checker2) {
					if(arr.getSubType() != null && (arr.getSubType() instanceof ArrayType) &&
							pt2.getSubType() != null && (pt2.getSubType() instanceof PointerType)) {
						arr = (ArrayType) arr.getSubType();
						pt2 = (PointerType) pt2.getSubType();
					}
					else{
						if(arr.getSubType().isAssignable(pt2.getSubType())) {
							checker2 = false;
						} else {
							PointerType pt3 = (PointerType)((PointerGroupType)id.getType()).getPtr();	
							Type temp2 = typ;
							while(temp2 instanceof ArrayType) {
								temp2 = ((ArrayType) temp2).getSubType();
							}
							
							String temp;
							if (typ instanceof ArrayType) 
								temp = temp2.getName()+"*";
							else
								temp = typ.getName()+"*";
							while(pt3.getSubType() instanceof PointerType) {
								temp+="*";
								pt3 = (PointerType) pt3.getSubType();
							}
							
							if(pt3.getSubType() instanceof ArrayType) {
								ArrayType pt4 = (ArrayType) pt3.getSubType();
								temp+="*";
							
								while(pt4.getSubType() instanceof ArrayType) {
									temp+="*";
									pt4 = (ArrayType) pt4.getSubType();
								}
							}
							m_nNumErrors++;
							m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, sto1.getType().getName(), temp));
							return;
						}
					}
				}
				
				}
				else if( (sto1 != null) && !(sto1.getType() instanceof NullPointerType) && !(sto1.getType() instanceof PointerType)  &&
							!(sto1.getType() instanceof ArrayType) ) {
					PointerType tru = (PointerType) ((PointerGroupType) id.getType()).getPtr();
					String temp2 = typ.getName()+"*";
					while (tru.getSubType() instanceof PointerType) {
						tru = (PointerType) tru.getSubType();
					}
					m_nNumErrors++;
					m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, sto1.getType().getName(), temp2));
				}
				sto = new VarSTO ( id.getId(),((PointerGroupType)id.getType()).getPtr(),
						((PointerGroupType)id.getType()).getSize() );
				m_symtab.insert(sto);
			}
			else if(typ instanceof FunctionPointerType){
				if(!(id.getType() instanceof VoidType) && !(id.getType().isNulPtr()) && (!(id.isFunc()) && !id.getType().isFuncPtr())){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, (id.getType()).getName(), id.getType().getName()));
					return;
				}
				else if(!(id.getType() instanceof VoidType) &&!(id.getType().isNulPtr()) && (!(id.isFunc()) && !id.getType().isFuncPtr())){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, (id.getType()).getName(),typ.getName()));
					return;
				}
				else if( !(id.isConst() && id.getType() instanceof VoidType) && !(id.getType().isNulPtr()) && ((id.isFunc()) || typ.isFuncPtr())){
					Vector<STO> desVec = ((FunctionPointerType)typ).getParams();
					Vector<STO> rVec = new Vector<STO>();
					
					if(!id.isVar())
						rVec = ((FuncSTO)id).getParamList();
					else if(id.isVar())
						rVec = ((FunctionPointerType)(id.getType())).getParams();
					
					String strR = "";
					int numStar = 0;
					
					if(!((FunctionPointerType)id.getType()).getReturnType().isPtr())
						strR = "funcptr : " + (((FunctionPointerType)id.getType()).getReturnType()).getName();
					else if(((FuncSTO)id).getReturnType().isPtr()){
						strR = "funcptr : " + ((PointerType)((FunctionPointerType)id.getType()).getReturnType()).getBase().getName();
						numStar = ((PointerType)((FuncSTO)id).getReturnType()).getPtrLvl();
						while(numStar != 0){
							strR += "*";
							--numStar;
						}
					}
					if(id.isFunc())
						if( ((FuncSTO) id).getRef() ) 
							strR+=" &";
					else if(id.isVar())
						if(((FunctionPointerType)id.getType()).getIsRef())
							strR+=" &";
						
					strR += " (" ;
						
					for(int j = 0; j < rVec.size();++j){
						if(!rVec.elementAt(j).getType().isPtr())
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
					strR += ") ";
					if(id.isVar())
						strR = id.getType().getName();
					strR = id.getType().getName();
					
					String strDes = "";
					
					if(!((FunctionPointerType)typ).getReturnType().isPtr())
						strDes = "funcptr : " + (((FunctionPointerType)typ).getReturnType()).getName();
					else {
						strDes = "funcptr : " + ((PointerType)((FunctionPointerType)typ).getReturnType()).getBase().getName();
						numStar = ((PointerType)((FunctionPointerType)typ).getReturnType()).getPtrLvl();
						while(numStar != 0){
							strDes += "*";
							--numStar;
						}
					}
						
					if( ((FunctionPointerType) typ).getIsRef() ) {
						strDes += " &";
					}
					
					strDes += " (" ;
					
					for(int j = 0; j < rVec.size();++j){
						if(!desVec.elementAt(j).getType().isPtr())
							strDes += desVec.elementAt(j).getType().getName();
						else{
							strDes += ((PointerType)desVec.elementAt(j).getType()).getBase().getName();
							
							numStar = ((PointerType) (desVec.elementAt(j).getType())).getPtrLvl();
							
							while(numStar != 0){
								strDes += "*";
								--numStar;
							}
						}
						
						if(((VarSTO) desVec.elementAt(j)).getRef()) 
							strDes += " &" + desVec.elementAt(j).getName();
						else
							strDes += " " + desVec.elementAt(j).getName();
						
						if((j + 1 ) != desVec.size()){
							strDes += ", ";
						}
					}
					strDes += ") ";
					
					
					if(!typ.getName().equals("funcptr") )
						strDes = typ.getName();
					
					if(!id.isVar()){	
						if(!((FunctionPointerType)typ).getReturnType().isEquivalent(((FuncSTO)id).getReturnType())){
							m_nNumErrors++;
							m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strDes, strR));
							return;
						}
						rVec = ((FuncSTO)id).getParamList();
					}
					else if(id.isVar()){
						if(!((FunctionPointerType)typ).getReturnType().isEquivalent(((FunctionPointerType)(id.getType())).getReturnType())){
							m_nNumErrors++;
							m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strDes, strR));
							return;
						}
						rVec = ((FunctionPointerType)(id.getType())).getParams();
					}
					
					if(desVec.size() != rVec.size()) {
						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error5n_Call, rVec.size(), desVec.size()));
						return;
					}
						
					STO stoLParam;
					STO stoRParam;
					
					for(int j = 0; j < desVec.size(); ++j){
						stoLParam = desVec.elementAt(j);
						stoRParam = rVec.elementAt(j);
						
						if(!(stoLParam.getType().isEquivalent(stoRParam.getType()))){
							m_nNumErrors++;
							m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
						}
						else if(!(((VarSTO)stoLParam).getRef() ==  ((VarSTO)stoRParam).getRef())){
							m_nNumErrors++;
							m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
						}
					}
						((FunctionPointerType)typ).setFunc(id);
					}
					sto = new VarSTO (id.getId(),typ);
					m_symtab.insert (sto);
			}
			else if(typ instanceof StructType){
				
				StructType  ttt = new StructType(typ.getName(),0);
				ttt.setStruct(typ.getStruct());

				ttt.setBase(((StructType)typ).getBase());
				sto = new VarSTO (id.getId(),ttt);
				m_symtab.insert (sto);
			}
			else {
				if(m_symtab.getLevel() == 1 || isStatic) {
					sto = new VarSTO (id.getId(),typ, 0, -1, isStatic);
					if(m_symtab.getLevel()!=1) {
						sto.setGlobal(false);
					}
				}
				else{
					sto = new VarSTO (id.getId(),typ, 0, offset, isStatic);
				}
				m_symtab.insert (sto);
			}
		}
	}
	
	void DoAutoDecl(String id, STO sto, boolean isStatic){
		sto.setId(id);
		if(m_symtab.getLevel() == 1 || isStatic){
			vecId.addElement(sto);
			vecType.addElement(sto.getType());
			vecBool.addElement(isStatic);
			vecArraySize.addElement(arraySize);
			//this remembers function name for static variable name mangling
			if(m_symtab.getLevel() != 1 ){
				vecFunc.addElement(curFunc);
				m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
			}
			else
				vecFunc.addElement("");
		}
		else if(lastFunc == null){	
			lastFunc = curFunc;
			offset++;
			m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
		}
		else{
			if(lastFunc.equals(curFunc)){
				offset++;
				m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
			}
			else{
				lastFunc = curFunc;
				offset = 1;
				m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
			}
		}
		VarSTO sto1 = new VarSTO (id, sto.getType(), 0, offset, isStatic);
		if(isStatic && m_symtab.getLevel() != 1)
			sto1.setGlobal(false);
		m_symtab.insert (sto1);
		
		/*This checks if VarSTO name and type with auto declaration
		 */

	}
	
	void DoAutoConstDecl(String id, STO sto,  boolean isStatic){
		sto.setId(id);
		/*if(m_symtab.getLevel() == 1 || isStatic){
			vecId.addElement(sto);
			vecType.addElement(sto.getType());
			vecBool.addElement(isStatic);
			vecArraySize.addElement(arraySize);
			//this remembers function name for static variable name mangling
			if(m_symtab.getLevel() != 1 ){
				vecFunc.addElement(curFunc);
				m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
			}
			else
				vecFunc.addElement("");
		}
		else if(lastFunc == null){	
			lastFunc = curFunc;
			offset++;
			m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
		}
		else{
			if(lastFunc.equals(curFunc)){
				offset++;
				m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
			}
			else{
				lastFunc = curFunc;
				offset = 1;
				m_asmgen.localInit( sto, sto.getType(), offset, curFunc, id, isStatic);
			}
		}*/
		
		ConstSTO sto1;
		
		
		sto1 = new ConstSTO (id,sto.getType(), isStatic, sto.getValue());
		
		if(m_symtab.getLevel() != 1)
			sto1.setOffset(offset);
		sto1.setIsStatic(isStatic);
		m_symtab.insert (sto1);
		
		/*This checks if VarSTO name and type with auto declaration
		 */

	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoExternDecl (Vector<String> lstIDs)
	{
		for (int i = 0; i < lstIDs.size (); i++)
		{
			String id = lstIDs.elementAt (i);

			if (m_symtab.accessLocal (id) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
			}
			
			VarSTO 		sto = new VarSTO (id);
			m_symtab.insert (sto);
		}
		
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoConstDecl (Vector<STO> lstIDs, Type typ, boolean isStatic)
	{
		boolean flag = true;
		
		for (int i = 0; i < lstIDs.size (); i++)
		{
			STO id = lstIDs.elementAt (i);
			
			//checks if the scope is in global or local, and checks correct function for locals 
			/*if(m_symtab.getLevel() == 1 || isStatic){
				STO ids = lstIDs.elementAt (i);
				vecId.addElement(ids);
				vecType.addElement(typ);
				vecBool.addElement(isStatic);
				vecArraySize.addElement(0);
				//this remembers function name for static variable name mangling
				if(m_symtab.getLevel() != 1 ){
					vecFunc.addElement(curFunc);
					m_asmgen.localInit( ids, typ, offset, curFunc, null, isStatic);
				}
				else
					vecFunc.addElement("");
			}
			else if(lastFunc == null){	
				lastFunc = curFunc;
				offset++;
				STO ids = lstIDs.elementAt (i);
				m_asmgen.localInit( ids, typ, offset, curFunc, null, isStatic);
			}
			else{
				if(lastFunc.equals(curFunc)){
					offset++;
					STO ids = lstIDs.elementAt (i);
					m_asmgen.localInit( ids, typ, offset, curFunc, null, isStatic);
				}
				else{
					lastFunc = curFunc;
					offset = 1;
					STO ids = lstIDs.elementAt (i);
					m_asmgen.localInit( ids, typ, offset, curFunc, null, isStatic);
				}
			}*/

			/*if(id.isError() || (id instanceof VarSTO)){
				
			}
			else if (m_symtab.accessLocal (id.getId()) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id.getId()));
				flag = false;
			}
			else if(!(id.getType() instanceof VoidType) && (id instanceof ConstSTO) && (((ConstSTO)id).getValue()==null)){
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error8b_CompileTime, id.getName()));
			}
			else if(!(id.getType() instanceof VoidType) && !(typ.isAssignable(id.getType()))){
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error8_Assign, id.getType().getName(), typ.getName()));
			}
			else */if(id instanceof ConstSTO){
				ConstSTO 	sto = new ConstSTO (id.getId(),typ,(id).getValue());
				if(m_symtab.getLevel() != 1)
					sto.setOffset(offset);
				sto.setIsStatic(isStatic);
				m_symtab.insert (sto);
			}
			//If we did not show erroe message for redeiclare, check #8 
			if(flag){
				/*if(m_symtab.accessGlobal (id.getName()) != null){
					if(!(id.isConst())){
						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, id.getId()));
					}
				}
				else if(isStatic){
					if(!(id.isConst())){
						m_nNumErrors++;
						m_errors.print (Formatter.toString(ErrorMsg.error8a_CompileTime, id.getId()));
					}
				}*/
				//reset flag
				flag = true;
			}
		}
		
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoTypedefDecl (Vector<String> lstIDs,Type typ)
	{
			
		for (int i = 0; i < lstIDs.size (); i++)
		{
			
			String id = lstIDs.elementAt (i);
			
			if (m_symtab.accessLocal (id) != null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
			}
			Type  ttt = typ;
			if(typ instanceof PointerGroupType){
				((PointerGroupType)typ).getPtr().setName(id);
			}
			if(typ instanceof StructType){
				ttt = new StructType(typ.getName(),0);
				(ttt).setStruct((typ).getStruct());
				if(((StructType)typ).getBase() == null)
					((StructType)ttt).setBase(typ.getName());
				else
					((StructType)ttt).setBase(((StructType)typ).getBase());
			}
			TypedefSTO 	sto = new TypedefSTO (id,ttt);
			m_symtab.insert (sto);
		}
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoStructdefDecl (String id)
	{
	
		if (m_symtab.accessLocal (id) != null)
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
		}	
		TypedefSTO 	sto = new TypedefSTO (id, new StructType(id,4));

		m_symtab.insert (sto);
		return sto;
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	Type curReturnType;
	Type lastReturnType;
	void
	DoFuncDecl_1 (String id, Type typ, boolean ref)
	{
			curReturnType = typ;

		curFunc = id;
		vecFuncId.addElement(id);
		vecRetType.addElement(typ);
		vecRetRef.addElement(ref);
		if (m_symtab.accessLocal (id) != null)
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.redeclared_id, id));
		}
		int retPtrLvl = 0;
		if(typ.isPtrGrp()){
			((PointerGroupType)typ).getPtr().setName(((PointerGroupType)typ).getBase().getName());
			((PointerType)((PointerGroupType)typ).getPtr()).setBase(((PointerGroupType)typ).getBase());
			//keep remains names for struct
			retPtrLvl = ((PointerType)((PointerGroupType)typ).getPtr()).getPtrLvl();
			if(((PointerGroupType)typ).getBase() instanceof StructType){
				((StructType)((PointerGroupType)typ).getBase()).setBase(((StructType)((PointerGroupType)typ).getBase()).getBase());
				 //storing pointer level of return type
				retPtrLvl = ((PointerGroupType)typ).getSize();
			}
			typ =((PointerGroupType)typ).getPtr();
		}
		FuncSTO sto = new FuncSTO (id);
		sto.setRetPtrLvl(retPtrLvl);
		sto.setReturnType(typ);
		sto.setRef(ref);
		sto.setType(new FunctionPointerType(id,typ,ref));
		m_symtab.insert (sto);

		m_symtab.openScope ();
		m_symtab.setFunc (sto);
	}
    
	
	void endingFunc(){
		m_asmgen.rememberHighestOffset();
		if(curReturnType instanceof VoidType)
			m_asmgen.returnRestore(curFunc);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoFuncDecl_2 (String isBreakCont)
	{	
		if(!(m_symtab.getFunc().getReturnType() instanceof VoidType)&&!(m_symtab.getFunc().getIsReturned())) {
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error6c_Return_missing);
		}
		else if(isBreakCont != null && isBreakCont.equals("break")){
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error12_Break);
		}
		else if(isBreakCont != null &&isBreakCont.equals("continue")){
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error12_Continue);
		}
		m_symtab.closeScope ();
		m_symtab.setFunc (null);
	}
	
	Type
	setPtrBaseType(Type typ) {
		
		Type ttt = ((PointerGroupType)typ).getPtr();
		
		((PointerType)ttt).setBase(((PointerGroupType)typ).getBase());
		
		return ttt;
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoFormalParams (Vector<STO> paramList)
	{
		if (m_symtab.getFunc () == null)
		{
			m_nNumErrors++;
			m_errors.print ("internal: DoFormalParams says no proc!");
		}
		else
		{
			m_symtab.getFunc().setNumOfParams(paramList.size());
			m_symtab.getFunc().setParamList(paramList);
			if(m_symtab.getFunc().getType() instanceof FunctionPointerType)
				((FunctionPointerType)m_symtab.getFunc().getType()).setParams(paramList);
		}

		for(int i = 0; i < paramList.size(); i++ ){
			m_symtab.insert(paramList.elementAt(i));
		}
	}

	/*Check 6a, b,and c?*/
    void DoReturnCheck(STO s){
    	if(s.isError()){return;}
    	Type t = m_symtab.getFunc().getReturnType();

    	m_asmgen.writeReturn(s, t,curFunc);
    	if(s.getType() instanceof VoidType){}
    	
    	/*if types are basic they keep basic types name*/
    	String strR = s.getType().getName(); 
    	String strRT =  t.getName();

    	int numStr = 0, equal = 0;
    	Type sType = s.getType();
    	/*this gets string for pointer*/
    	if(s.getType() instanceof PointerType){
    		strR = ((PointerType)s.getType()).getBase().getName();
			numStr = ((PointerType)s.getType()).getPtrLvl();
			equal = numStr;
			while(numStr != 0){
				strR +="*";
				--numStr;
			}
		}
		if(t instanceof PointerType){
			strRT = ((PointerType)t).getName();
			if(((PointerType)t).getBase() instanceof StructType){
				//strRT = t.getName();
				strRT = ((StructType)((PointerType)t).getBase()).getBase();
			}
			numStr = (m_symtab.getFunc().getRetPtrLvl());
			while(numStr != 0){
				if(equal == numStr){
					t = ((PointerType)t).getSubType();
					sType = ((PointerType)sType).getSubType();
					equal--;
				}
				strRT +="*";
				--numStr;
			}
		}
		/*if both are array getting subtype of arrays to compare assignability*/
		if(t instanceof PointerType && sType instanceof PointerType){
    		t = ((PointerType)t).getBase();
    		sType = ((PointerType)sType).getBase();
    	}
    	/*
    	if(!(t instanceof VoidType) && !(t instanceof FunctionPointerType && strR.equals("nullptr"))){
    		
    		if(sType instanceof VoidType){
    			m_nNumErrors++;
    			m_errors.print(ErrorMsg.error6a_Return_expr);
    		} 		
    		else if(!(t.isAssignable(sType))){
    			m_nNumErrors++;
    			m_errors.print(Formatter.toString(ErrorMsg.error6a_Return_type,strR,strRT));
    		}
    	}
    	else if(m_symtab.getFunc().getRef()){
    		//if statement below is check #6b and should be tested
			 if(!(s.getType().isEquivalent(t))){
		    	 m_nNumErrors++;
				 m_errors.print (Formatter.toString(ErrorMsg.error6b_Return_equiv,s.getType().getName(),t.getName()));
		     }
			 else if(!((s).isModLValue())){
		    	 m_nNumErrors++;
				 m_errors.print (ErrorMsg.error6b_Return_modlval);
		     }
		 }*/
    
    	if(m_symtab.getLevel() == 2) {
    		m_symtab.getFunc().setIsReturned(true);
    	}
    }
    /*Check #7, checks if arg for Exit(arg) is assingable to int.
     * I test with input float and bool gave me correct error
     * message. and input int compiled successfully. 
     */
    void DoExitCheck(STO s){
    	if(s.isError()){return;}
    	m_asmgen.writeExit(s, curFunc);
    	IntType i = new IntType("int",4);
    	if(!(i.isAssignable(s.getType()))){
    		m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error7_Exit ,s.getType().getName()));
    	}
    }
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoBlockOpen ()
	{
		// Open a scope.
		m_symtab.openScope ();
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void
	DoBlockClose()
	{
		m_symtab.closeScope ();
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoAssignExpr (STO stoDes,STO r)
	{
		if(stoDes.isError())
			return stoDes;
		if(r.isError() )
			return r;
		if(lastFunc == null){	
			lastFunc = curFunc;
			m_asmgen.assingInit( r, stoDes.getType(), stoDes.getType(), stoDes.getOffset(), curFunc, stoDes.getName(), stoDes.getIsStatic(), stoDes.getGlobal());
		}
		else{
			if(lastFunc.equals(curFunc)){
				m_asmgen.assingInit( r, stoDes.getType(), stoDes.getType(), stoDes.getOffset(), curFunc, stoDes.getName(), stoDes.getIsStatic(), stoDes.getGlobal());
			}
			else{
				lastFunc = curFunc;
				m_asmgen.assingInit( r, stoDes.getType(), stoDes.getType(), stoDes.getOffset(), curFunc, stoDes.getName(), stoDes.getIsStatic(), stoDes.getGlobal());
			}
		}
		
		if(stoDes.getType()== null)
			return stoDes;
		
		if(!(stoDes.getType().isPtr()) && !(stoDes.getType().isFuncPtr())){
			if (stoDes.isConst() || !stoDes.isModLValue())
			{
				m_nNumErrors++;
				m_errors.print(ErrorMsg.error3a_Assign);
				return new ErrorSTO ("not modlval in DoAssign");
			}
			if(!(r.getType() instanceof ArrayType) && !(stoDes.getType() instanceof ArrayType)) {
			
				if(!(stoDes.getType().isAssignable(r.getType()))){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, r.getType().getName(),stoDes.getType().getName()));
					return new ErrorSTO ("not assignable in DoAssign");
				}
			}
			else if((r.getType() instanceof ArrayType) && (stoDes.getType() instanceof ArrayType)) {
				if(!((ArrayType)stoDes.getType()).getSubType().isAssignable(((ArrayType)r.getType()).getSubType()) ) {
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, ((ArrayType)r.getType()).getSubType().getName(),((ArrayType)stoDes.getType()).getSubType().getName()));
					return new ErrorSTO ("not assignable in DoAssign");
				}
			}
			else {
				if((r.getType() instanceof ArrayType)) {
					if(!(stoDes.getType().isAssignable(((ArrayType)r.getType()).getSubType()))){
						m_nNumErrors++;
						m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, ((ArrayType)r.getType()).getSubType().getName(),stoDes.getType().getName()));
						return new ErrorSTO ("not assignable in DoAssign");
					}
				} 
				else {
					if(!(((ArrayType)stoDes.getType()).getSubType().isAssignable(r.getType()))){
						m_nNumErrors++;
						m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign,r.getType().getName() ,((ArrayType)stoDes.getType()).getSubType().getName() ));
						return new ErrorSTO ("not assignable in DoAssign");
					}
				}
			}
		}
		//check pointer assinability
		else if((stoDes.getType().isPtr())){
			
			if (stoDes.isConst() || !stoDes.isModLValue())
			{
				m_nNumErrors++;
				m_errors.print(ErrorMsg.error3a_Assign);
				return new ErrorSTO ("not modlval in DoAssign");
			}
			else if (!(stoDes.getType().isAssignable(r.getType()))){
				if(true){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, (r.getType()).getName(),stoDes.getType().getName()));
					return new ErrorSTO ("not assignable in DoAssign");
				}
			}
		}
		else if((stoDes.getType().isFuncPtr())){
			//Getting params from both side
			Vector<STO> desVec = ((FunctionPointerType)stoDes.getType()).getParams();
			Vector<STO> rVec = new Vector<STO>();
			int numStar = 0;
			String strR = r.getType().getName();
			//Getting param from FuncSTO or VarSTO
			if(!(r instanceof ConstSTO)&&!(r instanceof ExprSTO)){
				if(!r.isVar()){	
					rVec = ((FuncSTO)r).getParamList();
				}
				else if(r.isVar() ){
					rVec = ((FunctionPointerType)(r.getType())).getParams();
				}
				
				/*Start writing error message on rho*/
				if(!((FunctionPointerType)r.getType()).getReturnType().isPtr())
					strR = "funcptr : " + (((FunctionPointerType)r.getType()).getReturnType()).getName();
				else if(((FuncSTO)r).getReturnType().isPtr()){
					strR = "funcptr : " + ((PointerType)((FunctionPointerType)r.getType()).getReturnType()).getBase().getName();
					numStar = ((PointerType)((FuncSTO)r).getReturnType()).getPtrLvl();
					while(numStar != 0){
						strR += "*";
						--numStar;
					}
				}
				if(r.isFunc()){
					if( ((FuncSTO) r).getRef() ) {
						strR+=" &";
					}
				}
				else if(r.isVar()){
					if(((FunctionPointerType)r.getType()).getIsRef()){
						strR+=" &";
					}
				}
				
				strR += " (" ;
				
				for(int j = 0; j < rVec.size();++j){
					if(!rVec.elementAt(j).getType().isPtr())
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
				strR += ") ";
				
				/*need to be fixed*/
				//if(!r.getType().getName().equals("funcptr"))
				//	strR = r.getType().getName();
				//strR = r.getType().getName();
				
				/*ending to get strR*/
			}
			/*starting to get strDes*/
			
			String strDes = "";
		
			if(!((FunctionPointerType)stoDes.getType()).getReturnType().isPtr()){
				strDes = "funcptr : " + (((FunctionPointerType)stoDes.getType()).getReturnType()).getName();
			}
			else {
				strDes = "funcptr : " + ((PointerType)((FunctionPointerType)stoDes.getType()).getReturnType()).getBase().getName();
				numStar = ((PointerType)((FunctionPointerType)stoDes.getType()).getReturnType()).getPtrLvl();
				while(numStar != 0){
					strDes += "*";
					--numStar;
				}
			}
			
			if( ((FunctionPointerType) stoDes.getType()).getIsRef() ) {
				strDes += " &";
			}
			
			strDes += " (" ;
			
			for(int j = 0; j < desVec.size();++j){
				if(!desVec.elementAt(j).getType().isPtr())
					strDes += desVec.elementAt(j).getType().getName();
				else{
					strDes += ((PointerType)desVec.elementAt(j).getType()).getBase().getName();
					
					numStar = ((PointerType) (desVec.elementAt(j).getType())).getPtrLvl();
					while(numStar != 0){
						strDes += "*";
						--numStar;
					}
				}
				
				if(((VarSTO) desVec.elementAt(j)).getRef()) 
					strDes += " &" + desVec.elementAt(j).getName();
				else
					strDes += " " + desVec.elementAt(j).getName();
				
				if((j + 1 ) != desVec.size()){
					strDes += ", ";
				}
			}
			strDes += ") ";
			
			if(!stoDes.getType().getName().equals("funcptr") )
				strDes = stoDes.getType().getName();
			
			
			if(!(r.getType().isNulPtr()) && (!(r.isFunc()) && !r.getType().isFuncPtr())){
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
				return new ErrorSTO ("not assignable in DoAssign");
			}
			
			if(!(r.getType().isNulPtr()) && ((r.isFunc()) || r.getType().isFuncPtr())){
				
				if(!r.isVar()){	
					if(!((FunctionPointerType)stoDes.getType()).getReturnType().isEquivalent(((FuncSTO)r).getReturnType())){
						m_nNumErrors++;
						m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
						return new ErrorSTO ("not assignable in DoAssign");
					}
					rVec = ((FuncSTO)r).getParamList();
				}
				else if(r.isVar()){
					if(!((FunctionPointerType)stoDes.getType()).getReturnType().isEquivalent(((FunctionPointerType)(r.getType())).getReturnType())){
						m_nNumErrors++;
						m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
						return new ErrorSTO ("not assignable in DoAssign");
					}
					rVec = ((FunctionPointerType)(r.getType())).getParams();
				}
				

				if(desVec.size() != rVec.size()) {
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
					return (new ErrorSTO (r.getName ()));
				}
				
				STO stoLParam;
				STO stoRParam;
				
				for(int i = 0; i < desVec.size(); ++i){
					stoLParam = desVec.elementAt(i);
					stoRParam = rVec.elementAt(i);
					
					if(!(stoLParam.getType().isEquivalent(stoRParam.getType()))){
						
						m_nNumErrors++;
						m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
						return new ErrorSTO ("FuncType not assignable in DoAssign");
					}
					
					else if(!(((VarSTO)stoLParam).getRef() ==  ((VarSTO)stoRParam).getRef())){
						
						m_nNumErrors++;
						m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, strR, strDes));
						return new ErrorSTO ("FuncType not assignable in DoAssign");
					}
				}
				((FunctionPointerType)stoDes.getType()).setFunc(r);
			}
		}
		return stoDes;
	}
	
	
	STO DoBinaryExpr(STO l, Operator o, STO r) {
	  //if(l.isError()){return l;}
      //if(r.isError()){return r;}
		
	  STO result = o.checkOperands(l, r, o,	m_errors);
	  /*if (result instanceof ErrorSTO) {
		  m_nNumErrors++;
		  
		  result = new ErrorSTO ("");
	  }*/
	  
	  if((!(l instanceof ConstSTO) || !(r instanceof ConstSTO))) {
		  result.setOffset(m_asmgen.writeBinary(l, o, r, curFunc));
	  }
	  /*else if(!(o instanceof ArithmeticOp) && (!(l instanceof ConstSTO) || !(r instanceof ConstSTO))){
		  result.setOffset(m_asmgen.writeBinary(l, o, r, curFunc));
	  }*/
	  return result ;
	}
	
	STO DoUnaryOp(Operator o, STO r, boolean b) {
		STO result = o.checkOperands(r,o,m_errors);
		
		if(o instanceof IncOp || o instanceof DecOp){
			if(r.getType() instanceof IntType)
				result.setOffset(m_asmgen.writeIncDec(r, o, b, curFunc));
			else
				result.setOffset(m_asmgen.writeIncDecFloat(r, o,b, curFunc));
		}
		else if(o instanceof NotOp) {
			result.setOffset(m_asmgen.UnaryNotFmt(curFunc, r));
		}
		return result;
	}
	
	void StartIf(STO sto){
		sto.m_ifCounter = ifCounter; 
		sto.vecIfCount.addElement(ifCounter); 
		ifCounter++;
	    m_asmgen.writeIf(sto);
	}
	
	int ifCounter = 0, lastIf  = 0;
	STO CheckIf(STO r, String isBreakCont) {
		
	    m_asmgen.endIf(r);
	    
		if(r.isError()){return r;}
		
		if(isBreakCont.equals("break")){
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error12_Break);
			return new ErrorSTO ("");
		}
		
		if(isBreakCont.equals("continue")){
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error12_Continue);
			return new ErrorSTO ("");
		}
		
		if (!(r.getType() instanceof IntType) && !(r.getType() instanceof BoolType)) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error4_Test, r.getType().getName()));
			return new ErrorSTO ("");
		}
		
		return r;
	}
   
   STO CheckWhile(STO r, String isBreakCont) {
		
		if(r.isError()){return r;}
		
		if (!(r.getType() instanceof IntType) && !(r.getType() instanceof BoolType)) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error4_Test, r.getType().getName()));
			r = new ErrorSTO ("");
		}
		
		return r;
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
   STO
	DoFuncCall (STO sto, Vector<STO> v)
	{
		
		Type functype;
		functype = sto.getType();
		if((sto instanceof VarSTO)){
			sto = ((FunctionPointerType)sto.getType()).getFunc();
		}

		if(sto == null)return null;
		/*if (!sto.isFunc() && !functype.isFuncPtr())
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.not_function, sto.getName()));
			return (new ErrorSTO (sto.getName ()));
		} 
		else if(sto.isFunc()){
			if(((FuncSTO)sto).getNumOfParams() != v.size()) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error5n_Call, v.size(), ((FuncSTO)sto).getNumOfParams()));
				return (new ErrorSTO (sto.getName ()));
			}
		}
		else if(functype.isFuncPtr()){
			if(((FunctionPointerType)functype).getParams().size() != v.size()) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error5n_Call, v.size(), ((FunctionPointerType)functype).getParams().size()));
				return (new ErrorSTO (sto.getName ()));
			}
		}
		STO error = null;
		if(!v.isEmpty() && sto.isFunc()){
		  STO vSto, inSto;
		  Vector<STO> in = ((FuncSTO)sto).getParamList();
		  for(int i = 0; i < v.size(); i++){
			 vSto = v.elementAt(i);
			 inSto = in.elementAt(i);
			 
			 
			 if(((VarSTO)inSto).getRef()){
				 if(!(inSto.getType().isEquivalent(vSto.getType()))){
					 
			    	 m_nNumErrors++;
					 m_errors.print (Formatter.toString(ErrorMsg.error5r_Call,  vSto.getType().getName(), sto.getName(),inSto.getType().getName()));
					 error = (new ErrorSTO (sto.getName ()));
			     }
				 else if(!(inSto.isModLValue())){
			    	 m_nNumErrors++;
					 m_errors.print (Formatter.toString(ErrorMsg.error5c_Call, sto.getName(),inSto.getType().getName()));
					 error = (new ErrorSTO (sto.getName ()));
			     }
			 }
			 else if( (inSto.getType() instanceof PointerType) && (vSto.getType() instanceof ArrayType)) {
				 if(!((ArrayType) vSto.getType()).getSubType().isEquivalent(((PointerType) inSto.getType()).getSubType())) {
					 m_nNumErrors++;
					 m_errors.print (Formatter.toString(ErrorMsg.error5a_Call,  vSto.getType().getName(), sto.getName(),inSto.getType().getName()));
					 error =  (new ErrorSTO (sto.getName ()));
				 }
					 
			 }
			 else if( (vSto.getType() instanceof PointerType) && (inSto.getType() instanceof ArrayType)) {
				 if(!((ArrayType) inSto.getType()).getSubType().isEquivalent(((PointerType) vSto.getType()).getSubType())) {
					 m_nNumErrors++;
					 m_errors.print (Formatter.toString(ErrorMsg.error5a_Call,  vSto.getType().getName(), sto.getName(),inSto.getType().getName()));
					 error =  (new ErrorSTO (sto.getName ()));
				 }
					 
			 }
			 
			 else if(!(inSto.getType().isAssignable(vSto.getType()))){
				 
		    	 m_nNumErrors++;
				 m_errors.print (Formatter.toString(ErrorMsg.error5a_Call,  vSto.getType().getName(), sto.getName(),inSto.getType().getName()));
				 error =  (new ErrorSTO (sto.getName ()));
		     }
		  }
		}
		else if(!v.isEmpty() && functype.isFuncPtr()){
			  STO vSto, inSto;
			  Vector<STO> in = ((FunctionPointerType)functype).getParams();
			  for(int i = 0; i < v.size(); i++){
				 vSto = v.elementAt(i);
				 inSto = in.elementAt(i);
				 if(((VarSTO)inSto).getRef()){
					 if(!(inSto.getType().isEquivalent(vSto.getType()))){
				    	 m_nNumErrors++;
						 m_errors.print (Formatter.toString(ErrorMsg.error5r_Call,  vSto.getType().getName(), sto.getName(),inSto.getType().getName()));
						 error = (new ErrorSTO (sto.getName ()));
				     }
					 else if(!(vSto.isModLValue())){
				    	 m_nNumErrors++;
						 m_errors.print (Formatter.toString(ErrorMsg.error5c_Call, sto.getName(),inSto.getType().getName()));
						 error = (new ErrorSTO (sto.getName ()));
				     }
				 }
				 else if(!(inSto.getType().isAssignable(vSto.getType()))){

			    	 m_nNumErrors++;
					 m_errors.print (Formatter.toString(ErrorMsg.error5a_Call,  vSto.getType().getName(), inSto.getName(),inSto.getType().getName()));
					 error = (new ErrorSTO (sto.getName ()));
			     }
			  }
		}*/
		/*if(error != null)
			return error;*/
		STO sto1;
		if(((FuncSTO) sto).getRef()){
			sto1 = new VarSTO (sto.getName()+"()", ((FuncSTO) sto).getReturnType());
			return sto1;
	    }
		else{
			sto1 =new ExprSTO (sto.getName()+"()", ((FuncSTO) sto).getReturnType(), sto);
			sto1.setOffset(m_asmgen.writeFuncCall(sto, curFunc, offset));
			return sto1;
		}
		
	}
	


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoDesignator2_Dot (STO sto, String strID)
	{
		if(sto.isError()){return sto;}
		// Good place to do the struct checks
		if(!(sto.getType() instanceof StructType)){
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error14t_StructExp, sto.getType().getName()));
			return new ErrorSTO("Struct Error");
		}
		StructType t = (StructType)sto.getType();
		if(!(t.checker(strID)) ){
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error14f_StructExp, strID, sto.getType().getName()));			
			return new ErrorSTO("Struct Error");
		}

		Vector<STO> fields = t.getStruct();
		
		for(int i = 0; i < fields.size(); i++){
			if(strID.equals(fields.elementAt(i).getName())){
				return fields.elementAt(i);
			}
		}
		return sto;
	}
	
	STO
	DoDesignator2_Arrow (STO sto, String strID)
	{
		if(sto.isError()){return sto;}
		// Good place to do the struct checks
		Type t = new VoidType("dummy",1);
		String s = "";
		int numStr = 0;
		if(sto.getType() instanceof PointerType){
			t = ((PointerType)sto.getType()).getBase();
			s = ((PointerType)sto.getType()).getBase().getName();
			
			numStr = ((PointerType)sto.getType()).getPtrLvl();
			while(numStr != 0){
				s +="*";
				--numStr;
			}
			
		}

		if(!(t instanceof StructType)){
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error15_ReceiverArrow, s));
			return new ErrorSTO("Struct Error");
		}
		
		//if(sto.getType() instanceof StructType)
		//StructType t = (StructType)sto.getType();
		if(!(((StructType)t).checker(strID)) ){
			String s1 = sto.getType().getName();
			if(sto.getType() instanceof PointerType)
				s1 = ((PointerType)sto.getType()).getBase().getName();
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error14f_StructExp, strID, s1));			
			return new ErrorSTO("Struct Error");
		}
		
		sto = ((StructType)t).getField(strID);

		return sto;
	}
    
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoDesignator2_Array (STO des,STO expr)
	{
		// Good place to do the array checks
		if(des.isError()) {return des;}
		if(expr.isError()) {return expr;};
		STO sto = des;
		
		if(!(des.getType() instanceof ArrayType) && !(des.getType() instanceof PointerType)) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error11t_ArrExp, des.getType().getName()));
			return new ErrorSTO ("not arraytype or ptrtype");
		}
		else if(!(expr.getType() instanceof IntType)) {
			if(!(expr.getType() instanceof ArrayType)){
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error11i_ArrExp, expr.getType().getName()));
				return new ErrorSTO ("index not inttype");
			}
			else if(!(((ArrayType)(expr.getType())).getSubType() instanceof IntType)){
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error11i_ArrExp, expr.getType().getName()));
				return new ErrorSTO ("index not inttype");
			}
		}
		else if(expr instanceof ConstSTO) {
			int indValue = ((Type)des.getType()).pop();
			if( ((ConstSTO)expr).getNeg() || ((ConstSTO)expr).getIntValue() >= indValue ) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error11b_ArrExp, expr.getName(),indValue));
				return  new ErrorSTO ("out of bounds");
			}
		}
		if((des.getType() instanceof ArrayType) && (expr.getType() instanceof ArrayType)) 
			((ArrayType)sto.getType()).setSubType(((ArrayType)expr.getType()).getSubType());
		else{
			//This convert index array to int and add to the offset of index 0 to set this offset
			arrayInd = expr.getIntValue();
			sto = new VarSTO(des.getName()+arrayInd/*+"["+arrayInd+"]"*/,(des.getType()).getSubType(), 0, des.getOffset() + arrayInd, false);
		}
		return sto;
	}

	Type
	DoArrayIndexTypeCheck (STO sto)
	{		
		/*if(sto.isError()){
			return new ArrayType("dummy error",1,new StructType("dummy",4));
		}
		
		if(!(sto.getType() instanceof IntType)) {
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.error10i_Array, sto.getType().getName()));
			sto = new ErrorSTO ("index not inttype");
		}
		else if (!(sto instanceof ConstSTO)) {
			m_nNumErrors++;
			m_errors.print (ErrorMsg.error10c_Array);
			sto = new ErrorSTO ("index value not known at compile time");
		} 
		else {
			if( !(((ConstSTO)sto).getValue() > 0)) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error10z_Array,((ConstSTO)sto).getIntValue()));
				sto = new ErrorSTO ("index value not greater than 0");
			}
		}
		
		if(sto.isError()){
			return new ArrayType("dummy error",1,new StructType("dummy",4));
		}*/
		arraySize = ((ConstSTO)sto).getIntValue();
		//formating array name
		String name = /*"["+*/""+((ConstSTO)sto).getIntValue()/*+"]"*/;
		return new ArrayType(name,((ConstSTO)sto).getIntValue());
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoDesignator3_ID (String strID, boolean global)
	{
		STO		sto;

		if (global) {
			if ((sto = m_symtab.accessGlobal (strID)) == null) {
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.error0g_Scope, strID));
				sto = new ErrorSTO (strID);
			}
		}
		else { 
			if ((sto = m_symtab.access (strID)) == null)
			{
				m_nNumErrors++;
				m_errors.print (Formatter.toString(ErrorMsg.undeclared_id, strID));	
				sto = new ErrorSTO (strID);
			}
		}
		return (sto);
	}

	STO unary_plusmin(boolean minus,STO sto) {
		STO result;
		result = new ExprSTO( sto.getName(), sto.getType());
		result.setOffset(m_asmgen.UnaryFmt(curFunc,sto,minus));
		if(sto instanceof ConstSTO){
			result = new ConstSTO( sto.getName(), sto.getType());
			result.setValue(sto.getValue());
		}
		if(minus) 
			result.setNeg(true);
		System.out.println(result.getOffset());
		return result;
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	STO
	DoQualIdent (String strID)
	{
		STO		sto;
		if ((sto = m_symtab.access (strID)) == null)
		{
			m_nNumErrors++;
		 	m_errors.print (Formatter.toString(ErrorMsg.undeclared_id, strID));	
			return (new ErrorSTO (strID));
		}

		if (!sto.isTypedef())
		{
			m_nNumErrors++;
			m_errors.print (Formatter.toString(ErrorMsg.not_type, sto.getName ()));
			return (new ErrorSTO (sto.getName ()));
		}

		((Type)sto.getType()).setName(sto.getName());
		return (sto);
	}

	/*For check #8a I made and return dummy ConstSTO if arg STO is a null so we will be able to
	 * have problem with this!!!
	 */
	STO DoCreateSTO(Type mod, String id, STO sto){
   	 if(mod != null) {
   		 STO nsto = new ExprSTO(id,mod,sto);
   		 ((STO)nsto).setId(id);
   		 return nsto; 
   	 } else {
   		 if(sto != null){
   			 sto.setId(id);
   			 if(!(sto instanceof ConstSTO)){
   				 return sto;
   			 }
   		 }
   		 else{
   			 sto = new ConstSTO("dummy", new VoidType("dummy",4));
   			 sto.setId(id);
   		 }
   	 }
   	 return sto;
    }
     
     void DoStructVarDec(Vector<Pair> lstPair, String name){
    	 Pair p, p2;
    	 Vector<Pair> vec;
    	 Type typ, typ2;
    	 STO sto = m_symtab.accessGlobal(name);
    	 if(sto.getType() != null){
	    	 StructType ttt = (StructType)sto.getType();
	    	 String name2;
	    	 name = sto.getName();
	    	 for (int i = 0; i < lstPair.size (); i++)
	 		{
	 			p = lstPair.elementAt (i);
	 			
	 			vec = p.getVector();
	 			typ = p.getType();
	 			
	 			String nam = p.getType().getName();
	 			
	 			
	 			for (int j = 0; j < vec.size (); j++){
	 			    
	 			    p2 = vec.elementAt(j);
	 			    
	 			    typ2 = p2.getType();
	 			    name2 = p2.getName();
	 			    
	 				if (m_symtab.accessLocal (name2) != null)
	 				{
	 					m_nNumErrors++;
	 					m_errors.print (Formatter.toString(ErrorMsg.error13a_Struct, name2));
	 				}
	 				else if(name.equals(nam) && typ2 == null){
	 					m_nNumErrors++;
	 					m_errors.print (Formatter.toString(ErrorMsg.error13b_Struct, name2));
	 				}
	 				
	 				VarSTO 		sto2;
	 				if(typ2 instanceof ArrayType) {
	 					((ArrayType) typ2).setSubType(typ);
	 					sto2 = new VarSTO (name2,typ2);
	 				}
	 				else 
	 					sto2 = new VarSTO (name2,typ);
	 				
	 				ttt.push(sto2);
	 				m_symtab.insert (sto2);
	 		
	 			}
	 		}
 		}
     }
     
     void DoPtrCheck(STO sto){
    	 Type typ = sto.getType();
    	 if(!typ.isPtr()){
    		 m_nNumErrors++;
			 m_errors.print (Formatter.toString(ErrorMsg.error15_Receiver, typ.getName()));
    	 }
    	     	 
     }
     
     void DoNewCheck(STO sto){
    	 if(sto.isError()) {return;}
    	 
    	 if(!sto.isModLValue()){
    		 m_nNumErrors++;
			 m_errors.print (ErrorMsg.error16_New_var);
    	 }
    	 else if(!(sto.getType().isPtrGrp()) && !(sto.getType().isPtr())) {
        		 m_nNumErrors++;
    			 m_errors.print (Formatter.toString(ErrorMsg.error16_New, sto.getType().getName()));
    	 }
    	     	 
     }
     
     void DoDeleteCheck(STO sto){
    	 if(sto.isError()) {return;}
    	 
    	 if(!sto.isModLValue()){
    		 m_nNumErrors++;
			 m_errors.print (ErrorMsg.error16_Delete_var);
    	 }
    	 else if(!(sto.getType().isPtrGrp()) && !(sto.getType().isPtr())) {
    		 
        		 m_nNumErrors++;
    			 m_errors.print (Formatter.toString(ErrorMsg.error16_Delete, (sto.getType()).getName()));
    	 }
    	     	 
     }
     
     STO DoCast(Type cast,STO sto){
    	 if(cast.isPtrGrp()) {
    		 cast = ((PointerGroupType) cast).getPtr();
    	 }
    	 
    	 if(sto.isError()) {return sto;}
    	 if((!cast.isInt() && !cast.isFloat() && !cast.isBool() && !cast.isPtr()) || sto.getType().isArray() || sto.getType().isStruct()) {
    		 m_nNumErrors++;
			 m_errors.print (Formatter.toString(ErrorMsg.error20_Cast, sto.getType().getName(), cast.getName()));
			 return (new ErrorSTO ("invalid type casting"));
    	 } 
    	
    	 if(sto.isConst()) {
    		 STO nSto;
    		 if(sto.getType().isBool()) {
    			 if(cast.isInt()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getIntValue());
    			 else if(cast.isFloat()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getFloatValue());
    			 else if(cast.isPtr()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getValue());
    			 else 
    				 nSto = sto;
    		 }
    		 else if(sto.getType().isInt()) {
    			 if(cast.isBool())
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getValue());
    			 else if(cast.isFloat()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getFloatValue());
    			 else if(cast.isPtr()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getValue());
    			 else 
    				 nSto = sto;
    		 }
    		 else if(sto.getType().isFloat()) {
    			 if(cast.isInt()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getIntValue());
    			 else if(cast.isBool()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getValue());
    			 else if(cast.isPtr()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getIntValue());
    			 else 
    				 nSto = sto;
    		 }
    		 else {
    			 if(cast.isInt()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getValue());
    			 else if(cast.isFloat()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getFloatValue());
    			 else if(cast.isBool()) 
    				 nSto = new ConstSTO("const",cast,((ConstSTO) sto).getValue());
    			 else 
    				 nSto = sto;
    		 }
    		 return nSto;
    	 }
    	 else {
    		 return  new ExprSTO("casted",cast);
    	}
     }
     
     public STO DoAddressabla(STO sto){
    	 if(sto.isFunc()){
    		m_nNumErrors++;
 		 	m_errors.print (Formatter.toString(ErrorMsg.error21_AddressOf, sto.getType().getName()));	
 			return (new ErrorSTO (sto.getType().getName()));
    	 }
    	 if(sto.isConst()){
    		 if(((ConstSTO)sto).getLit()){
    			 m_nNumErrors++;
  		 		m_errors.print (Formatter.toString(ErrorMsg.error21_AddressOf, sto.getType().getName()));	
  		 		return (new ErrorSTO (sto.getType().getName()));
    		 }
     	 }
    	 
    	 PointerType pt = new PointerType("ptr",4);
    	 if(sto.getType().isArray()){
    		 pt.setSubType(((ArrayType)sto.getType()).getSubType());
    		 pt.setBase(((ArrayType)sto.getType()).getSubType());
    	 }
    	 else if(sto.getType().isPtr()){
    		 pt.setSubType(sto.getType());
    		 pt.setBase(((PointerType)sto.getType()).getBase());
    	 }
    	 else{
    		 pt.setSubType(sto.getType());
    		 pt.setBase(sto.getType());
    	 }
    	 return new ExprSTO(sto.getName(), pt);
     }
     
     public STO DoSizeOf(STO sto,Type typ){
    	 STO rSto;// = new ConstSTO("sizeof",new IntType("sizeof",4), 4);
    	/*if(typ != null) {
    		rSto = new ConstSTO((typ).getName(),new IntType("int",4),(typ).getSize());
    		rSto.setIsAddressable(false);
    	}
    	else {
    		if(!(sto.getIsAddressable())) {
   			 	m_nNumErrors++;
 		 		m_errors.print(ErrorMsg.error19_Sizeof);	
 		 		return (new ErrorSTO ("operand not addressable::sizeof"+sto.getType().getName()));
    		}
    		
    		rSto = new ConstSTO((sto).getType().getName(),new IntType("int",4),(sto).getType().getSize());
    		rSto.setIsAddressable(false);
    	}*/
    	rSto = new ConstSTO((typ).getName(),new IntType("int",4),(typ).getSize());
    	return rSto;
     }
     
     public void add_endl() {
    	 m_asmgen.do_cout_endl(curFunc);
     }
     public void add_expr(STO sto) {
    	 m_asmgen.do_cout_expr(sto,curFunc);
     }
     
     public void dump_cin(STO sto) {
    	 m_asmgen.dump_cin(curFunc,sto);
     }
     public void unaryFmt(boolean isneg,STO sto) {
    	 if(isneg)
    		 m_asmgen.UnaryFmt(curFunc,sto,isneg);
     }
     public void unaryNotFmt(STO sto) {
    	 m_asmgen.UnaryNotFmt(curFunc,sto);
     }
     
//----------------------------------------------------------------
//	Instance variables
//----------------------------------------------------------------
	private Lexer			m_lexer;
	private ErrorPrinter		m_errors;
	private int 			m_nNumErrors;
	private String			m_strLastLexeme;
	private boolean			m_bSyntaxError = true;
	private int			m_nSavedLineNum;
	
	private AssemblyCodeGenerator m_asmgen;

	private SymbolTable		m_symtab;
}
