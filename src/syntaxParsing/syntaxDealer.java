package syntaxParsing;

import java.util.ArrayList;

import WordHandle.nodeClass;
import WordHandle.wordHandleResult;
import base.compileException;

public class syntaxDealer 
{
	protected nodeClass sym;
	protected int sym_place;
	protected final wordHandleResult wordRes;
	protected final ArrayList<nodeClass> nodeList;
	protected boolean hasMoreToken = true;
	public syntaxDealer(final wordHandleResult _result)
	{
		wordRes = _result;
		nodeList = wordRes.getNodeList();
		sym_place = 0;
		sym = nodeList.get(sym_place);
	}

	protected void Advance() throws compileException
	{
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
		//correct
		PartProgram();
		if(sym.getNodeType() != nodeClass.SYM_dot)
		{
			throw new compileException("The end of Program is not a single dot. at" + sym_place);
		}
		else Advance();
		if (hasMoreToken)
		{
			throw new compileException("Exist elements after final dot '.'");
		}
	}
	protected void PartProgram() throws compileException
	{
		//correct
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
	}
	protected void ConstExplain() throws compileException
	{
		if(sym.getNodeType() != nodeClass.SYM_const)
		{
			throw new compileException("In const explain: 'const' not found.");
		}
		else Advance();
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
	}
	protected void VaribleExplain() throws compileException
	{
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
	}
	protected void ProcedureExplain() throws compileException
	{
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
	}
	protected void Statement() throws compileException
	{
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
	}
	protected void ConstDefine() throws compileException
	{
		Identifier();
		if(sym.getNodeType() != nodeClass.SYM_equal)
		{
			throw new compileException("Const Defination Error, '=' Required.");
		}
		Advance();
		UnsignedInteger();
	}
	protected void Identifier() throws compileException
	{
		if(!sym.isNodeTypeVarName())
		{
			throw new compileException("Format of Varible name wrong.");
		}
		Advance();
	}
	protected void UnsignedInteger() throws compileException
	{
		if(!sym.isNodeTypeInt())
		{
			throw new compileException("Format of Unsigned Integer wrong.");
		}
		Advance();
	}
	protected void ProcedureBegin() throws compileException
	{
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
	}
	protected void AssignStatement() throws compileException
	{
		Identifier();
		if(sym.getNodeType() != nodeClass.SYM_assign)
		{
			throw new compileException("Assignment Format Wrong.");
		}
		Advance();
		Expression();
		//if(sym.getNodeType() != nodeClass.SYM_semicolon)
		//{
		//	throw new compileException("Assignment Format Wrong");
		//}
		//Advance();
	}
	protected void Condition() throws compileException
	{
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
	}
	
	protected void ConditionStatement() throws compileException
	{
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
	}
	protected void WhileLoopStatement() throws compileException
	{
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
	}
	protected void ProcedureCallingStatement() throws compileException
	{
		if (sym.getNodeType() != nodeClass.SYM_call)
		{
			throw new compileException("call error.");
		}
		Advance();
		Identifier();
	}
	protected void ReadStatement() throws compileException
	{
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
	}
	protected void WriteStatement() throws compileException
	{
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
	}
	protected void CombinationStatement() throws compileException
	{
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
		//Statement();
		//while(sym.getNodeType() == nodeClass.SYM_semicolon)
		//{
		//	Advance();
		//	Statement();
		//}
		//if(sym.getNodeType() != nodeClass.SYM_end)
		//{
		//	throw new compileException("End missing. at" + sym_place);
		//}
		//Advance();
	}
	protected void Expression() throws compileException
	{
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
	}
	protected void RelationOperator() throws compileException
	{
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
	}
	
	protected void Item() throws compileException //Ïî
	{
		Factor();
		while(sym.getNodeType() == nodeClass.SYM_mul || sym.getNodeType() == nodeClass.SYM_div)
		{
			MulOrDivOperator();
			Factor();
		}
	}
	protected void Factor() throws compileException //Òò×Ó
	{
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
	}
	protected void MulOrDivOperator() throws compileException
	{
		if(sym.getNodeType() != nodeClass.SYM_mul && sym.getNodeType() != nodeClass.SYM_div)
		{
			throw new compileException("Format of Multiply or Divide Operator Wrong");
		}
		Advance();
	}
	protected void AddOrSubOperator() throws compileException
	{
		if(sym.getNodeType() != nodeClass.SYM_add && sym.getNodeType() != nodeClass.SYM_minus)
		{
			throw new compileException("Format of Add or Minus Operator Wrong");
		}
		Advance();
	}
}
