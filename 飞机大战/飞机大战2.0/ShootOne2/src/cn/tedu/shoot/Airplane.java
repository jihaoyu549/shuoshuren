package cn.tedu.shoot;

import java.awt.image.BufferedImage;

/*
 * 小敌机类，继承FlyingObject,实现小敌机的移动，死亡后爆破
 */
public class Airplane extends FlyingObject implements Enemy {

	private int speed;
	
	Airplane(){
		super(48,50,1);
		speed = 2;
	}
	
	public void step() {
		y += speed;
	}

	/*
	 * 获取小敌机的图片，状态为LIFE时返回小敌机图片
	 * 状态为DEAD时返回4张爆破图片，全部返回后将状态改为REMOVE，返回null
	 */
	int index = 1;
	public BufferedImage getImage() {
		if(isLife()){
			return Images.airplanes[0];
		}else if(isDead()){
			if(index == 5){
				state = REMOVE;
				return null;
			}
			return Images.airplanes[index++];
		}
		return null;
	}
	
	//生成一颗子弹的子弹数组子弹的初始坐标为当前小敌机的下面，子弹方向向下
	public Bullet[] shoot(){
		Bullet[] res = new Bullet[1];
		res[0] = new Bullet(x+this.width/2,y+this.height+10,"down");
		return res;
	}
	
	//小敌机的y坐标大于窗口的高返回ture
	public boolean outBround() {
		return y>=World.HEIGHT;
	}

	//返回小敌机的分数
	public int getScore() {
		return 1;
	}

}
