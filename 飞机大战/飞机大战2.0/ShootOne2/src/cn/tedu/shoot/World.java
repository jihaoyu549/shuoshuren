package cn.tedu.shoot;

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

//import javazoom.jl.decoder.JavaLayerException;

//import java.applet.Applet;
import java.io.File;
//import java.io.FileInputStream;
import java.net.MalformedURLException;
//import java.util.Scanner;
//import javax.sound.sampled.*;
import javax.swing.JApplet;

//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.Player;

public class World extends JPanel implements Runnable,KeyListener {
	
	java.applet.AudioClip all_bomb,enemy_bomb,bg,hero_bomb,hero_bullet;
	public static final int WIDTH = 400;
	public static final int HEIGHT = 700;
	private Sky sky = new Sky();
	private Hero hero = new Hero();
	private FlyingObject[] enemies = {};
	private Bullet[] heroBullets = {};
	private Bullet[] enemiesBullets = {};
	
	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	private int state = START;
	
	public static BufferedImage start;
	public static BufferedImage pause;
	public static BufferedImage gameover;
	static{
		start = Images.readImage("start.png");
		pause = Images.readImage("pause.png");
		gameover = Images.readImage("gameover.png");
	}
	
	public World(){
		try{
			all_bomb = JApplet.newAudioClip(new File("music/all_bomb.wav").toURI().toURL());
			enemy_bomb = JApplet.newAudioClip(new File("music/enemy_bomb.wav").toURI().toURL());
			bg = JApplet.newAudioClip(new File("music/bg.wav").toURI().toURL());
			hero_bomb = JApplet.newAudioClip(new File("music/hero_bomb.wav").toURI().toURL());
			hero_bullet = JApplet.newAudioClip(new File("music/hero_bomb.wav").toURI().toURL());
		}catch(MalformedURLException e){
			e.printStackTrace();
		}
	}
	
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
	
	private int enemiesIndex = 0;
	public void enterAction(){
		enemiesIndex++;
		if(enemiesIndex%30==0){
			enemies = Arrays.copyOf(enemies, enemies.length+1);
			enemies[enemies.length-1] = nextOne();
		}
		if(enemiesIndex%1000==0 && score>100){
			enemies = Arrays.copyOf(enemies, enemies.length+1);
			enemies[enemies.length-1] = new BossAirplane();
		}
	}
	
	private int enemiesShootIndex = 0;
	public void enemiesShoot(){
		enemiesShootIndex++;
		if(enemiesShootIndex%100==0){
			for(int i=0;i<enemies.length;i++){
				FlyingObject f = enemies[i];
				if(f.isLife() && !(f instanceof Bee)){
					Bullet[] b = f.shoot();
					enemiesBullets = Arrays.copyOf(enemiesBullets, enemiesBullets.length+b.length);
					System.arraycopy(b, 0, enemiesBullets, enemiesBullets.length-b.length,b.length);
				}
			}
		}
	}
	
	private int shootIndex = 0;
	public void shootAction(){
		shootIndex++;
		if(shootIndex%40==0){
			Bullet[] b = hero.shoot();
			heroBullets = Arrays.copyOf(heroBullets, heroBullets.length+b.length);
			System.arraycopy(b, 0, heroBullets, heroBullets.length-b.length,b.length);
		}
	}
	
	public void stepAction(){
		sky.step();
		for(int i=0;i<enemies.length;i++){
			enemies[i].step();
		}
		for(int i=0;i<heroBullets.length;i++){
			heroBullets[i].step();
		}
		for(int i=0;i<enemiesBullets.length;i++){
			enemiesBullets[i].step();
		}
	}
	
	int score = 0;
	public void bangAction(){
		for(int i=0;i<enemies.length;i++){
			FlyingObject f = enemies[i];
			for(int j=0;j<heroBullets.length;j++){
				Bullet b = heroBullets[j];
				if(b.isLife() && f.isLife() && b.hit(f)){
					if(f.life>1){
						f.subtractLife();
						b.goDead();
					}else{
						enemy_bomb.play();
						f.goDead();
						b.goDead();
						if(f instanceof Enemy){
							Enemy e = (Enemy) f;
							score += e.getScore();
						}
						if(f instanceof Award){
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

	public void outOfBoundsAction(){
		int index = 0;
		FlyingObject[] fs = new FlyingObject[enemies.length];
		for(int i=0;i<enemies.length;i++){
			FlyingObject f = enemies[i];
			if(!f.isRemove() && !f.outBround()){
				fs[index++] = f;
			}
		}
		enemies = Arrays.copyOf(fs, index);

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
	
	public void hitAction(){
		for(int i=0;i<enemies.length;i++){
			FlyingObject f = enemies[i];
			if(f.isLife() && hero.isLife() && hero.hit(f)){
				f.goDead();
				hero_bullet.play();
				hero.clearDoubleFire();
				hero.subtractLife();
			}
		}
		
		for(int i=0;i<enemiesBullets.length;i++){
			Bullet b = enemiesBullets[i];
			if(b.hit((FlyingObject)hero) && b.isLife()){
				b.goDead();
				hero_bullet.play();
				hero.subtractLife();
				hero.clearDoubleFire();
			}
		}
	}
	
	public void checkGameOverAction(){
		if(hero.getLife()<0){
			hero_bomb.play();
			state = GAME_OVER;
		}
	}
	
	public void action(){
		
		MouseAdapter l = new MouseAdapter(){
			public void mouseMoved(MouseEvent e){
				if(state==RUNNING){
					int x=e.getX();
					int y=e.getY();
					hero.moveTo(x, y);
				}
			}
			public void mouseClicked(MouseEvent e){
				if(state==START){
					bg.loop();
					state = RUNNING;
				}else if(state==GAME_OVER){
					all_bomb.play();
					state = START;
					score = 0;
					enemies = new FlyingObject[0];
					heroBullets = new Bullet[0];
					enemiesBullets = new Bullet[0];
					hero = new Hero();
				}
			}
			public void mouseExited(MouseEvent e){
				if(state==RUNNING){
					state = PAUSE;
					bg.stop();
				}
			}
			public void mouseEntered(MouseEvent e){
				if(state==PAUSE){
					bg.loop();
					state = RUNNING;
				}
			}
		};
		
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
		
		Timer timer = new Timer();
		int intervel = 10;
		timer.schedule(new TimerTask(){
			public void run() {
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
				repaint();
			}
		}, intervel,intervel);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(state==RUNNING){
			switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				all_bomb.play();
				enemies = new FlyingObject[0];
				enemiesBullets = new Bullet[0];
				break;
			}
		}
		
	}
	
	public void paint(Graphics g){
		sky.paintObject(g);
		hero.paintObject(g);
		for(int i=0;i<enemies.length;i++){
			enemies[i].paintObject(g);
		}
		for(int i=0;i<heroBullets.length;i++){
			heroBullets[i].paintObject(g);
		}
		for(int i=0;i<enemiesBullets.length;i++){
			enemiesBullets[i].paintObject(g);
		}
		
		g.drawString("SCORE:"+score, 10, 25);
		g.drawString("LIFE:"+hero.getLife(), 10, 45);
		
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
		}
		
	}
	
	@Override
	public void run() {
		action();
	}
	
	public static void main(String[] args) {
		World world = new World();
		JFrame frame = new JFrame("·É»ú´óÕ½");
		
		frame.add(world);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null); 
		frame.setVisible(true); 
		frame.addKeyListener(world);
		Thread t=new Thread(world);
		t.start();
		//world.action();
		System.out.println("111");
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
