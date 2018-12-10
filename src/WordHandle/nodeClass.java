package WordHandle;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class nodeClass extends base.allSymType
{
	public final int nodeType;
	public final Integer nodeValue;
	
	protected static TreeMap<String, Integer> reservedWord = new TreeMap<String, Integer>();
	protected static List<String> keyWordList, puncList;
	protected static TreeMap<String, Integer> Punctuation = new TreeMap<String, Integer>();
	protected static TreeMap<Integer, String> idOfName = new TreeMap<Integer, String>();
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append(nodeType);
		builder.append(',');
		if(nodeValue == null)
		{
			builder.append("-)");
		}
		else
		{
			builder.append(nodeValue.toString());
			builder.append(')');
		}
		return builder.toString();
	}
	
	public static int getNodeType(String s)
	{
		if(reservedWord.containsKey(s))
		{
			return reservedWord.get(s);
		}
		else
		{
			return -1;//NOT Found.
		}
	}
	
	public static int getPunctuation(String s)
	{
		if(Punctuation.containsKey(s))
		{
			return Punctuation.get(s);
		}
		else
		{
			return -1;//NOT Found.
		}
	}
	
	public static String getReservedWordTable()
	{
		StringBuffer buffer = new StringBuffer();
		int printCnt = 0;
		for(Map.Entry<String, Integer> m:reservedWord.entrySet())
		{
			buffer.append('(');
			buffer.append(m.getKey());
			buffer.append(',');
			buffer.append(m.getValue());
			buffer.append(");");
			if((++printCnt) % 5 == 0)
				buffer.append('\n');
		}
		return buffer.toString();
	}
	
	public static String getNameByType(final int type)
	{
		return idOfName.get(type);
	}
	
	public static String printPunctuationTable()
	{
		StringBuilder build = new StringBuilder();
		for(Iterator<Entry<Integer, String>> iter=idOfName.entrySet().iterator(); iter.hasNext();)
		{
			Entry<Integer, String> entry = iter.next();
			build.append(entry.getValue());
			build.append(": ");
			build.append(entry.getKey());
			build.append('\n');
		}
		return build.toString();
	}
	
	static
	{
		keyWordList = Arrays.asList("const","var","procedure","begin","end","odd","if","then",
				"call","while","do","read","write");
		int tot = 2;
		for(Iterator<String> i=keyWordList.iterator(); i.hasNext();)
		{
			reservedWord.put(i.next(), tot++);
		}
		puncList = Arrays.asList(".",",",";","=","+","-","*","/","#","<",">","(",")",":=","<=",">=");
		for(Iterator<String> i=puncList.iterator(); i.hasNext();)
		{
			Punctuation.put(i.next(), tot++);
		}
		final List<List<String>> l=Arrays.asList(keyWordList, puncList);
		tot = 2;
		for(Iterator<List<String>> i1=l.iterator(); i1.hasNext();)
		{
			for(Iterator<String> i2=i1.next().iterator(); i2.hasNext();)
			{
				idOfName.put(tot++, i2.next());
			}
		}
	}
	
	public nodeClass(int _nodeType, Integer _nodeValue)
	{
		nodeType = _nodeType;
		nodeValue = _nodeValue;
	}
	
	public int getNodeType()
	{
		return nodeType;
	}
	
	public boolean isNodeTypeInt()
	{
		return nodeType == nodeClass.intType;
	}
	
	public boolean isNodeTypeVarName()
	{
		return nodeType == nodeClass.varNameType;
	}
	
	public final Integer getNodeValue()
	{
		return nodeValue;
	}
	
	public String getNodeTypeStr()
	{
		if(nodeClass.idOfName.containsKey(this.nodeType))
		{
			return nodeClass.idOfName.get(this.nodeType);
		}
		else return null;
	}
}