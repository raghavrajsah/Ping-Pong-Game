import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;


//Handles the graphical code (and some minor game logic aspects) for the JPong game
//Additional Ball class included at the bottom of the file (to reduce number of classes
//students need to deal with).
public class JPongWindow extends JComponent implements KeyListener, ActionListener{ 
   
  
  //Frequency that the window "repaints" itself, in milliseconds
  private static final int RENDER_INTERVAL = 16;  
  
  
  //Dimensions of windows (including borders, actual window space will vary per OS)  
  public static final int WINDOW_WIDTH = 800;
  public static final int WINDOW_HEIGHT = 480; 
  
  //Width of Paddle
  public static final int PADDLE_WIDTH = 12;  
  //Padding (distance between paddles and edge of window
  public static final int PADDING = PADDLE_WIDTH * 3;
     
  //x coordinates for paddles
  public static final int CPU_DEFAULT_X = WINDOW_WIDTH - PADDLE_WIDTH - PADDING;  
  public static final int HUMAN_DEFAULT_X = PADDING; 
  
  //Size of squares used in debug mode
  public static final int DEBUG_HEIGHT = 10;   
  
  //Colors for ball and debug square
  public static final Color BALL_COLOR = Color.white;
  public static final Color DEBUG_COLOR = Color.red;
  
  //How much slower the game should run when DEBUG_SLOW_MODE is enabled
  private static final int SLOW_SPEED_MODIFIER = 3;
  
  //Window where the entirety of the game is rendered
  private JFrame window;
  
  //Paddles for human (p1) and CPU (p2)
  private HumanPaddle p1;
  private CPUPaddle p2;
  
  //Single ball object
  private Ball b;
  
  //tracks if the ball is in the neutral position (center of screen with no velocity)
  //waiting to be "launched" via the space bar key
  private boolean waitingForLaunch = true;
  
  //tracks if the human player is currently holding the up or down arrow keys
  private boolean upPress = false;
  private boolean downPress = false;
  
  
  
  //Accepts the two paddle types to be used in the game
  //Game is always 1 x Human (left paddle, p1) vs 1 x CPU (right paddle, p2)
  public JPongWindow(HumanPaddle p1, CPUPaddle p2){
    super();
    this.b = new Ball();
    this.p1 = p1;
    this.p2 = p2;
  }
  
  
  //Initializes the window and containing JFrame
  //contains (most of) the gross Java GUI code
  public void initWindow() {
    
    //** FYI ** Feel free to trace this code if you like, but
    //don't worry if doesn't make total sense yet
    window = new JFrame("");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    window.setSize(this.WINDOW_WIDTH, this.WINDOW_HEIGHT);
    window.add(this);
    
    window.setBackground(Color.WHITE);
    
    
    //Fix Windows listener bug(?)
    this.setFocusable(true);
    //window.requestFocus();
    this.requestFocusInWindow();
    
    
    this.setOpaque(true);
    window.setVisible(true);
    window.setResizable(false);
    
    int heightDiff = this.WINDOW_HEIGHT - this.getHeight();
    int widthDiff = this.WINDOW_WIDTH - this.getWidth();
    window.setSize(this.WINDOW_WIDTH + widthDiff, this.WINDOW_HEIGHT + heightDiff);
    
    
    
    //Instantiation of our timer -- look at this API page:
    //http://docs.oracle.com/javase/7/docs/api/javax/swing/Timer.html
    
    Timer timer = new Timer(0, this);
    if (Launcher.DEBUG_SLOW_MODE)
       timer.setDelay(RENDER_INTERVAL * SLOW_SPEED_MODIFIER);
    else
       timer.setDelay(RENDER_INTERVAL);
    timer.start();
    
    //Let's the window know that the methods to react to keyboard/mouse actions
    //are implemented in this class
    addKeyListener(this);    
  }  
  

  //Called automatically when the timer ticks
  //Handles ball/paddle movement and detecting collisions
  public void actionPerformed(ActionEvent ae){
    moveBall();    
    movePaddles();
    checkPaddleCollisions();
    repaint(); //re-render the window after everything has been updated
  }
  
