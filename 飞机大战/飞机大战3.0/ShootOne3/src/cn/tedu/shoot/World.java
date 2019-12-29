package cn.tedu.shoot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;


import java.io.*;

import javax.swing.JApplet;

@SuppressWarnings("deprecation")
public class World extends JPanel implements Runnable,KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//声明java.applet.AudioClip类型的引用用来储存音频
	java.applet.AudioClip all_bomb,enemy_bomb,hero_bomb,hero_bullet,wi,wh,bosswaring,equip,readygo,p,zh;
	java.applet.AudioClip bg;
	public static final int WIDTH = 400;    //窗口的宽
	public static final int HEIGHT = 700;   //窗口的高
	
	private Sky sky = new Sky();            //声明并创建天空对象
	private Hero hero = new Hero();         //声明并创建英雄机对象
	
	private FlyingObject[] enemies = {};    //声明敌人数组enemies数组向上造型为FlyingObject超类方面使用
	private Bullet[] heroBullets = {};      //声明英雄机的子弹数组heroBullets
	private Bullet[] enemiesBullets = {};   //声明敌人的子弹数组enemiesBullets
	
	
	//设置四个状态量，一个当前状态量
	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	public static final int WIN = 4;
	
	private int state = START;
	
	//声明图片类静态量：启动图、暂替图和游戏结束图并赋值
	public static BufferedImage start;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	public static BufferedImage win;
	
	static{
		start = Images.readImage("start.png");
		pause = Images.readImage("pause.png");
		gameover = Images.readImage("gameover.png");
		win = Images.readImage("win.png");
	}
	
	//在构造方法里为各音频赋值
	public World(){
		try{
			all_bomb = JApplet.newAudioClip(new File("music/all_bomb.wav").toURI().toURL());
			enemy_bomb = JApplet.newAudioClip(new File("music/enemy_bomb.wav").toURI().toURL());
			bg = JApplet.newAudioClip(new File("music/bg.wav").toURI().toURL());
			hero_bomb = JApplet.newAudioClip(new File("music/hero_bomb.wav").toURI().toURL());
			hero_bullet = JApplet.newAudioClip(new File("music/hero_bomb.wav").toURI().toURL());
			
			wi= JApplet.newAudioClip(new File("music/wi.wav").toURI().toURL()); //赢了声音
			wh= JApplet.newAudioClip(new File("music/wh.wav").toURI().toURL()); // 输语
			bosswaring= JApplet.newAudioClip(new File("music/bosswaring.wav").toURI().toURL()); //boss提示声音
			equip= JApplet.newAudioClip(new File("music/equip.wav").toURI().toURL());  //结束、赢了切换音
			readygo= JApplet.newAudioClip(new File("music/readygo.wav").toURI().toURL());   //刚开始readygo声音
			p= JApplet.newAudioClip(new File("music/p.wav").toURI().toURL());         //暂停声音
			zh= JApplet.newAudioClip(new File("music/zh.wav").toURI().toURL());       //暂停回来声音
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	///生成敌人
	//随机生成大敌机、小敌机、侦察机的对象
	public FlyingObject nextOne(){
			int n = (int)(Math.random()*100);
		if(n>70){
			return new Bee();
		}else if(n>40){
			return new BigAirplane();
		}else{
			return new Airplane();
		}
	  }
		
	/*
	*每隔一段时间调用nextOne方法生成一个敌人添加到敌人数组当中
	*当分数大于100后隔更长一段时间生成boss机添加到敌人数组
	*/
	private int enemiesIndex = 0;
	public void enterAction(){
		enemiesIndex++;
		if(enemiesIndex%30==0){
			enemies = Arrays.copyOf(enemies, enemies.length+1); 
			enemies[enemies.length-1] = nextOne(); 
		}
		if(enemiesIndex%1000==0 && score>100){     //100
			if(ckt==1) {
				bosswaring.play();     //boss提示音
			}
				enemies = Arrays.copyOf(enemies, enemies.length+1);
			    enemies[enemies.length-1] = new BossAirplane();	
		}
	}
	 
	///敌人发射子弹
	//每隔一段时间遍历敌人敌人数组让每一个敌人数组发射子弹添加到敌人子弹数组
	private int enemiesShootIndex = 0;
	public void enemiesShoot(){
		enemiesShootIndex++;
		if(enemiesShootIndex%100==0){
			for(int i=0;i<enemies.length;i++){   //遍历敌人数组
				FlyingObject f = enemies[i];
				//敌人还活着与不是侦察机时发射子弹
				if(f.isLife() && !(f instanceof Bee)){
					//敌人调用shoot方法生成子弹数组
					Bullet[] b = f.shoot();
					//将生成的子弹数组添加到敌人子弹数组
					enemiesBullets = Arrays.copyOf(enemiesBullets, enemiesBullets.length+b.length);
					System.arraycopy(b, 0, enemiesBullets, enemiesBullets.length-b.length,b.length);
				}
			}
		}
	}
	
	///英雄机发射子弹
	private int shootIndex = 0;
	public void shootAction(){
		shootIndex++;
		if(shootIndex%40==0){
			//英雄机调用shoot方法生成子弹数组
			Bullet[] b = hero.shoot();
			//将子弹数组添加到英雄机子弹数组中
			heroBullets = Arrays.copyOf(heroBullets, heroBullets.length+b.length);
			System.arraycopy(b, 0, heroBullets, heroBullets.length-b.length,b.length);
		}
	}
	
	///对象都动起来
	public void stepAction(){
		sky.step();      //天空动起来
		//敌人动起来
		for(int i=0;i<enemies.length;i++){
			enemies[i].step();
		}
		//英雄机子弹动起来
		for(int i=0;i<heroBullets.length;i++){
			heroBullets[i].step();
		}
		//敌人子弹动起来
		for(int i=0;i<enemiesBullets.length;i++){
			enemiesBullets[i].step();
		}
	}
	
	///判断英雄机发射的子弹是否击中敌人
	static int score = 0;         //初始化分数为0
	public void bangAction(){
		//遍历敌人数组
		for(int i=0;i<enemies.length;i++){
			FlyingObject f = enemies[i];
			//遍历英雄机子弹数组
			for(int j=0;j<heroBullets.length;j++){
				Bullet b = heroBullets[j];
				//判断敌人和子弹的状态是否为LIFE和子弹调用hit方法判断是否发生碰撞
				if(b.isLife() && f.isLife() && b.hit(f)){
					if(f.life>1){
						//如果敌人的生命大于1生命减一，子弹状态改变为DEAD
						f.subtractLife();
						b.goDead();
					}else{
						//播放爆破音效
						enemy_bomb.play();
						//当敌人的生命不大于一时改变子弹和敌人的状态为DEAD
						f.goDead();
						b.goDead();
						if(f instanceof Enemy){
							//如果是实现了加分的接口则给score加分
							Enemy e = (Enemy) f;
							score += e.getScore();
						}
						if(f instanceof Award){
							//如果是加奖励则添加对应的奖励
							Award a = (Award) f;
							int type = a.getType();
							switch(type){
							case Award.DOUBLE_FIRE:
								hero.addDoubleFire();
								break;
							case Award.LIFE:
								hero.addLife();
								break;
							}
						}
					}
				}
			}
		}
	}

	///清除状态为REMOVE和超出窗口的敌人和子弹
	//防止对象过多发生内存泄漏
	public void outOfBoundsAction(){
		//创建一个FlyingObject的数组来储存没REMOVE和没超出窗口的敌人
		int index = 0;
		FlyingObject[] fs = new FlyingObject[enemies.length];
		for(int i=0;i<enemies.length;i++){
			//如果!f.isRemove() && !f.outBround()则存储在fs里
			FlyingObject f = enemies[i];
			if(!f.isRemove() && !f.outBround()){
				fs[index++] = f;
			}
		}
		//缩减敌人数组为fs中的元素
		enemies = Arrays.copyOf(fs, index);
		
		//下面和上面同理
		index = 0;
		Bullet[] bs = new Bullet[heroBullets.length];
		for(int i=0;i<heroBullets.length;i++){
			Bullet b = heroBullets[i];
			if(!b.isRemove() && !b.outBround()){
				bs[index++] = b;
			}
		}
		heroBullets = Arrays.copyOf(bs, index);
		
		index = 0;
		Bullet[] ebs = new Bullet[enemiesBullets.length];
		for(int i=0;i<enemiesBullets.length;i++){
			Bullet b = enemiesBullets[i];
			if(!b.isRemove() && !b.outBround()){
				ebs[index++] = b;
			}
		}
		enemiesBullets = Arrays.copyOf(ebs, index);
	}
	
	///判断敌人的子弹是否击中英雄机和英雄机是否与敌人相撞
	public void hitAction(){
		//遍历敌人数组
		for(int i=0;i<enemies.length;i++){
			FlyingObject f = enemies[i];
			//敌人和英雄机都还活着，英雄机调用hit方法检测碰撞
			if(f.isLife() && hero.isLife() && hero.hit(f)){
				//碰撞后改编敌人状态为DEAD
				f.goDead();
				//播放碰撞音效
				hero_bullet.play();
				//英雄机减命和清空火力
				hero.clearDoubleFire();
				hero.subtractLife();
			}
		}
		
		///遍历敌人子弹数组
		for(int i=0;i<enemiesBullets.length;i++){
			Bullet b = enemiesBullets[i];
			//如果英雄机的状态为LIFE，调用hit方法判断是否发生碰撞
			if(b.hit((FlyingObject)hero) && b.isLife()){
				//碰撞后改变子弹的状态为DEAD
				b.goDead();
				//播放碰撞音效
				hero_bullet.play();
				//英雄机减命和清空火力
				hero.subtractLife();
				hero.clearDoubleFire();
			}
		}
	}
	
	//英雄机赢了和没命结束
	public void checkGameOverAction(){
		if(score>=400) {     //分数大于≥400，英雄机胜利
			wi.play();
			state = WIN;
		}
		if(hero.getLife()==0){     //英雄机没命结束
			hero_bomb.play();
			wh.play();
			state = GAME_OVER;
		}
	}
	
	///游戏运行方法
	public void action(){
		//创建监听鼠标事件匿名内部类
		MouseAdapter l = new MouseAdapter(){
			//鼠标移动事件
			public void mouseMoved(MouseEvent e){
				//状态为RUNNING时获取鼠标坐标，英雄机调用moveTo方法让英雄机随着鼠标移动
				if(state==RUNNING){
					int x=e.getX();
					int y=e.getY();
					hero.moveTo(x, y);
				}
			}
			//鼠标单击事件
			public void mouseClicked(MouseEvent e){
				
				//状态为START时单击状态改变为RUNNING并且播放背景音乐
				if(state==START){
					readygo.play();
					//bg.loop为循环播放音乐
					 bg.loop();
					state = RUNNING;
				}
				else if(state==GAME_OVER){
					//状态为GAME_OVER时改变状态为START
					//all_bomb.play();
					equip.play();
					state = START;
					//分数重置为0
					score = 0;
					rt = 5;          //大招次数重置
					ckt =1;          //关卡重置
					//重置所有对象为下一盘游戏做准备
					enemies = new FlyingObject[0];
					heroBullets = new Bullet[0];
					enemiesBullets = new Bullet[0];
					hero = new Hero();
				}
				
				else {       
					//状态为WIN时改变状态为START
					equip.play();
					state = START;
					//分数重置为0
					score = 0;
					rt = 5;       //大招次数重置
					ckt =1;       //关卡重置
					//重置所有对象为下一盘游戏做准备
					enemies = new FlyingObject[0];
					heroBullets = new Bullet[0];
					enemiesBullets = new Bullet[0];
					hero = new Hero();
				}
				
			}
			//鼠标移出窗口事件
			public void mouseExited(MouseEvent e){
				//当状态为RUNNING时改变状态为PAUSE
				if(state==RUNNING){
					state = PAUSE;
					//停止背景音乐播放
				     bg.stop();
				     p.play();    //播放暂停音效
				}
			}
			//鼠标进入窗口事件
			public void mouseEntered(MouseEvent e){
				//如果状态为PAUSE改变状态为RUNNING并播放背景音乐
				if(state==PAUSE){
					bg.loop();
					zh.play();    //播放开始音效
					state = RUNNING;
					
				}
			}
		};
		
		//为监听鼠标对象添加Java的监听
			this.addMouseListener(l);
		    this.addMouseMotionListener(l);
		
		//创建计时器定时运行上面的各种方法让程序运行
		Timer timer = new Timer();
		//intervel 为启动延时和以后运行时间间隔，以毫秒为单位
		int intervel = 10;
		timer.schedule(new TimerTask(){
			public void run() {
				//如果状态为RUNNING时开始运行
				if(state==RUNNING){
					enterAction();
					enemiesShoot();
					shootAction();
					stepAction();
					bangAction();
					outOfBoundsAction();
					hitAction();
					checkGameOverAction();
				}
				//每次都重画保证刷新率
				repaint();
			}
		}, intervel,intervel);
	}
	
	///旋风无敌大大大招
	//这里实现里监听键盘的接口KeyListener，重写按键时间
	//@Override	
	 int rt = 5;  //初始化大招次数 
	public void keyPressed(KeyEvent e) {
		//当状态为RUNNING时
		if(state==RUNNING){
			
			if(rt!=0) {
			//判断是否按下空格键
			switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				//按下空格键后播放爆炸音效同时清空所有地方对象
				all_bomb.play();
				enemies = new FlyingObject[0];
				enemiesBullets = new Bullet[0];
				break;
			}
			rt=rt-1;
		}
	}
		
}
	//关卡判断
	 int ckt =1;
	 public  int ChecKpoints(){
		if(score>=260) {              //分数≥180进入第二关
			ckt = 2;
		}
		return ckt;
	}
	 
	///在画板上画出所有对象
	public void paint(Graphics g){
		sky.paintObject(g);       //画天空
		hero.paintObject(g);      //画英雄机
		//画敌人
		for(int i=0;i<enemies.length;i++){
			enemies[i].paintObject(g);
		}
		//画英雄机子弹
		for(int i=0;i<heroBullets.length;i++){
			heroBullets[i].paintObject(g);
		}
		//画敌人子弹数组
		for(int i=0;i<enemiesBullets.length;i++){
			enemiesBullets[i].paintObject(g);
		}
		//画出得分和生命
		g.setFont(new Font("楷体",Font.BOLD,20));      //修改字体
		g.setColor(Color.WHITE);                         //修改字体颜色
		
		g.drawString("大招剩余次数:"+rt,10,25); // 10 660
		g.drawString("火力值:"+hero.getDoubleFire(),10,45);
		g.drawString("生命值:"+hero.getLife(), 10, 65);// 10 45
		g.drawString("关卡:"+ChecKpoints(), 10, 85);
		g.drawString("分数:"+score, 10, 105);  // 10 25
		
		//游戏处于不同状态画出不同状态图
		switch(state){
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0,null);
			break;
	    case WIN:
			g.drawImage(win, 0, 0, null);
			break;
		}
	}
	
	///实现线程运行
	/*因为播放音效会阻塞程序，而使用java.applet.AudioClip类又要在线程里才能播放出声音
           所以这里就实现了Runnable接口，重写run方法运行程序*/
	@Override
	public void run() {
		action();
	}
	
	public static void main(String[] args) {
		World world = new World();       //创建World对象，World类继承了画板类JPanel
		JFrame frame = new JFrame("雷霆战机");   //创建窗口对象
		
		frame.add(world);      //将画板添加到窗口中去
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //设置窗口默认关闭操作
		frame.setSize(WIDTH, HEIGHT);     //设置窗口大小
		frame.setLocationRelativeTo(null); 
		frame.setIconImage(Images.tb);  //设置窗口图标
		frame.setVisible(true);         //设置窗口可见
		frame.addKeyListener(world);    //添加键盘监听
		Thread t=new Thread(world);     //创建线程对象
		t.start();             	//启动线程
		//world.action();
		System.out.println("雷霆战机开始啦!!!");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
