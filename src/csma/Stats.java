package csma;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public class Stats {
	static private long steps, free, succTransmit, collisions;
	static private ArrayList<Message> msdus;
	
	public Stats(){
		steps=0;
		free=0;
		succTransmit=0;
		collisions=0;
		msdus=new ArrayList<Message>();
	}
	
	public void updateStats(){
		steps=BSS.step;
		free=BSS.getFree();
		succTransmit=BSS.getSuccTrans();
		collisions=BSS.getCollisions();
	}
	
	public long getSteps(){
		return steps;
	}
	
	public long getFree(){
		return free;
	}
	
	public long getSuccTransmit(){
		return succTransmit;
	}
	
	public long getCollisions(){
		return collisions;
	}
	
	// Προσθήκη νέου μηνύματος στη λίστα μηνυμάτων
	public void addMessage(Message msg){
		msdus.add(msg);
	}
	
	public void setTransmissionStarts(int node, long step){
		int i;
		for(i=msdus.size()-1;i>=0;i--){
			Message msg=msdus.get(i);
			if(msg.getSender()==node){
				msg.setTransStarts(BSS.step);
				break;
			}
		}
	}
	
	// Εγγραφή στατιστικών δεδομένων σε αρχείο
	public void writeDataToFile(){
		try{
			// Create file 
			FileWriter fstream = new FileWriter(Main.FILENAME);
			BufferedWriter out = new BufferedWriter(fstream);
			int i;
			for(i=0;i<msdus.size();i++){
				out.write(msdus.get(i).getStep()+" "+msdus.get(i).getSender()+" ");
				out.write(msdus.get(i).getReceiver()+" "+msdus.get(i).getLength()+" ");
				out.write(msdus.get(i).getTransStarts()+"\n");
			}
			out.close();
		}catch (Exception e){//Catch exception if any
			  System.out.println("Error: " + e.getMessage());
		  }
	}
	
	// Υπολογισμός μέσης καθυστέρησης
	public float avgDelay(){
		float mo=0;
		int i;
		if(msdus.size()==0)
			return 0;
		Message msg;
		for(i=0;i<msdus.size();i++){
			msg=msdus.get(i);
			if(msg.getTransStarts()>0)
				mo+=msg.getTransStarts()-msg.getStep();
		}
		return mo/msdus.size();
	}
	
	// Υπολογισμός μέγιστης καθυστέρησης
	public long maxDelay(){
		long max=-1;
			int i;
			Message msg;
			for(i=0;i<msdus.size();i++){
				msg=msdus.get(i);
				if(msg.getTransStarts()-msg.getStep()>max)
					max=msg.getTransStarts()-msg.getStep();
			}
			return max;
		}
	
	// Υπολογισμός ελάχιστης καθυστέρησης
	public long minDelay(){
		long min;
		if(msdus.size()==0)
			return 0;
		min=msdus.get(0).getTransStarts()-msdus.get(0).getStep();
			int i;
			Message msg;
			for(i=1;i<msdus.size();i++){
				msg=msdus.get(i);
				if(msg.getTransStarts()>0)
					if(msg.getTransStarts()-msg.getStep()<min)
							min=msg.getTransStarts()-msg.getStep();
			}
			return min;
		}
}


