import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;
import javax.transaction.xa.XAException;


public class PacMan extends JPanel  implements ActionListener , KeyListener{
	
	class Block
	{
		int x;
		int y;
		int width;
		int height;
		Image image;
		
		int startx;
		int starty;
		char direction='U';
		int velocityX=0;
		int velocityY=0;
		
		
		
		Block(Image image,int x,int y,int width,int height)
		{
			this.image=image;
			this.x=x;
			this.y=y;
			this.width=width;
			this.height=height;
			this.startx=x;
			this.starty=y;
		}
		
		void updatedirection(char direction)
		{
			char prevdirection=this.direction;//to store previous direction to move pacman only if he can
			this.direction=direction;
			updatevelocity();
			this.x += this.velocityX;
			this.y += this.velocityY;
			
			for(Block wall:walls)
			{
				if(collision(this, wall))
				{
					this.x -=this.velocityX;
					this.y -=this.velocityY;
					this.direction=prevdirection;
					updatevelocity();
				}
			}
		}
		
		void pause() 
		{
			gamepause=true;
		}
		
		void updatevelocity()
		{
			if(this.direction=='U')
			{
				this.velocityX=0;
				this.velocityY=-tilesize/4; //for upward(x,-y) and 8px move for each direction
			}
			else if(this.direction=='D')
			{
				this.velocityX=0;
				this.velocityY=tilesize/4;//(x,y)
			}
			else if(this.direction=='L')
			{
				this.velocityX=-tilesize/4;//(-x,y)
				this.velocityY=0;
			}
			else if(this.direction=='R')
			{
				this.velocityX=tilesize/4;//(x,y)
				this.velocityY=0;
			}
		}
		
		void reset() //when collision is made between ghost and pacman
		{
			this.x=this.startx;
			this.y=this.starty;
		}
		
		
	}
	
	
	private int rowcount=21;
	private int columncount=19;
	private int tilesize=32;
	private int boardwidth=columncount*tilesize;
	private int boardheight=rowcount*tilesize;
	
	private Image wallImage;
	private Image blueghostImage;
	private Image pinkghostImage;
	private Image redghostImage;
	private Image orangeghostImage;
	
	private Image pacmanUpImage;
	private Image pacmanDownImage;
	private Image pacmanLeftImage;
	private Image pacmanRightImage;
	
	//X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };
	
	HashSet<Block> walls;
	HashSet<Block> foods;
	HashSet<Block> ghosts;
	Block pacman;
	
	Timer gameloop;//use to set time to repaint images
	char[] directions= {'U','D','L','R'};
	Random random=new Random();//random move for ghosts
	
	int score=0;
	int lives=3;
	boolean gameover=false;
	boolean gamepause=false;
	
	PacMan()
	{
		setPreferredSize(new Dimension(boardwidth,boardheight));
		setBackground(Color.black);
		addKeyListener(this);
		
		setFocusable(true);
		
		//loading image
		wallImage=new ImageIcon(getClass().getResource("/Image/wall.png")).getImage();
		
		blueghostImage=new ImageIcon(getClass().getResource("/Image/blueGhost.png")).getImage();
		pinkghostImage=new ImageIcon(getClass().getResource("/Image/pinkGhost.png")).getImage();
		orangeghostImage=new ImageIcon(getClass().getResource("/Image/orangeGhost.png")).getImage();
		redghostImage=new ImageIcon(getClass().getResource("/Image/redGhost.png")).getImage();
		
		pacmanUpImage=new ImageIcon(getClass().getResource("/Image/pacmanUp.png")).getImage();
		pacmanDownImage=new ImageIcon(getClass().getResource("/Image/pacmanDown.png")).getImage();
		pacmanLeftImage=new ImageIcon(getClass().getResource("/Image/pacmanLeft.png")).getImage();
		pacmanRightImage=new ImageIcon(getClass().getResource("/Image/pacmanRight.png")).getImage();
		
		loadMap();
		for(Block ghost:ghosts)
		{
			char newDirection=directions[random.nextInt(4)];//random generation of 4 directions
			ghost.updatedirection(newDirection);
		}
		
		gameloop=new Timer(50, this); //here, 1000/50=>20fps/sec (images are repainted)
		gameloop.start();
		
	}
	
