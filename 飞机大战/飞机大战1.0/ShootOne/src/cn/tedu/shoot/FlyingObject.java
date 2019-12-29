package cn.tedu.shoot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class FlyingObject {
	//共同属性
	protected int width;
	protected int height;
	protected int x;
	protected int y;
	protected int life;
	
	//状态常量
	public static final int LIFE = 0;
	public static final int DEAD = 1;
	public static final int REMOVE = 2;
	
	//当前状态
	protected int state = LIFE;
	
	//为敌人提供的构造方法，
	FlyingObject(int width,int height,int life){
		this.width = width;
		this.height = height;
		this.x = (int)(Math.random()*(World.WIDTH-width));
		this.y = -height;
		this.life = life;
	}
	
	//为天空和英雄机子弹提供的构造方法
	FlyingObject(int width,int height,int x,int y){
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	//生成子弹组数的方法
	public Bullet[] shoot(){
		return new Bullet[0];
	}
	
	//判断碰撞的方法
	public boolean hit(FlyingObject other){
		int x1 = other.x - this.width;
		int x2 = other.x + other.width;
		int y1 = other.y - this.height;
		int y2 = other.y + other.height;
		return x>=x1 && x<=x2 && y>=y1 && y<=y2;
	}
	
	//生命减一
	public void subtractLife(){
		life--;
	}
	
	//判断是否活着
	public boolean isLife(){
		return state==LIFE;
	}
	
	//判断是否死了，后面有用
	public boolean isDead(){
		return state==DEAD;
	}
	
	//判断状态是否为移除
	public boolean isRemove(){
		return state==REMOVE;
	}
	
	//将状态改为DEAD
	public void goDead(){
		state = DEAD;
	}
	
	//在画板上画出图片
	public void paintObject(Graphics g){
		g.drawImage(getImage(),x,y,null);
	}
	
	//移动抽象方法
	public abstract void step();
	
	//获取图片的抽象方法
	public abstract BufferedImage getImage();
	
	//判断是否越界的抽象方法
	public abstract boolean outBround();
	
}
