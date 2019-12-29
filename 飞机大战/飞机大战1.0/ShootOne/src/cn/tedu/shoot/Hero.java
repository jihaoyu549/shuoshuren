package cn.tedu.shoot;

import java.awt.image.BufferedImage;

/*
 * 英雄机类，实现了英雄机随鼠标的坐标移动
 * 根据火力发射不同火力的子弹
 */
public class Hero extends FlyingObject {

	Hero(){
		super(46,66,170,400);
		life = 3;
	}
	
	//随着鼠标的x，y移动
	public void moveTo(int x,int y){
		this.x = x - this.width/2;
		this.y = y - this.height/2;
	}
	
	//根据火力生成想上的子弹数组
	public Bullet[] shoot(){
		int xStep = this.width/4;
		int yStep = -20;
			Bullet[] b = new Bullet[1];
			b[0] = new Bullet(this.x+2*xStep,this.y+yStep,"up");
			return b;
	}
	
	//返回生命数
	public int getLife(){
		return life;
	}
	
	//生命数加一
	public void addLife(){
		life++;
	}
	
	public void step() {
	}

	//英雄机活着时每次返回不同的图片实现英雄机的喷火
	private int index = 0;
	public BufferedImage getImage() {
		if(isLife()){
			return Images.heros[index++%2];
		}
		return null;
	}

	//英雄机不存在越界行为
	public boolean outBround() {
		return false;
	}
}
