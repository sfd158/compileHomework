package syntaxParsing;

import WordHandle.wordHandleResult;
import base.compileException;
import base.handleType;

public class syntaxHandle implements handleType
{
	//protected final wordHandleResult wordRes;
	//wordhandle���ű���ˣ���Ϊû�в�Σ������¹���
	//�����Ǵʷ����������ĵ���
	private boolean printHandleResult;
	protected syntaxDealer deal; 
	public syntaxHandle(final wordHandleResult _wordResult)
	{
		this(_wordResult, false);
	}
	public syntaxHandle(final wordHandleResult _wordResult, final boolean _printHandleResult)
	{
		//wordRes = _wordResult;
		printHandleResult = _printHandleResult;
		deal = new syntaxDealer(_wordResult);
	}

	public boolean handle()
	{
		try
		{
			deal.Program();
		}
		catch (compileException e)
		{
			e.printStackTrace();
		}
		if(printHandleResult)
		{
			printResult();
		}
		return false;
	}
	public void printResult()
	{
		
	}
}
