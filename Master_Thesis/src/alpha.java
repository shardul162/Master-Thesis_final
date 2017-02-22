
public class alpha {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//Eye_Tracker summon = new Eye_Tracker();
		
		
		Thread t1 = new Thread(new GUI());
		//Thread t2 = new Thread(new Eye_Tracker());
		
		//t2.start();
		t1.start();
		//t2.start();
		
		//if(summon.Tstart==true){
			//t2.start();
			
		//}

	}

}
