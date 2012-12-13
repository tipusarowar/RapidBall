/**
 * 
 */
package com.tipusarowar.rapidball;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author Administrator
 *
 */
public class RapidView extends SurfaceView implements SurfaceHolder.Callback {
	/************************************************   GLOBAL VARIABLES *******************************************************/
	public static boolean WIN_STATE;

	public static boolean GAME_OVER=false;

	public static boolean PAUSE=false;



	/*************************************************** CORD CLASS **************************************************************/
	class Ball{
		public float x;
		public float y;
		public int radius;
		public int cord_id;
		public boolean onBar=true;
		public boolean right=true;
		public boolean left=false;
		public Ball(){
			x=y=radius=0;

		}
	}	

	class Cord {
		public int x;
		public int y;
		public int type;
		public int life;
		public Cord(){
			this.x=0;
			this.y=0;
			this.type=0;
		}
	}

	/****************************************************************************************************************************/	
	/************************************************** RAPIDTHREAD *************************************************************/
	/****************************************************************************************************************************/

	class RapidThread extends Thread{

		/********************** MY VARIABLES ***********************************************/
		public Ball ball;
		int gy= 4; //2;
		int gx= 6;//4;

		Cord[] cords;
		int incY= 4; //2;
		int decY= 4; //2;
		/**********************************************************************************/
		private int level;
		
		private int score=0;
		
		private int life=3;
		
		private int newly_created_bar_id;
		
		boolean running;
		SurfaceHolder rSurfaceHolder;

		private Bitmap rBackgroundBitmap;
		private Bitmap rRapidBar;
		private Bitmap rRapidBall;
		private Bitmap rRedBar;
		private Bitmap rPinkBar;

		/***************************************************************************************************************************/
		public RapidThread(SurfaceHolder surfaceHolder, Context context/*,
                Handler handler*/) {

			this.rSurfaceHolder =(SurfaceHolder) surfaceHolder;
			running = true;

			/************************* CORD *****************************/
			cords = new Cord[10];//Allocation
			ball = new Ball();
			//initialization
			for(int i=0 ; i<10 ; i++){
				cords[i] =(Cord) new Cord();
			}
			int count=680;        	
			for(int i=0 ; i<10 ; i++){
				cords[i].x = (int) new Random().nextInt(340);//440-100
				cords[i].y = count;
				count-=60;
				//also have to set type
				cords[i].type = (int) new Random().nextInt(3);
			}	
			newly_created_bar_id = 9;
			
			ball.x= cords[5].x+45;
			//ball.y = cords[5].y;//+20;
			ball.y = cords[5].y-10;
			ball.radius = 10;
			ball.onBar = true;
			ball.right = false;
			ball.left = false;
			ball.cord_id = 5;
			
			score = 0;
			life = 3;
			
			GAME_OVER = false;
			/**********************************/

			Resources res = context.getResources();
			rBackgroundBitmap = BitmapFactory.decodeResource(res,
					R.drawable.rapid_space);
			rRapidBar = BitmapFactory.decodeResource(res, R.drawable.rapid_bar);
			rRapidBall = BitmapFactory.decodeResource(res, R.drawable.rapid_ball);
			rRedBar = BitmapFactory.decodeResource(res, R.drawable.red_bar);
			rPinkBar = BitmapFactory.decodeResource(res,R.drawable.pink_bar);
			/*********************************/
		}

		/*************************************************************************************************************************/

		/*************************************************************************************************************************/

