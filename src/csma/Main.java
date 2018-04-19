package csma;


public class Main {
	static String FILENAME="out.dat";
	public static void main(String[] args) {
        // Δημιουργία κεντρικού παραθύρου
        MainFrame mainFrame = new MainFrame();
        mainFrame.setTitle("DCFSim");
        mainFrame.setBounds(200,50,800, 550);
        mainFrame.setVisible(true);
    }
}
