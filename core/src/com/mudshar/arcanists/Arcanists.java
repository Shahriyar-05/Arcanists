package com.mudshar.arcanists;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class Arcanists extends ApplicationAdapter implements InputProcessor{
	SpriteBatch batch;
	ShapeRenderer shapeRenderer;
	Pixmap mask1;
	Sprite minimap1;
	Player player;
	ArrayList<Player>players;
	Music menuMusic,grassyHillsMusic;
	HashMap<String,Sound>sounds = new HashMap<String,Sound>();
	HashMap<String,Texture>pics = new HashMap<String,Texture>();
	Texture map1, playerpic, fireballpic, lavabombpic, teleportpic, waterpic;
	Texture[]fireballpics,lavabombpics,teleportpics, waterpics;
	Texture[][]sprites, bpics, ppics;
	Texture[][][]charPics;
	String currentScreen = "main menu", selection = "", playerSelection = "", timeSelection = "", gameSelection = "",
			playerUsername = "naze my soul";
	Button highlight1, highlight2, highlight3, highlight4, highlight5, highlight6, highlight7,highlight8, highlight9,
		   players2, players3, players4, players5, players6,
		   time10,time20,time30,time45,time60,time90,time120,gameFFA,gameTeam,
		   gameButton, lobbyButton, friendsButton, ignoreButton, addButton1, addButton2, removeButton1, removeButton2,
		   returnToLobbyCreateButton, inviteButton, closeInviteButton,startButton,
		   arcaneGateButton, fireBallButton, lavaBombButton, gameReturntolobby;
	Button[]menuButtons, playerButtons, timeButtons, spellButtons;
	BitmapFont playerText, timeText, spellText, healthText;
	Color[]RANDOMCOLOURS,TEAMCOLOURS4,TEAMCOLOURS6;
	double spot, radians, waterspot;
	long clicktime, magnitude, attackstart,turnTime = 0, time1970 = 0, waitTime;
	Rectangle miniMapRect, backRect = new Rectangle(0,123,199,41),inviteRect = new Rectangle(202,123,218,41),
			closeRect = new Rectangle(341,145,80,24), playRect = new Rectangle(423,123,217,42),
			returnToGameRect = new Rectangle(26,118,253,29), returnToLobbyRect = new Rectangle(26,25,253,29),
			gameReturnToLobby = new Rectangle(26+54, 118-81, 500-19, 100-68);
	boolean mouseReady = true, usernameCreated = false, inLobbyMenu = true, inGameMenu = false, inFriendsMenu = false,
	inIgnoreMenu = false, inInviteMenu = false, inExitMenu = false, startTimer = false, canfit, scrolling = false,
	arrowUP, arrowDOWN, animation, selecting, hitPlayer, endGame = false;
	int MAGENTA, CLEAR, WHITE, BLACK, GRAY,
	offsetx,offsety, guyx,guyy, oldx,oldy, teleportingx,teleportingy, tx,ty, fx,fy, fvx,fvy, attackx,attacky,
	LSTAND,RSTAND,LJUMP,RJUMP,LWALK,RWALK, currentPlayer = 0, numRemovedPlayers=0, arrowY=0,
	turnLimit, redCount, blueCount, ratioX = 1906/125, ratioY = 1070/68, spellMenuY;
	
	public int cnum(int r, int g, int b, int a){ // returns integer value for an rgba colour value, used mostly for pixmap colour identification
		return (r<<24) + (g<<16) + (b<<8) + a;
	}
	
	public void moveUp(Player p, int vy){ //moves player up with a particular vy
		for(int i=0;i < vy; i++){ //all non masked pixels above within range of vy can be moved into by player
			if (mask1.getPixel(p.getX(),1070-p.getY()-40)!=MAGENTA){ //checking mask
				p.setY(p.getY()+1);
				p.setLAND(false);
			}
			else{
				p.setVY(0); //when player hits mask, vy becomes 0, stopping the player from trying to move up and mimic hitting in real life
			}
		}
	}
	public void moveDown(Player p, int vy){ //moves player down with vy, checks all pixels downward within vy, moves character down if they are not masked
		for(int i=0;i < vy; i++){
			if (mask1.getPixel(p.getX(),1070-p.getY())!=MAGENTA){
				p.setY(p.getY()-1);
			}
			else{
				p.setVY(0); //when the player lands, they are no longer leaping or jumping, used for proper horizontal movement while in air
				p.setONGROUND(true);
				p.setLEAPING(false);
				if(p.getJUMPING()==true){
					p.setJUMPING(false);
				}
				if(p.getLAND()==false){ //plays sound if they are landing
					p.setLAND(true);
					sounds.get("walk").play();
				}
			}
		}
	}
	public void moveRight(Player p, int vx){
		for(int i=0;i < vx; i++){ //moves into non masked pixels within vx given
			if(mask1.getPixel(p.getX()+16,1070-p.getY())==MAGENTA && mask1.getPixel(p.getX()+16,1070-p.getY()-20)!=MAGENTA){
				p.setX(p.getX()+1);
				moveUp(p,3); //if spot directly to the right is masked but a slope can be climbed, it is climbed
			}
			if(mask1.getPixel(p.getX()+16,1070-p.getY())!=MAGENTA){
				p.setX(p.getX()+1); //standard movement
			}
			else{
				p.setVX(0); //vx becomes 0, used for players that are launched from an attack blast
			}
		}
	}
	public void moveLeft(Player p, int vx){
		for(int i=0;i < vx; i++){
			if(mask1.getPixel(p.getX()-16,1070-p.getY())==MAGENTA && mask1.getPixel(p.getX()-16,1070-p.getY()-20)!=MAGENTA){
				p.setX(p.getX()-1);
				moveUp(p,3); //climbing left
			}
			if(mask1.getPixel(p.getX()-16,1070-p.getY())!=MAGENTA){
				p.setX(p.getX()-1); //standard movement 
			}
			else{
				p.setVX(0); //for players launched from attack blast
			}
		}
	}
	
	public void teleport(Player p){
		p.setVY(0); //avoids constant looping settting teleport settings
		if(Gdx.input.isKeyPressed(Keys.LEFT) && teleportingx > 320){
			teleportingx-=5;
		}
		if(Gdx.input.isKeyPressed(Keys.RIGHT) && teleportingx < 1586){
			teleportingx+=5;
		}
		if(Gdx.input.isKeyPressed(Keys.UP) && teleportingy < 1070){
			teleportingy+=5;
		}
		if(Gdx.input.isKeyPressed(Keys.DOWN) && teleportingy > 150){
			teleportingy-=5;
		}
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)){
			mouseReady = true;
		}
		if(mouseReady && animation==false){
			tx = Gdx.input.getX(); //cursor spot on screen
			ty = 480-Gdx.input.getY();
			canfit = true;
			for(int i=0; i<40; i++){ //checks if there is space for the character, checks a rect the size of the player
				for(int j=-16; j<16; j++){
					if(mask1.getPixel(tx-offsetx+j, 1070-(ty-offsety-20+i))==MAGENTA){
						canfit = false;
					}
				}
			}
			if(Gdx.input.isButtonPressed(Buttons.LEFT) && canfit == true){
				mouseReady = false; 
				p.setX(tx-offsetx); //sets player x and y to the clicked spot if it can fit, identified by green circle
				p.setY(ty-offsety-20);
				sounds.get("arcane gate").play();
				animation = true; //starts animation
			}
		}
		if(animation==true){
			if(spot>=(double)(teleportpics.length)-0.5){ //once animation is done, teleporting is done, turn changes
				teleportpic = null;
				animation = false;
				p.setTELEPORTING(false);
				p.setCurrentSpell("");
				changeTurn();
			}
			if((int)(spot)<teleportpics.length){ //going through teleport pictures
				teleportpic = teleportpics[(int)(spot)];
				spot+=0.1;
			}
		}
	}
	
	public void movePlayer(Player p){
		p.setONGROUND(false);
		if(p.getVY()<60){ //max vy (critical velocity)
			p.setVY(p.getVY()+1);
			if(p.getTELEPORTING()==true){
				p.setVY(0);
			}
		}
		//used for movement when player is given a vx or vy either from jumping, leaping 
		if(p.getVY() < 0){ 
			moveUp(p,-p.getVY());
		}
		else if(p.getVY() > 0){
			moveDown(p,p.getVY());
		}
		if(p.getVX() < 0){
			moveLeft(p,-p.getVX());
		}
		else if(p.getVX() > 0){
			moveRight(p,p.getVX());
		}
		p.setNEWMOVE(-1);
		if(p.getTELEPORTING()==false){ //player cannot move if they are teleporting
			if(Gdx.input.isKeyJustPressed(Keys.BACKSPACE) && p.getONGROUND()== true){
				sounds.get("jump").play();
				p.setONGROUND(false);
				p.setVY(-15); //jumping 
				p.setJUMPING(true);
			}//jump
			if(Gdx.input.isKeyJustPressed(Keys.ENTER) && p.getONGROUND()==true){
				sounds.get("leap").play();
				p.setONGROUND(false);
				p.setLEAPING(true);
				p.setVY(-9); //leaping
			}//leap
			if(p.getJUMPING()==true){ //horizontal movement while jumping
				if(p.getLEFT()==true){
					moveLeft(p,1);
				}
				else{
					moveRight(p,1);
				}
			}
			if(p.getLEAPING()==true){ //horizontal movement while leaping is larger
				if(p.getLEFT()==true){
					moveUp(p,2);
					moveLeft(p,5);
				}
				else{
					moveUp(p,2);
					moveRight(p,5);
				}
			}
			if(Gdx.input.isKeyPressed(Keys.X) && p.getX() < 1906 && p.getJUMPING()==false){ //draws walking animation if you aren't jumping
				p.setLEFT(false);
				moveRight(p,1);
				p.setNEWMOVE(RWALK); //starts walking animation
				p.setMOVING(true);
			}//walk right
			else if(Gdx.input.isKeyPressed(Keys.Z) && p.getX() > 0 && p.getJUMPING()==false){
				p.setLEFT(true);
				moveLeft(p,1);
				p.setNEWMOVE(LWALK);
				p.setMOVING(true);
			}//walk left
			else{
				if(p.getLEFT()==true){
					p.setNEWMOVE(LSTAND); //player is just standing
					p.setMOVING(false);
				}
				else{
					p.setNEWMOVE(RSTAND);
					p.setMOVING(false);
				}
				p.setFRAME(0.0); 
			}
		}
		
		else{ //if teleporting, the players move is standing
			if(p.getLEFT()==true){
				p.setNEWMOVE(LSTAND);
			}
			else{
				p.setNEWMOVE(RSTAND);
			}
			p.setFRAME(0.0);
		}
		
		if(p.getMOVE()==p.getNEWMOVE()){ //iterating through frames
			if(p.getFRAME()<(double)(p.getPics()[p.getMOVE()].length)-0.2){
				p.setFRAME(p.getFRAME()+0.1);
			}
			else if(p.getFRAME()>=(double)(p.getPics()[p.getMOVE()].length)-0.2){
				p.setFRAME(0.0);
			}
		}
		else if(p.getNEWMOVE()!=-1){ //setting move
			p.setMOVE(p.getNEWMOVE());
			p.setFRAME(0.0);
		}
		
		if(p.getY()<-200){ //setting teleporting to true once if the player falls off map
			p.setY(-199);
			p.setVY(0);
			p.setVX(0);
			p.setJUMPING(false); //leaping and jumping become false so player will fall straight down when teleported
			p.setLEAPING(false);
			spot = 0;
			teleportingx = player.getX();
			if(teleportingx>1586){ //does not move off the map too much
				teleportingx = 1586;
			}
			if(teleportingx<320){
				teleportingx = 320;
			}
			teleportingy = 150;
			tx = Gdx.input.getX();
			ty = 480-Gdx.input.getY();
			teleportpic = null;
			player.setTELEPORTING(true); //starts calling teleport
		}
		
		
		if(p.getTELEPORTING()==true){
			teleport(p);
		}
		
		if(p.getONGROUND()==false){
			p.setMOVING(true);
		}
		
		if(mouseReady){
			if(Gdx.input.isButtonPressed(Buttons.LEFT)){
				clicktime = System.currentTimeMillis(); //used for power on projectile attacks
				selecting = false;
				for(int i=0;i<spellButtons.length;i++){ //cannot cast spell if cursor is on a spell selection button
					if(spellButtons[i].getCirc().contains(Gdx.input.getX(),480-Gdx.input.getY())){
						selecting = true; 
					}
				}
				if(selecting==false){
					if(p.getATTACK().equals("Fire Ball") && p.getATTACKING()==false){ //calling fireball attack
						fx = p.getX();  
						fy = p.getY()+20;
						spot = 0;
						fireballpic = fireballpics[(int)(spot)];
						p.setATTACKING(true);
					}
					if(p.getATTACK().equals("Lava Bomb") && p.getATTACKING()==false){ //calling lavabomb attack
						fx = p.getX();
						fy = p.getY()+20;
						spot = 0;
						lavabombpic = lavabombpics[(int)(spot)];
						p.setATTACKING(true);
					}
				}
				mouseReady = false;
			}
			
		}
		
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)){
			if(p.getATTACK().equals("Fire Ball") && p.getATTACKING()==true || p.getATTACK().equals("Lava Bomb") && p.getATTACKING()==true){
				fvx = (int)((double)(magnitude)*(Math.cos(radians))); //sets vx and vy for attack when you stop holding according to angle 
				fvy = -1*(int)((double)(magnitude)*(Math.sin(radians)));
				p.setATTACK(""); // only calls once
			}
			mouseReady = true;
		}
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT)){
			if(p.getATTACK().equals("Fire Ball") || p.getATTACK().equals("Lava Bomb")){
				magnitude = (long)((double)(System.currentTimeMillis() - clicktime)/100.0); //increases as you hold
				if((int)(magnitude)>=42){
					magnitude = 42; //max magnitude, fills meter
				}
				radians = Math.atan2(480-Gdx.input.getY()-guyy, Gdx.input.getX()-guyx);
				attackstart= System.currentTimeMillis(); //used for lavabomb timer
				fvx = 0;
				fvy = 0;
			}
		}
		
		if(p.getATTACKING()==true){
			if(fireballpic!=null){ //pictures are null when the attack is done so this case check works
				moveFireball(p,fvx,fvy);
			}
			else{
				moveLavabomb(p,fvx,fvy);
			}
			fvy+=1; //projectile motion for attacks
		}
		
	}
	public void moveFireball(Player p,int vx, int vy){
		if(fy<=0){ //if attack goes off map, end it and change turn
			fireballpic = null; //setting pic to null stops calling moveFireball
			p.setATTACKING(false);
			p.setATTACK("");
			p.setCurrentSpell("");
			changeTurn();
		}
		hitPlayer = false;
		for(int i=0;i<players.size();i++){ //checking if attack hits a player and if it does, it blows up
			if(players.get(i)!=p){
				if(vx<0){
					for(int j=0;j<-vx;j++){
						if(players.get(i).getCirc().contains(fx-j,fy)){
							hitPlayer = true;
						}
					}
				}
				if(vx>0){
					for(int j=0;j<vx;j++){
						if(players.get(i).getCirc().contains(fx+j,fy)){
							hitPlayer = true;
						}
					}
				}
				if(vy<0){
					for(int j=0;j<-vy;j++){
						if(players.get(i).getCirc().contains(fx,fy+j)){
							hitPlayer = true;
						}
					}
				}
				if(vy>0){
					for(int j=0;j<vy;j++){
						if(players.get(i).getCirc().contains(fx,fy-j)){
							hitPlayer = true;
						}
					}
				}
			}
		}
		if(vx<0){
			for(int i=0;i < -vx; i++){
				if(mask1.getPixel(fx,1070-fy)!=MAGENTA && animation==false && hitPlayer==false){
					fx-=1;
				}
				else{
					fvx = 0;
					fvy = 0;
					if(animation==false){ //damages map once, starts animation
						sounds.get("fire ball").play();
						damageMap(map1,mask1,fx,fy,60,30);
					}
					animation = true;
					if(spot>=(double)(fireballpics.length)-0.5){ //once animation is done the attack is done and turn changes
						fireballpic = null;
						animation=false;
						p.setATTACKING(false);
						p.setATTACK("");
						p.setCurrentSpell("");
						changeTurn();
						break; 
					}
					if((int)(spot)<fireballpics.length){ //going thorugh attack sprites
						fireballpic = fireballpics[(int)(spot)];
						spot+=0.1;
						break; //used to break for loop so animation speed doesn't go to fast
					}
				}
			}
		}
		if(vx>0){
			for(int i=0;i < vx; i++){
				if(mask1.getPixel(fx,1070-fy)!=MAGENTA && animation==false && hitPlayer==false){
					fx+=1;
				}
				else{
					fvx = 0;
					fvy = 0;
					if(animation==false){
						sounds.get("fire ball").play();
						damageMap(map1,mask1,fx,fy,60,30);
					}
					animation = true;
					if(spot>=(double)(fireballpics.length)-0.5){
						fireballpic = null;
						animation=false;
						p.setATTACKING(false);
						p.setATTACK("");
						p.setCurrentSpell("");
						changeTurn();
						break;
					}
					if((int)(spot)<fireballpics.length){
						fireballpic = fireballpics[(int)(spot)];
						spot+=0.1;
						break;
					
					}
				}
			}
		}
		if(vy<0){
			for(int i=0;i < -vy; i++){
				if (mask1.getPixel(fx,1070-fy)!=MAGENTA && animation==false && hitPlayer==false){
					fy+=1;
				}
				else{
					fvy = 0;
					fvx = 0;
					if(animation==false){
						sounds.get("fire ball").play();
						damageMap(map1,mask1,fx,fy,60,30);
					}
					animation = true;
					if(spot>=(double)(fireballpics.length)-0.5){
						fireballpic = null;
						animation=false;
						p.setATTACKING(false);
						p.setATTACK("");
						p.setCurrentSpell("");
						changeTurn();
						break;
					}
					if((int)(spot)<fireballpics.length){
						fireballpic = fireballpics[(int)(spot)];
						spot+=0.1;
						break;
					}
				}
			}
		}
		if(vy>0){
			for(int i=0;i < vy; i++){
				if (mask1.getPixel(fx,1070-fy)!=MAGENTA && animation==false && hitPlayer==false){
					fy-=1;
				}
				else{
					fvy = 0;
					fvx = 0;
					if(animation==false){
						sounds.get("fire ball").play();
						damageMap(map1,mask1,fx,fy,60,30);
					}
					animation = true;
					if(spot>=(double)(fireballpics.length)-0.5){
						fireballpic = null;
						animation = false;
						p.setATTACKING(false);
						p.setATTACK("");
						p.setCurrentSpell("");
						changeTurn();
						break;
					}
					if((int)(spot)<fireballpics.length){
						fireballpic = fireballpics[(int)(spot)];
						spot+=0.1;
						break;
					}
				}
			}
		}
	}
	
	public void moveLavabomb(Player p,int vx, int vy){
		if(fy<=0){ //stops turn if attack leaves map 
			lavabombpic = null; //setting picture to null stops calling moveLavaBomb
			p.setATTACKING(false);
			p.setATTACK("");
			p.setCurrentSpell("");
			changeTurn();
		}
		hitPlayer = false; 
		for(int i=0;i<players.size();i++){ //checking collision with players, attack can bounce off of them if speed is not too slow
			if(players.get(i)!=p){
				if(vx<0){
					for(int j=0;j<-vx;j++){
						if(players.get(i).getCirc().contains(fx-j,fy)){
							hitPlayer = true;
						}
					}
				}
				if(vx>0){
					for(int j=0;j<vx;j++){
						if(players.get(i).getCirc().contains(fx+j,fy)){
							hitPlayer = true;
						}
					}
				}
				if(vy<0){
					for(int j=0;j<-vy;j++){
						if(players.get(i).getCirc().contains(fx,fy+j)){
							hitPlayer = true;
						}
					}
				}
				if(vy>0){
					for(int j=0;j<vy;j++){
						if(players.get(i).getCirc().contains(fx,fy-j)){
							hitPlayer = true;
						}
					}
				}
			}
		}
		if((int)((System.currentTimeMillis()-attackstart)/100)>=20){ //stops moving after timer ends, goes through animation 
			fvx = 0; 
			fvy = 0;
			if(spot==0){
				sounds.get("fire ball").play();
				damageMap(map1,mask1,fx,fy,80,50);
			}
			if(spot>=(double)(lavabombpics.length)-0.5){ //attacking ends and turn changed after animation is deone
				lavabombpic = null;
				p.setATTACKING(false);
				p.setATTACK("");
				p.setCurrentSpell("");
				changeTurn();
			}
			if((int)(spot)<lavabombpics.length){
				lavabombpic = lavabombpics[(int)(spot)];
				spot+=0.1;
			}
		}
		else{
			if(vx<0){
				for(int i=0;i < -vx; i++){
					if(mask1.getPixel(fx-5,1070-fy)!=MAGENTA && hitPlayer==false){
						fx-=1;
					}
					else{
						if(vx<-2){
							fvx = (fvx/2)*-1; //changes direction and loses speed when the bomb hits something, used for vx and vy of attack
							break;
						}
						else{
							fvx = 0;
						}
					}
				}
			}
			if(vx>0){
				for(int i=0;i < vx; i++){
					if(mask1.getPixel(fx+5,1070-fy)!=MAGENTA && hitPlayer==false){
						fx+=1;
						sounds.get("lava bomb hit").play();
					}
					else{
						if(vx>2){
							fvx = (fvx/2)*-1;
							break;
						}
						else{
							fvx = 0;
						}
					}
				}
			}
			if(vy<0){
				for(int i=0;i < -vy; i++){
					if (mask1.getPixel(fx,1070-fy-5)!=MAGENTA && hitPlayer==false){
						fy+=1;
					}
					else{
						fvy *= -1;
					}
				}
			}
			if(vy>0){
				for(int i=0;i < vy; i++){
					if (mask1.getPixel(fx,1070-fy+5)!=MAGENTA && hitPlayer==false){
						fy-=1;
					}
					else{
						if(vy>2){
							fvy = (fvy/2)*-1;
							break;
						}
						else{
							fvy = 0;
						}
					}
				}
			}
		}
	}

	
	public void damageMap(Texture map, Pixmap mask, int impactx, int impacty, int damagerad, int damage){
		damagePlayers(impactx, impacty, damagerad, damage);
		Pixmap.setBlending(Pixmap.Blending.None); //setting pixels to a colour in pixmap
		mask.setColor(CLEAR);
		mask.fillCircle(impactx, mask.getHeight()-impacty, damagerad); //changes masked image 
		if(!map.getTextureData().isPrepared()){
			map.getTextureData().prepare();
		}
		Pixmap changedmap = map.getTextureData().consumePixmap();
		changedmap.setColor(BLACK);
		for(int i=-damagerad-4;i<damagerad+4;i++){ //draws black border in map in actual terrain
			for(int j=-damagerad-4;j<damagerad+4;j++){
				if(changedmap.getPixel(impactx+i, mask.getHeight()-impacty+j)!=CLEAR && Math.pow(Math.pow(i,2)+Math.pow(j,2),0.5)<=damagerad+4){
					changedmap.drawPixel(impactx+i, mask.getHeight()-impacty+j);
				}
			}
		}
		changedmap.setColor(GRAY);
		for(int i=-damagerad-3;i<damagerad+3;i++){ //gray border in actual terrain
			for(int j=-damagerad-3;j<damagerad+3;j++){
				if(changedmap.getPixel(impactx+i, mask.getHeight()-impacty+j)!=CLEAR && Math.pow(Math.pow(i,2)+Math.pow(j,2),0.5)<=damagerad+3){
					changedmap.drawPixel(impactx+i, mask.getHeight()-impacty+j);
				}
			}
		}
		changedmap.setColor(WHITE);
		for(int i=-damagerad-2;i<damagerad+2;i++){ //white border in terrain
			for(int j=-damagerad-2;j<damagerad+2;j++){
				if(changedmap.getPixel(impactx+i, mask.getHeight()-impacty+j)!=CLEAR && Math.pow(Math.pow(i,2)+Math.pow(j,2),0.5)<=damagerad+2){
					changedmap.drawPixel(impactx+i, mask.getHeight()-impacty+j);
				}
			}
		}
		changedmap.setColor(CLEAR);
		changedmap.fillCircle(impactx, mask.getHeight()-impacty, damagerad); //clear circle corresponding to circle in mask
		map1 = new Texture(changedmap);
	}
	
	public void damagePlayers(int impactx, int impacty, int damagerad, int damage){
		Circle damageCirc = new Circle(impactx,impacty,damagerad*2);
		for(int i=0;i<players.size();i++){
			if(damageCirc.contains(players.get(i).getX(),players.get(i).getY()+20)){
				int dist = (int)(Vector2.dst(players.get(i).getX(),players.get(i).getY()+20,impactx,impacty));
				//damage depends on distance from impact
				if(dist<=damagerad/2){
					players.get(i).setHealth(players.get(i).getHealth()-damage);
				}
				else{
					players.get(i).setHealth(players.get(i).getHealth()-(damage-((dist-(damagerad/2))/3)));
				}
				//blast speed depends on distance from impact
				if(players.get(i).getY()+20-impacty!=0){
					players.get(i).setVY(-250/(players.get(i).getY()+20-impacty));
				}
				if(players.get(i).getX()-impactx!=0){
					players.get(i).setVX(250/(players.get(i).getX()-impactx));
				}
			}
		}
	}

	public Texture[] makeMove(String section,String ctype,String name,int start,int end){
		Texture[]move = new Texture[end-start+1];
		for(int i=start;i<end+1;i++){
			move[i-start]= new Texture(String.format("%s/%s/%s%d.png",section,ctype,name,i));
		}
		return move;
	}
	
	public void updateFriendsandIgnoreMenu(String screen){ //updates a small menu to left of screen for friends
		//and ignored characters for multiplayer
		//takes input based on screen because different buttons are drawn in the lobby and create game screens
		//Lobby menu button: (is supposed to show all the players in the lobby)
		if(screen.equals("lobby")){
			if(lobbyButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						mouseReady = false;
						inLobbyMenu = true;
						inFriendsMenu = false;
						inIgnoreMenu = false;
					}
				}
			}
		}
		//game menu button: (is supposed to show all the members joining the game)
		else if(screen.equals("game")){ 
			if(gameButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						mouseReady = false;
						inGameMenu = true;
						inFriendsMenu = false;
						inIgnoreMenu = false;
					}
				}
			}
		}
		//Friends menu button: (all of the players friends)
		if(friendsButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					inLobbyMenu = false;
					inFriendsMenu = true;
					inIgnoreMenu = false;
				}
			}
		}
		//Ignore menu button: (all of the players ignored players)
		if(ignoreButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					inLobbyMenu = false;
					inIgnoreMenu = true;
					inFriendsMenu = false;
				}
			}
		}
	}
	
	public void drawFriendsandIgnoreMenu(String screen){ //draws the side friends and lobby menu based on input
		if(currentScreen.equals("multiplayer menu")){
			if(inLobbyMenu){
				batch.draw(pics.get("lobby menu"),0,166);
			}
		}
		if(currentScreen.equals("create menu")){
			if(inGameMenu){
				batch.draw(pics.get("game menu"),0,166);
			}
		}
			
		//Friends menu:
		if(inFriendsMenu){
			batch.draw(pics.get("friendsMenu"), 0,166);
			if(screen.equals("lobby")){
				batch.draw(pics.get("lobbyButton"), lobbyButton.getRect().x+lobbyButton.getOffsetX(), lobbyButton.getRect().y+lobbyButton.getOffsetY());
			}
			if(screen.equals("game")){
				batch.draw(pics.get("gameNotSelected"), gameButton.getRect().x+gameButton.getOffsetX(), gameButton.getRect().y+gameButton.getOffsetY());
			}
			if(addButton1.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(addButton1.getImage(), addButton1.getRect().x+addButton1.getOffsetX(), addButton1.getRect().y+addButton1.getOffsetY());
			}
			if(removeButton1.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(removeButton1.getImage(), removeButton1.getRect().x+removeButton1.getOffsetX(), removeButton1.getRect().y+removeButton1.getOffsetY());
			}
		}
		//Ignore menu:
		if(inIgnoreMenu){
			batch.draw(pics.get("ignoreMenu"), 0,166);
			if(screen.equals("lobby")){
				batch.draw(pics.get("lobbyButton"), lobbyButton.getRect().x+lobbyButton.getOffsetX(), lobbyButton.getRect().y+lobbyButton.getOffsetY());
			}
			if(screen.equals("game")){
				batch.draw(pics.get("gameNotSelected"), gameButton.getRect().x+gameButton.getOffsetX(), gameButton.getRect().y+gameButton.getOffsetY());
			}
			if(addButton2.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(addButton2.getImage(), addButton2.getRect().x+addButton2.getOffsetX(), addButton2.getRect().y+addButton2.getOffsetY());
			}
			if(removeButton2.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(removeButton2.getImage(), removeButton2.getRect().x+removeButton2.getOffsetX(), removeButton2.getRect().y+removeButton2.getOffsetY());
			}
		}
		//Lobby menu button:
		if(screen.equals("lobby")){
			if(lobbyButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(inLobbyMenu==false){
					batch.draw(lobbyButton.getImage(), lobbyButton.getRect().x+lobbyButton.getOffsetX(), lobbyButton.getRect().y+lobbyButton.getOffsetY());
				}
			}
		}
		else if(screen.equals("game")){
			if(gameButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(inGameMenu==false){
					batch.draw(gameButton.getImage(), gameButton.getRect().x+gameButton.getOffsetX(), gameButton.getRect().y+gameButton.getOffsetY());
				}
			}
		}
		//Friends menu button:
		if(friendsButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(inFriendsMenu==false){
				batch.draw(pics.get("highlightFriends"), friendsButton.getRect().x+friendsButton.getOffsetX(), friendsButton.getRect().y+friendsButton.getOffsetY());
			}
		}
		//Ignore menu button:
		if(ignoreButton.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(inIgnoreMenu==false){
				batch.draw(pics.get("highlightIgnore"), ignoreButton.getRect().x+ignoreButton.getOffsetX(), ignoreButton.getRect().y+ignoreButton.getOffsetY());
			}
		}
	}
	
	public void checkExitMenu(){ //updating to check if the player is exiting the game
		if(Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			if(inExitMenu){
				inExitMenu = false;
			}
			else{
				inExitMenu = true;
			}
		}
	}
	public void checkGameEnd(){ //updating to check if the game is over
		if(gameSelection.equals("FFA")){ //in free for all, the game is over when all players are dead or 1 is alive
			if(players.size()==1 || players.size()==0){
				endGame = true;
			}
		}
		else if(gameSelection.equals("TEAM")){ //in team game, one team has 0 players alive, the other wins
			redCount = 0; //both counters for players start at 0
			blueCount = 0;
			for(int i=0;i<players.size();i++){ //for each player, their team colour is counted and added
				if(players.get(i).getTeamColor().equals("RED")){
					redCount+=1;
				}
				else if(players.get(i).getTeamColor().equals("BLUE")){
					blueCount+=1;
				}
			}
			if(blueCount == 0 || redCount == 0){ //if one of them is still 0, it means no players on one are alive
				endGame = true;
			}
		}
	}
	public void updatePLAYERPIC(){ //updating player sprite
		player.setPLAYERPIC((player.getPics())[player.getMOVE()][(int)(player.getFRAME())]);
		if(player.getNEWMOVE()==LSTAND){ //setting standing pic
			player.setPLAYERPIC(player.getPics()[LSTAND][0]);
		}
		if(player.getNEWMOVE()==RSTAND){
			player.setPLAYERPIC(player.getPics()[RSTAND][0]);
		}
		if(player.getJUMPING()==true || player.getLEAPING()==true){ //setting jumping pic accoriding to vy, different pic for going up and down
			if(player.getVY() < 5 && player.getONGROUND()==false){
				if(player.getLEFT()==true){
					player.setPLAYERPIC(player.getPics()[LJUMP][0]);
				}
				else{
					player.setPLAYERPIC(player.getPics()[RJUMP][0]);
				}
				if(player.getVY() >=5 && player.getONGROUND()==false){
					if(player.getLEFT()==true){
						player.setPLAYERPIC(player.getPics()[LJUMP][1]);
					}
					else{
						player.setPLAYERPIC(player.getPics()[RJUMP][1]);
					}
				}
			}
		}
		if(player.getVY() >=10 && player.getONGROUND()==false){ //if falling with high speed, playerpic is falling 
			if(player.getLEFT()==true){	//image, not called in moving left or right unless they fall of ledge so walking animation isn't destroyed
				player.setPLAYERPIC(player.getPics()[LJUMP][1]);
			}
			else{
				player.setPLAYERPIC(player.getPics()[RJUMP][1]);
			}
		}
	}
	public void updateOffset(){ //updating offset for the map and where guy is drawn
		if(animation==false){
			if(player.getATTACKING()==false){ //offset during normal movement
				guyx = 320-15;
				guyy = 150;
				offsetx = 320 - player.getX();
				offsety = 150-player.getY();
			}
			else{
				attackx = 320; //offset while attacking 
				attacky = 150;
				offsetx = 320 - fx;
				offsety = 150-fy+20;
			}
		}
		if(offsetx>0){ //not letting offset go past 0
			if(player.getATTACKING()==false){
				guyx-=offsetx; //changing where guy is drawn
			}
			else{
				attackx -= offsetx; //where attack is drawn
			}
			offsetx=0;
		}
		if(offsetx<-1906+640){
			offsetx=-1906+640;
			if(player.getATTACKING()==false){
				guyx = player.getX()-1906+640-15;
			}
			else{
				attackx = fx - 1906 +640;
			}
		}
		if(offsety>0){ //vertical offset limit
			if(player.getATTACKING()==false){
				guyy-=offsety;
			}
			else{
				attackx-=offsety;
			}
			offsety = 0;
		}
		if(player.getTELEPORTING()==true){
			offsetx = 320 - teleportingx; //changing offset by keyboard controls
			offsety = 150-teleportingy;
			if(offsetx>0){
				guyx-=offsetx;
				offsetx=0;
			}
			if(offsetx<-1906+640){
				offsetx=-1906+640;
				guyx = player.getX()-1906+640; //changing where guy is drawn when scrolling around
			}
			if(offsety>0){
				guyy-=offsety;
				offsety = 0;
			}
			guyx = player.getX() + offsetx -15;
			guyy = player.getY() + offsety;
			if(animation==false){
				oldx = guyx; //setting other spot where animation will happen
				oldy = guyy;
			}
		}
	}
	
	public void checkSkipTurn(){ //updating to check if the player has skipped their turn
		if(!inExitMenu || !endGame){
			if(Gdx.input.isKeyJustPressed(Keys.Q)){
				changeTurn();
			}
		}
	}
	
	public void updatePlayerArrow(){ //updating the arrow that is drawn above the player
		if(arrowY==0){ //if it is at its lowest point, it starts to move up again
			arrowDOWN = false;
			arrowUP = true;
		}
		else if(arrowY==100){ //if it is at its highest point, it starts to move down again 
			arrowUP = false;
			arrowDOWN = true;
		}
		if(arrowUP){ //arrow position adds by one each time it goes up 
			arrowY++;
		}
		if(arrowDOWN){ //arrow position decreases by one each time it goes down
			arrowY--;
		}
	}
	
	public void updateTimer(){ //updating the game timer 
		if(!endGame){ //if the game isn't over the timer continues to update 
			turnTime = (System.currentTimeMillis() - time1970)/1000; //current timer start - original timer start in seconds
			turnLimit = Integer.parseInt(timeSelection); //the turn time that the player selected
			if(turnLimit-turnTime<0){ //if the time counts down and the last second is over, the turn is over
				changeTurn();
			}
			else{
				if(!inExitMenu){ //if the player isn't in the exit game menu and the turn isn't over they can move
					movePlayer(player);
				}
			}
		}
	}
	
	public void drawMap(){ //drawing map and water
		batch.begin();
		batch.draw(pics.get("back1"), -30, -100);
		batch.draw(waterpics[(int)(waterspot)],3,offsety-48);
		batch.draw(map1, offsetx,offsety);
		batch.draw(waterpics[(int)(waterspot)],3,offsety-54);
		batch.end();
		waterspot+=0.05; //going through water sprites
		if(waterspot>=5.5){
			waterspot=0;
		}
	}
	public void drawTeleport(){ //drawing teleporting circle according to whether or not spot is suitable to teleport to
		if(player.getTELEPORTING()==true){ 
			shapeRenderer.begin(ShapeType.Filled);
			if(animation==false){
				if(canfit==true){
					shapeRenderer.setColor(new Color(0,1,0,1)); //green means you can teleport there
				}
				else{
					shapeRenderer.setColor(new Color(1,0,0,1)); //red means not enough space
				}
				shapeRenderer.circle(tx,ty,20);
			}
			shapeRenderer.end();
		}
	}
	
	public void drawPlayer(){ //drawing the player
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		if(player.getATTACKING()==false){ //drawing player normally and in center of screen when not attacking. draws arrow, guy and other features
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(1,1,1,1));
			shapeRenderer.circle(guyx+12, guyy+18, 16);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(player.getColor().r, player.getColor().g, player.getColor().b, 0.4f );
			shapeRenderer.circle(guyx+12, guyy+18, 15);
			shapeRenderer.end();
			if(player.getMOVING()==false){
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.triangle(guyx-19, guyy+111+arrowY, guyx+37, guyy+111+arrowY, guyx+9, guyy+82+arrowY);
				shapeRenderer.rect(guyx, guyy+110+arrowY, 18, 31);
				shapeRenderer.setColor(player.getColor());
				shapeRenderer.triangle(guyx-17, guyy+110+arrowY, guyx+35, guyy+110+arrowY, guyx+9, guyy+83+arrowY);
				shapeRenderer.rect(guyx+1, guyy+110+arrowY, 16, 30);
				shapeRenderer.end();
			}
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(1,1,1,1));
			shapeRenderer.circle(guyx+12, guyy+18, 16);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(player.getColor().r, player.getColor().g, player.getColor().b, 0.4f );
			shapeRenderer.circle(guyx+12, guyy+18, 15);
			shapeRenderer.end();
			if(player.getMOVING()==false){
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.triangle(guyx-19, guyy+111+arrowY, guyx+37, guyy+111+arrowY, guyx+9, guyy+82+arrowY);
				shapeRenderer.rect(guyx, guyy+110+arrowY, 18, 31);
				shapeRenderer.setColor(player.getColor());
				shapeRenderer.triangle(guyx-17, guyy+110+arrowY, guyx+35, guyy+110+arrowY, guyx+9, guyy+83+arrowY);
				shapeRenderer.rect(guyx+1, guyy+110+arrowY, 16, 30);
				shapeRenderer.end();
			}
			batch.begin();
			playerText.setColor(player.getColor());
			playerText.draw(batch, player.getHealth()+"", guyx+4, guyy+52);
			playerText.draw(batch, player.getUsername(), guyx-7, guyy-10);
			batch.draw(player.getPLAYERPIC(), guyx, guyy);
			batch.end();
		}
		if(player.getATTACKING()==true){ //drawing player and everything around it according to offset when camera follows attack
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(new Color(1,1,1,1));
			shapeRenderer.circle(player.getX()+offsetx-15+12, player.getY()+offsety+18, 16);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(player.getColor().r, player.getColor().g, player.getColor().b, 0.4f );
			shapeRenderer.circle(player.getX()+offsetx-15+12, player.getY()+offsety+18, 15);
			shapeRenderer.end();
			if(player.getMOVING()==false){
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(Color.BLACK);
				shapeRenderer.triangle(player.getX()+offsetx-15-19, player.getY()+offsety+111+arrowY, player.getX()+offsetx-15+37, player.getY()+offsety+111+arrowY, player.getX()+offsetx-15+9, player.getY()+offsety+82+arrowY);
				shapeRenderer.rect(player.getX()+offsetx-15, player.getY()+offsety+110+arrowY, 18, 31);
				shapeRenderer.setColor(player.getColor());
				shapeRenderer.triangle(player.getX()+offsetx-15-17, player.getY()+offsety+110+arrowY, player.getX()+offsetx-15+35, player.getY()+offsety+110+arrowY, player.getX()+offsetx-15+9, player.getY()+offsety+83+arrowY);
				shapeRenderer.rect(player.getX()+offsetx-15+1, player.getY()+offsety+110+arrowY, 16, 30);
				shapeRenderer.end();
			}
			if(Gdx.input.isButtonPressed(Buttons.LEFT) && player.getATTACK().equals("")==false){
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(0,0,0,255); //draws meter for attacks
				shapeRenderer.triangle(player.getX()+offsetx+(int)(40*Math.cos(radians)),player.getY()+offsety+20+(int)(40*Math.sin(radians)),
						player.getX()+offsetx+(int)(40*Math.cos(radians))+(int)(210*Math.cos(radians-0.1)),player.getY()+offsety+20+(int)(40*Math.sin(radians))+(int)(210*Math.sin(radians-0.1)),
						player.getX()+offsetx+(int)(40*Math.cos(radians))+(int)(210*Math.cos(radians+0.1)),player.getY()+offsety+20+(int)(40*Math.sin(radians))+(int)(210*Math.sin(radians+0.1)));
				shapeRenderer.triangle(player.getX()+offsetx+(int)(40*Math.cos(radians)),player.getY()+offsety+20+(int)(40*Math.sin(radians)),
						player.getX()+offsetx+(int)(40*Math.cos(radians))+(int)((5*magnitude)*Math.cos(radians-0.1)),player.getY()+offsety+20+(int)(40*Math.sin(radians))+(int)((5*magnitude)*Math.sin(radians-0.1)),
						player.getX()+offsetx+(int)(40*Math.cos(radians))+(int)((5*magnitude)*Math.cos(radians+0.1)),player.getY()+offsety+20+(int)(40*Math.sin(radians))+(int)((5*magnitude)*Math.sin(radians+0.1)),Color.BLACK,player.getColor(),player.getColor());
				shapeRenderer.end();
			}
			batch.begin();
			playerText.setColor(player.getColor()); //drawing health and username using player colour
			playerText.draw(batch, player.getHealth()+"", player.getX()+offsetx-15+4, player.getY()+offsety+52);
			playerText.draw(batch, player.getUsername(), player.getX()+offsetx-15-7, player.getY()+offsety-10);
			batch.draw(player.getPLAYERPIC(), player.getX()+offsetx-15, player.getY()+offsety);
			if(fireballpic!=null){ //pics are not null while attacking is true and that attackpic is set
				batch.draw(fireballpic,fx+offsetx-fireballpic.getWidth()/2,fy+offsety-fireballpic.getHeight()/2);
			}
			if(lavabombpic!=null && fireballpic==null){
				batch.draw(lavabombpic,fx+offsetx-lavabombpic.getWidth()/2,fy+offsety-lavabombpic.getHeight()/2);
			}
			batch.end();
		}
		if(teleportpic!=null && animation==true && player.getTELEPORTING()==true){ //draws teleporting pics if teleport is called and destination is clicked
			batch.begin(); 
			batch.draw(teleportpic,tx-teleportpic.getWidth()/2,ty-teleportpic.getHeight()/2); //draws at newspot and old spot
			batch.draw(teleportpic,oldx+10-teleportpic.getWidth()/2,oldy+20-teleportpic.getHeight()/2);
			batch.end();
		}
	}
	
	public void updateOtherPlayers(){
		for(int i=0;i<players.size();i++){
			if(players.get(i)!=player){ //moves all other players according to their vx and vy.=
				players.get(i).setVY(players.get(i).getVY()+1); //all players will fall if a hole is made
				if(players.get(i).getVY()<0){ //same movement in moveplayer for all players when vx and vy is set
					moveUp(players.get(i),-players.get(i).getVY()); 
				}
				if(players.get(i).getVY()>0){
					moveDown(players.get(i),players.get(i).getVY());
				}
				if(players.get(i).getVX()<0){
					moveLeft(players.get(i),-players.get(i).getVX());
				}
				if(players.get(i).getVX()>0){
					moveRight(players.get(i),players.get(i).getVX());
				}
				players.get(i).setCirc(new Circle(players.get(i).getX(), players.get(i).getY()+20, 15)); //sets their hitcircle to where their moving
			}
			if(players.get(i).getHealth()<=0){ //checking if the players are dead 
				if(players.get(i)==player && animation==false){ //if the current player is the one dying
					players.remove(players.get(i)); //they are removed from the list of players
					numRemovedPlayers+=1; //counter that adds the number of removed players so the list can
					//advance properly
					changeTurn(); //if a player dies that means damage is done which means a spell was used and 
					//the turn is over
				}
				else{
					players.remove(players.get(i)); //if any of the other players are dead
					numRemovedPlayers+=1; //the number of removed players goes up 
				}
			}
		}
	}
	
	public void drawOtherPlayers(){ //drawing the other players
		for(int i=0;i<players.size();i++){
			if(players.get(i)!=player){ //for all the players but the current player
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(players.get(i).getColor()); //a circle behind them is drawn
				shapeRenderer.circle(players.get(i).getX()+offsetx-3, players.get(i).getY()+offsety+18, 15);
				shapeRenderer.end();
				batch.begin();
				batch.draw(players.get(i).getPLAYERPIC(),players.get(i).getX()-15+offsetx,players.get(i).getY()+offsety);
				playerText.setColor(players.get(i).getColor()); //their sprite is drawn, as well as their name and health
				playerText.draw(batch, players.get(i).getHealth()+"", players.get(i).getX()+offsetx-15+4, players.get(i).getY()+offsety+52);
				playerText.draw(batch, players.get(i).getUsername(), players.get(i).getX()+offsetx-15-7, players.get(i).getY()+offsety-10);
				batch.end();
			}
		}
	}
	public void drawTimer(){ // drawing the timer
		if(!endGame){ //it continues if the game is not over
			batch.begin();
			timeText.setColor(player.getColor());
			timeText.draw(batch, "Time:", 302, 471);
			batch.draw(pics.get("time border"), 270, 433);
			if(turnLimit-turnTime>0){ //it only draws when the time is greater than 0 so it doesn't draw negative time
				timeText.draw(batch, (turnLimit-turnTime)+"", 312,452);
			}
			batch.end();
		}
		
	}
	public void drawSlidingSpellMenu(){ //drawing the spell menu transition
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(player.getColor().r,player.getColor().g, player.getColor().b, 0.7f);
		shapeRenderer.rect(30,0+spellMenuY,146,18);
		shapeRenderer.rect(30,-49+spellMenuY, 145, 40);
		shapeRenderer.end();
		batch.begin(); //draws two rectangles the colour of the player with a changing y int so it can slide on screen
		batch.draw(pics.get("spell display"), 5, -44+spellMenuY);
		batch.end();
	}
	public void drawSlidingSpellMenuUp(){ //drawing it while its sliding onto the screen
		spellMenuY+=2; //the y value increases so it slides up
		drawSlidingSpellMenu();
		
	}
	public void drawSlidingSpellMenuDown(){//drawing while its sliding down from the screen
		spellMenuY-=2; //the y value decreases so it slides down
		drawSlidingSpellMenu();
	}
	public void drawSpellMenu(){ //drawing once it stops sliding
		Gdx.gl.glEnable(GL20.GL_BLEND); //the same thing without a changing y, fixed y value
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(player.getColor().r,player.getColor().g, player.getColor().b, 0.7f);
		shapeRenderer.rect(30,0, 145, 40);
		shapeRenderer.setColor(player.getColor());
		shapeRenderer.rect(30,49,146,18);
		shapeRenderer.end();
		batch.begin();
		batch.draw(pics.get("spell display"), 5, 5);
		batch.end();
	}
	
	public void drawExitMenu(){ //drawing the exit menu for the player if esc is pressed
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(0, 0, 0, 0.82f)); //translucent black background
		shapeRenderer.rect(0,0,640,480);
		shapeRenderer.end();
		batch.begin();
		batch.draw(pics.get("game exit menu"), 5, 5);
		batch.draw(pics.get("arcanists pause"), 120,220);
		if(returnToGameRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){ //rects as buttons as there are only 2 
			batch.draw(pics.get("return to game highlight"), returnToGameRect.x-1, returnToGameRect.y);
		}
		if(returnToLobbyRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			batch.draw(pics.get("return to lobby highlight"), returnToLobbyRect.x-1, returnToLobbyRect.y);
		}
		batch.end();
	}

	public void drawSelectedSpells(){ //drawing the spells that are selected by the player
		batch.begin();
		for(int i=0;i<spellButtons.length;i++){ //for all the spell buttons
			if(player.getCurrentSpell().equals(spellButtons[i].getSpellSelection())){ //if the current spell is one of the spells being checked
				batch.draw(spellButtons[i].getBookPic(), 21, 50); //the picture of it selected is drawn above the book
				if(player.getATTACKING()==false){ //if theyre not attacking the text is drawn for that spell
					spellText.draw(batch, player.getCurrentSpell(), 71, 63);
					batch.draw(spellButtons[i].getAbovePic(), guyx-8, guyy+57);
				}
			}
		}
		batch.end();
	}
	
	public void updateSpellHighlights(){ //updating all the spell highlights 
		for(int i=0;i<spellButtons.length;i++){ //checks for all the buttons
			if(spellButtons[i].getCirc().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(Gdx.input.isButtonPressed(Buttons.LEFT) && player.getATTACKING()==false && animation==false){
					if(player.getATTACK().equals(spellButtons[i].getSpellSelection())==false){ //if the attack is not already selected
							player.setATTACK(spellButtons[i].getSpellSelection()); //it is set to the current attack
							spellButtons[i].getSound().play(); //and the sound is played
					}
					if(spellButtons[i].getSpellSelection().equals("Arcane Gate")==false){ //if you choose another spell teleporting is set to false
						player.setTELEPORTING(false);
					}
					if(spellButtons[i].getSpellSelection().equals("Arcane Gate")){ //sets values for arcane gate
						spot = 0;
						teleportingx = player.getX();
						teleportingy = player.getY();
						tx = Gdx.input.getX();
						ty = 480-Gdx.input.getY();
						teleportpic = null;
						player.setTELEPORTING(true);
					}
					player.setCurrentSpell(spellButtons[i].getSpellSelection());  //the current spell is set to the selected spell
					mouseReady = false;
				}
			}
		}
	}
	
	public void drawSpellHighlights(){ //drawing the highlighted spells
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		for(int i=0;i<spellButtons.length;i++){
			if(spellButtons[i].getCirc().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				shapeRenderer.begin(ShapeType.Filled);
				shapeRenderer.setColor(player.getColor()); //player colour rect is drawn overtop text area
				shapeRenderer.rect(65,49,106,16);
				shapeRenderer.end();
				batch.begin(); //the selected highlight is drawn and the text selection is drawn as well
				batch.draw(spellButtons[i].getImage(), spellButtons[i].getOffsetX()-24, spellButtons[i].getOffsetY()-20);
				spellText.draw(batch, spellButtons[i].getSpellSelection(), 71, 63);
				batch.end();
			}
		}
	}
	
	public void updateSpells(){  //updating the spells
		if(!endGame && player.getATTACKING()==false){
			if(spellMenuY>=49){ //if the spell menu is done sliding it updates
				updateSpellHighlights();
			}
		}
	}
	
	public void drawSpells(){ //drawing the spells
		if(!endGame && player.getATTACKING()==false){ //if the game isnt ended and theyre not attacking
			if(spellMenuY<49){ //it slides up if the value is less than 49
				drawSlidingSpellMenuUp();
			}
			else{ //once its greater tha 49 the spell menu draws properly and can be interacted with
				drawSpellMenu();
				drawSelectedSpells();
				drawSpellHighlights();
			}
		}
		if(!endGame && player.getATTACKING()==true && spellMenuY>-25){ //if theyre attacking it keeps sliding down
			drawSlidingSpellMenuDown();
		}
	}
	
	public void updateMiniMapRect(){ //updating the rectangle that is drawn on the minimap rect
		if(player.getATTACKING()==false && player.getTELEPORTING()==false){ //if they're not attacking or teleporting its them moving
			miniMapRect = new Rectangle(513+(int)(player.getX()/ratioX), 6+(int)(player.getY()/ratioY), ratioX, ratioY);
		}
		else if (player.getATTACKING()==true && player.getTELEPORTING()==false){ //if they're attacking it follows their attack
			miniMapRect = new Rectangle(513+(int)(fx/ratioX), 6+(int)(fy/ratioY), ratioX, ratioY);
		}
		else if(player.getTELEPORTING()==true){ //if they're teleporting it follows on the screen
			miniMapRect = new Rectangle(513+(int)(teleportingx/ratioX), 6+(int)(teleportingy/ratioY), ratioX, ratioY);
		}
	}
	
	public void drawMiniMapRect(){ //drawing the minimap rect
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(new Color(1,1,1,1));
		if(player.getX()>0 && player.getX()<1906 && player.getY()>0 && player.getY()<880){
			if(miniMapRect.x<=536 && miniMapRect.y+miniMapRect.height+18>87){ //when it reaches the left corner it doesn't protrude
				shapeRenderer.rect(514,54-13,miniMapRect.width+29,miniMapRect.height+18);
			}
			else if(miniMapRect.x<536){ //if it only reaches the left side it wont protrude
				shapeRenderer.rect(514,miniMapRect.y-13,miniMapRect.width+29,miniMapRect.height+18);
			}
			else if(miniMapRect.x>612){ //if it reaches the right side it wont protrude
				shapeRenderer.rect(613-22,miniMapRect.y-13,miniMapRect.width+29,miniMapRect.height+18);
			}
			else if(miniMapRect.x>536 && miniMapRect.x<612 && miniMapRect.y+miniMapRect.height+18>87){ 
				shapeRenderer.rect(miniMapRect.x-22,54-13,miniMapRect.width+29,miniMapRect.height+18);
			} //if it reaches the top it wont protrude
			else{
				shapeRenderer.rect(miniMapRect.x-22,miniMapRect.y-13,miniMapRect.width+29,miniMapRect.height+18);
			} //otherwise it is drawn normally
		}
		shapeRenderer.end();
	}
	public void drawMiniMapDots(){ //drawing the dots for all the players that are on the minimap
		shapeRenderer.begin(ShapeType.Filled);
		for(int i=0; i<players.size(); i++){ //if each player is in a position to be drawn
			if(players.get(i).getX()>0 && players.get(i).getY()<900){
				shapeRenderer.setColor(players.get(i).getColor()); // a dot of their colour is drawn on the map
				shapeRenderer.circle(513+players.get(i).getX()/ratioX, 5+players.get(i).getY()/ratioY, 3);
			}
		}
		shapeRenderer.end();
	}
	public void drawMiniMap(){ //drawing the minimap
		shapeRenderer.begin(ShapeType.Filled); //gradient is drawn in the back
	    shapeRenderer.rect(513,5,150,73,new Color(179f/255f,198/255f,206/255f,1),new Color(179f/255f,198/255f,206/255f,1),new Color(101/255f,134/255f,151/255f,1),new Color(101/255f,134/255f,151/255f,1));
		shapeRenderer.end();
		batch.begin();
		minimap1 = new Sprite(map1); //the smaller minimap is updated based on the current image of the map and is scaled down
		batch.draw(minimap1, 513,5, 125, 68);
		batch.end();
		drawMiniMapDots(); //draws the dots
		drawMiniMapRect(); //draws the rectangle on top
		batch.begin();
		batch.draw(pics.get("map border"), 509, 5); //draws the border around
		batch.end();
		
	}
	
	public void drawBorder(){ //drawing the border around the screen
		batch.begin();
		batch.draw(pics.get("border"), 0,0);
		batch.end();
	}
	
	public void drawHealth(){ //drawing the health of the players
		for(int i=0;i<players.size();i++){ //for all the players
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
			Player p = players.get(i); //p is the player being referred to
			Rectangle healthRect = new Rectangle(2, 443-(i*30)+2, p.getHealth()/2, 15); //creates a health rectangle
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(Color.BLACK); //draws the healths rectangle along with the outline of the healths
			shapeRenderer.rect(healthRect.x, healthRect.y-1, healthRect.width+1, healthRect.height+1);
			shapeRenderer.rect(1,441-((players.size()-1)*30), 133,(players.size()*30)+3);
			shapeRenderer.rect(1,442-((players.size()-1)*30), 132,(players.size()*30)+1);
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(0,0,0,0.65f);
			shapeRenderer.rect(2,443-(i*30),130,30); //draws a transparent background for the health
			shapeRenderer.end();
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(p.getColor()); //draws the health with the color of the player, gradient
			shapeRenderer.rect(healthRect.x, healthRect.y, healthRect.width, healthRect.height,
			new Color(p.getColor().r-5, p.getColor().g-5, p.getColor().b-5, 1),
			p.getColor(),
			p.getColor(),
			new Color(p.getColor().r-5, p.getColor().g-5, p.getColor().b-5, 1));
			shapeRenderer.end();
			batch.begin(); //draws the username of the player above their corresponding health
			healthText.draw(batch, p.getUsername(), 9,456-(i*30)+ 16);
			batch.end();
		}
	}
	
	public void updateExitMenu(){ //updates the exit menu of the game
		if(returnToGameRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					inExitMenu = false; //they can return back to the game. simply closes the exxit menu
				}
			}
		}
		if(returnToLobbyRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){ //they can return back to the lobby
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					inExitMenu = false;
					resetGameOptions(); //the game options are reset 
					returnToMenuFromGame(); //the players screen is changed
				}
			}
		}
	}
	
	public void updateEndGame(){ //updates the screen when the game has ended
		if(gameReturnToLobby.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					resetGameOptions(); //return to lobby from end game same as return to lobby in exit menu
					returnToMenuFromGame();
				}
			}
		}
	}
	
	public void drawEndGame(){ //drawing the end game screen
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
		shapeRenderer.rect(0,0,640,480); //translucent background is drawn
		shapeRenderer.end();
		shapeRenderer.begin(ShapeType.Filled);
		if(gameSelection.equals("FFA")){ //ffa win
			if(players.size()==1){ //draws the colour of the winning player 
				Color c = players.get(0).getColor();
				shapeRenderer.setColor(c.r, c.g, c.b, 0.8f);
			}
			if(players.size()==0){ //if the game is drawn, no players win so its just black
				shapeRenderer.setColor(0,0,0, 0.8f);
			}
		}
		else if(gameSelection.equals("TEAM")){ //team win
			if(blueCount==0){ //if all the blue players are dead, the red team wins
				shapeRenderer.setColor(new Color(225,0,0,0.8f));
			}
			else{ //if all the red players are dead. the blue team wins
				shapeRenderer.setColor(new Color(0,0,225,0.8f));
			}
		}
		shapeRenderer.ellipse(85, 115, 470, 310);
		shapeRenderer.end();
		batch.begin();
		batch.draw(pics.get("end game"), 640/2-320, 480/2-210);
		batch.draw(pics.get("end game return to lobby"), gameReturnToLobby.x, gameReturnToLobby.y+1);
		//draws text based on who wins
		if(gameSelection.equals("FFA")){
			if(players.size()==1){
				spellText.draw(batch, players.get(0).getUsername() + " wins!", 278, 300);
			}
			if(players.size()==0){
				shapeRenderer.setColor(0,0,0, 0.8f);
				spellText.draw(batch, "All players are dead! Game drawn.", 220, 300);
			}
		}
		if(gameSelection.equals("TEAM")){
			if(blueCount==0){
				spellText.draw(batch, "Red team wins!", 278, 300);
			}
			else{
				spellText.draw(batch, "Blue team wins!", 278, 300);
			}
		}
		if(gameReturnToLobby.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			batch.draw(pics.get("end game return to lobby highlight"), gameReturnToLobby.x, gameReturnToLobby.y+1);
		}
		batch.end();
	}
	public void resetGameOptions(){ //updating the game options when they are reset
		endGame = false;
		players = new ArrayList<Player>();
		currentPlayer = 0;
		//player = players.get(currentPlayer);
		numRemovedPlayers = 0;
		spot = 0;
		waterspot = 0;
		timeSelection = "";
		playerSelection = "";
		gameSelection = "";
		mask1 = new Pixmap(Gdx.files.internal("maps/grassy hills mask.png"));
		map1 = new Texture("maps/grassy hills.png");
		minimap1 = new Sprite(map1);
		
	}
	public void returnToMenuFromGame(){ //changes the screen and stops the music when going from the game to menu
		grassyHillsMusic.stop();
		currentScreen = "multiplayer menu";
		menuMusic.play();
	}
	
	public void createPlayers(){ //creates players and adds them to the array list
		int numPlayers = Integer.parseInt(playerSelection); //converts number of players to an integer
		for(int i=1;i<numPlayers+1;i++){ //starts at 1 so player names start at player 1,2, etc instead of 0
			Random rand = new Random(); //names them player number, puts them at a random spot on the map, chooses from players
			players.add(new Player(("Player " + i), (100+rand.nextInt(1720)), 1000, charPics[rand.nextInt(2)]));
			if(gameSelection.equals("TEAM")){ //sets the colour of the player based on the game mode
				if(numPlayers==4){ //selects player colour and team colour for 4 players
					players.get(i-1).setColor(TEAMCOLOURS4[i-1]);
					if((i-1)<=1){
						players.get(i-1).setTeamColor("RED");
					}
					else{
						players.get(i-1).setTeamColor("BLUE");
					}
				}
				else if(numPlayers==6){ //selects team colour and player colour for 6 players
					players.get(i-1).setColor(TEAMCOLOURS6[i-1]);
					if((i-1)<=2){
						players.get(i-1).setTeamColor("RED");
					}
					else{
						players.get(i-1).setTeamColor("BLUE");
					}
				}
			}
			else if(gameSelection.equals("FFA")){ //no team colour for FFA, just a random colour
				players.get(i-1).setColor(RANDOMCOLOURS[i-1]);
			}
		}
		player = players.get(0); //sets the first player as the current player
		guyx = 320-15; //sets their position and the offset
		guyy = 150;
		offsetx = 320 - player.getX();
		offsety = 150-player.getY();
	}
	
	public void changeTurn(){ //changes the turn
		int numCurrentPlayers = Integer.parseInt(playerSelection)-numRemovedPlayers;
		//the current spot in the list is the number of players - the number of players dead
		currentPlayer+=1; //spot advanced, but if the spot is outside of the index it is set to the first player
		if(currentPlayer>=numCurrentPlayers || players.size()==currentPlayer){
			currentPlayer = 0;
		}
		player.setCurrentSpell(""); //when their turn ends, their attack is set to nothing and their current spell
		player.setATTACK("");
		spellMenuY = 0;  //the menu sliding value is set to 0 so it can slide for the next players turn
		player = players.get(currentPlayer);//the player is changed and advanced forward
		if(!endGame){ //if the game is not over the time is reset for the next players turn 
			time1970 = System.currentTimeMillis();
		}
	}
	
	public void updateMenuButtons(){ //the menu buttons are updated from all the button sections
		updatePlayerButtons();
		updateTimeButtons();
		updateGameModeButtons();
	}
	
	public void drawMenuButtons(){ //the menu buttons are drawn from all the sections
		drawPlayerButtons();
		drawTimeButtons();
		drawGameModeButtons();
		drawSelectedButtons();
	}
	
	public void updatePlayerButtons(){ //the player button section is updated
		for(int i=0;i<playerButtons.length;i++){
			if(playerButtons[i].getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){ //if the button rect is being hovered over
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						if(i==0 || i==1 || i==3){ // if its pressed and it isn't an odd number or 2 people
							if(gameSelection!="TEAM"){
								playerSelection = playerButtons[i].getSelection(); //the game mode is set to team
							}
						}
						else{
							playerSelection = playerButtons[i].getSelection(); //otherwise its set to ffa
						}
						mouseReady = false;
					}
				}
			}
		}
	}
	
	public void drawPlayerButtons(){ //drawing player button highlights
		for(int i=0;i<playerButtons.length;i++){ //
			if(playerButtons[i].getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(playerButtons[i].getImage(), playerButtons[i].getRect().x+playerButtons[i].getOffsetX(),playerButtons[i].getRect().y+playerButtons[i].getOffsetY());
			}
		}
	}
	
	public void updateTimeButtons(){ //the time button section is updated
		for(int i=0;i<timeButtons.length;i++){
			if(timeButtons[i].getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){ //the time selection is set if its pressed
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						timeSelection = timeButtons[i].getSelection();
						mouseReady = false;
					}
				}
			}
		}
	}
	
	public void drawTimeButtons(){ //drawing the time button highlights
		for(int i=0;i<timeButtons.length;i++){
			if(timeButtons[i].getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(timeButtons[i].getImage(), timeButtons[i].getRect().x+timeButtons[i].getOffsetX(),timeButtons[i].getRect().y+timeButtons[i].getOffsetY());
			}
		}
	}
	
	public void updateGameModeButtons(){ //only two buttons so don't need a loop or buttons for game modes to update
		if(gameFFA.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					gameSelection = gameFFA.getSelection();
				}
			}
		}
		if(gameTeam.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					if(playerSelection.equals("4") || playerSelection.equals("6")){
						gameSelection = gameTeam.getSelection();
					}
					mouseReady = false;
				}
			}
		}
	}
	
	public void drawGameModeButtons(){ //drawing the game button highlights
		if(gameFFA.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			batch.draw(gameFFA.getImage(), gameFFA.getRect().x+gameFFA.getOffsetX(), gameFFA.getRect().y+gameFFA.getOffsetY());
		}
		if(gameTeam.getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
			batch.draw(gameTeam.getImage(), gameTeam.getRect().x+gameTeam.getOffsetX(), gameTeam.getRect().y+gameTeam.getOffsetY());
		}
	}
	
	public void drawSelectedButtons(){ //drawing the selected buttons
		//each button section is looped except for game modes and the selected image is drawn overtop
		for(int i=0;i<playerButtons.length;i++){
			if(playerSelection.equals(playerButtons[i].getSelection())){
				batch.draw(playerButtons[i].getSelectedImage(),playerButtons[i].getRect().x+playerButtons[i].getOffsetX(),playerButtons[i].getRect().y+playerButtons[i].getOffsetY());
			}
		}
		for(int i=0;i<timeButtons.length;i++){
			if(timeSelection.equals(timeButtons[i].getSelection())){
				batch.draw(timeButtons[i].getSelectedImage(),timeButtons[i].getRect().x+timeButtons[i].getOffsetX(),timeButtons[i].getRect().y+timeButtons[i].getOffsetY());
			}
		}
		if(gameSelection.equals("FFA")){batch.draw(pics.get("selectFFA"), gameFFA.getRect().x, gameFFA.getRect().y);}
		if(gameSelection.equals("TEAM")){batch.draw(pics.get("selectTeam"), gameTeam.getRect().x-1, gameTeam.getRect().y);}
	}
	
	@Override
	public void create() {
		Gdx.input.setInputProcessor(this);
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();
		MAGENTA = cnum(255,0,255,255);
		CLEAR = cnum(255,255,255,0);
		WHITE = cnum(210,210,210,255);
		BLACK = cnum(0,0,0,255);
		GRAY = cnum(128,128,128,255);
		LSTAND = 0;
		RSTAND = 1;
		LJUMP = 2;
		RJUMP = 3;
		LWALK = 4;
		RWALK = 5;
		bpics = new Texture[6][1];
		bpics[0] = makeMove("characters","male blue","stand_left_",0,0);
		bpics[1] = makeMove("characters","male blue","stand_right_",0,0);
		bpics[2] = makeMove("characters","male blue","jump_left_",0,1);
		bpics[3] = makeMove("characters","male blue","jump_right_",0,1);
		bpics[4] = makeMove("characters","male blue","walk_left_",0,3);
		bpics[5] = makeMove("characters","male blue","walk_right_",0,3);
		ppics = new Texture[6][1];
		ppics[0] = makeMove("characters","male purple","stand_left_",0,0);
		ppics[1] = makeMove("characters","male purple","stand_right_",0,0);
		ppics[2] = makeMove("characters","male purple","jump_left_",0,1);
		ppics[3] = makeMove("characters","male purple","jump_right_",0,1);
		ppics[4] = makeMove("characters","male purple","walk_left_",0,3);
		ppics[5] = makeMove("characters","male purple","walk_right_",0,3);
		fireballpics = new Texture[5];
		fireballpics = makeMove("attacks","fire ball","",1,5);
		lavabombpics = new Texture[5];
		lavabombpics = makeMove("attacks","lava bomb","",1,5);
		teleportpics = new Texture[5];
		teleportpics = makeMove("attacks","arcane gate","",1,5);
		spot = 0;
		waterspot = 0;
		charPics = new Texture[2][6][1];
		charPics[0] = bpics;
		charPics[1] = ppics;
		waterpics = new Texture[6];
		waterpics = makeMove("maps","water","water",1,6);
		mask1 = new Pixmap(Gdx.files.internal("maps/grassy hills mask.png"));
		map1 = new Texture("maps/grassy hills.png");
		minimap1 = new Sprite(map1);
		playerText = new BitmapFont();
		//timeText = new BitmapFont(Gdx.files.internal("font.fnt"), true);
		timeText = new BitmapFont();
		spellText = new BitmapFont();
		healthText = new BitmapFont();
		healthText.getData().setScale(0.75f, 0.75f);
		
		RANDOMCOLOURS = new Color[]{new Color(0,191/255f,1,1), new Color(68/255f,227/255f,79/255f,1), 
									new Color(1,119/255f,0,1), new Color(230/255f,16/255f,16/255f,1),
									new Color(0,1,221/255f,1), new Color(162/255f,0,1,1)};
		TEAMCOLOURS4 = new Color[]{new Color(237/255f,17/255f,17/255f,1), new Color(181/255f,9/255f,9/255f,1), 
								   new Color(21/255f,75/255f,237/255f,1), new Color(91/255f,129/255f,245/255f,1)};
		TEAMCOLOURS6 = new Color[]{new Color(237/255f,17/255f,17/255f,1), new Color(181/255f,9/255f,9/255f,1), new Color(245/255f,45/255f,0,1),
				new Color(21/255f,75/255f,237/255f,1), new Color(91/255f,129/255f,245/255f,1), new Color(70/255f,147/255f,224/255f,1)};
		
		playerText.setColor(1,1,1,1);
		timeText.setColor(1,1,1,1);
		spellText.setColor(1,1,1,1);
		healthText.setColor(1,1,1,1);
		
		players = new ArrayList<Player>();
		playerpic = new Texture("characters/male blue/stand_left_0.png");
		//-------------------------------LOAD TEXTURES-------------------------------------------------
		String[]files = {"maps/grassy hills.png","maps/grassy hills background.png","maps/grassy hills mask.png",
		"screens/menu.png","screens/menu 1.png","screens/menu 2.png","screens/menu 3.png","screens/menu 4.png",
		"screens/menu 5.png","screens/menu 6.png","screens/menu 7.png","screens/menu 8.png","screens/menu 9.png",
		"screens/multiplayer menu.png", "screens/buttons/lobby/create unrated game highlight.png",
		"screens/buttons/lobby/return to main menu highlight.png",
		"screens/create menu.png","screens/buttons/game/return to lobby highlight.png",
		//createStart = new Texture("screens/buttons/game/.png");
		"screens/buttons/game/start highlight.png","screens/buttons/game/invite highlight.png",
		"screens/invite.png", "screens/buttons/game/close highlight.png",
		"screens/friends menu.png","screens/ignore menu.png",
		"screens/buttons/player menu/lobby not selected.png","screens/buttons/player menu/lobby highlight.png",
		"screens/buttons/player menu/game highlight.png","screens/buttons/player menu/friends highlight.png",
		"screens/buttons/player menu/ignore highlight.png","screens/buttons/player menu/add friend highlight.png",
		"screens/buttons/player menu/add name highlight.png","screens/buttons/player menu/remove friend highlight.png",
		"screens/buttons/player menu/remove name highlight.png","screens/buttons/game/players/2 highlight.png",
		"screens/buttons/game/players/3 highlight.png","screens/buttons/game/players/4 highlight.png",
		"screens/buttons/game/players/5 highlight.png","screens/buttons/game/players/6 highlight.png",
		"screens/buttons/game/times/10 highlight.png","screens/buttons/game/times/20 highlight.png",
		"screens/buttons/game/times/30 highlight.png","screens/buttons/game/times/45 highlight.png",
		"screens/buttons/game/times/60 highlight.png","screens/buttons/game/times/90 highlight.png",
		"screens/buttons/game/times/120 highlight.png","screens/buttons/game/modes/FFA highlight.png",
		"screens/buttons/game/modes/team highlight.png","screens/buttons/game/players/2 select.png",
		"screens/buttons/game/players/3 select.png","screens/buttons/game/players/4 select.png",
		"screens/buttons/game/players/5 select.png","screens/buttons/game/players/6 select.png",
		"screens/buttons/game/times/10 select.png","screens/buttons/game/times/20 select.png",
		"screens/buttons/game/times/30 select.png","screens/buttons/game/times/45 select.png",
		"screens/buttons/game/times/60 select.png","screens/buttons/game/times/90 select.png",
		"screens/buttons/game/times/120 select.png",
		"screens/buttons/game/modes/FFA select.png","screens/buttons/game/modes/team select.png",
		"screens/buttons/player menu/game not selected.png","screens/borders/border.png","screens/game exit menu.png",
		"screens/borders/time border.png","screens/borders/map border.png","screens/lobby menu.png","screens/game menu.png",
		"attacks/spell display.png", 
		"attacks/arcane gate/highlight.png", "attacks/arcane gate/book.png", "attacks/arcane gate/above.png",
		"attacks/fire ball/highlight.png", "attacks/fire ball/book.png", "attacks/fire ball/above.png",
		"attacks/lava bomb/highlight.png", "attacks/lava bomb/book.png", "attacks/lava bomb/above.png",
		"screens/arcanists pause.png", "screens/return to game highlight.png", "screens/return to lobby highlight.png",
		"screens/end game.png", "screens/buttons/return to lobby.png", "screens/buttons/return to lobby selected.png"};
		
		String[]picKeys = {"map1","back1","mask1","menu","menuHighlight1","menuHighlight2","menuHighlight3",
		"menuHighlight4","menuHighlight5","menuHighlight6","menuHighlight7","menuHighlight8", "menuHighlight9",
		"multiplayerMenu","multiplayerCreate","multiplayerBack","createMenu","createBack",
		//"createStart",
		"createStartHighlight","createInviteHighlight","inviteMenu", "highlightClose", "friendsMenu", "ignoreMenu",
		"lobbyButton", "highlightLobby","highlightGame", "highlightFriends", "highlightIgnore", "highlightAddFriend",
		"highlightAddName", "highlightRemoveFriend", "highlightRemoveName","highlight2", "highlight3", "highlight4",
		"highlight5", "highlight6","highlight10", "highlight20", "highlight30", "highlight45", "highlight60",
		"highlight90","highlight120","highlightFFA", "highlightTeam","select2", "select3", "select4", "select5","select6",
		"select10", "select20", "select30", "select45","select60","select90","select120","selectFFA","selectTeam",
		"gameNotSelected","border","game exit menu","time border","map border","lobby menu","game menu", "spell display",
		"arcane gate highlight", "arcane gate book", "arcane gate above", 
		"fire ball highlight", "fire ball book", "fire ball above",
		"lava bomb highlight", "lava bomb book", "lava bomb above", "arcanists pause", "return to game highlight", 
		"return to lobby highlight", "end game", "end game return to lobby", "end game return to lobby highlight"};	
		
		//all the file sources and keys are added to a hash map of pictures for easy access and adding
		for(int i=0; i<files.length; i++){
			pics.put(picKeys[i], new Texture(files[i]));
		}
		
		String[]soundFiles = new String[]{"sound effects/arcane gate.mp3","sound effects/cast arcane gate.mp3",
				"sound effects/cast fire ball.mp3","sound effects/cast lava bomb.mp3",
				"sound effects/fire ball.mp3","sound effects/jump.mp3",
				"sound effects/land.mp3","sound effects/lava bomb hit.mp3",
				"sound effects/lava bomb.mp3","sound effects/leap.mp3",
				"sound effects/walk.mp3", "sound effects/water.mp3"};
		String[]soundKeys = {"arcane gate", "cast arcane gate", 
				"cast fire ball", "cast lava bomb", 
				"fire ball", "jump",
				"land", "lava bomb hit", 
				"lava bomb", "leap", 
				"walk", "water"};
		
		//same hashmap for sound same way for pictures
		for(int i=0; i<soundFiles.length; i++){
			sounds.put(soundKeys[i], Gdx.audio.newSound(Gdx.files.internal(soundFiles[i])));
		}
		//---------------------------------------------------------------------------------------
		//-------------------------- BUTTONS ----------------------------
		//spells------------------------------------------------------------
		arcaneGateButton = new Button(pics.get("arcane gate highlight"),new Circle(90,21,14),90,21,"Arcane Gate",pics.get("arcane gate book"),pics.get("arcane gate above"),sounds.get("cast arcane gate"));
		fireBallButton = new Button(pics.get("fire ball highlight"),new Circle(123,21,14),123,21,"Fire Ball",pics.get("fire ball book"),pics.get("fire ball above"),sounds.get("cast fire ball"));
		lavaBombButton = new Button(pics.get("lava bomb highlight"),new Circle(156,21,14),156,21,"Lava Bomb",pics.get("lava bomb book"),pics.get("lava bomb above"),sounds.get("cast lava bomb"));
		spellButtons = new Button[]{arcaneGateButton,fireBallButton,lavaBombButton};
		//menu -------------------------------------------------------------
		highlight1 = new Button(pics.get("menuHighlight1"),new Rectangle(354,305,120,27),0,0,"main menu");
		highlight2 = new Button(pics.get("menuHighlight2"),new Rectangle(333,275,160,30),0,0,"multiplayer menu");;
		highlight3 = new Button(pics.get("menuHighlight3"),new Rectangle(278,245,270,30),0,0,"main menu");;
		highlight4 = new Button(pics.get("menuHighlight4"),new Rectangle(328,215,173,30),0,0,"main menu");;
		highlight5 = new Button(pics.get("menuHighlight5"),new Rectangle(326,185,176,30),0,0,"main menu");;
		highlight6 = new Button(pics.get("menuHighlight6"),new Rectangle(352,158,124,27),0,0,"main menu");;
		highlight7 = new Button(pics.get("menuHighlight7"),new Rectangle(323,129,180,29),0,0,"main menu");;
		highlight8 = new Button(pics.get("menuHighlight8"),new Rectangle(285,99,255,30),0,0,"main menu");;
		highlight9 = new Button(pics.get("menuHighlight9"),new Rectangle(382,69,60,30),0,0,"main menu");;
		//game options -------------------------------------------------------------------------------------------------
		//select # of players -----------------------------
		players2 = new Button(pics.get("highlight2"),new Rectangle(313,370,62,31),-1,1,"2",pics.get("select2"));
		players3 = new Button(pics.get("highlight3"),new Rectangle(377,370,63,31),0,1,"3",pics.get("select3"));
		players4 = new Button(pics.get("highlight4"),new Rectangle(442,370,62,31),0,1,"4",pics.get("select4"));
		players5 = new Button(pics.get("highlight5"),new Rectangle(506,370,65,31),1,1,"5",pics.get("select5"));
		players6 = new Button(pics.get("highlight6"),new Rectangle(573,370,63,31),-1,1,"6",pics.get("select6"));
		//select turn time limit -------------------------------------------------------------
		time10 = new Button(pics.get("highlight10"),new Rectangle(591,339,43,27),-1,-1,"10",pics.get("select10"));
		time20 = new Button(pics.get("highlight20"),new Rectangle(545,339,42,27),-1,-1,"20",pics.get("select20"));
		time30 = new Button(pics.get("highlight30"),new Rectangle(498,339,43,27),-1,-1,"30",pics.get("select30"));
		time45 = new Button(pics.get("highlight45"),new Rectangle(452,339,42,27),-1,-1,"45",pics.get("select45"));
		time60 = new Button(pics.get("highlight60"),new Rectangle(406,339,42,27),-2,-1,"60",pics.get("select60"));
		time90 = new Button(pics.get("highlight90"),new Rectangle(359,339,43,27),-1,-1,"90",pics.get("select90"));
		time120 = new Button(pics.get("highlight120"),new Rectangle(313,339,42,27),-1,-1,"120",pics.get("select120"));
		//select game mode --------------------------------------------------------------------
		gameFFA = new Button(pics.get("highlightFFA"),new Rectangle(312,305,160,29),0,0,"FFA",pics.get("selectFFA"));
		gameTeam = new Button(pics.get("highlightTeam"),new Rectangle(475,305,160,29),-1,0,"TEAM",pics.get("selectTeam"));
		//arrays -----------------------------------------------------------------------------------------------------------------------
		menuButtons = new Button[]{highlight1,highlight2,highlight3,highlight4,highlight5,highlight6,highlight7,highlight8,highlight9};
		playerButtons = new Button[]{players2,players3,players4,players5,players6};
		timeButtons = new Button[]{time10,time20,time30,time45,time60,time90,time120};
		//player menu -------------------------------------------------------------------------------------------------------------
		lobbyButton = new Button(pics.get("highlightLobby"),new Rectangle(1,365,66,25),-1,1,new boolean[]{inLobbyMenu,inFriendsMenu,inIgnoreMenu});
		gameButton = new Button(pics.get("highlightGame"),new Rectangle(1,365,66,25),-1,1,new boolean[]{inGameMenu,inFriendsMenu,inIgnoreMenu,inInviteMenu});
		friendsButton = new Button(pics.get("highlightFriends"),new Rectangle(69,365,64,25),-3,1,new boolean[]{inGameMenu,inFriendsMenu,inIgnoreMenu,inInviteMenu});
		ignoreButton = new Button(pics.get("highlightIgnore"),new Rectangle(135,365,64,25),-3,1,new boolean[]{inGameMenu,inFriendsMenu,inIgnoreMenu,inInviteMenu});
		addButton1 = new Button(pics.get("highlightAddFriend"),new Rectangle(6,172,92,17),-1,-1,new boolean[]{});
		addButton2 = new Button(pics.get("highlightAddName"),new Rectangle(6,172,92,17),-1,-1,new boolean[]{});
		removeButton1 = new Button(pics.get("highlightRemoveFriend"),new Rectangle(99,172,92,17),1,-1,new boolean[]{});
		removeButton2 = new Button(pics.get("highlightRemoveName"),new Rectangle(99,172,92,17),1,-1,new boolean[]{});
		//------------------------------------- LOAD MUSIC --------------------------------------
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu soundtrack.mp3"));
		grassyHillsMusic = Gdx.audio.newMusic(Gdx.files.internal("music/grassy hills soundtrack.mp3"));
	}//------------------------------------------------------------------------------------------------

	public void update(String screen){ //the update method is called in render
		if(screen.equals("game")){     //the String flag currentScreen decides what
			updateGame();              //will be rendered. each section of the game
		} 							   //has a different update method
		if(screen.equals("main menu")){
			updateMenu();
		}
		if(screen.equals("multiplayer menu")){
			updateMultiplayerMenu();
		}
		if(screen.equals("create game")){
			updateCreateGame();
		}
	}
	
	public void updateGame(){
		grassyHillsMusic.play();
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)){
			mouseReady = true;
		}
		checkGameEnd();
		checkExitMenu();
		checkSkipTurn();
		updatePlayerArrow();
		updateTimer();
		updateOffset();
		updatePLAYERPIC();
		updateOtherPlayers();
		updateSpells();
		updateMiniMapRect();
		if(inExitMenu){
			updateExitMenu();
		}
		if(endGame){
			updateEndGame();
		}
	}
	
	public void drawGame(){
		drawMap();
		drawOtherPlayers();
		drawPlayer();
		drawTeleport();
		drawSpells();
		drawMiniMap();
		drawHealth();
		drawTimer();
		if(inExitMenu){
			drawExitMenu();
		}
		if(endGame){
			drawEndGame();
		}
		drawBorder();
	}
	
	public void updateLogIn(){ //we didnt have multiplayer so its ok
		/*Scanner kb = new Scanner(System.in);
		TextField userinput = new TextField("",new Skin());
		if(Gdx.input.isKeyPressed(Keys.ANY_KEY)){
			userinput.appendText(""+Keys.ANY_KEY);
		}
		batch.begin();
		userinput.draw(batch, 1);
		String e = kb.nextLine();
		playerUsername+=e;
		char c = kb.next().charAt(0);
		String let = Character.toString(c);
		playerUsername+=let;
		char a= (char)br.read();*/
		usernameCreated = true;
		//batch.end();
	}
	public void updateMenu(){
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)){
			mouseReady = true;
		}
		menuMusic.setVolume(0.9f);
		menuMusic.play();
		//updates the menu selection based on the mouse input
		for(int i=0;i<menuButtons.length;i++){
			if(menuButtons[i].getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						mouseReady = false;
						currentScreen = menuButtons[i].getScreen();
					}
				}
			}
		}	
	}
	
	//draws the menu based on player input
	public void drawMenu(){
		batch.begin();
		batch.draw(pics.get("menu"), 0,0);
		for(int i=0;i<menuButtons.length;i++){
			if(menuButtons[i].getRect().contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(menuButtons[i].getImage(),menuButtons[i].getOffsetX(),menuButtons[i].getOffsetY());
			}
		}	
		batch.end();
	}
	
	public void updateMultiplayerMenu(){ //updates player menu
		Rectangle backRect = new Rectangle(0,123,200,40);
		Rectangle createGameRect = new Rectangle(422,123,220,40);
		
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)){
			mouseReady = true;
		}
		
		if(backRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					currentScreen = "main menu"; //goes back to menu screen if pressed
					inGameMenu = false;
				}
			}
		}
		if(createGameRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			if(mouseReady){ //if a game is created the screen is changed and the game mini menu is displayed
				if(Gdx.input.isButtonPressed(Buttons.LEFT)){
					mouseReady = false;
					currentScreen = "create game";
					inGameMenu = true;
					inFriendsMenu = false;
					inIgnoreMenu = false;
					inLobbyMenu = false;
				}
			}
		}
	}
	
	public void drawMultiplayerMenu(){ //draws the multiplayer lobby
		Rectangle backRect = new Rectangle(0,123,200,40);
		Rectangle createGameRect = new Rectangle(422,123,220,40);
		
		batch.begin();
		batch.draw(pics.get("multiplayerMenu"), 0,0);
		drawFriendsandIgnoreMenu("lobby");
		//highlights
		if(backRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			batch.draw(pics.get("multiplayerBack"), backRect.x, backRect.y+1);
		}
		if(createGameRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
			batch.draw(pics.get("multiplayerCreate"), createGameRect.x, createGameRect.y+1);
		}
		batch.end();
	}
	
	public void updateCreateGame(){
		if(!Gdx.input.isButtonPressed(Buttons.LEFT)){ //mouse button up flag for single clicks
			mouseReady = true;
		}
		updateFriendsandIgnoreMenu("game");

		if(inInviteMenu==false){
			//----------------------- START BUTTON ------------------------------------------
			if(playRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						if(timeSelection!="" && playerSelection!="" && gameSelection!=""){
							mouseReady = false;
							createPlayers();
							currentScreen = "game";
							menuMusic.stop();
							time1970 = System.currentTimeMillis();
						}
					}
				}//----------------------------------------------------------------
			}//----------------------- RETURN TO LOBBY ----------------------------
			if(backRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
							mouseReady = false;
							gameSelection = "";
							timeSelection = "";
							playerSelection = "";
							inFriendsMenu = false;
							inIgnoreMenu = false;
							inLobbyMenu = true;
							currentScreen = "multiplayer menu";
					}
				}//------------------------------------------------------------------------
			}//-------------------------- INVITE PLAYERS ----------------------------------
			if(inviteRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
							mouseReady = false;
							inInviteMenu = true;
					}
				}
			}
			updateMenuButtons();
		}
		//-------------------------------- INVITE PLAYERS OVERLAY --------------------------------------
		if(inInviteMenu){
			if(closeRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				if(mouseReady){
					if(Gdx.input.isButtonPressed(Buttons.LEFT)){
						mouseReady = false;
						inInviteMenu = false;
					}
				}
			}
		}
	}
	
	public void drawCreateGame(){
		batch.begin();
		batch.draw(pics.get("createMenu"),0,0);
		//----------------------------------- PLAYER MENU --------------------------------------
		drawFriendsandIgnoreMenu("game");
		//----------------------------------------------------------------------------------

		if(inInviteMenu==false){
			//----------------------- START BUTTON ------------------------------------------
			if(playRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(pics.get("createStartHighlight"), playRect.x-1, playRect.y+1);
			}//----------------------- RETURN TO LOBBY ----------------------------
			if(backRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(pics.get("createBack"), backRect.x, backRect.y+1);
			}//-------------------------- INVITE PLAYERS ----------------------------------
			if(inviteRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(pics.get("createInviteHighlight"), inviteRect.x, inviteRect.y+1);
			}//----------------------------------------------------------------------------
			drawMenuButtons();
		}
		//-------------------------------- INVITE PLAYERS OVERLAY --------------------------------------
		if(inInviteMenu){
			batch.draw(pics.get("inviteMenu"), 202, 140);
			if(closeRect.contains(Gdx.input.getX(),480-Gdx.input.getY())){
				batch.draw(pics.get("highlightClose"), closeRect.x+1, closeRect.y);
			}
		}
		//----------------------------------------------------------------------------------------------
		batch.end();
	}
	
	@Override
	public void render () {
		update(currentScreen);
		if(currentScreen.equals("game")){     
			drawGame();
		}
		if(currentScreen.equals("main menu")){
			drawMenu();
		}
		if(currentScreen.equals("multiplayer menu")){
			drawMultiplayerMenu();
		}
		if(currentScreen.equals("create game")){
			drawCreateGame();
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
