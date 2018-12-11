package syntaxParsing;

import java.util.ArrayList;

import WordHandle.nodeClass;
import WordHandle.wordHandleResult;
import base.commandClass;
import base.compileException;
import syntaxParsing.tableClass;

public class syntaxDealer
{
	protected nodeClass sym;
	protected int sym_place;
	protected final wordHandleResult wordRes;
	protected final ArrayList<nodeClass> nodeList;
	protected final ArrayList<String> symbolList;
	protected boolean hasMoreToken = true;
	protected StringBuilder build = new StringBuilder();
	protected boolean isPrint = true;
	protected int doingStrap = 0;
	protected int callingStrap = 0;
	protected int advanceStrap = 0;
	
	protected ArrayList<commandClass> commands = new ArrayList<>();
	protected ArrayList<tableClass> nameTable = new ArrayList<>();
	protected ArrayList<Integer> DXList = new ArrayList<>();
	protected int level = 0;
	
	protected int nowProcedure = 0;
	protected int parentProcedure = -1;
	
	protected int stackUseCnt = 0, maxUseCnt = 0;
	protected void stackPush()
	{
		stackUseCnt++;
		if(stackUseCnt > maxUseCnt)
		{
			maxUseCnt = stackUseCnt;
		}
	}
	protected void stackPop()
	{
		stackUseCnt--; //为什么push和pop不匹配呢...
	}
	public final ArrayList<commandClass> getCommands()
	{
		return commands;
	}
	public String toJson()
	{
		return build.toString();
	}
	
	public String toJsonNameTable()
	{
		StringBuilder build = new StringBuilder();
		build.append('{');
		for(int i=0; i<nameTable.size(); i++)
		{
			build.append(i);
			build.append(':');
			build.append(nameTable.get(i).toJson());
			build.append(",\n");
		}
		build.append('}');
		return build.toString();
	}
	
	public syntaxDealer(final wordHandleResult _result)
	{
		wordRes = _result;
		nodeList = wordRes.getNodeList();
		symbolList = wordRes.getSymbolList();
		sym_place = 0;
		sym = nodeList.get(sym_place);
	}

	protected String Advance() throws compileException
	{
		if(isPrint)
		{
			build.append("\"advance\": \"");
			build.append(symbolList.get(sym_place));
			build.append("\",\n");
		}
		String name = symbolList.get(sym_place);
		if(sym_place+1 < nodeList.size())
		{
			sym = nodeList.get(++sym_place);
		}
		else
		{
			sym = null;
			hasMoreToken = false;
		}
		return name;
	}
	
	protected void Program() throws compileException
	{
		if(isPrint)
		{
			build.append("{\n");
			build.append("\"doing\": \"Program\",\n");
		}
		PartProgram();
		if(sym.getNodeType() != nodeClass.SYM_dot)
		{
			throw new compileException("The end of Program is not a single dot. at" + sym_place);
		}
		else 
		{
			Advance();
			if(isPrint)
			{
				build.append("}\n");
			}
		}
		if (hasMoreToken)
		{
			throw new compileException("Exist elements after final dot '.'");
		}
		System.out.println(this.DXList);
		System.out.println(this.toJsonNameTable());
		for(int i=0;i<commands.size();i++)
		{
			System.out.println("("+i+")"+commands.get(i));
		}
	}
	
