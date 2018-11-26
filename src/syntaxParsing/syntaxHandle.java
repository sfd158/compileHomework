package syntaxParsing;

import WordHandle.wordHandleResult;
import base.compileException;
import base.handleType;

public class syntaxHandle implements handleType
{
	//protected final wordHandleResult wordRes;
	//wordhandle符号表废了，因为没有层次，再重新构建
	//输入是词法分析处理后的单词
	private boolean printHandleResult;
	protected syntaxDealer deal;
	String jsonOut;
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
		//System.out.println(deal.toJson());
		System.out.println(toJson());
	}
	public String toJson()
	{
		String t = deal.toJson();
		StringBuilder build = new StringBuilder();
		int i=0, p1=0;
		while(i<t.length())
		{
			while(i<t.length() && t.charAt(i)!=',')
			{
				build.append(t.charAt(i));
				i++;
			}
			if(i>=t.length())break;
			p1 = i++;
			while(i<t.length() && (t.charAt(i)==' ' || t.charAt(i)=='\n'))i++;
			if(i<t.length() && t.charAt(i)=='}')
			{
				p1++;
			}
			if(i>13400)
			{
				i=i+1-1;
			}
			for(int j=p1;j<i;j++)
			{
				build.append(t.charAt(j));
			}
		}
		return build.toString();
	}
}
