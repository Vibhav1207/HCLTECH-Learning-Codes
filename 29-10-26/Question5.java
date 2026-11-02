import java.util.ArrayList;

public class Question5 {
    public static void main(String[] args) {
        ArrayList ar = new ArrayList();
        ar.add(11);
        ar.add("India");
        ar.add('G');
        ar.add(23.67);
        ar.add(true);

        System.out.println(ar);
        System.out.println("----------");
        for(Object x: ar){
            System.out.println(x);
        }
    }
}
