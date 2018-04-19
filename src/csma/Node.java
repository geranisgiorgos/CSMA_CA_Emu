package csma;

import java.util.Random;

public class Node {
	static final int IDLE_STATE=0;
	static final int READY_STATE=1;
	static final int SENDING_STATE=2;
    static final int BACKOFF_STATE=3;
	static final int DEFER_STATE=4;
	static final int COLLISION_STATE=5;
	static final int RECEIVING_STATE=6;
	static final int WAIT_FOR_ACK_STATE=7;
	static final int WAIT_ACK_STATE=8;
	static final int SEND_ACK_STATE=9;
	static final int RECEIVE_ACK_STATE=10;
	static final int FREEZE_STATE=11;
	
	
	// Καταστάσεις στις οποίες μπορεί να βρεθεί ο κόμβος
    private boolean idle, ready, sending, backoff, wait_for_ack, collision, receiving, defer, send_ack, receive_ack;
    // Χαρακτηριστικό κόμβου (id)
    private int nodeId;
    // μετρητής που μετράει πόσο χρόνο θα βρίσκεται σε μία κατάσταση (ίσο με DIFS ή SIFS)
    private int counter_ifs;
    // μετρητής για defer
    private int counter_defer;
    // μετρητής για αναμονή για επιβεβαίωση
    private int counter_ack;
    // μετρητής για backoff
    private int counter_bo;
    // πλήθος δεδομένων για αποστολή (bytes)
    private int dataLength;
    // private int freeze_state;
    private double probError;
    private boolean is_ack;
    
    // Μετρητής συγκρούσεων
    private int collisionCounter;
    // Χρόνος  αναμονής μετά από σύκγρουση
    private int collisionWaitTime;
    // contention window
    private int cw;
    // backoff time
    private int bo;
    // παραλείπτης και αποστολέας κόμβος 
    private int sendTo, receiveFrom;
    // Πλήθος πακέτων MPDU που θέλει να μεταδώσει ο κόμβος
    private int packets;
    // τιμή NAV (Network Allocation Vector)
    private int nav=0;
    private long nav_stamp;
    // μέγεθος MPDU
    private int mpduSize, threshold, header, preamble;
    
    // constructor
    public Node(int id){
    	this.nodeId=id;
    	idle=true;
    	ready=false; 
    	sending=false;
    	backoff=false;
    	wait_for_ack=false;
    	collision=false;
    	receiving=false; 
    	defer=false;
    	send_ack=false;
    	receive_ack=false;
    }
    
    // Επιστρέφει το node id του κόμβου
    public int getNodeId(){
    	return nodeId;
    }
    
    // επιστρέφει τυχαίο χρόνο backoff ανάμεσα στο 0 και στο cw
    private int get_bo(){
    	Random r=new Random();
    	return r.nextInt(cw)+1;
    }
    
    // Ορισμός του NAV για τον κόμβο
    public void setNAV(int n, long step){
    	if(collision)
    		nav=n>collisionWaitTime?n-collisionWaitTime:n-collisionWaitTime;
    	else
    		nav=n;
    	nav_stamp=step;
    }
    
    // επιστρέφει την κατάσταση του κόμβου
    public int getState(){
    	if(!sending && !collision && nav>0 && BSS.step>nav_stamp)
    		return FREEZE_STATE;
    	if(receiving)
    		return RECEIVING_STATE;
    	if(ready)
    		return READY_STATE;
    	if(sending)
    		return SENDING_STATE;
    	if(wait_for_ack)
    		return WAIT_FOR_ACK_STATE;
    	if(collision)
    		return COLLISION_STATE;
    	if(send_ack)
    		return SEND_ACK_STATE;
    	
    	if(receive_ack)
    		return RECEIVE_ACK_STATE; 
    	if(backoff)
    		return BACKOFF_STATE;
    	if(defer)
    		return DEFER_STATE;
    	if(idle){
    		return IDLE_STATE;	
			
    	}
    	return -1;
    }
    