	public void loadMap() 
	{
		walls=new HashSet<Block>();
		foods=new HashSet<Block>();
		ghosts=new HashSet<Block>();
		
		for(int r=0;r<rowcount;r++)
		{
			for(int c=0;c<columncount;c++)
			{
				String row=tileMap[r];//find string
				char tileMapChar=row.charAt(c);//find char to check the tile
				//both the below line is todetermine the x and y position using tilesize
				int x=c*tilesize;
				int y=r*tilesize;
				
				if(tileMapChar=='X')
				{
					Block wall=new Block(wallImage, x, y, tilesize,tilesize);
					walls.add(wall);
				}
				
				else if(tileMapChar=='b')
				{
					Block ghost=new Block(blueghostImage, x, y, tilesize, tilesize);
					ghosts.add(ghost);
				}
				else if(tileMapChar=='o')
				{
					Block ghost=new Block(orangeghostImage, x, y, tilesize, tilesize);
					ghosts.add(ghost);
				}
				else if(tileMapChar=='p')
				{
					Block ghost=new Block(pinkghostImage, x, y, tilesize, tilesize);
					ghosts.add(ghost);
				}
				else if(tileMapChar=='r')
				{
					Block ghost=new Block(redghostImage, x, y, tilesize, tilesize);
					ghosts.add(ghost);
				}
				else if(tileMapChar=='P')
				{
					pacman=new Block(pacmanRightImage, x, y, tilesize, tilesize);
					
				}
				else if(tileMapChar==' ')
				{
					Block food=new Block(null, x+14, y+14, 4, 4);
					foods.add(food);
					
				}
			}
		}
	}
	//below function is used to draw the board and
	//also to show the moving images
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
		
		
		
		if (gamepause)//show pause in the screen
		{
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setColor(Color.WHITE);
	        
	        // Set a larger font
	        Font pauseFont = new Font("Arial", Font.BOLD, 48);
	        g2.setFont(pauseFont);

	        // Use FontMetrics to center the text
	        String text = "Paused";
	        
	        FontMetrics fm = g2.getFontMetrics(pauseFont);
	        int textWidth = fm.stringWidth(text);
	        int textHeight = fm.getHeight();
            
	        int x = (boardwidth - textWidth) / 2;
	        int y = (boardheight - textHeight) / 2 + fm.getAscent(); // ascent adjusts vertical alignment

	        g2.drawString(text, x, y);
	    }
		
