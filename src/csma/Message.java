package csma;

public class Message {
	private long stepCreated, stepTransmissionStarts;
	private int length;
	private int senderId, receiverId;
	
	public Message(){
		stepCreated=0;
		stepTransmissionStarts=0;
		length=0;
		senderId=0;
		receiverId=0;
	}
	
	public long getStep(){
		return stepCreated;
	}
	
	public int getLength(){
		return length;
	}
	
	public int getSender(){
		return senderId;
	}
	
	public int getReceiver(){
		return receiverId;
	}
	
	public long getTransStarts(){
		return this.stepTransmissionStarts;
	}
	
	public void setTransStarts(long step){
		stepTransmissionStarts=step;
	}
	
	public Message(long step,int l, int sender, int receiver){
		stepCreated=step;
		length=l;
		senderId=sender;
		receiverId=receiver;
	}
}
