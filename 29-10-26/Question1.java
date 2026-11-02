import java.util.Scanner;

public class Question1{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a nuber to get the factors: ");
        int N = sc.nextInt();
        int count =0;
        int[] num = { };

        for(int i=1;i<=N;i++){
            if(N%i==0){
                count++;
            }
        }
        System.out.println(count);
    }
}