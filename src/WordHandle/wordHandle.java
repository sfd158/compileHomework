package WordHandle;

import java.util.StringTokenizer;

import base.compileException;

public class wordHandle
{	
	private int nowInCurrentLineCount = 0;
	
	private String inputString;
	private String currentLine;
	
	protected wordHandleResult result = new wordHandleResult();
	private boolean printAnsAfterHandle = false;
	
	public wordHandle(final String _inputString)
	{
		this(_inputString, false);
	}
	
	public wordHandle(final String _inputString, final boolean _printAnsAfterHandle)
	{
		this.inputString = _inputString;
		this.printAnsAfterHandle = _printAnsAfterHandle;
	}
	public final wordHandleResult getResult()
	{
		return result;
	}
	private boolean skipSpace()
	{
		while(nowInCurrentLineCount < currentLine.length()
				&& currentLine.charAt(nowInCurrentLineCount) == ' ')
		{
			nowInCurrentLineCount++;
		}
		return nowInCurrentLineCount < currentLine.length();
	}
	
	private char currentChar()
	{
		if (nowInCurrentLineCount < currentLine.length())
		{
			return currentLine.charAt(nowInCurrentLineCount);
		}
		else return '\0';
	}
	
	private void pushBackChar()
	{
		nowInCurrentLineCount--;
	}
	
	private char getCurrentCharAndGetChar()
	{
		if(nowInCurrentLineCount < currentLine.length())
		{
			return currentLine.charAt(nowInCurrentLineCount++);
		}
		else return '\0';
	}
	
	private boolean isCurrentCharLetter()
	{
		return Character.isLetter(currentChar());
	}
	
	private boolean isCurrentCharDigit()
	{
		return Character.isDigit(currentChar());
	}
	
	private String readNameInCurrentLine()
	{
		StringBuilder currentNameBuilder = new StringBuilder();
		while (isCurrentCharLetter() || isCurrentCharDigit())
		{
			currentNameBuilder.append(getCurrentCharAndGetChar());
		}
		return currentNameBuilder.toString();
	}
	
	private int insertConstNumber(int val)//return id in constNumberTable
	{
		if (result.constNumberTable.containsKey(val))
		{
			return result.constNumberTable.get(val);
		}
		else
		{
			int id = result.constNumberTable.size();
			result.constNumberTable.put(val, id);
			return id;
		}
		
	}
	
	private int insertVarName(String s)
	{
		if (result.varNameTable.containsKey(s))
		{
			return result.varNameTable.get(s);
		}
		else
		{
			int id = result.varNameTable.size();
			result.varNameTable.put(s, id);
			return id;
		}
	}
	
	private String readNumInCurrentLine()
	{
		StringBuilder currentDigitBuilder = new StringBuilder();
		while(isCurrentCharDigit())
		{
			currentDigitBuilder.append(getCurrentCharAndGetChar());
		}
		return currentDigitBuilder.toString();
	}
	
	private nodeClass handleCurrentLine() throws compileException//处理当前行的输入
	{
		if(isCurrentCharLetter())
		{
			String currentName = readNameInCurrentLine();
			result.symbolList.add(currentName);
			int code = nodeClass.getNodeType(currentName);
			if (code != -1)
			{
				return new nodeClass(code, null);
			}
			else
			{
				int varNameID = insertVarName(currentName);
				return new nodeClass(nodeClass.varNameType, varNameID);
			}
		}
		else if(isCurrentCharDigit())
		{
			String num = readNumInCurrentLine();
			result.symbolList.add(num);
			int constNumID = insertConstNumber(Integer.parseInt(num));
			return new nodeClass(nodeClass.intType, constNumID);
		}
		else
		{
			//判断是否为标点
			char c1 = getCurrentCharAndGetChar();
			String tarPunc = Character.toString(c1);
			switch(c1)
			{
			case ':':
			case '<':
			case '>'://next is '='
				{
					char c2 = getCurrentCharAndGetChar();
					if(c2 != '=')
					{
						if (c1 == ':')
						{
							throw new compileException("':' is not followed by '=' ");
						}
						pushBackChar();
					}
					else tarPunc += c2;
					break;
				}
			default:
				break;
			}
			result.symbolList.add(tarPunc);
			int puncID = nodeClass.getPunctuation(tarPunc);
			if(puncID == -1)
			{
				throw new compileException("This is not a valid Punctuation.");
			}
			else return new nodeClass(puncID,null);
		}
	}
	
	public boolean handle()//处理所有的输入
	{
		int nowLineCount = 0, outputItemCount = 0;
		StringTokenizer tok = new StringTokenizer(inputString, "\n");
		while(tok.hasMoreTokens())
		{
			currentLine = tok.nextToken();
			nowLineCount++;
			nowInCurrentLineCount = 0;
			while (skipSpace())
			{
				try
				{
					nodeClass returnVal = handleCurrentLine();
					result.nodeList.add(returnVal);
					outputItemCount++;
				}
				catch(compileException e)
				{
					outputError(nowLineCount, outputItemCount, e.getMessage());
					return false;
				}
			}
		}
		if(this.printAnsAfterHandle)
		{
			printResult();
		}
		return true;
	}
	
	public void printResult()
	{
		outputItem.output(result.symbolList, result.nodeList);
	}
	
	private void outputError(int nowLineCount, int outputItemCount, String reason)
	{
		System.err.println("Error at line:"+nowLineCount);
		System.err.println("Output item count:" + (outputItemCount+1));
		System.err.println("Error Reason: " + reason);
	}
	
}
