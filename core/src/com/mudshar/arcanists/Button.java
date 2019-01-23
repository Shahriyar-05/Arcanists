package com.mudshar.arcanists;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class Button{
	private Texture image, selectedImage, bookPic, abovePic;
	private Rectangle bRect;
	private int offsetX, offsetY;
	private boolean[]flags;
	private String screen, selection, spellSelection;
	private Circle bCirc;
	private Sound sound;
	
	//main menu buttons
	public Button(Texture image, Rectangle bRect, int offsetX, int offsetY, String screen){
		this.image = image;
		this.bRect = bRect;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.screen = screen;
	}
	
	//many buttons have boolean flags that need to be changed instead of one string flag
	public Button(Texture image, Rectangle bRect, int offsetX, int offsetY, boolean[]flags){
		this.image = image;
		this.bRect = bRect;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.flags = flags;
	}
	
	//create game options
	//don't have boolean flags, but they have a selection and a corresponding image that shows the selection
	public Button(Texture image, Rectangle bRect, int offsetX, int offsetY, String selection, Texture selectedImage){
		this.image = image;
		this.bRect = bRect;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.selection = selection;
		this.selectedImage = selectedImage;
	}
	
	//spell buttons
	public Button(Texture image, Circle bCirc, int offsetX, int offsetY, String spellSelection, Texture bookPic, Texture abovePic, Sound sound){
		this.image = image;
		this.bCirc = bCirc;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.spellSelection = spellSelection;
		this.bookPic = bookPic;
		this.abovePic = abovePic;
		this.sound = sound;
	}
	
	public Texture getImage(){return image;}
	public Rectangle getRect(){return bRect;}
	public int getOffsetX(){return offsetX;}
	public int getOffsetY(){return offsetY;}
	public boolean[] getFlags(){return flags;}
	public String getScreen(){return screen;}
	public String getSelection(){return selection;}
	public String getSpellSelection(){return spellSelection;}
	public Texture getSelectedImage(){return selectedImage;}
	public Circle getCirc(){return bCirc;}
	public Texture getBookPic(){return bookPic;}
	public Texture getAbovePic(){return abovePic;}
	public Sound getSound(){return sound;}
}
