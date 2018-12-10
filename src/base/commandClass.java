package base;

public class commandClass extends toJsonInterface
{
	public static final int UNDEFINED_com = -1;
	public static final int LIT_com = 100, LOD_com = 101, STO_com = 102, CAL_com = 103,
			INT_com = 104, JMP_com = 105, JPC_com = 106, OPR_com = 107;
	public static String[] comStr = {"LIT","LOD","STO","CAL","INT","JMP","JPC","OPR"};
	public static boolean outputAttr = false;
	public static String getComStr(final int com)
	{
		if(com < LIT_com || com > OPR_com)
		{
			return "undefined";
		}
		else return comStr[com-LIT_com];
	}
	public int fCode = UNDEFINED_com;
	public int lCode = UNDEFINED_com;
	public int aCode = UNDEFINED_com;
	
	public commandClass()
	{
		
	}
	
	public commandClass(final int _fCode, final int _lCode, final int _aCode)
	{
		fCode = _fCode;
		lCode = _lCode;
		aCode = _aCode;
	}
	
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		if(outputAttr)build.append("fCode:");
		build.append(getComStr(fCode));
		build.append(',');
		if(outputAttr)build.append("lCode:");
		build.append(lCode);
		build.append(',');
		if(outputAttr)build.append("aCode:");
		if(fCode == OPR_com)
		{
			switch(aCode)
			{
			case 0:
				build.append(aCode);
				break;
			case WordHandle.nodeClass.SYM_writeln:
				build.append("writeln");
				break;
			default:
				build.append(WordHandle.nodeClass.getNameByType(aCode));
			}
		}
		else build.append(aCode);
		return build.toString();
	}
	
	@Override
	public String toJson()
	{
		return '{' + this.toString() + '}';
	}
}