		@Override
		public void run() {
			while (running) {
				Canvas c = null;              
				try {
					//this.sleep(1000);
					c = rSurfaceHolder.lockCanvas(null);
					synchronized (rSurfaceHolder) {
						// if (mMode == STATE_RUNNING) updatePhysics();
						upDate();
						doDraw(c);
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						rSurfaceHolder.unlockCanvasAndPost(c);
						//Log.d("unlockCanvasAndPost","In the run");
					}
				}
			}
		}
		/*************************************************************************************************************************/
		/********************** UPDATE FUNCTION : MAIN CONTROLLER OF VARIABLES OF BARS AND BALL **********************************/
		/*************************************************************************************************************************/
		private void upDate(){
			//Log.d("Update","updating cords");//updating cords
			for (int i = 0; i < 10 ; i++) {  				
				if(cords[i].y <= 40){ // SCORE <= 40
					cords[i].x=new Random().nextInt(340);
					cords[i].y = 680;
					cords[i].type= new Random().nextInt(3);
					newly_created_bar_id = i;
					cords[i].life = new Random().nextInt(10);
					if(cords[i].type == 2 && cords[i].life == 5)
							cords[i].life++;
				}else {
					cords[i].y-=decY;
				}
			}
			/**********************************************************************************************************************/
			/**************************************************************************************************************************/
			/******************************************** BALL'S POSITION **********************************************************/
			
			int beforex,afterx;
			int beforey,aftery; 

			beforex=(int)ball.x;
			beforey=(int)ball.y;

			if(ball.right==true){ // RIGHT MOVE CHANGE : X
				ball.x += gx;
				if(ball.x>430)		 // RIGHT MOST LIMIT
					ball.x=430;
			}
			else if(ball.left==true){	 // LEFT MOVE CHANGE : X
				ball.x -= gx;
				if(ball.x<0)		 // LEFT MOST LIMIT
					ball.x=0;
			}
			if(ball.onBar){			// Y MOVE
				if( ball.x< (cords[ball.cord_id].x) || ball.x > (cords[ball.cord_id].x+100) ){	// BALL FALLING FIRST TIME
					ball.y+=gy;   
					ball.onBar=false;
				}else 
					ball.y-=decY;	// BALL MOVING UP WITH BAR
			}
			else{
				ball.y+=gy;		//FALLING AND SCOREING
				score += 10;
			}
			afterx = (int) ball.x;
			aftery = (int) ball.y;

			for(int i=0; i<10 ; i++){//WHETHER BALL LIES ON A BAR
				/*if( beforey <= cords[i].y && cords[i].y <= aftery && cords[i].x < afterx && afterx < (cords[i].x+100) ){*/
				if( beforey <= (cords[i].y-10) && (cords[i].y-10) <= aftery && cords[i].x < afterx && afterx < (cords[i].x+100) ){
					ball.y = cords[i].y-10;
					ball.onBar=true;
					ball.cord_id=i;
					/*if(cords[i].life == 5 ){
						Log.d("Life:","cords[i].life == 5"+" ball.x :"+ball.x+" (cords[i].x+ 40):"+(cords[i].x+ 40)+" (cords[i].x+50): "+(cords[i].x+50) );
						if(ball.x >= (cords[i].x+ 40) && ball.x <= (cords[i].x+50)){
							life++;
							cords[i].life++;
							Log.d("NewLIfe", "life: "+life+"");
						}							
					}*/
					if(cords[i].type == 2){
						newLifeState();
					}
					//Log.d("Inside_RightMoveCheck", "ball.x: "+ball.x+" cords[i].x: "+cords[i].x+" ball.y: "+ball.y+" Cords[i].y: "+cords[i].y);
					break;
				}
			}
			
			if(ball.onBar == true)
				if(cords[ball.cord_id].life == 5 ){
					Log.d("Life:","cords[i].life == 5"+" ball.x :"+ball.x+" (cords[i].x+ 40):"+(cords[ball.cord_id].x+ 40)+" (cords[i].x+50): "+(cords[ball.cord_id].x+50) );
					if(ball.x >= (cords[ball.cord_id].x+ 40) && ball.x <= (cords[ball.cord_id].x+50)){
						life++;
						cords[ball.cord_id].life++;
						Log.d("NewLIfe", "life: "+life+"");
					}							
				}
			/************* LIFE CHECKING OF GAME **********************/
			if( (ball.y-10) <= 40 ){ // SCORE <= 40
				newLifeState();
			}
			else if(ball.y >= 680 ){
				newLifeState();
			}
			//Log.d("Ball's Position", "ball.x: "+ball.x+" ball.y: "+ball.y+" spentTime: "+timeSpent);              
			/**********************************************************************************************************************/           
		}
		
		private void newLifeState(){
			if(life>0){
				life--;
				for( ; cords[newly_created_bar_id].type == 2; newly_created_bar_id++){
					Log.d("ARRAY INDEX OUT OF BOUND: ","newly_created_bar_id :"+newly_created_bar_id);
					if(newly_created_bar_id==10){
						newly_created_bar_id=0;
						Log.d("IF(BAR_ID == 10 )","newly_created_bar_id :"+newly_created_bar_id);
					}
				}
								
				ball.x= cords[newly_created_bar_id].x+45;
				ball.y = cords[newly_created_bar_id].y-10;
				ball.radius = 10;
				ball.onBar = true;
				ball.left = false;
				ball.right = false;
				ball.cord_id = newly_created_bar_id;	
			}
			else{
				GAME_OVER = true;
			}
		}
		/*************************************************************************************************************************/

