package explainRun;

import java.util.ArrayList;
import java.util.Scanner;

import base.commandClass;
import base.compileException;
import base.allSymType;
public class interpreter 
{
	protected final ArrayList<commandClass> commands;
	//INT暂时还是有问题, var的还没算上...
	protected Scanner scan = new Scanner(System.in);
	
	//protected int nowStackTop = 0; //top指向栈顶上面那个
	protected int nowStackBase = 0;
	protected int globalStackTop = 0;
	//但是换句话说, globalStackTop能否代替nowStackTop..
	//感觉这是有相似的功能..
	//
	//嗯, 其实nowStackTop可以省掉,  反正是单线程的, 用到哪里开到哪里, 用完再把栈退掉, 什么事都没发生似的...
	//protected int maxStackLength;
	//protected int nowHead = 0;
	Integer[] stack;
	public interpreter(final ArrayList<commandClass> _comList)
	{
		this(_comList, 100);
	}
	
	public interpreter(final ArrayList<commandClass> _comList, final int stackMaxLen)
	{
		commands = _comList;
		stack = new Integer[stackMaxLen];
	}
	//这么搞有问题, 全局都是在一个栈里...而不是单独的栈
	//我觉得当前栈顶指针也得保存..
	protected Integer getTop()
	{
		if(globalStackTop > nowStackBase)
			return stack[globalStackTop-1];
		else return null;
	}
	
	protected Integer getSecondTop()
	{
		if(globalStackTop > nowStackBase + 1)
			return stack[globalStackTop-2];
		else return null;
	}
	
	protected void setTop(final int value)
	{
		stack[globalStackTop-1] = value;
	}
	
	protected void push(final int value)
	{
		stack[globalStackTop++] = value;
	}
	
	protected void pop() throws compileException
	{
		if(globalStackTop <= nowStackBase)
			throw new compileException("pop from stack base.");
		stack[--globalStackTop] = null;
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
		//nowHead = 0;
		//0:静态链, 1:动态链: 2:上一次运行时地址,  3.变量
		//栈顶指针不保存根本不行..
		//初始化main的调用
		globalStackTop = 0;
		push(-1); push(-1); push(-1);
		commandClass now = null;
		for(;;)
		{
			now = commands.get(i);
			switch(now.fCode)
			{
			case commandClass.LIT_com:
			{
				push(now.aCode);
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
				i++;
				break;
			}
			case commandClass.STO_com:
			{
				int p = nowStackBase;
				//问题: nowStackBase没有更新..
				for(int j=0; j<now.lCode; j++)
				{
					p = stack[p];
				}
				stack[p+now.aCode] = getTop();
				pop();
				i++;
				break;
			}
			case commandClass.CAL_com:
			{
				//TODO:构造静态链:
				int nowHead = (stack[nowStackBase+2] == -1)?-1:commands.get(stack[nowStackBase+2]).aCode, ans = nowStackBase;
				//System.err.println(nowHead);
				if(nowHead == now.aCode)
				{
					//递归
					//System.out.println(ans + " " + stack[ans] + " " + stack[stack[ans]]);
					ans = stack[ans];
					//ans = 7;
				}
				/*else if(nowHead < now.aCode)
				{
					//同一procedure内
					//ans = stack[nowStackBase];
				}
				else if(nowHead > now.aCode)
				{
					//前面的...
				}*/
				stack[globalStackTop++] = ans;
				//构造动态链:
				stack[globalStackTop++] = nowStackBase;
				//保存运行时地址:
				stack[globalStackTop++] = i;
				i = now.aCode;
				nowStackBase = globalStackTop-3;
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
				pop();
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
						int tp = stack[nowStackBase + 1];
						i = stack[nowStackBase + 2];
						for(int j=globalStackTop-1; j>=nowStackBase; j--)
							stack[j] = null;
						globalStackTop = nowStackBase;
						nowStackBase = tp;
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
