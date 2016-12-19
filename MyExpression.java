import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

class MyExpression
{
    public ArrayList<MyExpression> expressions=new ArrayList<MyExpression>();
    private ArrayList<String> strings=new ArrayList<String>();
    private ArrayList<String> operators=new ArrayList<String>();
    private static final int BORDER=3;
    //********目前的除号是从右向左运算的
    //********根号还不能画出
    //需要处理的符号：
    //替换：int sum root
    //图像处理（优先级升序）：(^) /
    //比/优先级低的符号：=|+|-|sin|cos|tan|cot|sec|csc|sinh|cosh|tanh|coth|sech|csch|log|ln|lg|\\{|\\}|\\(|\\)|\\|

    MyExpression(){}

    public static void main(String[] args)
    {
        Scanner s=new Scanner(System.in);
        MyExpression abc=new MyExpression();
        if(!abc.read(s.nextLine()))
        {
            System.out.println("格式有误");
            return;
        }
        abc.toImage();
    }

    public boolean read(String e)
    {
        int i,index,cur=0,first1=-1,last1=-1,last=-1;
        boolean ok=true;
        //中文字符变英文字符
        //
        //括号配对查错及去最外层括号
        for(i=0;i<e.length();i++)
        {
            if(e.charAt(i)=='(')
            {
                if(first1==-1) first1=i;
                cur++;
            }
            else if(e.charAt(i)==')')
                if(cur<=0) return false;
                else if (cur--==1)
                {
                    last1=i+1;
                    break;
                }
        }
        if(first1==0&&last1==e.length())
            e=e.substring(1,e.length()-1);
        
        //寻找^
        index=getOuterCharIndex('^',e);

        //找到^，开始处理
        if(index>=0)
        {
            operators.add("^");
            strings.add(e.substring(0,index));
            expressions.add(new MyExpression());
            ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
            if(!ok) return false;
            strings.add(e.substring(index+1,e.length()));
            expressions.add(new MyExpression());
            ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
            if(!ok) return false;
            //运算符个数查错
            return operators.size()<expressions.size();
        }

        //没有读到^，开始尝试/
        index=getOuterCharIndex('/',e);

        //找到外层/，开始处理
        if(index>=0)
        {
            int l=getLeftBorder(e,index);
            if(l==-1) return false;
            int r=getRightBorder(e,index);
            if(r==-1) return false;
            if(l>0)
            {
                strings.add(e.substring(0,l));
                operators.add("");
                expressions.add(new MyExpression());
                ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
                if(!ok) return false;
            }
            strings.add(e.substring(l,index));
            expressions.add(new MyExpression());
            ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
            if(!ok) return false;
            operators.add("/");
            strings.add(e.substring(index+1,r));
            expressions.add(new MyExpression());
            ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
            if(!ok) return false;
            if(r<e.length()-1)
            {
                strings.add(e.substring(r,e.length()));
                operators.add("");
                expressions.add(new MyExpression());
                ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
                if(!ok) return false;
            }
            //运算符个数查错
            return operators.size()<expressions.size();
        }

        //没有找到外层/，开始读括号
        cur=0;
        first1=-1;
        for(i=0;i<e.length();i++)
        {
            if(e.charAt(i)=='(')
            {
                if(first1==-1) first1=i;
                cur++;
            }
            else if(e.charAt(i)==')')
                if(--cur==0)
                {
                    last1=i+1;
                    break;
                }
        }
        if(first1==-1)
        {
            //未找到括号
            strings.add(e);
            return true;
        }
        else
        {
            if(first1>0)
            {
                strings.add(e.substring(0,first1));
                operators.add("");
                expressions.add(new MyExpression());
                ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
                if(!ok) return false;
            }
            strings.add(e.substring(first1,last1));
            expressions.add(new MyExpression());
            ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
            if(!ok) return false;
            if(last1<e.length())
            {
                strings.add(e.substring(last1,e.length()));
                operators.add("");
                expressions.add(new MyExpression());
                ok=ok&&expressions.get(expressions.size()-1).read(strings.get(strings.size()-1));
                if(!ok) return false;
            }
            //运算符个数查错
            return operators.size()<expressions.size();   
        }
    }

    protected int getWidth()
    {
        int sum=0;
        Iterator<MyExpression> itm=expressions.iterator();
        Iterator<String> its=strings.iterator();
        Iterator<String> itc=operators.iterator();
        while(itm.hasNext())
            sum+=itm.next().getWidth()+BORDER;
        if(sum==0)
            while(its.hasNext())
                sum+=getWidth(its.next())+BORDER;
        while(itc.hasNext())
            sum+=getWidth(itc.next())+BORDER;
        return sum;
    }

    protected int getHeight()
    {
        if(expressions.size()==0) return getHeight(strings.get(0))+BORDER*2;
        int max=expressions.get(0).getHeight();
        for(int i=0;i!=operators.size();i++)
        {
            if(operators.get(i)=="/")
                max=Math.max(max,expressions.get(i).getHeight()+expressions.get(i+1).getHeight()+BORDER*2);
            else if(operators.get(i)=="^")
                max=Math.max(max,expressions.get(i).getHeight()+expressions.get(i+1).getHeight()/3);
            else
            {
                max=Math.max(max,expressions.get(i+1).getHeight());
                max=Math.max(max,getHeight(operators.get(i)));
            }
        }
        return max+BORDER*2;
    }

    public String toString()
    {
        String a=strings.get(0);
        Iterator<String> it1=strings.iterator();
        it1.next();
        Iterator<String> it2=operators.iterator();
        while(it1.hasNext()&&it2.hasNext())
            a+=it2.next()+it1.next();
        return a;
    }

