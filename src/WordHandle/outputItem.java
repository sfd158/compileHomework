package WordHandle;
import java.util.ArrayList;

public class outputItem
{
	private static int outputMaxLength = 7; //"address"
	private static int spaceCount = 4;
	
	public static void setSpaceCount(int _spaceCount)
	{
		if(_spaceCount > 0)
		{
			spaceCount = _spaceCount;
		}
	}
	
	public static void setOutputMaxLength(int _outputMaxLength)
	{
		if(_outputMaxLength >= 7)
		{
			outputMaxLength = _outputMaxLength;
		}
	}
	
	public static int getSpaceCount()
	{
		return spaceCount;
	}
	
	public static int getOutputMaxLength()
	{
		return outputMaxLength;
	}
	
	public static boolean output(final ArrayList<String> symbolList, 
			final ArrayList<nodeClass> nodeList)
	{
		outputHeader();
		return outputContent(symbolList, nodeList);
	}
	
	public static void outputHeader()
	{
		StringBuffer buffer = new StringBuffer();
		final String[] headerList = {"Symbol", "Class", "Address"};
		final int colWidth = outputMaxLength + spaceCount;
		for(int i=0; i<headerList.length; i++)
		{
			outputStringWithSpace(buffer, headerList[i], colWidth);
		}
		System.out.println(buffer.toString());
	}
	
	private static void outputStringWithSpace(StringBuffer buffer, final String str, final int totlength)
	{
		buffer.append(str);
		for(int j=str.length(); j<totlength; j++)
		{
			buffer.append(' ');
		}
	}
	
	public static boolean outputContent(final ArrayList<String> symbolList, 
			final ArrayList<nodeClass> nodeList)
	{
		StringBuffer buffer = new StringBuffer();
		if (symbolList.size() != nodeList.size())
		{
			return false;
		}
		final int colWidth = outputMaxLength + spaceCount;
		final String emptyStr = "";
		for(int i=0; i<symbolList.size(); i++)
		{
			outputStringWithSpace(buffer, symbolList.get(i), colWidth);
			outputStringWithSpace(buffer, Integer.toString(nodeList.get(i).nodeType), colWidth);
			final Integer nodeAddr = nodeList.get(i).nodeValue;
			final String addr = (nodeAddr == null)?(emptyStr):(nodeAddr.toString());
			outputStringWithSpace(buffer, addr, colWidth);
			buffer.append('\n');
		}
		System.out.println(buffer.toString());
		return true;
	}
	
}