    // ορίζουμε την κατάσταση
    public void setState(int  state){
    	if(state==RECEIVING_STATE)
    		receiving=true;
    	if(state==READY_STATE)
    		ready=true;
    	if(state==SENDING_STATE)
    		sending=true;
    	if(state==WAIT_FOR_ACK_STATE)
    		wait_for_ack=true;
    	if(state==COLLISION_STATE)
    		collision=true;
    	if(state==SEND_ACK_STATE)
    		send_ack=true;
    	if(state==RECEIVE_ACK_STATE)
    		receive_ack=true;
    	if(state==BACKOFF_STATE)
    		backoff=true;
    	if(state==DEFER_STATE)
    		defer=true;
    	if(state==IDLE_STATE)
    		idle=true;
    }
    // Eπιστρέφει την κατάσταση του κόμβου κατά τη συγκεκριμένη χρονοθυρίδα
    public void state(){
    	//if(BSS.isMediumFree())
    	//	System.out.println("free");
    	//else
    	//	System.out.println("Not free");
    	if(!sending && !collision && nav>0 && BSS.step>nav_stamp){
    		nav--;
    		return;
    	}
     	// Αν ο κόμβος είναι αδρανής τότε αν έχει δεδομένα τότε 
    	// περνάει σε κατάσταση διεκδίκησης του καναλιού
    	if(idle){	
    		// Αν έχει δεδομένα για αποστολή πάει στην κατάσταση ready
    		if(hasDataToSend()){
    			int maxMSDU=BSS.getMaxMSDU();
    			threshold=BSS.getFragmentThreshold();
    			header=BSS.getHeader();
    			preamble=BSS.getPreamble();
    			Random r=new Random();
    	    	dataLength=r.nextInt((int)(maxMSDU-((float)maxMSDU/threshold+1)*(header+preamble)))+1;
    	    	if(!is_ack){
    	    		BSS.addLoad(dataLength);
    	    		}
    			// πλήθος πακέτων
    			//packets=getNoOfPackets();
    	    	packets=dataLength/(threshold-(header+preamble))+1;
    			is_ack=false;
    			// παραλήπτης κόμβος
    			sendTo=getDestination();
    			BSS.offered[nodeId-1][sendTo-1]+=dataLength;
				ready=true;
				idle=false;
				Message msg = new Message(BSS.step,dataLength,nodeId,sendTo);
				BSS.getStats().addMessage(msg);
				counter_ifs=BSS.getDIFS()-1;
				collisionCounter=0; 			
			// αλλιώς παραμένει idle
    		}
    		return;
    	}	
      	// Διεκδίκηση καναλιού για αποστολή δεδομένων
    	if(ready){
    		if(!BSS.isMediumFree()){
    			ready=false;
    			backoff=true;
    			cw=BSS.get_cw_min();
    			bo=get_bo();
    			counter_bo=bo;
    			return;
    		} else 	if(counter_ifs>0){
    			counter_ifs--;	
    			return;
    		}else {
    			ready=false;
    			sending=true;
       			nav=0;
       			if(packets==dataLength/(threshold-(header+preamble))+1)
       				BSS.getStats().setTransmissionStarts(nodeId, BSS.step);
    			// μέγεθος MPDU
    			mpduSize=BSS.getMPDUSize();
    			
    			
    			
    			
    			collisionCounter=0;
    			BSS.setMediumFree(false, BSS.step+1);
    			BSS.getNode(sendTo-1).setReceiving(nodeId, true);
    			BSS.incTrans(BSS.step-1);
    			int  nav_time=(mpduSize+BSS.getSIFS()+BSS.getAckTime()+BSS.getSIFS());
    			BSS.setNAV(nav_time, nodeId, sendTo);
    			return;
    		}
    	}
    	// Αποστολή δεδομένων
    	if(sending){
    		// έλεγχος για σύγκρουση
    		if(BSS.collision()){
    			sending=false;
    			receiving=false;
    			collision=true;
    			//if(BSS.getNode(sendTo-1).getState()==Node.RECEIVING_STATE)
    			//	BSS.getNode(sendTo-1).setState(Node.IDLE_STATE);
    			collisionCounter++;
    			BSS.setMediumFree(true, BSS.step+1);
    			collisionWaitTime=getCollisionWaitTime(collisionCounter);
    			return;
    		}
    		mpduSize--;
    		if(mpduSize == 0){
    			if(is_ack){
    				BSS.setMediumFree(true, BSS.step+1);
    				is_ack=false;
					sending=false;
    				//if(backoff){
    				//	System.out.println("!!!");
    				//	return;
    				//}
    				if(!backoff && !defer){

    					idle=true;
    					
    				}
    				
    				BSS.getNode(sendTo-1).setReceiveAck(nodeId, false);
    				return;
    			} else {
    				packets--;
        			sending=false;
    				counter_ifs=BSS.getSIFS();
    				wait_for_ack=true;
    				
    				BSS.getNode(sendTo-1).sendAck(nodeId);
    				counter_ack=BSS.getDIFS();
    				

        			int mpduLength=0;
        			if(packets>0)
    					mpduLength=BSS.getFragmentThreshold()-header-preamble;
        			else
    					mpduLength=dataLength%BSS.getFragmentThreshold();
        			// Πιθανότητα σφάλματος
        			probError=1-Math.pow((1-BSS.getBER()),mpduLength*8);
        			Random r=new Random();
        			if(!is_ack && r.nextFloat()>probError){
        				BSS.addThroughput(mpduLength);
        				BSS.transmitted[nodeId-1][sendTo-1]+=mpduLength;
        			}
        			is_ack=true;
    				
    				return;
    			}
    		}
    		BSS.incTrans(BSS.step-1);
    	}
    	// κατάσταση backoff
    	if(backoff){
    		if(BSS.isMediumFree()){
    			counter_bo--;
    			if(counter_bo<=0){
    				backoff=false;
    				ready=true;
    			}
    			//return;
    		} else {
    			counter_defer=BSS.getDIFS();
    			backoff=false;
    			defer=true;
    		}
    	}
    	//κατάσταση defer
    	if(defer){
    		counter_defer--;
    		if (counter_defer==0){
    			defer=false;
    			backoff=true;
    			
    		}
    	}
    	// σύγκρουση
    	if(collision){
    		collisionWaitTime--;
    		if(collisionWaitTime==0){
    			collision=false;
    			ready=true;
    			return;
    		}
    		return;
    	}
    	// κατάσταση αναμονής για λήψη επιβεβαίωσης
    	if(wait_for_ack){
    		counter_ack--;
    		//if(receiving && sendTo == receiveFrom){
    		if(receiving){
    			packets--;
    			receiving=false;
    			wait_for_ack=false;
    			receive_ack=true;
    			return;
    		}
    	}
    	// λήψη επιβεβαίωσης
    	if(receive_ack){
			is_ack=false;
			//packets--;
    		if(packets>0) {	
    			receive_ack=false;
    			receiving=false;
    			
    			counter_ifs=BSS.getSIFS()-1;
    			ready=true;
    			return;
    		} else {
    			if(nodeId>receiveFrom){
    				return;
    			}
    			receive_ack=false;
    			receiving=false;
    			idle=true;
    			
    			BSS.setMediumFree(true, BSS.step+1);
    			return;
    		}
    	}
    	// Λήψη δεδομένων
    	if(receiving){
    		//return;
    	}
    	// αποστολή επιβεβαίωσης
    	if(send_ack){
    		counter_ifs--;
    		if(counter_ifs==0){
    			sending=true;
    			BSS.incTrans(BSS.step-1);
    			send_ack=false;
    			is_ack=true;
    			mpduSize=BSS.getAckTime();
    			//BSS.setMediumFree(false);
    			BSS.getNode(sendTo-1).setReceiveAck(nodeId, true);
    			//MainFrame.states[sendTo-1]=RECEIVE_ACK_STATE;
    			return;
    		}
    		return;
    	} 
    	
    }
    
