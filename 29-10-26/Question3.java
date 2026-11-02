import java.util.ArrayList;

public class Question3 {
    public static void main(String[] args) {
        ArrayList arr = new ArrayList();
        arr.add(11);
        arr.add(22);
        arr.add(33);
        System.out.println(arr);
        System.out.println(arr.size());
        System.out.println("---------------");

        arr.add(44);
        arr.add(55);
        System.out.println(arr);
        System.out.println(arr.size());
        System.out.println("---------------");

        arr.add(0,10);
        System.out.println(arr);
        System.out.println(arr.size());
        System.out.println("---------------");

        arr.remove(1);
        System.out.println(arr);
        System.out.println(arr.size());
        System.out.println("---------------");

        //update
        arr.set(0,5);
        System.out.println(arr);
        

    }
}

