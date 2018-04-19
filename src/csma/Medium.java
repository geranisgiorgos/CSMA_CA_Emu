package csma;

public class Medium {
	private boolean free;
	private long stamp;
	
	public Medium(){
		free=true;
		stamp=0;
	}
	
	public boolean getState(){
		if(!free && BSS.step>=stamp)
			return false;
		return true;
	}
	public void setState(boolean b, long s){
		free=b;
		stamp=s;
	}
}