    // Επιστρέφει true αν ο κόμβος έχει δεδομένα για αποστολή
    // αλλιώς επιστρέφει false
    private boolean hasDataToSend(){
    	Random r = new Random();
    	if(r.nextFloat()<BSS.getProb())
    		return true;
    	else
    		return false;
    }
    
    // Επιστρέφει το πλήθος των χρονοθυρίδων που πρέπει να περιμένει ο κόμβος
    // όταν έχουν συμβεί i διαδοχικές συγκρούσεις
    private int getCollisionWaitTime(int i){
    	Random r=new Random();
    	return (int)(Math.pow(2,2+i)*r.nextFloat())+1;
    }

    // Επιλέγει  με τυχαίο τρόπο έναν σταθμό που θα είναι ο προορισμός
    // για τα δεδομένα
    private int getDestination(){
    	int sendTo;
    	Random r=new Random();
    	do{
    		sendTo=(int) (r.nextFloat()*BSS.getNumberOfNodes())+1;
    	}while(sendTo == nodeId);
    	return sendTo;
    }
    
    // Επιστρέφει πλήθος πλαισίων που θα σταλούν
    //private int getNoOfPackets(){
    //	Random r=new Random();
    //	return (int)(r.nextFloat()*BSS.getPackets())+1;
    //}
    
    // ενεργοποιεί την κατάσταση receiving όπου γίνεται λήψη από τον κόμβο id
    public void setReceiving(int id, boolean b){
    	if(b) {
    		receiving=true;
    		if(collision){
    			collision=false;
    			cw=BSS.get_cw_min();
    			bo=get_bo();
    			counter_bo=bo;
    			backoff=true;
    		}
     		nav=0;
    		idle=false;
    		wait_for_ack=false;
    		if(ready && counter_ifs>0 ){
    			backoff=true;
    			cw=BSS.get_cw_min();
    			bo=get_bo();
    			counter_bo=bo;
    		}
        	receiveFrom=id;	
    	}else {
    		receiving=false;
    		if(collision){
    			collision=false;
    			cw=BSS.get_cw_min();
    			bo=get_bo();
    			counter_bo=bo;
    			backoff=true;
    		} else {
    			idle=true;

    			receiveFrom=-1;
    		}
    	}
    }
    
    // ενεργοποιεί την κατάσταση receive_ack όπου γίνεται λήψη από τον κόμβο id
    public void setReceiveAck(int id, boolean b){
    	if(b) {
    		receive_ack=true;
   
    		//idle=false;
    		wait_for_ack=false;
        	receiveFrom=id;	
    	}else {
    		receive_ack=false;
    		if(packets<=0) {
    			idle=true;
    			sending=false;
    			receiveFrom=-1;
    		} else {
    			ready=true;
    			sending=false;
    			counter_ifs=BSS.getSIFS()-1;
    		}
    	}
    }
    
    // o παραλήπτης πρέπει να στείλει επιβεβαίωση
    public void sendAck(int id){
    	receiving=false;
    	send_ack=true;
    	sendTo=id;
    	counter_ifs=BSS.getSIFS();
    	if(nodeId>id)
    		counter_ifs++;
    }
 
}

