package explainRun;

import java.util.ArrayList;
import java.util.Scanner;

import base.commandClass;
import base.compileException;
import base.allSymType;
public class interpreter_back
{
	protected final ArrayList<commandClass> commands;
	//INT暂时还是有问题, var的还没算上...
	protected Scanner scan = new Scanner(System.in);
	
	protected int nowStackTop = 0; //top指向栈顶上面那个
	protected int nowStackBase = 0;
	protected int globalStackTop = 0;
	//但是换句话说, globalStackTop能否代替nowStackTop..
	//感觉这是有相似的功能..
	//
	//嗯, 其实nowStackTop可以省掉,  反正是单线程的, 用到哪里开到哪里, 用完再把栈退掉, 什么事都没发生似的...
	Integer[] stack = new Integer[100];
	public interpreter_back(final ArrayList<commandClass> _comList)
	{
		commands = _comList;
	}
	
	//这么搞有问题, 全局都是在一个栈里...而不是单独的栈
	//我觉得当前栈顶指针也得保存..
	protected Integer getTop()
	{
		if(nowStackTop > nowStackBase)
			return stack[nowStackTop-1];
		else return null;
	}
	
	protected Integer getSecondTop()
	{
		if(nowStackTop > nowStackBase + 1)
			return stack[nowStackTop-2];
		else return null;
	}
	
	protected void setTop(final int value)
	{
		stack[nowStackTop-1] = value;
	}
	
	protected void push(final int value)
	{
		stack[nowStackTop++] = value;
	}
	
	protected void pop() throws compileException
	{
		if(nowStackTop <= nowStackBase)
			throw new compileException("pop from stack base.");
		nowStackTop--;
	}
	
	public boolean run()
	{
		try
		{
			handle();
			return true;
		}
		catch(compileException e)
		{
			return false;
		}
	}
	
	public void handle() throws compileException
	{
		int i = 0;
		//0:静态链, 1:动态链: 2:上一次运行时地址, 3:栈顶指针. 4.变量
		//栈顶指针不保存根本不行..
		//初始化main的调用
		nowStackTop = globalStackTop = 3;
		stack[0] = -1; stack[1] = -1; stack[2] = -1;
		
		commandClass now = null;
		
		for(;;)
		{
			now = commands.get(i);
			switch(now.fCode)
			{
			case commandClass.LIT_com:
			{
				setTop(now.aCode);
				i++;
				break;
			}
			case commandClass.LOD_com:
			{
				int p = nowStackBase;
				for(int j=0; j<now.lCode; j++)
				{
					p = stack[p];
				}
				push(stack[p+now.aCode]);
				break;
			}
			case commandClass.STO_com:
			{
				int p = nowStackBase;
				for(int j=0; j<now.lCode; j++)
				{
					p = stack[p];
				}
				stack[p+now.aCode] = getTop();
				pop();
				break;
			}
			case commandClass.CAL_com:
			{
				//构造静态链:
				int p = nowStackBase;
				while(now.aCode < p && p != -1)
				{
					p = stack[p];
				}
				stack[globalStackTop++] = p;
				//构造动态链:
				stack[globalStackTop++] = nowStackBase;
				//保存运行时地址:
				stack[globalStackTop++] = i+1;
				i = now.aCode;
				break;
			}
			case commandClass.INT_com:
			{
				for(int j=0; j<now.aCode; j++)
				{
					stack[globalStackTop++] = 0;
				}
				//先进行CAL操作, 再进行INT操作
				//所以其实可以在CAL里申请3个空间, 然后在INT里在申请对应的空间..
				i++;
				break;
			}
			case commandClass.JMP_com:
			{
				i = now.aCode;//fla
				break;
			}
			case commandClass.JPC_com:
			{
				int val = getTop();
				switch(val)
				{
				case 0:
					i = now.aCode;
					break;
				case 1:
					i++;
					break;
				default:
					throw new compileException("condition judge error");
				}
				break;
			}
			case commandClass.OPR_com:
			{
				Integer first = getTop(), second = getSecondTop();
				int ans = 0;
				switch(now.aCode)
				{
				case allSymType.SYM_add:
					ans = second + first;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_minus:
					ans = second - first;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_mul:
					ans = second * first;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_div:
					ans = second / first;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_read:
					System.out.println("Please input a number:");
					ans = scan.nextInt();
					push(ans);
					break;
				case allSymType.SYM_write:
					System.out.print(first + " ");
					pop();
					break;
				case allSymType.SYM_writeln:
					System.out.println();
					break;
				case allSymType.SYM_odd:
					ans = (first % 2 == 1)?1:0;
					pop();
					push(ans);
					break;
				case allSymType.SYM_le:
					ans = (second < first)?1:0;
					pop(); pop(); 
					push(ans);
					break;
				case allSymType.SYM_leq:
					ans = (second <= first)?1:0;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_ge:
					ans = (second > first)?1:0;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_geq:
					ans = (second >= first)?1:0;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_pound:
					ans = (second != first)?1:0;
					pop(); pop();
					push(ans);
					break;
				case allSymType.SYM_equal:
					ans = (second == first)?1:0;
					pop(); pop();
					push(ans);
					break;
				case 0:
					if(now.lCode == 0)
					{
						//return
						i = stack[nowStackBase + 2];
						globalStackTop = nowStackBase;
						nowStackBase = stack[nowStackBase + 1];
						if(globalStackTop == 0)return;
					}
					else throw new compileException("Ret wrong.");
				}
				i++;
				break;
			}
			}
		}
	}
	
}
