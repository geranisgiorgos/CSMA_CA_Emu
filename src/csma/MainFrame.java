package csma;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class MainFrame  extends JFrame implements WindowListener,ActionListener{
	private int timeSlot=100;
	static final char IDLE_CHAR='.';
	static final char READY_CHAR='^';
	static final char SENDING_CHAR='*';
	static final char BACKOFF_CHAR=':';
	static final char RECEIVING_CHAR='$';
	static final char COLLISION_CHAR='x';
	static final char WAIT_FOR_ACK_CHAR='~';
	static final char SEND_ACK_CHAR=',';
	static final char RECEIVE_ACK_CHAR='&';
	static final char DEFER_CHAR='w';
	static final char FREEZE_CHAR='-';
	
	JLabel symbols1, symbols2, stationsLabel, stepsLabel,probLabel, speedLabel, sifsLabel, rateLabel, headerLabel, preambleLabel, berLabel, cwminLabel;
	
	JButton startButton, clearButton, statsButton, saveButton;
	JTextField nodesTextField, transmissionsTextField, probTextField, sifsTextField, rateTextField, headerTextField, preambleTextField, berTextField, cwminTextField;
	JPanel simPanel;
	JScrollPane scrollPane;
	JRadioButton slowButton, mediumButton, fastButton;
	ButtonGroup group;
	
	private BSS bss;
	static ArrayList<JLabel> nodeLabels;
	Font font;
	final int JLABEL_HEIGHT=20;
	int JLABEL_CHAR_WIDTH=15;
	Timer timer1;
	static int states[];
	public MainFrame() {
		// Δημιουργία BSS
		bss = new BSS();
		// labels για τους κόμβους
		nodeLabels = new ArrayList<JLabel>(); 
		this.setLayout(null);
		slowButton = new JRadioButton("Slow");
		mediumButton = new JRadioButton("Medium");
		fastButton = new JRadioButton("Fast");
		fastButton.setSelected(true);
		group = new ButtonGroup();
		group.add(slowButton);
		group.add(mediumButton);
		group.add(fastButton);
		statsButton=new JButton("Statistics");
		saveButton=new JButton("Save Stats");
		slowButton.setBounds(695, 30, 100, 15);
		mediumButton.setBounds(695, 50, 100, 15);
		fastButton.setBounds(695, 70, 100, 15);
		statsButton.setBounds(670,100,110,30);
		statsButton.setForeground(new Color(250,20,20));
		saveButton.setBounds(670,140,110,30);
		
		stationsLabel = new JLabel("Number of Stations:");
		stepsLabel = new JLabel("Sim steps:");
		probLabel = new JLabel("Tx Prob:");
		speedLabel = new JLabel("Sim speed:");
		sifsLabel = new JLabel("SIFS (μsec):");
		rateLabel = new JLabel("Tx Rate (Mbps):");
		headerLabel = new JLabel("Header (bytes):");
		preambleLabel = new JLabel("Preamble (bytes):");
		berLabel = new JLabel("BER (10e-6):");
		cwminLabel = new JLabel("CW_min:");
		symbols1 = new JLabel();
		symbols2 = new JLabel();
		nodesTextField=new JTextField(3);
		transmissionsTextField=new JTextField(5);
		probTextField=new JTextField(5);
		sifsTextField=new JTextField(5);
		berTextField=new JTextField(5);
		cwminTextField=new JTextField(5);
		rateTextField=new JTextField(5);
		headerTextField=new JTextField(5);
		preambleTextField=new JTextField(5);
		startButton=new JButton("Start");
		clearButton=new JButton("Clear");

		simPanel=new JPanel();
		font = new Font("Courier", Font.BOLD,12);
		stationsLabel.setBounds(20, 10, 160, 30);
		nodesTextField.setBounds(170, 10, 40, 30);
		stepsLabel.setBounds(220, 10, 80, 30);
		transmissionsTextField.setBounds(300, 10, 40, 30);
		probLabel.setBounds(350, 10, 60, 30);
		probTextField.setBounds(420, 10, 40, 30);
		speedLabel.setBounds(690, 10, 100, 15);
		rateLabel.setBounds(470, 10, 120, 30);
		rateTextField.setBounds(600, 10, 40, 30);
		sifsLabel.setBounds(20, 60, 85, 30);
		sifsTextField.setBounds(115, 60, 40, 30);
		headerLabel.setBounds(165,60,120,30);
		headerTextField.setBounds(295,60,40,30);
		preambleLabel.setBounds(345,60,130,30);
		preambleTextField.setBounds(485,60,40,30);
		berLabel.setBounds(20,100,90,30);
		berTextField.setBounds(115,100,40,30);
		cwminLabel.setBounds(170,100,70,30);
		cwminTextField.setBounds(240,100,50,30);
		
		startButton.setBounds(550,100,80,30);
		clearButton.setBounds(450,100,80,30);
		symbols1.setBounds(20, 140, 700, 15);
		symbols1.setForeground(new Color(200,100,0));
		symbols2.setBounds(20, 160, 600, 15);
		symbols2.setForeground(new Color(200,100,0));

		simPanel.setBounds(20,200,700,300);
		simPanel.setBackground(new Color(200,200,200));
		simPanel.setLayout(null);
		// Καθαρισμός πλαισίων 
		clearButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  	if(timer1 != null)
				  		timer1.stop();
				    nodesTextField.setText("");
				    transmissionsTextField.setText("");
				    probTextField.setText("");
				    sifsTextField.setText("");
				    headerTextField.setText("");
				    preambleTextField.setText("");   
				    rateTextField.setText("");
				    berTextField.setText("");
				    cwminTextField.setText("");
				    
				    BSS.clearTrans();
				    int nodes = BSS.getNumberOfNodes();
				    for(int i=0;i<nodes;i++)
				    	  nodeLabels.get(i).setText("");
				    bss.clearNodes();
				    BSS.step=0;
				    BSS.setMediumFree(true, BSS.step+1);
				  } 
				});
		startButton.addActionListener(new ActionListener() { 
			  @SuppressWarnings("static-access")
			public void actionPerformed(ActionEvent e) { 
				  if(slowButton.isSelected())
			    		timeSlot=2000;
			    	else if(mediumButton.isSelected())
			    		timeSlot=800;
			    	else
			    		timeSlot=100;
			    	
				    try{			    	
				    	// Παίρνουμε τα δεδομένα
				    	int nodes = Integer.parseInt(nodesTextField.getText());
				    	states=new int[nodes];
				    	long transmissions = Integer.parseInt(transmissionsTextField.getText());
				    	float prob=Float.parseFloat(probTextField.getText());
				    	//int maxPackets = Integer.parseInt(packTextField.getText());
				    	int sifsDuration=Integer.parseInt(sifsTextField.getText());
				    	float txRate=Float.parseFloat(rateTextField.getText());
				    	int header=Integer.parseInt(headerTextField.getText());
				    	int preamble=Integer.parseInt(preambleTextField.getText());
				    	int ber=Integer.parseInt(berTextField.getText());
				    	int cwmin=Integer.parseInt(cwminTextField.getText());
				    	if(prob<=0 || prob>1) 
				    		System.out.println("Wrong Probability");
				    	else {
				    		BSS.offered=new long[nodes][];
				    		BSS.transmitted=new long[nodes][];
				    		
				    		for(int k=0;k<nodes;k++) {
				    			BSS.offered[k]=new long[nodes];
				    			BSS.transmitted[k]=new long[nodes];
				    		}
				    		for(int k=0;k<nodes;k++)
				    			for(int l=0;l<nodes;l++){
				    				BSS.offered[k][l]=0;
				    				BSS.transmitted[k][l]=0;
				    			}
				    		bss.setProb(prob);
				    		//bss.setPackets(maxPackets);
				    		bss.setNumberOfNodes(nodes);
				    		bss.setNumberOfTrans(transmissions);
				    		// υπολογισμός για το πλήθος των χρονοθυρίδων που καταλαμβάνει ένα SIFS
				    		bss.setSIFS((int)((float)sifsDuration/bss.getSlotTime()+0.9));
				    		// υπολογισμός για το πλήθος των χρονοθυρίδων που καταλαμβάνει ένα MPDU
				    		bss.setMPDUSize((int)(bss.getFragmentThreshold()*8/(txRate*bss.getSlotTime()))+1);
				    		bss.setHeader(header);
				    		bss.setPreamble(preamble);
				    		bss.setBER(ber);
				    		bss.set_cw_min(cwmin);
				    		
				    		for(int i=0;i<nodes;i++){
				    			// προσθήκη κόμβων στο BSS
				    			bss.addNode(new Node(i+1));
				    			
				    			// Προσθήκη κατάλληλων labels
				    			nodeLabels.add(new JLabel());
				    			simPanel.add(nodeLabels.get(i));
				    			char [] nodeString = new char[13];
				    			String s="Node "+(i+1)+":";
				    			nodeString = s.toCharArray();
				    			s=new String(nodeString);
				    			for(int k=s.length();k<15;k++)
				    				s+=" ";
				    			nodeLabels.get(i).setFont(font);
				    			nodeLabels.get(i).setText(s);  
				    			nodeLabels.get(i).setBounds(10,i*JLABEL_HEIGHT,(int) (110+BSS.getNumberOfTrans()*JLABEL_CHAR_WIDTH*2),JLABEL_HEIGHT);
				    			nodeLabels.get(i).setVisible(true);
				    		}
				    		simPanel.setPreferredSize(new Dimension((int) (110+BSS.getNumberOfTrans()*JLABEL_CHAR_WIDTH),nodes*JLABEL_HEIGHT));  
				    		// Action που εκτελείται κάθε timeslot
				    		ActionListener taskPerformer1 = new ActionListener() {
				    			public void actionPerformed(ActionEvent evt) {
				    				BSS.step++;
				    				BSS.addTrans(0);
				    				// επανασχεδιασμός των αναμεταδώσεων
				    				redraw(BSS.getNumberOfNodes(), BSS.getNumberOfTrans());
				    				//BSS.showTrans();
				    			}
				    		};
				    		// Δημιουργία timer
				    		timer1=new Timer(timeSlot, taskPerformer1);
				    		// Δημιουργία δεύτερου timer για να σταματάει τον πρώτο πετά από
				    		// το σύνολο των μεταδόσεων
				    		ActionListener taskPerformer2 = new ActionListener() {
				    			public void actionPerformed(ActionEvent evt) {
				    				timer1.stop();
				    			}
				    		};
				    		Timer timer2=new Timer((int) (timeSlot*(BSS.getNumberOfTrans()+1)), taskPerformer2);
				    		timer2.setRepeats(false);
				    		timer2.start();
				    		timer1.start();
				    	}
				      	}catch(NumberFormatException nfe){
					      System.out.println("NumberFormatException: " + nfe.getMessage());
				      	}
				  	} 
				});
		
		statsButton.addActionListener(new ActionListener() {   
			public void actionPerformed(ActionEvent e) { 
				  StatsFrame statsFrame = new StatsFrame();
				  statsFrame.setTitle("Statistics");
				  statsFrame.setVisible(true);
				  statsFrame.setBounds(400,100,900,400);
			  }
		});
		saveButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				  BSS.getStats().writeDataToFile();
			  }
		});
		
		scrollPane = new JScrollPane(simPanel);
		scrollPane.setVisible(true);
		scrollPane.setBounds(30,200,700,300);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		String symbolsMessage="idle="+IDLE_CHAR+"                     sending="+SENDING_CHAR+"      ready="+READY_CHAR+"              backoff="+BACKOFF_CHAR;
		symbolsMessage+="          receiving="+RECEIVING_CHAR+"     collision="+COLLISION_CHAR;
		symbols1.setText(symbolsMessage);
		symbolsMessage="wait for ack="+WAIT_FOR_ACK_CHAR+"     send ack="+SEND_ACK_CHAR+"     receive ack="+RECEIVE_ACK_CHAR;
		symbolsMessage+="     defer="+DEFER_CHAR+"           freeze="+FREEZE_CHAR;
		symbols2.setText(symbolsMessage);
		this.getContentPane().add(stationsLabel);
		this.getContentPane().add(nodesTextField);
		this.getContentPane().add(stepsLabel);
		this.getContentPane().add(transmissionsTextField);
		this.getContentPane().add(probLabel);
		this.getContentPane().add(probTextField);
		this.getContentPane().add(speedLabel);
		this.getContentPane().add(sifsLabel);
		this.getContentPane().add(sifsTextField);
		this.getContentPane().add(rateLabel);
		this.getContentPane().add(rateTextField);
		this.getContentPane().add(headerLabel);
		this.getContentPane().add(headerTextField);
		this.getContentPane().add(preambleLabel);
		this.getContentPane().add(preambleTextField);
		this.getContentPane().add(berLabel);
		this.getContentPane().add(berTextField);
		this.getContentPane().add(cwminLabel);
		this.getContentPane().add(cwminTextField);
		this.getContentPane().add(startButton);
		this.getContentPane().add(clearButton);
		this.getContentPane().add(symbols1);
		this.getContentPane().add(symbols2);
		this.getContentPane().add(scrollPane);
		this.getContentPane().add(slowButton);
		this.getContentPane().add(mediumButton);
		this.getContentPane().add(fastButton);
		this.getContentPane().add(statsButton);
		this.getContentPane().add(saveButton);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
	
	private void redraw(int nodes, long transmissions){	
		
		// Παίρνουμε τις καταστάσεις των σταθμών
		for(int i=0;i<nodes;i++){					    		  
			BSS.getNode(i).state();
		}
		for(int i=0;i<nodes;i++){					    		  
			states[i]=BSS.getNode(i).getState();
		}
		
		// Εμφανίζουμε το αντίστοιχο σύμβολο για κάθε κατάσταση
		for(int i=0;i<nodes;i++){					    		  
			String mess=nodeLabels.get(i).getText();	
			if(states[i]==Node.IDLE_STATE){
	    		nodeLabels.get(i).setText(mess+" "+IDLE_CHAR);
	    	}
	    	else if(states[i]==Node.READY_STATE){
	    		nodeLabels.get(i).setText(mess+" "+READY_CHAR); 
    		}
	    	else if(states[i]==Node.SENDING_STATE){
	    		nodeLabels.get(i).setText(mess+" "+SENDING_CHAR);
	    	}
	    	else if(states[i]==Node.COLLISION_STATE){
	    		nodeLabels.get(i).setText(mess+" "+COLLISION_CHAR);
	    	}
	    	else if(states[i]==Node.BACKOFF_STATE)
	    		nodeLabels.get(i).setText(mess+" "+BACKOFF_CHAR);
	    	else if(states[i]==Node.RECEIVING_STATE)
	    		nodeLabels.get(i).setText(mess+" "+RECEIVING_CHAR);
	    	else if(states[i]==Node.WAIT_FOR_ACK_STATE)
	    		nodeLabels.get(i).setText(mess+" "+WAIT_FOR_ACK_CHAR);
	    	else if(states[i]==Node.SEND_ACK_STATE)
	    		nodeLabels.get(i).setText(mess+" "+SEND_ACK_CHAR);
	    	else if(states[i]==Node.RECEIVE_ACK_STATE)
	    		nodeLabels.get(i).setText(mess+" "+RECEIVE_ACK_CHAR);
	    	else if(states[i]==Node.DEFER_STATE)
	    		nodeLabels.get(i).setText(mess+" "+DEFER_CHAR);
	    	else if(states[i]==Node.FREEZE_STATE)
	    		nodeLabels.get(i).setText(mess+" "+FREEZE_CHAR);
			

	    	
		}    	
		//BSS.showTrans();
	}
	
	public void actionPerformed(ActionEvent e) {}
	public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
	}
	
//	static public void setLastChar(char c,int index){
//		JLabel label = nodeLabels.get(index);
//		String str=label.getText();
//		str=str.substring(0, str.length()-1)+c;
//		label.setText(str);
//	}
	
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
}
