package explainRun;

import java.util.ArrayList;
import java.util.Scanner;

import base.commandClass;
import base.compileException;
import base.allSymType;
public class interpreter_back
{
	protected final ArrayList<commandClass> commands;
	//INT��ʱ����������, var�Ļ�û����...
	protected Scanner scan = new Scanner(System.in);
	
	protected int nowStackTop = 0; //topָ��ջ�������Ǹ�
	protected int nowStackBase = 0;
	protected int globalStackTop = 0;
	//���ǻ��仰˵, globalStackTop�ܷ����nowStackTop..
	//�о����������ƵĹ���..
	//
	//��, ��ʵnowStackTop����ʡ��,  �����ǵ��̵߳�, �õ����￪������, �����ٰ�ջ�˵�, ʲô�¶�û�����Ƶ�...
	Integer[] stack = new Integer[100];
	public interpreter_back(final ArrayList<commandClass> _comList)
	{
		commands = _comList;
	}
	
	//��ô��������, ȫ�ֶ�����һ��ջ��...�����ǵ�����ջ
	//�Ҿ��õ�ǰջ��ָ��Ҳ�ñ���..
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
		//0:��̬��, 1:��̬��: 2:��һ������ʱ��ַ, 3:ջ��ָ��. 4.����
		//ջ��ָ�벻�����������..
		//��ʼ��main�ĵ���
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
				//���쾲̬��:
				int p = nowStackBase;
				while(now.aCode < p && p != -1)
				{
					p = stack[p];
				}
				stack[globalStackTop++] = p;
				//���춯̬��:
				stack[globalStackTop++] = nowStackBase;
				//��������ʱ��ַ:
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
				//�Ƚ���CAL����, �ٽ���INT����
				//������ʵ������CAL������3���ռ�, Ȼ����INT���������Ӧ�Ŀռ�..
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