  //Checks to see if the ball has collided with either paddle and reacts accordingly
  public void checkPaddleCollisions(){
    //generate hitboxes for the ball and paddles
    Rectangle2D p1HitBox = makePaddleHitbox(p1.getX(), p1.getY(), p1.getPaddleLength());  
    Rectangle2D p2HitBox = makePaddleHitbox(p2.getX(), p2.getY(), p2.getPaddleLength());
    Rectangle2D bHitBox = new Rectangle2D.Double(b.getX(), b.getY(), b.getBallDiameter(), b.getBallDiameter());
    //check for collision w/ human
    if (p1HitBox.intersects(bHitBox)){
      p1.handleBallCollision();
      b.handlePaddleCollision(p1.getY(), p1.getPaddleLength());
      updateWindowTitle(); //update title text as number of volleys will have changed
    }
    else if (p2HitBox.intersects(bHitBox)){ //check for collision w/ CPU
      b.handlePaddleCollision(p2.getY(), p2.getPaddleLength());
    }    
  }
  
  //Generates a hitbox for either paddle  
  private Rectangle2D makePaddleHitbox(int x, int y, int height){
    return new Rectangle2D.Double(x, y - (height/2), PADDLE_WIDTH, height);    
  }
  
  //Handles moving both the Player and CPU paddles, called on every update of the game
  public void movePaddles(){
    //react to player up/down key presses
    if (upPress && p1.getY() > p1.getPaddleLength()/2)
      p1.movePaddle(-1);
    else if (downPress && p1.getY() < this.getHeight() - p1.getPaddleLength()/2)
      p1.movePaddle(1);
    //determine how CPU wants to move per its logic
    int p2Dir = p2.determinePaddleMove(b.getX(), b.getY(), b.getXSpeed(), b.getYSpeed(), this.getHeight());
    //don't allow CPU to move outside the window
    if ((p2Dir < 0 && p2.getTopY() > 0) || (p2Dir > 0 && p2.getBottomY() < this.getHeight()))
      p2.movePaddle(p2Dir);
  }
    
  
  //updates the state of the ball on each refresh of the game
  public void moveBall(){
    b.move();
    checkForBallBounce();
    checkForBallScore();   
  }
  
  
  //Checks to see if ball has collided with top or bottom of window
  private void checkForBallBounce(){    
    if (b.getY() <= 0 && b.getYSpeed() < 0){
      b.setY(0);
      b.handleWindowCollision();
    }
    else if (b.getY() >= this.getHeight() - b.getBallDiameter() && b.getYSpeed() > 0){
      b.setY(this.getHeight() - b.getBallDiameter());
      b.handleWindowCollision();      
    }    
  }
  
  
  //Check to see if either player has scored (ball has gone off left or right edge of window)
  private void checkForBallScore(){
    //ball went off left edge (CPU scored)
    if (b.getX() <= 0){
      p1.goalScored(false);
      p2.goalScored(true);
      //move ball back to center of screen to be relaunched via spacebar press
      b.resetBall(this.getWidth() / 2, this.getHeight() / 2);
      waitingForLaunch = true;
      updateWindowTitle();
    }
    //ball went off right edge (Human scored)    
    if (b.getX() + b.getBallDiameter() >= this.getWidth()){
      p1.goalScored(true);
      p2.goalScored(false);
      b.resetBall(this.getWidth() / 2, this.getHeight() / 2);
      waitingForLaunch = true;
      updateWindowTitle();
    }          
  }
  
  
  // rerenders the game window
  // ran when repaint() is called (on each tick of the timer)
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    g.setColor(getForeground());
    Graphics2D g2D = (Graphics2D)g;
  g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
    //draw paddles and ball
    drawPaddle(p1.getPaddleColor(), p1.getX(), p1.getY(), p1.getPaddleLength(),  g2D);
    drawPaddle(p2.getPaddleColor(), p2.getX(), p2.getY(), p2.getPaddleLength(),  g2D);
    drawBall(b, g2D);
    //if debug mode is enabled in launcher, draw the debug hitboxes
    if (Launcher.DEBUG_SHOW_BOUNCES)
      drawDebug(g2D);
  }  
  
  //Draws the debug hitboxes that show future ball wall collisions and projected y trajectory
  //when it reaches CPU's end of the game board
  public void drawDebug(Graphics2D g2D){
    g2D.setPaint(DEBUG_COLOR);
    //only draw debug hitboxes when ball is moving towards CPU
    if (b.getXSpeed() <= 0)
      return;
    //retrieve all the bounces
    int[][] bounces = p2.getWallBounceCoords(b.getX(), b.getY(), b.getXSpeed(), b.getYSpeed(), this.getHeight());
    int expectY;
    //if ball is not going to bounce, just determine projected y coord when it reaches CPU paddle
    if (bounces.length == 0)
      expectY = p2.calculateTargetY(b.getX(), b.getY(), b.getXSpeed(), b.getYSpeed());
    else{
      //otherwise, draw hitboxes for each of the bounce coordinates
      for (int[] p : bounces)
        g2D.fill(new Rectangle2D.Double(p[0] - (DEBUG_HEIGHT/2), Math.min(p[1], this.getHeight() - DEBUG_HEIGHT), DEBUG_HEIGHT, DEBUG_HEIGHT));
      int[] lastBounce = bounces[bounces.length-1];
      //figure out the yVelocity following the last bounce (ie -- will it be going up or down given its current trajectory)
      int lastYSpeed = b.getYSpeed();
      if (bounces.length % 2 == 1)
        lastYSpeed *= -1;   
      //determine the expected y coord following the final bounce
      expectY = p2.calculateTargetY(lastBounce[0], lastBounce[1], b.getXSpeed(), lastYSpeed);
    }
    //draw a hitbox of where the ball will be when it reaches the CPU paddle
    g2D.fill(new Rectangle2D.Double(p2.getX(), expectY - (DEBUG_HEIGHT/2), DEBUG_HEIGHT, DEBUG_HEIGHT));
  }
  
  
  //Updates the text on the top menu bar of the game window
  public void updateWindowTitle(){
    window.setTitle("JPong!  Human: " + p1.getScore() + ", CPU: " + p2.getScore() + "   |   Volleys: " + p1.getVolleys());
  }
  
  //Draws a single paddle with the specified attributes (either Human or CPU)
  private void drawPaddle(Color c, int x, int y, int height, Graphics2D g2D){  
    g2D.setPaint(c);
    Rectangle2D shape = makePaddleHitbox(x, y, height);
    g2D.fill(shape);
    g2D.setPaint(Color.black);
    g2D.draw(shape);
  }
  
  
  //Draws the ball to the screen
  private void drawBall(Ball b, Graphics2D g2D){
    g2D.setPaint(b.getBallColor());    
    Ellipse2D shape = new Ellipse2D.Double(b.getX(), b.getY(), b.getBallDiameter(), b.getBallDiameter());
    g2D.fill(shape);
    g2D.setPaint(Color.black);
    g2D.draw(shape);    
  }
  
  
  //Called automatically whenver a keyboard key is depressed per the key listener
  public void keyPressed(KeyEvent e) {
    //'Esc' quits the program
    if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
      System.exit(0);  
    //If the ball is waiting to be launched, launch it when space is pushed
    else if (e.getKeyCode()==KeyEvent.VK_SPACE && waitingForLaunch){
      b.launchBall();          
      waitingForLaunch = false;
    }
    //If up or down is pressed, set the respective boolean to true
    //needed to allow players to be able to hold a key to continue moving up/down
    else if (e.getKeyCode()==KeyEvent.VK_UP)
      upPress = true;   
    else if (e.getKeyCode()==KeyEvent.VK_DOWN)
      downPress = true;            
    
  }    
  
  
  //Called autmomatically whenever a key is released (ie lifts back up after a depress)
  public void keyReleased(KeyEvent e) { 
    //don't set up/down move boolean back to false until
    //the player lets go of the respective key.
    //needed to allow players to be able to hold a key to continue moving up/down
    if (e.getKeyCode()==KeyEvent.VK_UP)
      upPress = false;   
    else if (e.getKeyCode()==KeyEvent.VK_DOWN)
      downPress = false;    
  }  
  
  
  //launches the window and starts playing the game
  public void playGame(){
    this.initWindow();
    b.resetBall(this.getWidth() / 2, this.getHeight() / 2); 
    updateWindowTitle();        
  }
  
  
  //Not used, needed to satisfy KeyListener interface...
  public void keyTyped(KeyEvent event) {  }    
    
  
}