		if (gameover) //to show score
		{
	        Graphics2D g2 = (Graphics2D) g;
	        g2.setColor(Color.WHITE);

	        // Set a larger font for game-over screen
	        Font gameOverFont = new Font("Arial", Font.BOLD, 48);
	        g2.setFont(gameOverFont);

	        // Show "Game Over" text
	        String gameOverText = "Game Over!";
	        FontMetrics fm = g2.getFontMetrics(gameOverFont);
	        int textWidth = fm.stringWidth(gameOverText);
	        int textHeight = fm.getHeight();

	        int x = (boardwidth - textWidth) / 2;
	        int y = (boardheight - textHeight) / 2 - fm.getAscent(); // adjust the position

	        g2.drawString(gameOverText, x, y);

	        // Show score after game over
	        String scoreText = "Score: " + score;
	        g2.setFont(new Font("Arial", Font.PLAIN, 30));
	        fm = g2.getFontMetrics();
	        textWidth = fm.stringWidth(scoreText);
	        x = (boardwidth - textWidth) / 2;
	        y = (boardheight - textHeight) / 2 + fm.getAscent() + 50; // position for score

	        g2.drawString(scoreText, x, y);
	    }
		
	}
	
	
	
	public void draw(Graphics g) 
	{
		g.drawImage(pacman.image,pacman.x, pacman.y, pacman.width, pacman.height, null);
		
		for(Block ghost:ghosts)
		{
			g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height,null);
		}
		
		for(Block wall:walls)
		{
			g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height,null);
		}
		
		g.setColor(Color.WHITE);
		for(Block food:foods)
		{
			g.fillRect(food.x, food.y, food.width, food.height);
		}
		
		g.setFont(new Font("Arial", Font.PLAIN,18));
		if(gameover)
		{//execute this only if the game over
			g.drawString("Game Over: "+ String.valueOf(score),tilesize/2,tilesize/2);
			
		}
		else
		{//when game is not over
			g.drawString("x" + String.valueOf(lives) + "Score: " +String.valueOf(score),tilesize/2,tilesize/2);
			
		}
		
	}
	
	public void move() //this function is used to update the x and y position
	{//this function is called in the below function
		pacman.x += pacman.velocityX;
		pacman.y += pacman.velocityY;
		
		for(Block wall:walls)
		{
			if(collision(pacman, wall))
			{//if collision occures,then pacman steback(so no  move maked)
				pacman.x -= pacman.velocityX;
				pacman.y -= pacman.velocityY;
				break;//move is breaked and called again only after clicking new arrow in keyboard
			}
		}
		
		//check collision with ghost
		for(Block ghost:ghosts)
		{
			if(collision(ghost, pacman))
			{
				lives -= 1;
				if(lives==0)
				{
					gameover=true;
					return;
				}
				resetpositions();
			}
			
			if(ghost.y == tilesize*9 && ghost.direction !='U' && ghost.direction!='D')
			{
				ghost.updatedirection('U');
			}
			ghost.x +=ghost.velocityX;
			ghost.y +=ghost.velocityY;
			//same collision check for ghost
			for(Block wall:walls)
			{
				if(collision(ghost, wall) || ghost.x <=0 || ghost.x+ghost.width >= boardwidth)
				{
					ghost.x -= ghost.velocityX;
					ghost.y -= ghost.velocityY;
					char newDirrection=directions[random.nextInt(4)];//chose another direction
					ghost.updatedirection(newDirrection);
				}
			}
		}
		
		
		//check food collision to remove it from hashset
		Block foodEaten=null;
		for(Block food:foods)
		{
			if(collision(pacman, food))
			{
				foodEaten=food;
				score+=10;
			}
		}
		foods.remove(foodEaten);
		
		if(foods.isEmpty())
		{//this shows pacman eaten all the foods
			loadMap();
			resetpositions();
		}
		
		
	}
	
	public boolean collision(Block a,Block b) 
	{//inside the function, it is a collision formula 
		return a.x < b.x + b.width &&
				a.x + a.width > b.x &&
				a.y < b.y + b.height &&
				a.y + a.height > b.y;
	}
	
	public void resetpositions() 
	{
		pacman.reset();
		pacman.velocityX=0;
		pacman.velocityY=0;
		
		for(Block ghost:ghosts)
		{
			ghost.reset();
			char newdirection=directions[random.nextInt(4)];
			ghost.updatedirection(newdirection);
		}
	}
	
	public void updategame()
	{
		if(gamepause)
			return;
	}
	
	
	//used to repaint the images in each block
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (!gamepause) 
		{
	        move();     // Move PacMan and ghosts only if not paused
	        updategame();  // Update other game logic
	    }
	    repaint();       // Always repaint to show "Paused" message or current state

	    if (gameover)
	    {
	        gameloop.stop();
	    }
	
	}
	
	
	//these below functions are used to move the images based on keyboard input 
	
	@Override
	public void keyTyped(KeyEvent e) {}// no typing is performed
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_P) {
	        gamepause = !gamepause; // toggle pause state
	        repaint(); // ensure pause screen is shown/cleared
	    }
		
		if(!gamepause)
		{
			if(e.getKeyCode()==KeyEvent.VK_UP)
			{
				pacman.updatedirection('U');
			}
			else if(e.getKeyCode()==KeyEvent.VK_DOWN)
			{
				pacman.updatedirection('D');
			}
			else if(e.getKeyCode()==KeyEvent.VK_LEFT)
			{
				pacman.updatedirection('L');
			}
			else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
			{
				pacman.updatedirection('R');
			}
			
			
			
			if(pacman.direction=='D')
			{
				pacman.image=pacmanDownImage;
			}
			else if(pacman.direction=='U')
			{
				pacman.image=pacmanUpImage;
			}
			else if(pacman.direction=='L')
			{
				pacman.image=pacmanLeftImage;
			}
			else if(pacman.direction=='R')
			{
				pacman.image=pacmanRightImage;
			}
		}
		
	
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println("KeyEvent: "+e.getKeyCode());///to get the code of key user press
		
		if(gameover)
		{//called once game is overed, we should load all elements again
			loadMap();
			resetpositions();
			lives=3;
			score=0;
			gameover=false;
			gameloop.start();
		}
		
		if(e.getKeyCode()==KeyEvent.VK_UP)
		{
			pacman.updatedirection('U');
		}
		else if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			pacman.updatedirection('D');
		}
		else if(e.getKeyCode()==KeyEvent.VK_LEFT)
		{
			pacman.updatedirection('L');
		}
		else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
		{
			pacman.updatedirection('R');
		}
		
		
		
		if(pacman.direction=='D')
		{
			pacman.image=pacmanDownImage;
		}
		else if(pacman.direction=='U')
		{
			pacman.image=pacmanUpImage;
		}
		else if(pacman.direction=='L')
		{
			pacman.image=pacmanLeftImage;
		}
		else if(pacman.direction=='R')
		{
			pacman.image=pacmanRightImage;
		}
		
		
		
	}
	
	
	
}
