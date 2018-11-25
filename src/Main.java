import base.compilePipe;

public class Main
{
	private static void wordhandle()
	{
		/*
		 * 
		 * 	(intType,0);(varName,1);("CONST",2);("VAR",3);("procedure",4);
		 *  ("begin",5);("end",6);("odd",7);("if",8);("then",9);
		 *	("call",10);("while",11);("do",12);("read",13);("write",14);
		 *	(".",15);(",",16);(";",17);("=",18);("+",19)
		 *	("-",20);("*",21);("/",22);("#",23);("<",24);
		 *	(">",25);("(",26);(")",27);(":=",28);("<=",29);
		 *	(">=",30);
		 */
		compilePipe compile = new compilePipe("D:\\programming\\eclipse-workspace\\java\\CompileExp\\input\\lv1.txt",false);
		compile.handle();
	}
	public static void main(String[] args)
	{
		wordhandle();
	}
}