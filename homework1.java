package homework;
import java.util.Scanner;

public class homework1{

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		System.out.println(isPalindrome(n));
		sc.close();
	}

	public static boolean isPalindrome(int n) {
		int temp, remainder, result = 0;
		if (n < 0)
			n = -n;
		temp = n;
		while (temp != 0) {
			remainder = temp % 10;
			result = result * 10 + remainder;
			temp = temp / 10;
		}
		return n == result;
	}

}
