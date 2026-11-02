import java.util.ArrayList;
public class c1_ArrayList_2 
{
	public static void main(String[] args) 
	{
		ArrayList ar=new ArrayList();
		ar.add(11);
		ar.add(22);
		ar.add(33);
		System.out.println(ar);
        System.out.println(ar.get(2));
        
        System.out.println("--------------");
        
        for(int i=0;i<ar.size();i++)
        {
        	System.out.println(ar.get(i));
        }
        
        System.out.println("--------------");
        
        for(Object x:ar)
        {
        	System.out.println(x);
        }
        
        
	}
}
