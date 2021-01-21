package org.phypo.Jixmu;


import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer; 


//**********************************************************
public class Viewer extends BorderPane {

	Stage       cStage  = null;
	Scene       cScene  = null;
	MediaView   cView   = null; 
	Pane        cPane   = null; 
	Player      cPlayer = null;

	public Viewer( Player iPlayer) {

		cPlayer = iPlayer;

		cStage = new Stage();
		cScene = new Scene( this, 720, 535, Color.BLACK);  
		cStage.setScene(cScene);
		

		cPane = new Pane(); 		
		setCenter(cPane); 
	}

	//--------------------------------------	

	public void newMedia( Media iMedia, MediaPlayer iMP ) {
		
		//cScene = new Scene( this, iMedia.getWidth(), iMedia.getHeight(), Color.BLACK);  
	//	cScene = new Scene( this, 640, 480, Color.BLACK);  
	//	cStage.setScene(cScene);

		cPane.setPrefWidth(800); 
		cPane.setPrefHeight(600); 
		cPane.setMinWidth(160); 
		cPane.setMinHeight(120); 
		
		cView = new MediaView( iMP ); 		
		cPane = new Pane(); 		
		cPane.getChildren().add(cView);
		setCenter(cPane);
	}
	//--------------------------------------	
	public void show(boolean cFlagView ){
		if( cFlagView ) 
			cStage.show();
		else
			cStage.hide();
	}
}
//**********************************************************
