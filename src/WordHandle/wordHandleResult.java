package WordHandle;

import java.util.ArrayList;
import java.util.TreeMap;

public class wordHandleResult
{
	protected TreeMap<Integer, Integer> constNumberTable = new TreeMap<Integer, Integer>();
	protected TreeMap<String, Integer> varNameTable = new TreeMap<String, Integer>();
	
	protected ArrayList<String> symbolList = new ArrayList<String>();
	protected ArrayList<nodeClass> nodeList = new ArrayList<nodeClass>();
	
	public wordHandleResult()
	{
		
	}
	
	public Integer getConstNumber(final nodeClass _node)
	{
		if (_node.isNodeTypeInt())
		{
			return constNumberTable.get(_node.nodeValue);
		}
		else return null;
	}
	
	public final TreeMap<Integer, Integer> getConstNumberTable()
	{
		return constNumberTable;
	}
	public final TreeMap<String, Integer> gerVarNameTable()
	{
		return varNameTable;
	}
	public final ArrayList<String> getSymbolList()
	{
		return symbolList;
	}
	public final ArrayList<nodeClass> getNodeList()
	{
		return nodeList;
	}
}
