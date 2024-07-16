import java.awt.Color;

//A human controlled JPong paddle
public class HumanPaddle{
  
  //Default size of paddle
  private static final int DEFAULT_PADDLE_LENGTH = 50;
  private static final int HUMAN_PADDLE_SPEED = 7;
  protected static final int MIN_PADDLE_LENGTH = 30;  
  
  protected int paddleLength;
  
  private int x, y, volleys, score;
  
  
  
  
  //Constructors
  
  public HumanPaddle(){
    this(DEFAULT_PADDLE_LENGTH);
  }  
  
  public HumanPaddle(int paddleLength){
    this.x = JPongWindow.HUMAN_DEFAULT_X;    
    this.y = JPongWindow.WINDOW_HEIGHT / 2;
    this.paddleLength = Math.max(paddleLength, MIN_PADDLE_LENGTH);
  }  
  


  
  
  //Called automatically whenver the ball collides with this paddle
  public void handleBallCollision(){
    volleys++;
    
  }  
  
  
  //Called automatically whenever EITHER the player or CPU scores a goal.
  //argument boolean indicates if it was the human who scored (true) or
  //the CPU (false).
  public void goalScored(boolean didHumanScore){
    if (didHumanScore){
      this.score++;
      
    }
    this.volleys=0;
  }
  


  
  //****************   ACCESSOR METHODS  *************
      
  public int getVolleys(){
    return volleys;
  }  
  
  public int getX(){
    return this.x;
  }  
  
  //Gets the y coordinate of the centermost point of the Paddle
  public int getY(){
    return this.y;
  } 
  
  //Gets the y coordinate of the topmost point of the Paddle  
  public int getTopY(){
    return this.y - this.paddleLength / 2;
  }  
  
  //Gets the y coordinate of the bottommost point of the Paddle
  public int getBottomY(){
    return this.y + this.paddleLength / 2;
  }    
  
  public int getScore(){
    return this.score;
  }  
  
  public Color getPaddleColor(){   
    return Color.black;
  }
  
  public int getPaddleLength(){
    return this.paddleLength;    
  }  
  
  //**************************************************  
  
  
  
  

  //called by JPongWindow to move this paddle
  //Only called by JPongWindow, you **do not need to call/modify this method**
  public void movePaddle(int dir){
      this.y += (HUMAN_PADDLE_SPEED * dir);
  }    
  
}