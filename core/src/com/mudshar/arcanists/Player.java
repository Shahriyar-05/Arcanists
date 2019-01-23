package com.mudshar.arcanists;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;

public class Player{
	private int x,y,vx,vy,health,move,newMove;
	private double frame;
	private boolean ONGROUND, TELEPORTING, LEFT, JUMPING, ATTACKING, MOVING, LEAPING, LAND;
	private String username, attack, currentSpell, teamColor;
	private Texture playerpic;
	private Texture[][]pics;
	private Color colour;
	private Circle boundingCirc;
	
	public Player(String name, int X, int Y, Texture[][]Pics){
		x = X;
		y = Y;
		vx = 0;
		vy = 0;
		ONGROUND = true;
		health = 250;
		username = name;
		TELEPORTING = false;
		LEFT = false;
		move = 0;
		frame = 0;
		newMove = -1;
		JUMPING = false;
		attack = "";
		currentSpell = "";
		MOVING = false;
		ATTACKING = false;
		playerpic = new Texture("characters/male blue/stand_left_0.png");
		pics = Pics;
		LEAPING = false;
		LAND = true;
		boundingCirc = new Circle(X,Y,15);
		teamColor = "";

	}
	public void setX(int newX){x = newX;}
	public void setY(int newY){y = newY;}
	public void setVX(int newVX){vx = newVX;}
	public void setVY(int newVY){vy = newVY;}
	public void setONGROUND(boolean newONGROUND){ONGROUND = newONGROUND;}
	public void setHealth(int newHealth){health = newHealth;}
	public void setColor(Color newColour){colour = newColour;}
	public void setTELEPORTING(boolean newTELEPORTING){TELEPORTING = newTELEPORTING;}
	public void setLEFT(boolean newLEFT){LEFT = newLEFT;}
	public void setMOVE(int newMOVE){move = newMOVE;}
	public void setFRAME(double newFRAME){frame = newFRAME;}
	public void setNEWMOVE(int newMOVE){newMove = newMOVE;}
	public void setJUMPING(boolean newJUMPING){JUMPING = newJUMPING;}
	public void setATTACK(String newATTACK){attack = newATTACK;}
	public void setATTACKING(boolean newATTACKING){ATTACKING = newATTACKING;}
	public void setPLAYERPIC(Texture newPIC){playerpic = newPIC;}
	public void setCurrentSpell(String newSpell){currentSpell = newSpell;}
	public void setMOVING(boolean newMOVING){MOVING = newMOVING;}
	public void setLEAPING(boolean newLEAPING){LEAPING = newLEAPING;}
	public void setLAND(boolean newLAND){LAND = newLAND;}
	public void setCirc(Circle newCirc){boundingCirc = newCirc;}
	public void setTeamColor(String newColor){teamColor = newColor;}

	public int getX(){return x;}
	public int getY(){return y;}
	public int getVX(){return vx;}
	public int getVY(){return vy;}
	public boolean getONGROUND(){return ONGROUND;}
	public int getHealth(){return health;}
	public String getUsername(){return username;}
	public Color getColor(){return colour;}
	public boolean getTELEPORTING(){return TELEPORTING;}
	public boolean getLEFT(){return LEFT;}
	public int getMOVE(){return move;}
	public double getFRAME(){return frame;}
	public int getNEWMOVE(){return newMove;}
	public boolean getJUMPING(){return JUMPING;}
	public String getATTACK(){return attack;}
	public boolean getATTACKING(){return ATTACKING;}
	public Texture getPLAYERPIC(){return playerpic;}
	public String getCurrentSpell(){return currentSpell;}
	public boolean getMOVING(){return MOVING;}
	public Texture[][] getPics(){return pics;}
	public boolean getLEAPING(){return LEAPING;}
	public boolean getLAND(){return LAND;}
	public Circle getCirc(){return boundingCirc;}
	public String getTeamColor(){return teamColor;}
}