		/*************************************************************************************************************************/
		/*************************************************************************************************************************/
		private void doDraw(Canvas canvas){

			//Log.d("doDraw","On the Canvas");
			Paint paint = new Paint();
			if(GAME_OVER==true){
				paint.setARGB(0, 159, 121, 238);
				canvas.drawBitmap(rBackgroundBitmap, 0, 0, null);//paint);
				canvas.drawRect(new Rect(0, 0, getWidth(), getHeight() ), paint);
				paint.setTextSize(40);
				paint.setColor(Color.CYAN);
				String string = "GAME OVER!";
				canvas.drawText(string, 100, 200, paint);
			}
			else{				
				//						SCORE DRAW
				paint.setARGB(200, 204,204, 255);
				canvas.drawRect(new Rect(0, 0, 440, 40), paint);
				paint.setColor(Color.BLACK);
				String scoreString = "Score :"+score+"                  "+"Life :"+life;
				paint.setTextSize(20);
				canvas.drawText(scoreString, 50, 20, paint);
				//						BACKGROUND
				canvas.drawBitmap(rBackgroundBitmap, 0, 40, null);//paint);//SCORE <= 40
				// 						BAR DRAW	
				

				for(int i = 0 ; i < 10 ; i++){
					if(cords[i].type == 2){
						//Log.d("RedBar", "i: "+i+"cords[i].type :"+cords[i].type);
						canvas.drawBitmap(rRedBar, cords[i].x,cords[i].y, null);
						//if()
					}
					else{
						if( cords[i].life == 5 ){
							paint.setARGB(150, 200, 150, 200);
							canvas.drawCircle( (cords[i].x+45), (cords[i].y -5 ), 5, paint);
						}
						/*paint.setARGB(150,159,121,238);
						canvas.drawRect(new Rect(cords[i].x,cords[i].y,cords[i].x+100,cords[i].y+10), paint);
						*/
						canvas.drawBitmap(rPinkBar, cords[i].x, cords[i].y, null);
					}
				}
				//color.rgb(85,	26	,139);//indigo
				paint.setARGB(200, 85, 26, 139);
				canvas.drawCircle(ball.x, ball.y, ball.radius, paint);
				//canvas.drawBitmap(rRapidBall, ball.x, ball.y, null);
			}
		}
		/*************************************************************************************************************************/

		public boolean doKeyDown(int keyCode, KeyEvent keyEvent) {
			// TODO Auto-generated method stub
			/******************************************************************************************************************/
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				ball.left=true;
				ball.right=false;
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
				ball.right=true;
				ball.left=false;
				return true;
			}	
			/******************************************************************************************************************/
			return false;
		}

		public boolean doKeyUp(int keyCode, KeyEvent keyEvent) {
			// TODO Auto-generated method stub
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				ball.left=false;
				ball.right=false;
				return true;
			}
			else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
				ball.left=false;
				ball.right=false;
				return true;
			}	
			return false;
		}

		public boolean doTouchEvent(MotionEvent event) {	
			/*if(event.getX()>x && event.getX()<(x+40) && 
					event.getY()>y && event.getY()<(y+40) ){
			 */
			if(event.getX()>ball.x){
				ball.right=true;
				ball.left=false;
				return true;
			}
			else if(event.getX()<ball.x){
				ball.left=true;
				ball.right=false;
				return true;
			}
			return false;		
		}



	}

	/****************************************************************************************************************************/	
	/************************************************** RAPIDVIEW CLASS CONSTRUCTOR *********************************************/
	/****************************************************************************************************************************/

	private RapidThread thread;


	public RapidView(Context context)/*, AttributeSet attrs)*/ {
		super(context);//,attrs);
		// TODO Auto-generated constructor stub
		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread =(RapidThread) new RapidThread(holder, context/*, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        }*/);



		setFocusable(true); // make sure we get key events

	}
	/****************************************************************************************************************************/
	public RapidThread getThread(){
		return thread;
	}

	@Override
	public boolean onKeyDown(int key,KeyEvent keyEvent){
		return thread.doKeyDown(key,keyEvent);
	}

	@Override
	public boolean onKeyUp(int key, KeyEvent keyEvent){
		return thread.doKeyUp(key, keyEvent);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event){
		return thread.doTouchEvent(event);
	}


	/****************************************************************************************************************************/	
	/************************************************** CALLBACK METHODS ********************************************************/
	/****************************************************************************************************************************/


	/****************************************************************************************************************************/	
	/************************************************** CALLBACK METHODS ********************************************************/
	/****************************************************************************************************************************/
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Canvas c = holder.lockCanvas(null);		
		onDraw(c);
		holder.unlockCanvasAndPost(c);
		Log.d("SurfaceCreated","Starting thread.start()");
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
}
