package base;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import WordHandle.wordHandle;
import explainRun.interpreter;
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
		inputString = buffer.toString();
		if(!caseSensitive)
		{
			inputString.toLowerCase();
		}
		//System.out.println(inputString);
		return true;
	}
	
	public boolean handle()
	{
		if(inputString == null)
		{
			return false;
		}
		wordHandle word = new wordHandle(inputString, false);
		word.handle();
		
		syntaxHandle syntax = new syntaxHandle(word.getResult());
		syntax.handle();

		interpreter expAndRun = new interpreter(syntax.getCommands());
		expAndRun.run();
		return true;
	}
	
}
