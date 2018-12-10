package syntaxParsing;

public class tableClass 
{
	public static final Integer UNDEFINED = null;
	public static final int kind_Const = 110;
	public static final int kind_Var = 111;
	public static final int kind_Procedure = 112;
	
	protected String name;
	protected int kind;
	protected Integer val;
	protected int level;
	protected Integer adr;
	protected int nowProcedure;
	protected int parentProcedure;
	protected Integer procedureEntry = null;
	public tableClass()
	{
		
	}
	
	public tableClass(final String _name, final int _kind, final Integer _val, final int _level, final Integer _adr,
			final int _nowProcedure, final int _parentProcedure)
	{
		name = _name;
		kind = _kind;
		val = _val;
		level = _level;
		adr = _adr;
		nowProcedure = _nowProcedure;
		parentProcedure = _parentProcedure;
	}
	
	public tableClass(final String _name, final int _kind, final Integer _val, final int _level, final Integer _adr,
			final int _nowProcedure, final int _parentProcedure, final Integer _procedureEntry)
	{
		this(_name,_kind,_val,_level,_adr,_nowProcedure,_parentProcedure);
		procedureEntry = _procedureEntry;
	}
	@Override
	public String toString()
	{
		StringBuilder build = new StringBuilder();
		build.append("name:");
		build.append(name);
		build.append(",kind:");
		switch(kind)
		{
		case kind_Const:
			build.append("const");
			break;
		case kind_Var:
			build.append("var");
			break;
		case kind_Procedure:
			build.append("procedure");
			break;
		default:
			build.append("undefined");
			break;
		}
		build.append(",val:");
		build.append(val == UNDEFINED? "undefined":val);
		build.append(",level:");
		build.append(level);
		build.append(",adr:");
		build.append(adr == UNDEFINED? "undefined":adr);
		build.append(",nowProcedure:");
		build.append(nowProcedure);
		build.append(",parentProcedure:");
		build.append(parentProcedure);
		return build.toString();
	}
	
	public String toJson()
	{
		return '{' + this.toString() + '}';
	}
}
