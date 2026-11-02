/*
Rotate Array

10 20 30 40 50 k=0
50 10 20 30 40 k=1
40 50 10 20 30 k=2
*/
import java.util.Scanner;

public class Question1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] arr = {10, 20, 30, 40, 50};

        System.out.println("Enter the k value to right rotate:");
        int k = sc.nextInt();

    }
}
// We can give absarct key to class as well as menthods 
//We cannot create object for absctract class 
//Inside abstract class we cna crerate abstract method as well as concetre method 
//If we are extending abtract class we need to overide them 
//