    public BufferedImage toImage()
    {
        int w=getWidth(),h=getHeight();
        BufferedImage b=new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g=b.createGraphics();
        g.setFont(new Font("Arial",0,30));
        g.setColor(new Color(0,0,0));
        g.setBackground(new Color(255,255,255));
        g.clearRect(0,0,w,h);
        draw(g,2,h/2+10,1);
        try
        {
            ImageIO.write(b, "jpg", new File("D:\\test.jpg"));
        }
        catch(IOException e)
        {
            System.out.println("写入图片出错！");//写入图片出错
        }
        return b;
    }

    void draw(Graphics2D g,int x,int y,int divide)
    {
        //如果到达最底部则绘制字符串
        if(expressions.size()==0)
        {
            g.setFont(new Font("Arial",0,30/divide));
            g.drawString(strings.get(0),x,y);
            x+=getWidth(strings.get(0))/divide;
            return;
        }

        //如果只有一个表达式
        if(expressions.size()==1)
        {
            expressions.get(0).draw(g,x,y,divide);
            return;
        }

        //对所有表达式依次绘图
        boolean last=false;//记录最后一个表达式是否被画过
        for(int i=0;i!=operators.size();i++)
        {
            if(operators.get(i)=="/")
            {
                g.setFont(new Font("Arial",0,30/divide));
                expressions.get(i).draw(g,x,y-BORDER-expressions.get(i).getHeight()/2,divide);
                g.drawLine(x,y-10/divide,
                           x+Math.max(expressions.get(i).getWidth(),expressions.get(i+1).getWidth())/divide,y-10/divide);
                expressions.get(i+1).draw(g,x,y+(BORDER+expressions.get(i+1).getHeight()/2)/divide,divide);
                last=true;
                x+=Math.max(expressions.get(i).getWidth(),expressions.get(i+1).getWidth())/divide;
            }
            else if(operators.get(i)=="^")
            {
                g.setFont(new Font("Arial",0,30/(divide<3?(divide+1):divide)));
                expressions.get(i).draw(g,x,y,divide);
                g.setFont(new Font("Arial",0,30/(divide<3?(divide+1):divide)));
                expressions.get(i+1).draw(g,x+expressions.get(i).getWidth()/divide,y-10/divide,(divide<3?(divide+1):divide));
                last=true;
                x+=expressions.get(i).getWidth()/divide+expressions.get(i+1).getWidth()/(divide<3?(divide+1):divide);
            }
            else
            {
                g.setFont(new Font("Arial",0,30/divide));
                expressions.get(i).draw(g,x,y,divide);
                last=false;
                x+=expressions.get(i).getWidth()/divide;
                continue;
            }
            if(++i!=operators.size())
            {
                g.setFont(new Font("Arial",0,30/divide));
                g.drawString(operators.get(i),x,y);
                x+=getWidth(operators.get(i))/divide;
            }
            else break;
        }

        //如果最后一个表达式未被画过则绘制
        if(!last)
        {
            g.setFont(new Font("Arial",0,30/divide));
            expressions.get(expressions.size()-1).draw(g,x,y,divide);
        }
    }

    private static int getWidth(String a)
    {
        if(a=="") return 0;
        char[] charArray=a.toCharArray();
        int sum=0;
        for(int i=0;i<charArray.length;i++)
            sum+=getWidth(charArray[i]);
        return sum;
    }

    private static int getHeight(String a)
    {
        return 30;
    }

    private static int getWidth(char a)
    {
        if(a=='/'||a=='f'||a=='l'||a=='i'||a=='j'||a=='I'||a=='r'||a=='t')
            return 8;
        else if(a>='A'&&a<='Z'&&a!='I'&&a!='M'&&a!='W')
            return 22;
        else if(a=='%'||a=='M'||a=='m'||a=='w')
            return 27;
        else if(a=='W')
            return 32;
        else
            return 17;
    }

    private static int getHeight(char a)
    {
        return 30;
    }

    private static int getOuterCharIndex(char a,String e)
    {
        int cur,index;
        index=e.indexOf(a);
        while(index>=0)
        {
            cur=0;
            for(int i=0;i<index;i++)
            {
                if(e.charAt(i)=='(') cur++;
                else if(e.charAt(i)==')') cur--;
            }
            if(cur==0) return index;
            index=e.indexOf(a,index+1);
        }
        return -1;
    }

    private static int getLeftBorder(String a,int cur)
    {
        int i=cur,k=0;
        char tmp;
        while(--i>=0)
        {
            tmp=a.charAt(i);
            if(tmp=='(') if(k>0) {k--; continue;} else return -1;
            if(tmp==')') {k++; continue;}
            if(k>0) continue;
            if((tmp>='a'&&tmp<='z')||(tmp>='A'&&tmp<='Z')||(tmp>='0'&&tmp<='9')||tmp=='.'||tmp=='*'||tmp=='!'||tmp=='?') continue;
            break;
        }
        if(k>0) return -1;
        return i+1;
    }

    private static int getRightBorder(String a,int cur)
    {
        int i=cur,k=0;
        char tmp;
        while(++i<a.length())
        {
            tmp=a.charAt(i);
            if(tmp=='(') {k++; continue;}
            if(tmp==')') if(k>0) {k--; continue;} else return -1;
            if(k>0) continue;
            if((tmp>='a'&&tmp<='z')||(tmp>='A'&&tmp<='Z')||(tmp>='0'&&tmp<='9')||tmp=='.'||tmp=='*'||tmp=='!'||tmp=='?') continue;
            break;
        }
        if(k>0) return -1;
        return i;
    }
}