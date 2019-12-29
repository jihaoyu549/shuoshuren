package cn.tedu.shoot;

import java.awt.image.BufferedImage;

/*
 * 侦察机类，实现侦察机的移动
 */

public class Bee extends FlyingObject implements Award {

	private int xSpeed;
	private int ySpeed;
	private int awardType;
	
	Bee(){
		super(60,51,2);
		xSpeed = 1;
		ySpeed = 2;
		awardType = (int)(Math.random()*2);
	}
	
	//返回奖励的类型
	public int getType() {
		return awardType;
	}

	//当侦察机碰到边界时改变x方向的移动
	public void step() {
		x += xSpeed;
		y += ySpeed;
		if(x<=0 || x>=World.WIDTH-this.width){
			xSpeed = -xSpeed;
		}
	}

	/*
	 * 获取侦察机的图片，状态为LIFE时返回侦察机图片
	 * 状态为DEAD时返回4张爆破图片，全部返回后将状态改为REMOVE，返回null
	 */
	int index = 1;
	public BufferedImage getImage() {
		if(isLife()){
			return Images.bees[0];
		}else if(isDead()){
			if(index==5){
				state = REMOVE;
				return null;
			}
			return Images.bees[index++];
		}
		return null;
	}
	
	//侦察机的y坐标大于窗口的高返回ture
	public boolean outBround() {
		return y>=World.HEIGHT;
	}

}
