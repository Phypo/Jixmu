package org.phypo.Jixmu;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import javafx.event.ActionEvent; 
import javafx.event.EventHandler; 
import javafx.geometry.Pos; 
import javafx.scene.media.MediaPlayer; 
import javafx.scene.media.MediaPlayer.Status; 

//*********************************************************
public class CmdBar extends HBox {
	
	
	Button cPlayButton      = new Button(">");
	Button cPreviousButton  = new Button("|<<"); 
	Button cNextButton      = new Button(">>|"); 
	Label  cInfoMedia       = new Label("");

	
	Player cPlayer = null;
	
	//--------------------------------------------------------------------------

	public CmdBar( Player iPlayer  ) {
		cPlayer = iPlayer;
		
		getChildren().addAll(  cPreviousButton, cPlayButton, cNextButton, cInfoMedia );
		
		cPlayButton.setPrefWidth(40); 
		cPlayButton.setMinWidth(40); 

		cPreviousButton.setPrefWidth(60); 
		cPreviousButton.setMinWidth(50); 
		cPreviousButton.setOnAction(new EventHandler<ActionEvent>() { 
			public void handle(ActionEvent e) {
				cPlayer.previous();
			} 
		}); 								
		
		cNextButton.setPrefWidth(60); 
		cNextButton.setMinWidth(50); 
		cNextButton.setOnAction(new EventHandler<ActionEvent>() { 
			public void handle(ActionEvent e) {
				cPlayer.next();
			} 
		}); 								
		
		cInfoMedia.setPrefWidth(800); 
		cInfoMedia.setMinWidth(200 );
		

		cPlayButton.setOnAction( (ActionEvent e)-> { 
				MediaPlayer lPM = cPlayer.getPlayer();
				if(  lPM == null ) {
					cPlayer.next();
					return;
				}
				
				Status status = lPM.getStatus(); // To get the status of Player 
				if (status == status.PLAYING) { 

					setAlignment(Pos.CENTER); // setting the HBox to center 
					// If the status is Video playing 
					if ( lPM.getCurrentTime().greaterThanOrEqualTo( lPM.getTotalDuration())) { 
						cPlayer.endOfTrack();						
					} 
					else { 
						cPlayer.pause(); 

					//	cPlayButton.setText(">"); 
					} 
				} // If stopped, halted or paused 
				if (status == Status.HALTED || status == Status.STOPPED || status == Status.PAUSED) { 
					lPM.play(); 
					cPlayButton.setText("||"); 
				}}); 				
	}
	//-------------------------------------------------------
	void setInfo( String iInfo ) { cInfoMedia.setText( iInfo );	}
	void play()  { cPlayButton.setText("||"); }
	void pause() { cPlayButton.setText(">"); }

}
//*********************************************************
