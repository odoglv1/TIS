package shooter;

import java.util.concurrent.CopyOnWriteArrayList;

import ddf.minim.AudioSample;
import ddf.minim.Minim;
import processing.core.PApplet;
import processing.core.PImage;

public class Shooter extends PApplet {
	
	PImage background;
	PImage logo;
	
	PImage ship1;
	PImage ship2;
	PImage ship3;
	PImage ship4;
	
	PImage eShip1;
	PImage eShip2;
	PImage eShip3;
	PImage eShip4;
	
	public int bks;
	public int spawnCount;
	public int currRow;
	public int spawnWait;
	
	public int level;
	
	public int i;
	public int qCount;
	public int qReset;
	
	public int shootClock;
	
	public Ship ship;
	
	public CopyOnWriteArrayList<Bullet> bullets;
	public CopyOnWriteArrayList<Enemy> enemies;
	
	public boolean shooting;
	public boolean drawText;
	public boolean wonGame;
	
	public boolean playingGame;
	public boolean endless;
	public boolean muted;
	
	public Minim minim;
	public AudioSample laser;

	public void setup() {
		surface.setTitle("The Impossible Shooter by game²");
		
		playingGame = false;
		endless = false;
		muted = false;
		
		shootClock = 0;
		
		i = 0;
		qCount = 0;
		qReset = 0;
		
		shooting = false;
		drawText = false;
		wonGame = false;
		
		bullets = new CopyOnWriteArrayList<Bullet>();
		enemies = new CopyOnWriteArrayList<Enemy>();
		
		bks = -500;
		currRow = 1;
		spawnCount = 1;
		spawnWait = 0;
		
		level = 1;
		
		minim = new Minim(this);
		laser = minim.loadSample("laser.wav");
		
		background = loadImage("background.png");
		logo = loadImage("game2.png");
		logo.resize(50, 50);
		ship1 = loadImage("Dove1.png");
		ship2 = loadImage("Dove2.png");
		ship3 = loadImage("Dove3.png");
		ship4 = loadImage("Dove4.png");
		eShip1 = loadImage("Lightning1.png");
		eShip2 = loadImage("Lightning2.png");
		eShip3 = loadImage("Lightning3.png");
		eShip4 = loadImage("Lightning4.png");
		
		surface.setIcon(ship1);
		
		ship = new Ship(ship1, ship2, ship3, ship4, width / 2, height - ship1.height - 5);
		
		background.resize(256 + 64, background.height);
		
		spawnMoreEnemies(spawnCount, 1);
	}

	public void draw() {
		//background.resize(width, height);
		
		image(background, 0, bks);
		image(background, 0, bks + background.height);
		
		bks += 6;
		
		if (bks >= 0) {
			bks = -height;
		}
		
		if (playingGame) {
			shootClock++;
			
			if (!wonGame) {
				fill(0, 255, 255, 64);
				rect(0, height - ship4.height - 10, width + 1, height - ship4.height - 10);
			}
			
			if (enemies.isEmpty() && !wonGame) {
				fill(204, 128, 0);
				textSize(14);
				text("Fleet destroyed.", 10, 20);
				bullets.removeAll(bullets);
				if (spawnWait == 135 && bullets.isEmpty()) {
					if (spawnCount < 5) {
						spawnCount++;
					}
					if (spawnCount == 5) {
						levelUp();
					}
					spawnMoreEnemies(spawnCount, currRow);
					spawnWait = 0;
				}
				else if (spawnWait < 135) {
					spawnWait++;
				}
				else {
					spawnWait = 0;
				}
			}
			
			if (!endless) {
				fill(204, 128, 0);
				textSize(14);
				text("Level: " + level, width - 55, 20);
			}
			
			ship.update();
			ship.drawShip();
			
			if (shooting && shootClock >= 12) {
				Bullet bullet = new Bullet(ship.xPos + ship.currentSprite.width / 2 - 4, ship.yPos - 23);
				bullets.add(bullet);
				if (!muted) {
					laser.trigger();
				}
				shootClock = 0;
			}
			
			for (Bullet bullet : bullets) {
				bullet.update();
				bullet.drawBullet();
			}
			
			for (Enemy enemy : enemies) {
				enemy.update();
				enemy.drawEnemy();
			}
			
			if (drawText) {
				wonGame = true; //To disable enemy spawning
				shooting = false;
				i++;
				fill(255);
				textSize(32);
				text("GAME OVER", 10, 35);
				if (i == 120) {
					playingGame = false;
					endless = false;
				}
			}
			
			if (!endless) {
				if (level == 4) {
					wonGame = true;
					shooting = false;
					enemies.removeAll(enemies);
					ship.yPos -= 10;
					if (ship.yPos + ship.currentSprite.height <= 0) {
						fill(255);
						textSize(16);
						text("YOU BEAT THE IMPOSSIBLE SHOOTER!", width / 2 - 150 / 2, height / 2 - 100 / 2, 150, 100);
						
						fill(204, 128, 0);
						textSize(12);
						textAlign(CENTER);
						text("Code by Owen Thompson\nArt by Michael Mincer and Jull on opengameart.org\nMusic by Michael Mincer and freesound.org", width / 2, height / 2 + 200);
						textAlign(CORNER);
					}
				}
				
				if (level > 4) {
					fill(255);
					textSize(16);
					wonGame = true;
					enemies.removeAll(enemies);
					shooting = false;
					text("You dirty hacker!", width / 2 - 150 / 2, height / 2 - 100 / 2, 150, 100);
				}
			}
			else {
				fill(204, 128, 0);
				textSize(14);
				text("Endless Mode", width - 105, 20);
			}
			
			if (qCount == 1) {
				fill(204, 128, 0);
				textSize(16);
				textAlign(CENTER);
				text("Press q again to quit.", width / 2, height / 2);
				textAlign(CORNER);
				qReset++;
				if (qReset >= 120) {
					qCount = 0;
					qReset = 0;
				}
			}
		}
		if (!playingGame) {
			//TODO: Show an actual title screen
			fill(255);
			textSize(12);
			text("TITLE SCREEN", 10, 10);
			image(logo, width - logo.width - 5, height - logo.height - 5);
		}
	}
	
