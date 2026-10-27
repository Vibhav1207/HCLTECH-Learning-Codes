/*1
23
456
78910*/

public class Question1{
    public static void main(String[] args){
       int c = 1;

       for(int i=1; i<=4; i++){
           for(int j=1; j<=i; j++){
               System.out.print(c);
               c++;
           }
           System.out.println();
       }
        
    }
}