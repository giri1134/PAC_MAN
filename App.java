import javax.swing.JFrame;



public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int rowcount=21;
		int columncount=19;
		int tilesize=32;
		int boardwidth=columncount*tilesize;
		int boardheight=rowcount*tilesize;
		
		JFrame frame=new JFrame("PAC MAN");
		// create the frame for game
		frame.setVisible(true);
		frame.setSize(boardwidth, boardheight);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		PacMan pacmanGame=new PacMan();
		frame.add(pacmanGame);
		frame.pack();
		pacmanGame.requestFocus();
		frame.setVisible(true);
		
		
		
		

	}

}
