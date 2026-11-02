import java.util.Scanner;

public class Question2{
    public static void main(String[] args){

        Scanner sc = new Scanner(System.in);
        int[] arr = {1,2,3,4,5,6,7,8};
        int N=arr.length;
        System.out.println("Enter k value: ");
        int k = sc.nextInt();
        for(int j=1;j<=k;j++){
            int temp = arr[N-1];
            for(int i=N-1;i>0;i--){
                arr[i]=arr[i-1];
            }
            arr[0]=temp;
        }
        for(int x:arr){
            System.out.print(x+" ");
        }
    }
}