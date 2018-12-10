package syntaxParsing;

import java.util.ArrayList;

import WordHandle.nodeClass;
import WordHandle.wordHandleResult;
import base.compileException;

public class syntaxDealer_back
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
	public String toJson()
	{
		return build.toString();
	}
	public syntaxDealer_back(final wordHandleResult _result)
	{
		wordRes = _result;
		nodeList = wordRes.getNodeList();
		symbolList = wordRes.getSymbolList();
		sym_place = 0;
		sym = nodeList.get(sym_place);
	}

	protected void Advance() throws compileException
	{
		if(isPrint)
		{
			build.append("\"advance\": \"");
			build.append(symbolList.get(sym_place));
			build.append("\",\n");
		}
		if(sym_place+1 < nodeList.size())
		{
			sym = nodeList.get(++sym_place);
		}
		else
		{
			sym = null;
			hasMoreToken = false;
		}
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
	}
	protected void PartProgram() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"PartProgram\",\n");
		}
		if (sym.getNodeType() == nodeClass.SYM_const)
		{
			ConstExplain();
		}
		if (sym.getNodeType() == nodeClass.SYM_var)
		{
			VaribleExplain();
		}
		if (sym.getNodeType() == nodeClass.SYM_procedure)
		{
			ProcedureExplain();
		}
		Statement();
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
	protected void VaribleExplain() throws compileException
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
		Identifier();
		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			Identifier();
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
	}
	protected void ProcedureExplain() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"ProcedureExplain\",\n");
		}
		ProcedureBegin();
		PartProgram();
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
		Identifier();
		if(sym.getNodeType() != nodeClass.SYM_equal)
		{
			throw new compileException("Const Defination Error, '=' Required.");
		}
		Advance();
		UnsignedInteger();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void Identifier() throws compileException
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
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void UnsignedInteger() throws compileException
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
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void ProcedureBegin() throws compileException
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
		Identifier();
		if(sym.getNodeType() != nodeClass.SYM_semicolon)
		{
			throw new compileException("Procedure Begin Format Wrong. semicolon miss.");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void AssignStatement() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"AssignStatement\",\n");
		}
		Identifier();
		if(sym.getNodeType() != nodeClass.SYM_assign)
		{
			throw new compileException("Assignment Format Wrong.");
		}
		Advance();
		Expression();
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
		}
		else
		{
			Expression();
			RelationOperator();
			Expression();
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
		if(sym.getNodeType() != nodeClass.SYM_then)
		{
			throw new compileException("then miss.");
		}
		Advance();
		Statement();
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
		if(sym.getNodeType() != nodeClass.SYM_do)
		{
			throw new compileException("While Loop statement Format wrong: 'do' miss");
		}
		Advance();
		Statement();
		if(isPrint)
		{
			build.append("},\n");
		}
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
		Identifier();
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
		Identifier();
		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			Identifier();
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
		while(sym.getNodeType() == nodeClass.SYM_comma)
		{
			Advance();
			Expression();
		}
		if(sym.getNodeType() != nodeClass.SYM_right)
		{
			throw new compileException("in write statement, right bracket miss.");
		}
		Advance();
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
		if(sym.getNodeType() == nodeClass.SYM_add || sym.getNodeType() == nodeClass.SYM_minus)
		{
			AddOrSubOperator();
		}
		Item();
		
		while(sym.getNodeType() == nodeClass.SYM_add || sym.getNodeType() == nodeClass.SYM_minus)
		{
			AddOrSubOperator();
			Item();
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void RelationOperator() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"RelationOperator\",\n");
		}
		switch(sym.getNodeType())
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
	}
	
	protected void Item() throws compileException //Ïî
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Item\",\n");
		}
		Factor();
		while(sym.getNodeType() == nodeClass.SYM_mul || sym.getNodeType() == nodeClass.SYM_div)
		{
			MulOrDivOperator();
			Factor();
		}
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void Factor() throws compileException //Òò×Ó
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"Factor\",\n");
		}
		switch(sym.getNodeType())
		{
		case nodeClass.varNameType:
			Identifier();
			break;
		case nodeClass.intType:
			UnsignedInteger();
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
	protected void MulOrDivOperator() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"MulOrDivOperator\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_mul && sym.getNodeType() != nodeClass.SYM_div)
		{
			throw new compileException("Format of Multiply or Divide Operator Wrong");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
	protected void AddOrSubOperator() throws compileException
	{
		if(isPrint)
		{
			build.append("\"call\":{\n");
			build.append("\"doing\": \"AddOrSubOperator\",\n");
		}
		if(sym.getNodeType() != nodeClass.SYM_add && sym.getNodeType() != nodeClass.SYM_minus)
		{
			throw new compileException("Format of Add or Minus Operator Wrong");
		}
		Advance();
		if(isPrint)
		{
			build.append("},\n");
		}
	}
}
