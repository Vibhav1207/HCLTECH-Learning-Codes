/*Checking if is a anagram using freq method */

import java.util.Arrays;
import java.util.Scanner;

public class Question3{
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter String A ");
        String a = sc.next();
        System.out.print("Enter String B ");
        String b = sc.next();
        
        if(a.length()!=b.length()){
            System.out.println("Not a anagram.");
            return;
        }
        char[] ar1=a.toCharArray();
        char[] ar2=b.toCharArray();

        Arrays.sort(ar1);
        Arrays.sort(ar2);

        if(Arrays.equals(ar1, ar2)){
            System.out.println("Its a anagram");
        }
        else{
            System.out.println("Not a anagram");
        }
    }
}