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