//input and  print first  non repeating character from the string using loops 
import java.util.Scanner;

public class Question1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter first input: ");
        String a = sc.next();

        for (int i = 0; i < a.length(); i++) {
            int count = 0;
            char ch = a.charAt(i);

            for (int j = 0; j < a.length(); j++) {
                if (ch == a.charAt(j)) {
                    count++;
                }
            }
            if (count == 1) {
                System.out.println(ch);
                break;
            }
        }
    }
}