
public class alpha {

	public static void main(String[] args) {
		
		Thread t1 = new Thread(new GUI());
		
		t1.start();
		
	}
	
}
