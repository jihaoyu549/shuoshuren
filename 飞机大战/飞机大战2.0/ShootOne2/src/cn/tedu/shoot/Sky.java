package cn.tedu.shoot;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/*
 * 继承FlyingObject
 */
public class Sky extends FlyingObject {
	//增加速度属性和y1坐标，因为要保证天空移动的连续，一张图片走了，另一张接着走要不然会出现空白
	private int speed;
	private int y1;
	
	/*
	 * 天空的构造方法，在画板中坐标是最坐上角的点，整个天空是作为背景的，所以坐标为（0，0）
	 * 天空的的宽高既窗口的宽高，World.WIDTH（400）和World.HEIGHT（700），World类里确定
	 */
	Sky(){
		super(World.WIDTH,World.HEIGHT,0,0);
		y1 = -this.height;
		speed = 1;
	}
	
	//天空向下移动，y和y1都增加speed的值
	public void step() {
		y += speed;
		y1 += speed;
		//保证连续，有图片走出框后将它移到最上方
		if(y>=World.HEIGHT){
			y = -this.height;
		}
		if(y1>=World.HEIGHT){
			y1 = -this.height;
		}
	}

	//画两次天空图片，一张在下面，一张在上面，保证背景的连续
	public void paintObject(Graphics g){
		g.drawImage(getImage(), x, y, null);
		g.drawImage(getImage(), x, y1, null);
	}
	
	//获取图片
	public BufferedImage getImage() {
		return Images.sky;
	}

	//天空没有越界行为直接返回false
	public boolean outBround() {
		return false;
	}

}
