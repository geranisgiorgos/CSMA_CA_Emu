package csma;

import java.util.ArrayList;

//Basic Service Set class

public class BSS {
	static private Medium medium;	
	static private ArrayList<Node> nodes;		// Λίστα κόμβων
	static private ArrayList<Integer> transmissions;		// Λίστα μεταδόσεων
	static private int numberOfNodes;	// Πλήθος κόμβων
	static private long numberOfSteps;	// Πλήθος μεταδόσεων
	static private int slotTime=20;	// Χρόνος Slot_Time
	static private  float probTrans;	// Πιθανότητα μετάδοσης
	static private int fragmentThreshold=800;	// Τιμή Fragmentation_Threshold
	//static private  int packets;	// Μέγιστο πλήθος πακέτων
	static private  int mpduSize;	// Μέγεθος πλαισίων MPDU
	static private  int DIFS, SIFS, header, preamble;	// τιμές για DIFS και SIFS
	static private  int CW_min=25, CW_max=1023;	// τιμές για CW_min και CW_max
	static private  int maxMSDU=2312;
	static private long offeredLoad=0, throughput=0;
	// Τρέχον βήμα
	static protected long step;
	// Statistics
	static private Stats stats;
	static private int ackTime=1;
	static private double ber=0.000001;
	static public long offered[][];
	static public long transmitted[][];
	
	public BSS(){
		step = 0;
		medium = new Medium();
		nodes = new ArrayList<Node>();
		transmissions = new ArrayList<Integer>();
		stats = new Stats();
		DIFS = SIFS + 2;
	}
	
	// Μέθοδος get για το DIFS
	public static int getDIFS(){
		return DIFS;
	}
	
	// Μέθοδος get για το Slot_Time
		static public  int getSlotTime(){
			return slotTime;
		}
	
	// Μέθοδος get για το DIFS
	static public  int getSIFS(){
		return SIFS;
	}	
	
	// Μέθοδος set για το DIFS	
	public void setSIFS(int sifs){
		SIFS=sifs;
		DIFS = SIFS + 2;
	}
	
	// Μέθοδος set για το header	
	public void setHeader(int h){
			header=h;
		}
	
	// Μέθοδος get για το header	
	static public int getHeader(){
		return header;
	}

	// Μέθοδος set για το preamble	
	public void setPreamble(int pr){
			preamble=pr;
		}

	// Μέθοδος get για το header	
	static public int getPreamble(){
		return preamble;
	}
	
	// επιστρέφει το Fragmentation_Threshold
	static public int getFragmentThreshold(){
		return fragmentThreshold;
	}
	
	// επιστρέφει το Μέγιστο μήκος MSDU
	static public int getMaxMSDU(){
		return maxMSDU;
	}
	
	// επιστρέφει χρόνο για ACK
	static public int getAckTime(){
			return ackTime;
		}
	
	// επιστρέφει CW_min
	static public int get_cw_min(){
		return CW_min;
	}
	
	// ρυθμίζει το CW_min
	static public void set_cw_min(int cw){
		CW_min=cw;
	}
	
	// επιστρέφει CW_max
	static public int get_cw_max(){
		return CW_max;
	}

	// ρυθμίζει το CW_min
	static public void set_cw_max(int cw){
		CW_max=cw;
	}
	
	// επιστρέφει το πλήθος των κόμβων
	static public int getNumberOfNodes(){
		return numberOfNodes;
	}
	
	// ορίζει το πλήθος των κόμβων
	public void setNumberOfNodes(int nodes){
		numberOfNodes=nodes;
	}
	// ορίζει τη πιθανότητα μετάδοσης
	static public void setProb(float p){
		probTrans=p;
	}
	
	// επιστρέφει τη πιθανότητα μετάδοσης
	static public float getProb(){
		return probTrans;
	}
	
	// ορίζει το μέγεθος των πακέτων
	static public void setMPDUSize(int size){
		mpduSize=size;
	}
	
	// επιστρέφει το μέγεθος των πακέτων
		static public int getMPDUSize(){
			return mpduSize;
		}
		
	// επιστρέφει το πλήθος των κόμβων
	static public long getNumberOfTrans(){
		return numberOfSteps;
	}
	
