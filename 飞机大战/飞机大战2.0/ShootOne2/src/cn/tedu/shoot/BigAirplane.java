package cn.tedu.shoot;

import java.awt.image.BufferedImage;

/*
 * 大敌机类，实现大敌机的移动和生成子弹数组以及返回分数
 */
public class BigAirplane extends FlyingObject implements Enemy {

	private int speed;
	BigAirplane(){
		super(66,89,3);
		speed = 2;
	}
	
	public void step() {
		y += speed;
	}

	/*
	 * 获取大敌机的图片，状态为LIFE时返回大敌机图片
	 * 状态为DEAD时返回4张爆破图片，全部返回后将状态改为REMOVE，返回null
	 */
	int index = 1;
	public BufferedImage getImage() {
		if(isLife()){
			return Images.bigairplanes[0];
		}else if(isDead()){
			if(index==5){
				state = REMOVE;
				return null;
			}
			return Images.bigairplanes[index++];
		}
		return null;
	}

	//生成一颗子弹的子弹数组子弹的初始坐标为当前小敌机的下面，子弹方向向下
	public Bullet[] shoot(){
		Bullet[] res = new Bullet[1];
		res[0] = new Bullet(x+this.width/2,y+this.height+10,"down");
		return res;
	}
	
	//大敌机的y坐标大于窗口的高返回ture
	public boolean outBround() {
		return y>=World.HEIGHT;
	}
	
	//直接返回大敌机的分数
	public int getScore() {
		return 3;
	}

}
