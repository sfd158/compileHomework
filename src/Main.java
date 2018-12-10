import base.compilePipe;

public class Main
{
	public static void main(String[] args)
	{
		compilePipe compile = new compilePipe("D:\\programming\\eclipse-workspace\\java\\CompileExp\\input\\lv1.txt",false);
		compile.handle();
	}
}