	public void settings() {
		size(256 + 64, 512);
	}
	
	public void keyPressed() {
		if (key == 'a') {
			ship.movingLeft = true;
		}
		if (key == 'd') {
			ship.movingRight = true;
		}
		if (key == ' ' && ship.exists) {
			shooting = true;
		}
		if (key == 'k' && !playingGame) {
			shootClock = 0;
			
			i = 0;
			qCount = 0;
			qReset = 0;
			
			shooting = false;
			drawText = false;
			wonGame = false;
			
			bullets = new CopyOnWriteArrayList<Bullet>();
			enemies = new CopyOnWriteArrayList<Enemy>();
			
			bks = -500;
			currRow = 1;
			spawnCount = 1;
			spawnWait = 0;
			
			level = 1;
			
			ship = new Ship(ship1, ship2, ship3, ship4, width / 2, height - ship1.height - 5);
			
			spawnMoreEnemies(spawnCount, 1);
			
			endless = false;
			playingGame = true;
		}
		if (key == 'm' && !playingGame) {
			shootClock = 0;
			
			i = 0;
			qCount = 0;
			qReset = 0;
			
			shooting = false;
			drawText = false;
			wonGame = false;
			
			bullets = new CopyOnWriteArrayList<Bullet>();
			enemies = new CopyOnWriteArrayList<Enemy>();
			
			bks = -500;
			currRow = 1;
			spawnCount = 1;
			spawnWait = 0;
			
			level = 1;
			
			ship = new Ship(ship1, ship2, ship3, ship4, width / 2, height - ship1.height - 5);
			
			spawnMoreEnemies(spawnCount, 1);
			
			endless = true;
			playingGame = true;
		}
	}
	
	public void keyReleased() {
		if (key == 'a') {
			ship.movingLeft = false;
		}
		if (key == 'd') {
			ship.movingRight = false;
		}
		if (key == ' ') {
			shooting = false;
		}
		if (key == 'q' && playingGame) {
			qCount++;
			if (qCount >= 2) {
				playingGame = false;
				endless = false;
			}
		}
		if (key == 'n') {
			muted ^= true;
		}
	}
	
	public class Ship {
		PImage sprite1;
		PImage sprite2;
		PImage sprite3;
		PImage sprite4;
		PImage currentSprite;
		
		int sprWait;
		
		float xPos;
		float yPos;
		
		boolean movingLeft;
		boolean movingRight;
		
		boolean exists;
		
		int xSpeed = 3;	
		
		public Ship(PImage spr, PImage spr2, PImage spr3, PImage spr4, float x, float y) {
			this.exists = true;
			
			this.movingLeft = false;
			this.movingRight = false;
			
			this.sprWait = 0;
			
			this.sprite1 = spr;
			this.sprite2 = spr2;
			this.sprite3 = spr3;
			this.sprite4 = spr4;
			this.currentSprite = spr;
			this.xPos = x;
			this.yPos = y;
		}
		
		public void update() {
			if (this.exists) {
				if (this.sprWait == 5) {
					this.sprWait = 0;
					if (this.currentSprite == this.sprite1) {
						this.currentSprite = this.sprite2;
					}
					else if (this.currentSprite == this.sprite2) {
						this.currentSprite = this.sprite3;
					}
					else if (this.currentSprite == this.sprite3) {
						this.currentSprite = this.sprite4;
					}
					else if (this.currentSprite == this.sprite4) {
						this.currentSprite = this.sprite1;
					}
				}
				else {
					this.sprWait++;
				}
				
				if (movingLeft) {
					this.xPos -= this.xSpeed;
				}
				if (movingRight) {
					this.xPos += this.xSpeed;
				}
				
				if (this.xPos <= 0) {
					this.xPos = 0;
				}
				if (this.xPos + this.currentSprite.width >= width) {
					this.xPos = width - this.currentSprite.width;
				}
			}
		}
		
		public void drawShip() {
			if (this.exists) {
				image(this.currentSprite, this.xPos, this.yPos);
			}
		}
	}
	