	protected void PartProgram() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"PartProgram\",\n");
		}
		DXList.add(nameTable.size()-1);
		commands.add(new commandClass(commandClass.JMP_com, 0, 0));
		int backPatchPlace = commands.size() - 1;
		if (sym.getNodeType() == nodeClass.SYM_const)
		{
			ConstExplain();
		}
		int varCount = 0;
		if (sym.getNodeType() == nodeClass.SYM_var)
		{
			varCount = VaribleExplain();
		}
		if (sym.getNodeType() == nodeClass.SYM_procedure)
		{
			ProcedureExplain();
		}
		commands.get(backPatchPlace).aCode = commands.size();
		commands.add(new commandClass(commandClass.INT_com,0,0));
		backPatchPlace = commands.size() - 1;
		stackUseCnt = 0;
		Statement();
		commands.get(backPatchPlace).aCode = varCount;//maxUseCnt + varCount;
		commands.add(new commandClass(commandClass.OPR_com,0,0));
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	
	protected void ConstExplain() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ConstExplain\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_const)
		{
			throw new compileException("In const explain: 'const' not found.");
		}
		else
		{
			Advance();
		}
		ConstDefine();
		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			ConstDefine();
		}		
		if(sym.getNodeType() != nodeClass.SYM_semicolon)
		{
			throw new compileException("The end of const explain is not ';'");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	
	protected int VaribleExplain() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"VaribleExplain\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_var)
		{
			throw new compileException("");
		}
		Advance();
		int varCnt = 0;
		String name = Identifier();
		nameTable.add(new tableClass(name,tableClass.kind_Var,
				tableClass.UNDEFINED,level,varCnt++,nowProcedure,parentProcedure));
		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			name = Identifier();
			nameTable.add(new tableClass(name,tableClass.kind_Var,
					tableClass.UNDEFINED,level,varCnt++,nowProcedure,parentProcedure));
		}
		if(sym.getNodeType() != nodeClass.SYM_semicolon)
		{
			throw new compileException("The end of var explain is not ';'");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
		return varCnt;
	}
	
	protected void ProcedureExplain() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ProcedureExplain\",\n");
		}
		level++;
		int nowProcedureBack = nowProcedure;
		int parentProcedureBack = parentProcedure;
		parentProcedure = nowProcedure;
		nowProcedure = nameTable.size();
		String name = ProcedureBegin();
		int backPatchPlace = commands.size() - 1;
		nameTable.add(new tableClass(name, tableClass.kind_Procedure, 
				tableClass.UNDEFINED, level-1, tableClass.UNDEFINED, nowProcedure, parentProcedure,
				commands.size()-1));
		
		PartProgram();
		commands.get(backPatchPlace).lCode = 0;
		nowProcedure = nowProcedureBack;
		parentProcedure = parentProcedureBack;
		level--;
		if(sym.getNodeType() != nodeClass.SYM_semicolon)
		{
			throw new compileException("Procedure needs ';' at end");
		}
		Advance();
		if(sym.getNodeType() == nodeClass.SYM_procedure)
		{
			ProcedureExplain();
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void Statement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Statement\",\n");
		}
		switch(sym.getNodeType())
		{
		case nodeClass.varNameType:
			AssignStatement();
			break;
		case nodeClass.SYM_if:
			ConditionStatement();
			break;
		case nodeClass.SYM_while:
			WhileLoopStatement();
			break;
		case nodeClass.SYM_call:
			ProcedureCallingStatement();
			break;
		case nodeClass.SYM_read:
			ReadStatement();
			break;
		case nodeClass.SYM_write:
			this.WriteStatement();
			break;
		case nodeClass.SYM_begin:
			this.CombinationStatement();
			break;
		default:
			break;//null
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void ConstDefine() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ConstDefine\",\n");
		}
		String name = Identifier();
		if(sym.getNodeType() != nodeClass.SYM_equal)
		{
			throw new compileException("Const Defination Error, '=' Required.");
		}
		Advance();
		int val = UnsignedInteger();
		nameTable.add(new tableClass(name, tableClass.kind_Const, 
				val, level, tableClass.UNDEFINED, nowProcedure, parentProcedure));
		//System.out.println(nameTable.get(nameTable.size()-1));
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	
	protected String Identifier() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Identifier\",\n");
		}
		if(!sym.isNodeTypeVarName())
		{
			throw new compileException("Format of Varible name wrong.");
		}
		String name = Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
		return name;
	}
	protected int UnsignedInteger() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"UnsignedInteger\",\n");
		}
		if(!sym.isNodeTypeInt())
		{
			throw new compileException("Format of Unsigned Integer wrong.");
		}
		int val = wordRes.getConstNumberTable().get(sym.nodeValue);
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
		return val;
	}
	protected String ProcedureBegin() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ProcedureBegin\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_procedure)
		{
			throw new compileException("Procedure Begin Format Wrong.");
		}
		Advance();
		String name = Identifier();
		
		//System.out.println(nameTable.get(nameTable.size()-1));
		if(sym.getNodeType() != nodeClass.SYM_semicolon)
		{
			throw new compileException("Procedure Begin Format Wrong. semicolon miss.");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
		return name;
	}
	protected int getNamePlace(final String name) throws compileException
	{
		int i, j, t;
		if(nowProcedure != 0)
		{
			for(t=DXList.size()-1; t>=0; t--)
			{
				if(DXList.get(t)== nowProcedure)break;
			}
		}
		else t = 0;
		do
		{
			j = (t+1<DXList.size())?(DXList.get(t+1)):(nameTable.size());
			i = (DXList.get(t)==-1)?0:DXList.get(t);
			for(int k=i; k<j; k++)
			{
				if(this.nameTable.get(k).name.equals(name))
				{
					return k;
				}
			}
			i = nameTable.get(i).parentProcedure;
			while(t>=0 && DXList.get(t)!=i)
			{
				t--;
			}
			if(t==-1)t=0;
		}while(i>=0);
		
		throw new compileException("Varible not defined before." + name);
	}
	protected void AssignStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"AssignStatement\",\n");
		}
		String name = Identifier();
		//name must exists before.
		int place = getNamePlace(name);
		if(sym.getNodeType() != nodeClass.SYM_assign)
		{
			throw new compileException("Assignment Format Wrong.");
		}
		Advance();
		Expression();
		stackPop();
		commands.add(new commandClass(commandClass.STO_com, level-nameTable.get(place).level, nameTable.get(place).adr+3));
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void Condition() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Condition\",\n");
		}
		if(sym.getNodeType() == nodeClass.SYM_odd)
		{
			Advance();
			Expression();
			commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_odd));
		}
		else
		{
			Expression();
			int rela = RelationOperator();
			Expression();
			stackPop();
			stackPop();
			commands.add(new commandClass(commandClass.OPR_com, 0, rela));
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	
	protected void ConditionStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ConditionStatement\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_if)
		{
			throw new compileException("'if' miss.");
		}
		Advance();
		Condition();
		stackPop();
		commands.add(new commandClass(commandClass.JPC_com,0,0));
		int backPatchPlace = commands.size() - 1;
		if(sym.getNodeType() != nodeClass.SYM_then)
		{
			throw new compileException("then miss.");
		}
		Advance();
		Statement();
		commands.get(backPatchPlace).aCode = commands.size();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void WhileLoopStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"WhileLoopStatement\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_while)
		{
			throw new compileException("While Loop statement Format wrong: 'while' miss");
		}
		Advance();
		Condition();
		//这里其实还需要回填..不如把condition都计算完。。
		stackPop();
		commands.add(new commandClass(commandClass.JPC_com, 0, 0));
		int backPatchPlace = commands.size()-1;
		if(sym.getNodeType() != nodeClass.SYM_do)
		{
			throw new compileException("While Loop statement Format wrong: 'do' miss");
		}
		Advance();
		Statement();
		commands.get(backPatchPlace).aCode = commands.size();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected int getProcedurePlace(final String name)
	{
		for(int i=DXList.size()-1;i>0;i--)
		{
			if(this.nameTable.get(DXList.get(i)).name.equals(name))
				return DXList.get(i);
		}
		return -1;
	}
	protected void ProcedureCallingStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ProcedureCallingStatement\",\n");
		}
		if (sym.getNodeType() != nodeClass.SYM_call)
		{
			throw new compileException("call error.");
		}
		Advance();
		String name = Identifier();
		int place = getProcedurePlace(name);
		commands.add(new commandClass(commandClass.CAL_com,
				level-nameTable.get(place).level, nameTable.get(place).procedureEntry + 1));
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void ReadStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ReadStatement\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_read)
		{
			throw new compileException("is not a read statement.");
		}
		Advance();
		if(sym.getNodeType() != nodeClass.SYM_left)
		{
			throw new compileException("in read statement: left bracket miss.");
		}
		Advance();
		String name = Identifier();
		int place = getNamePlace(name);
		stackPush();
		commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_read));
		stackPop();
		commands.add(new commandClass(commandClass.STO_com, 
				level-nameTable.get(place).level, nameTable.get(place).adr+3));

		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			name = Identifier();
			place = getNamePlace(name);
			stackPush();
			commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_read));
			stackPop();
			commands.add(new commandClass(commandClass.STO_com, 
					level-nameTable.get(place).level, nameTable.get(place).adr+3));
		}
		if(sym.getNodeType() != nodeClass.SYM_right)
		{
			throw new compileException("in read statement, right bracket miss.");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void WriteStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"WriteStatement\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_write)
		{
			throw new compileException("is not a write statement.");
		}
		Advance();
		if(sym.getNodeType() != nodeClass.SYM_left)
		{
			throw new compileException("in write statement: left bracket miss.");
		}
		Advance();
		Expression();
		stackPop();
		commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_write));
		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			Expression();
			stackPop();
			commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_write));
		}
		if(sym.getNodeType() != nodeClass.SYM_right)
		{
			throw new compileException("in write statement, right bracket miss.");
		}
		Advance();
		commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_writeln));
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void CombinationStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"CombinationStatement\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_begin)
		{
			throw new compileException("Begin missing.");
		}
		Advance();
		while(sym.getNodeType() != nodeClass.SYM_end)
		{
			Statement();
			switch(sym.getNodeType())
			{
			case nodeClass.SYM_end:
				break;
			case nodeClass.SYM_semicolon:
				Advance();
				break;
			default:
				throw new compileException("Combination Statement wrong. at"+sym_place);
			}
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void Expression() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Expression\",\n");
		}
		boolean beforeMinus = false;
		if(sym.getNodeType() == nodeClass.SYM_add || sym.getNodeType() == nodeClass.SYM_minus)
		{
			beforeMinus = sym.getNodeType() == nodeClass.SYM_minus;
			AddOrSubOperator();
		}
		Item();
		
		while(sym.getNodeType() == nodeClass.SYM_add || sym.getNodeType() == nodeClass.SYM_minus)
		{
			int type = AddOrSubOperator();
			Item();
			stackPop();
			this.commands.add(new commandClass(commandClass.OPR_com, 0, type));
		}
		if(beforeMinus)
		{
			stackPush();
			this.commands.add(new commandClass(commandClass.LIT_com, 0, 0));
			stackPop();
			this.commands.add(new commandClass(commandClass.OPR_com, 0, nodeClass.SYM_minus));
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected int RelationOperator() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"RelationOperator\",\n");
		}
		int type = sym.getNodeType();
		switch(type)
		{
		case nodeClass.SYM_equal:
		case nodeClass.SYM_pound:
		case nodeClass.SYM_le:
		case nodeClass.SYM_ge:
		case nodeClass.SYM_leq:
		case nodeClass.SYM_geq:
			Advance();
			break;
		default:
			throw new compileException("Wrong Relation Operator.");
		}
		if(isPrint)
		{
			build.append("},\n");
		}
		return type;
	}
	
	protected void Item() throws compileException //项
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Item\",\n");
		}
		Factor();
		while(sym.getNodeType() == nodeClass.SYM_mul || sym.getNodeType() == nodeClass.SYM_div)
		{
			int type = MulOrDivOperator();
			Factor();
			stackPop();
			this.commands.add(new commandClass(commandClass.OPR_com, 0, type));
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void Factor() throws compileException //因子
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Factor\",\n");
		}
		switch(sym.getNodeType())
		{
		case nodeClass.varNameType:
			String name = Identifier();
			int place = this.getNamePlace(name);
			if(nameTable.get(place).kind == tableClass.kind_Var)
			{
				stackPush();
				commands.add(new commandClass(commandClass.LOD_com, 
						level-nameTable.get(place).level, nameTable.get(place).adr+3));
			}
			else
			{
				stackPush();
				commands.add(new commandClass(commandClass.LIT_com, 0, nameTable.get(place).val));
			}
			break;
		case nodeClass.intType:
			int val = UnsignedInteger();
			stackPush();
			commands.add(new commandClass(commandClass.LIT_com, 0, val));
			break;
		case nodeClass.SYM_left:
			Advance();
			Expression();
			if(sym.getNodeType() != nodeClass.SYM_right)
			{
				throw new compileException("right bracket missing.");
			}
			Advance();
			break;
		default:
			throw new compileException("Not a factor.");
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected int MulOrDivOperator() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"MulOrDivOperator\",\n");
		}
		int type = sym.getNodeType();
		if(type != nodeClass.SYM_mul && type != nodeClass.SYM_div)
		{
			throw new compileException("Format of Multiply or Divide Operator Wrong");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
		return type;
	}
	protected int AddOrSubOperator() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"AddOrSubOperator\",\n");
		}
		int type = sym.getNodeType();
		if(type != nodeClass.SYM_add && type != nodeClass.SYM_minus)
		{
			throw new compileException("Format of Add or Minus Operator Wrong");
		}
		
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
		return type;
	}
}
