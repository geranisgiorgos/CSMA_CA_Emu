package csma;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



@SuppressWarnings("serial")
public class StatsFrame  extends JFrame implements WindowListener,ActionListener{
	JLabel label1, slotsLabel, freeLabel, succTransLabel, collisionsLabel, avgDelayLabel;
	JLabel maxDelayLabel, minDelayLabel, offeredLoadLabel, avgOfferedLoadLabel, throughputLabel, utilisationLabel;
	JPanel nodesPanel;
	JScrollPane scrollPane;
	Font font1;
	Formatter fmt;
	static ArrayList<JLabel> nodeLabels;
	public StatsFrame() {
		this.setLayout(null);
		BSS.updateStats();
		
		font1 = new Font("Courier", Font.BOLD,16);
		label1 = new JLabel("Statistics");
		slotsLabel=new JLabel("Timeslots: "+(BSS.getStats().getSteps()+2));
		freeLabel=new JLabel("Idle slots: "+(BSS.getStats().getFree()+2));
		succTransLabel=new JLabel("Successful Trans: "+BSS.getStats().getSuccTransmit());
		collisionsLabel=new JLabel("Collisions: "+BSS.getStats().getCollisions());
		avgDelayLabel=new JLabel("Average Delay: "+Math.round(100*BSS.getStats().avgDelay()*BSS.getSlotTime())/100.0+" μsec");
		maxDelayLabel=new JLabel("Maximum Delay: "+BSS.getStats().maxDelay()*BSS.getSlotTime()+" μsec");
		minDelayLabel=new JLabel("Minimum Delay: "+BSS.getStats().minDelay()*BSS.getSlotTime()+" μsec");
		offeredLoadLabel=new JLabel("Offered Load: "+BSS.getOfferedLoad()+" bytes");
		avgOfferedLoadLabel=new JLabel("Avg Offered Load: "+Math.round(BSS.getOfferedLoad()*100/(BSS.getSlotTime()*BSS.getStats().getSteps()*0.000001*1024*1024))/100.0+" MB/sec");
		throughputLabel=new JLabel("Throughput: "+Math.round(BSS.getThroughput()*100/(BSS.getSlotTime()*BSS.getStats().getSteps()*0.000001*1024*1024))/100.0+" MB/sec");
		utilisationLabel=new JLabel("Utilisation: "+Math.round(BSS.getStats().getSuccTransmit()*100.0/(BSS.getStats().getSteps()+2))/100.0);

		label1.setBounds(50, 10, 300, 30);
		slotsLabel.setBounds(100, 50, 300, 20);
		freeLabel.setBounds(100, 70, 300, 20);
		succTransLabel.setBounds(100,90,300,20);
		collisionsLabel.setBounds(100,110,300,20);
		avgDelayLabel.setBounds(100,130,300,20);
		maxDelayLabel.setBounds(100,150,300,20);
		minDelayLabel.setBounds(100,170,300,20);		
		offeredLoadLabel.setBounds(100,190,300,20);
		avgOfferedLoadLabel.setBounds(100,210,300,20);
		throughputLabel.setBounds(100,230,300,20);
		utilisationLabel.setBounds(100,250,300,20);
		label1.setFont(font1);
		this.getContentPane().add(label1);
		this.getContentPane().add(slotsLabel);
		this.getContentPane().add(freeLabel);
		this.getContentPane().add(succTransLabel);
		this.getContentPane().add(collisionsLabel);
		this.getContentPane().add(avgDelayLabel);
		this.getContentPane().add(maxDelayLabel);
		this.getContentPane().add(minDelayLabel);
		this.getContentPane().add(offeredLoadLabel);
		this.getContentPane().add(avgOfferedLoadLabel);
		this.getContentPane().add(throughputLabel);
		this.getContentPane().add(utilisationLabel);
		
		nodesPanel=new JPanel();
		nodeLabels = new ArrayList<JLabel>(); 
		nodeLabels.add(new JLabel());
		nodeLabels.get(0).setText("   Node to Node traffic");
		nodeLabels.get(0).setVisible(true);
		nodeLabels.get(0).setBounds(10, 5, 500, 20);
		nodesPanel.add(nodeLabels.get(0));
		nodeLabels.add(new JLabel());
		nodeLabels.get(1).setText(" ");
		nodeLabels.get(1).setVisible(true);
		nodeLabels.get(1).setBounds(10, 25, 500, 20);
		nodesPanel.add(nodeLabels.get(1));
		
		for(int k=1;k<=BSS.getNumberOfNodes();k++){
			String str=nodeLabels.get(1).getText();
			nodeLabels.get(1).setText(str+"                    "+k);
		}
		for(int k=1;k<=BSS.getNumberOfNodes();k++){
			nodeLabels.add(new JLabel());
			nodeLabels.get(k+1).setText(" "+k);
			for(int l=0;l<BSS.getNumberOfNodes();l++){
				String str=nodeLabels.get(k+1).getText();
				fmt = new Formatter();
			    //fmt.format("%15d/%-5d", BSS.transmitted[k-1][l], BSS.offered[k-1][l]);
				NumberFormat formatter = new DecimalFormat("#00000");
				String s1 = formatter.format(BSS.transmitted[k-1][l]);
				String s2 = formatter.format(BSS.offered[k-1][l]);
				
			    //nodeLabels.get(k+1).setText(str+fmt);
				nodeLabels.get(k+1).setText(str+"     "+s1+"/"+s2);
			}
				
			nodeLabels.get(k+1).setVisible(true);
			nodeLabels.get(k+1).setBounds(10,25+k*30,500,20);
			nodesPanel.add(nodeLabels.get(k+1));
		}
		

		for(int k=0;k<BSS.getNumberOfNodes();k++)
			nodeLabels.add(new JLabel());
		nodesPanel.setBounds(350,20,500,300);
		nodesPanel.setBackground(new Color(200,200,200));
		nodesPanel.setLayout(null);
		scrollPane = new JScrollPane(nodesPanel);
		scrollPane.setVisible(true);
		scrollPane.setBounds(350,20,500,300);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.getContentPane().add(scrollPane);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