	public class Bullet {
		float xPos;
		float yPos;
		
		public Bullet(float x, float y) {
			this.xPos = x;
			this.yPos = y;
		}
		
		public void update() {
			this.yPos -= 4;
			
			this.checkCollision();
		}
		
		public void checkCollision() {
			for (Enemy enemy : enemies) {
				if (this.xPos > enemy.xPos && this.xPos + 5 < enemy.xPos + enemy.currentSprite.width) {
					if (this.yPos < enemy.yPos + enemy.currentSprite.height && this.yPos > enemy.yPos) {
						if (!enemy.destroyed) {
							enemy.exists = false;
							enemy.destroyed = true;
							enemies.remove(enemy);
							bullets.remove(this);
						}
					}
				}
			}
			
			if (this.xPos <= 0) {
				bullets.remove(this);
			}
			if (this.xPos + 5 >= width) {
				bullets.remove(this);
			}
			if (this.yPos <= 0) {
				bullets.remove(this);
			}
			if (this.yPos + 20 >= height) {
				bullets.remove(this);
			}
		}
		
		public void drawBullet() {
			fill(204, 128, 0);
			rect(this.xPos, this.yPos, 5, 20, 15);
		}
	}
	
	public class EnemyBullet extends Bullet {
		public EnemyBullet(float x, float y) {
			super(x, y);
		}
		
		@Override
		public void update() {
			this.yPos += 4;
			
			this.checkCollision();
		}
		
		@Override
		public void checkCollision() {
			if (this.xPos > ship.xPos && this.xPos + 5 < ship.xPos + ship.currentSprite.width) {
				if (this.yPos + 20 > ship.yPos) {
					if (ship.exists) {
						ship.exists = false;
						drawText = true;
						bullets.remove(this);
					}
				}
			}
			
			if (this.xPos <= 0) {
				bullets.remove(this);
			}
			if (this.xPos + 5 >= width) {
				bullets.remove(this);
			}
			if (this.yPos <= 0) {
				bullets.remove(this);
			}
			if (this.yPos + 20 >= height) {
				bullets.remove(this);
			}
		}
		
		@Override
		public void drawBullet() {
			fill(179, 0, 0);
			rect(this.xPos, this.yPos, 5, 20, 15);
		}
	}
	
	public class Enemy {
		PImage sprite1;
		PImage sprite2;
		PImage sprite3;
		PImage sprite4;
		PImage currentSprite;
		
		int sprWait;
		
		float xPos;
		float yPos;
		
		int xSpeed;
		
		boolean exists = false;
		boolean destroyed = false;
		int shouldShoot;
		
		public Enemy(PImage spr, PImage spr2, PImage spr3, PImage spr4, float x, float y) {
			this.xSpeed = (int) Math.ceil(Math.random() * 3);
			
			this.destroyed = false;
			this.exists = true;
			this.shouldShoot = 0;
			
			this.sprWait = 0;
			
			this.sprite1 = spr;
			this.sprite2 = spr2;
			this.sprite3 = spr3;
			this.sprite4 = spr4;
			this.currentSprite = spr;
			this.xPos = x;
			this.yPos = y;
		}
		
		public void update() {
			if (this.exists) {
				this.xPos += this.xSpeed;
				this.yPos += level;
				
				if (this.sprWait == 5) {
					this.sprWait = 0;
					if (this.currentSprite == this.sprite1) {
						this.currentSprite = this.sprite2;
					}
					else if (this.currentSprite == this.sprite2) {
						this.currentSprite = this.sprite3;
					}
					else if (this.currentSprite == this.sprite3) {
						this.currentSprite = this.sprite4;
					}
					else if (this.currentSprite == this.sprite4) {
						this.currentSprite = this.sprite1;
					}
					
					if (shouldShoot >= 4) {
						EnemyBullet b = new EnemyBullet(this.xPos + this.currentSprite.width / 2 - 4, this.yPos + 23);
						bullets.add(b);
						shouldShoot = 0;
					}
					shouldShoot++;
				}
				else {
					this.sprWait++;
				}
				
				if (this.yPos + this.currentSprite.height >= height - ship.currentSprite.height - 10) {
					drawText = true;
				}
				
				if (this.xPos <= 0) {
					this.xSpeed = -this.xSpeed;
				}
				if (this.xPos + this.currentSprite.width >= width) {
					this.xSpeed = -this.xSpeed;
				}
			}
		}
		
		public void drawEnemy() {
			if (this.exists) {
				image(this.currentSprite, this.xPos, this.yPos);
			}
		}
	}
	
	public void spawnMoreEnemies(int e, int row) {
		for (int i = 0; i < e; i++) {
			Enemy enemy = new Enemy(eShip1, eShip2, eShip3, eShip4, (5 + abs(enemies.size()) * eShip1.width), (20 * row) + 5);
			enemies.add(enemy);
		}
	}
	
	public void levelUp() {
		level++;
		spawnCount = 1;
	}
	
	public static void main(String _args[]) {
		PApplet.main(new String[] { shooter.Shooter.class.getName() });
	}
}