
//Check if array is sorted or not 
public class Question2 {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 5, 4};
        boolean sorted = true;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                sorted = false;
                break;
            }
        }
        if (sorted) {
            System.out.println("The array is sorted.");
        } else {
            System.out.println("The array is not sorted.");
        }
    }
}