//Abstraction of the ball and its respective attributes in the game window
//Put inside this file to give students one fewer class to have to worry about
//As-is, they don't need to trace/modify/call anything in here anyways...
class Ball{
  
  //Fixed Diameter/Color of the ball
  public static final int BALL_DIAMETER = 10;
  public static final Color BALL_COLOR = Color.magenta;
  
  //Starting X velocity when ball is launched 
  private static final int STARTING_X_SPEED = 6;  
  //Range of possible random y velocities picked when ball is launched
  private static final int STARTING_Y_SPEED_RANGE = 7;
  
  //Constant total net velocity (used in paddle-ball deflection math)
  private static final int BALL_VELOCITY = 12;
  //Maximum deflection angle when a ball hits a paddle (used in paddle-ball deflection math)
  private static final double MAX_BOUNCE_ANGLE = Math.toRadians(75);
  //random object used when determining ball launch velocities
  public static final Random rand = new Random();
  
  
  
  //coordinate and velocity fields for ball
  private int x, y, xSpeed, ySpeed;
  
  
  
  //Resets the ball to center of screen
  //Called when game is first launched or following either player scoring
  public void resetBall(int startingX, int startingY){
    this.x = startingX;    
    this.y = startingY; 
    this.xSpeed = 0;
    this.ySpeed = 0;
  }
  
  
  //Launches the ball from its starting position with an initial, randomized velocity/direction
  public void launchBall(){
    this.xSpeed = STARTING_X_SPEED;
    //randomly determine if ball starts moving left or right
    int dir = rand.nextInt(2);
    if (dir == 0)
      this.xSpeed *= -1;
    //set a random y velocity per the starting y velocity speed range
    this.ySpeed = rand.nextInt(2 * STARTING_Y_SPEED_RANGE) - STARTING_Y_SPEED_RANGE;    
  }
  
  
  //Called each time the game updates... moves the ball per its current velocities
  public void move(){
    this.x += xSpeed;
    this.y += ySpeed;
  }  

  
  //Called when ball collides with either top or bottom of window
  public void handleWindowCollision(){
      //just inverts the current y velocity
      this.ySpeed *= -1;
  }
  
  
  //Called whenever ball collides with either human or CPU paddle 
  public void handlePaddleCollision(int paddleY, int paddleHeight){
    
    //determine angle via of deflection... done by using 
    //the difference between the ball's y and the center y of the paddle
    int yDiff = paddleY - this.getY();
    double normalizedYDiff = yDiff / (paddleHeight / 2.0);
    double bounceAngle = normalizedYDiff * MAX_BOUNCE_ANGLE;    
    this.ySpeed = (int)(BALL_VELOCITY * -Math.sin(bounceAngle));
    
    //Determine the x velocity from this angle as well
    //Need to also figure out if post-deflection ball is going left or right...
    if (this.xSpeed > 0)
      this.xSpeed = (int) (-BALL_VELOCITY * Math.cos(bounceAngle));
    else
      this.xSpeed = (int)(BALL_VELOCITY * Math.cos(bounceAngle));
    
    //offset the x by 2 moves to avoid a double collision/physics wonkiness...
    //could happen with a particular speed/angle)
    //(kind of a hack, I know...)
    this.x += (2 * xSpeed); 
  }
  
  
  
  
  //****************   ACCESSOR METHODS  *************    
  
  public int getX(){
    return this.x;
  }
    
  public int getY(){
    return this.y + (this.getBallDiameter() / 2);
  }
  
  public int getXSpeed(){
    return this.xSpeed;
  }
  
  public int getYSpeed(){
    return this.ySpeed;
  }  
  
  public int getBallDiameter(){
    return BALL_DIAMETER;
  }
  
  public Color getBallColor(){   
    return BALL_COLOR;
  }
  
  //************************************************  
  
  
  
  
  //****************   MUTATOR METHODS  *************  
  
  public void setY(int newY){
    this.y = newY;
  }
  
  public void setX(int newX){
    this.x = newX;
  }    
    
  //************************************************   
  

  
  
}