package cn.tedu.shoot;

import java.awt.image.BufferedImage;

/*
 * 子弹类，实现子弹的移动
 */
public class Bullet extends FlyingObject {

	//dir为不同的子弹图片标记，0为向上，1为向下
	private int speed;
	private int dir;
	
	Bullet(int x,int y,String direction){
		super(8,20,x,y);
		if(direction.equals("up")){
			speed = -2;
			dir = 0;
		}else if(direction.equals("down")){
			speed = 3;
			dir = 1;
		}
		
	}
	
	//子弹移动
	public void step() {
		y += speed;
	}
	
	/*
	 * 返回子弹图片，Images.bullets[0]是向上的，Images.bullets[1]是向下的
	 * 当状态为DEAD时,改变状态为REMOVE
	 */
	public BufferedImage getImage() {
		if(isLife()){
			return Images.bullets[dir];
		}else if(isDead()){
			state = REMOVE;
			return null;
		}
		return null;
	}

	//判断是否越界
	public boolean outBround() {
		return y<=0 || y>=World.HEIGHT;
	}

}
