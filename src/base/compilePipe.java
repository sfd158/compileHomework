package base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import WordHandle.wordHandle;
import syntaxParsing.syntaxHandle;

public class compilePipe
{
	private String inputString;
	private boolean caseSensitive = false;
	public compilePipe(final String _filename, final boolean _caseSensitive)
	{
		if(_filename != null)
		{
			readFile(_filename);
		}
		caseSensitive = _caseSensitive;
	}
	
	public String getInputString()
	{
		return inputString;
	}
	
	public boolean getCaseSensitive()
	{
		return caseSensitive;
	}
	
	public boolean readFile(final String filename)
	{
		StringBuffer buffer = new StringBuffer();
		String tp;
		try 
		{
			BufferedReader bf = new BufferedReader(new FileReader(filename));
			while((tp=bf.readLine())!=null)
			{
				buffer.append(tp);
				buffer.append('\n');
			}
			bf.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		//File f = new File(filename);
		//char[] buffer = new char[(int) f.length()];
		//note: '\r' is not supported in this file.
		inputString = buffer.toString();
		if(!caseSensitive)
		{
			inputString.toLowerCase();
		}
		//System.out.print(inputString);
		return true;
	}
	
	public boolean handle()
	{
		if(inputString == null)
		{
			return false;
		}
		wordHandle word = new wordHandle(inputString, true);
		word.handle();
		
		System.out.println(word.getResult().getNodeList());
		System.out.println(word.getResult().getSymbolList());
		
		syntaxHandle syntax = new syntaxHandle(word.getResult());
		syntax.handle();
		
		//wordHandleResult wordResult = word.getResult();
		//word.getConstNumberTable();
		//System.out.println(word.getResult().getConstNumberTable());
		//System.out.println(word.getResult().gerVarNameTable());
		
		return true;
	}
	
}