	// Ορίζει το πλήθος των κόμβων
	static public void setNumberOfTrans(long t){
		numberOfSteps=t;
	}
	// επιστροφή του i κόμβου 
	static public Node getNode(int i){
		if(i<numberOfNodes){
			return nodes.get(i);
		}
		return null;
	}
	
	// προσθήκη νέου κόμβου στο BSS
	public boolean addNode(Node node){
		if(!existsNode(node)){
			nodes.add(node);
			return true;
		}
		return false;
	}
	
	// Έλεγχος αν ένας κόμβος (το id) υπάρχει ήδη
	public boolean existsNode(Node node){
		for (Node n : nodes) 
		    if(n.getNodeId()==node.getNodeId())
		        return true;
		return false;
	}
	
	// Διαγράφει όλους τους κόμβους του BSS
	public void clearNodes(){
		nodes.clear();
		}
	
	// Ελέγχει αν το μέσο μετάδοσης είναι ελεύθερο
	static public boolean isMediumFree(){
		return medium.getState();
//		if (BSS.step==1)
//			return true;
//		if(BSS.trans.get((int) (BSS.step-2))==0) return true;
//		else return false;
	}
	
	// Ορίζει το μέσο μετάδοσης να είναι true  όχι
	static public void setMediumFree(boolean b, long s){
		medium.setState(b,s);
	}
	
	// προσθήκη μετάδοσης
	static public void addTrans(int count){
		transmissions.add(count);
	}
	
	// Αύξηση πλήθους κόμβων που μεταδίδουν κατά 1 σε κάποιο βήμα
	static public void incTrans(long s){
			Integer i=transmissions.get((int)s);
			transmissions.set((int)s,i+1 );
	}
	
	// καθαρίζει τις μεταδόσεις που έχουν γίνει
	static public void clearTrans(){
		transmissions.clear();
	}
	
	// Ελέγχει ένα έχει γίνει σύγκρουση
	// Αυτό συμβαίνει όταν έχουμε περισσότερες από μία μεταδόσεις
	static public boolean collision(){
		if(step==1) 
			return false;
		if(transmissions.get((int) (step-2))>1)
			return true;
		return false;
	}
	
	static public void setNAV(int nav, int sender, int receiver){
		for (Node n : nodes){
			if(n.getNodeId()!=sender && n.getNodeId()!=receiver && n.getState()!=Node.SENDING_STATE)
				n.setNAV(nav, step);
		}
	}
		
//	static public void showNodes(){
//		int i;
//		for(i=0;i<numberOfNodes;i++){
//			System.out.println("node-"+nodes.get(i).getId()+" ");
//		}
//	}
	
	static public void showTrans(){
		int i;
		for(i=0;i<transmissions.size();i++)
			System.out.print(" "+transmissions.get(i));
		System.out.println();
	}
	
	// Πλήθος χρονοθυρίδων που το κανάλι είναι free
	static public long getFree(){
		long k=0;
		int i;
		for(i=0;i<transmissions.size();i++)
			if(transmissions.get(i)==0)
				k++;
		return k;
	}
	
	// Πλήθος χρονοθυρίδων που στο κανάλι μεταδίδει ένας κόμβος
	static public long getSuccTrans(){
		long k=0;
		int i;
		for(i=0;i<transmissions.size();i++)
			if(transmissions.get(i)==1)
				k++;
		return k;
	}
	
	static public void addLoad(int load){
		offeredLoad+=load;
	}
	
	static public long getOfferedLoad(){
		return offeredLoad;
	}
	
	static public void addThroughput(int load){
		throughput+=load;
	}
	
	static public long getThroughput(){
		return throughput;
	}
	// Πλήθος χρονοθυρίδων που στο κανάλι μεταδίδει ένας κόμβος
	static public long getCollisions(){
		long k=0;
		int i;
		for(i=0;i<transmissions.size();i++)
			if(transmissions.get(i)>1)
				k++;
		return k;
	}
	
	// Ενημέρωση στατιστικών
	static public void updateStats(){
		stats.updateStats();
	}
	
	static public Stats getStats(){
		return stats;
	}
	
	static public double getBER(){
		return ber;
	}
	static public void setBER(int b ){
		ber=b*0.000001;
	}
}
