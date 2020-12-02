package org.phypo.Jixmu;


import org.phypo.PPg.PPgUtils.Log;

import javafx.application.Platform; 
import javafx.beans.InvalidationListener; 
import javafx.beans.Observable; 
import javafx.geometry.Insets; 
import javafx.geometry.Pos; 
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.Slider; 
import javafx.scene.layout.HBox; 
import javafx.scene.layout.Priority; 
import javafx.scene.media.MediaPlayer; 

//*********************************************************

public class MediaBar extends HBox { 

	Slider cSliderTime    = new Slider(); 
	Slider cSliderVolume  = new Slider(0,1,0.5); 
	Slider cSliderBalance = new Slider(-1,1,0 );
	
	Button cPlayButton    = new Button("||"); 
	Label  cLabelVolume   = new Label("Volume: "); 
	Label  cLabelBalance  = new Label("Balance: "); 
	Player cPlayer        = null;
	//--------------------------------------------------------------------------
	public void newMedia( ) {
		
		cSliderVolume.setValue(cPlayer.getVolume() );
		cSliderBalance.setValue(cPlayer.getBalance());

		cPlayer.getPlayer().currentTimeProperty().addListener(new InvalidationListener() { 
			public void invalidated(Observable ov) 
			{ 
				updatesValues(); 
			} 
		}); 
		cPlayer.getPlayer().setOnEndOfMedia(
                () -> { cPlayer.endOfTrack(); });						
		
		
		cSliderTime.valueProperty().addListener(new InvalidationListener() { 
			public void invalidated(Observable ov) 
			{ 
				if (cSliderTime.isPressed()) { 
					cPlayer.getPlayer().seek(cPlayer.getPlayer().getMedia().getDuration().multiply(cSliderTime.getValue() / 100)); 
				} 
				setAlignment(Pos.CENTER);
			} 
		}); 

		cSliderVolume.valueProperty().addListener(new InvalidationListener() { 
			public void invalidated(Observable ov) 
			{ 
				if (cSliderVolume.isPressed()) { 
					cPlayer.setVolume(cSliderVolume.getValue()); 
				} 
			} 
		}); 

		cSliderBalance.valueProperty().addListener(new InvalidationListener() { 
			public void invalidated(Observable ov) 
			{ 
				if (cSliderBalance.isPressed()) { 
					double lVal = cSliderBalance.getValue();
					Log.Dbg( "Balance:" + lVal );
					cPlayer.setBalance( lVal ); 
						
				} 
			} 
		}); 
	}
	//--------------------------------------------------------------------------
	public MediaBar(Player iPlayer ) {

		cPlayer = iPlayer;
		
		setAlignment(Pos.CENTER); // setting the HBox to center 
		setPadding(new Insets(5, 10, 5, 10)); 
		cSliderVolume.setPrefWidth(70); 
		cSliderVolume.setMinWidth(30); 
		
		cSliderBalance.setPrefWidth(70); 
		cSliderBalance.setMinWidth(30); 

		Label  cLabelBalance  = new Label("Balance: "); 

		HBox.setHgrow(cSliderTime, Priority.ALWAYS); 
//	cPlayButton.setPrefWidth(30); 

		// Adding the components to the bottom 
	//	getChildren().add(cPlayButton); // cPlayButton 
		getChildren().addAll(cSliderTime, cLabelVolume, cSliderVolume, cLabelBalance, cSliderBalance);		
	} 
	//--------------------------------------------------------------------------
	protected void updatesValues() {
			Platform.runLater(new Runnable() { 
				public void run() { 		
					// Updating to the new time value 
					// This will move the slider while running your video 

					MediaPlayer lPM = cPlayer.getPlayer();
					if( lPM != null ) {
						double lCurrent = lPM.getCurrentTime().toMillis();
						double lTotal   = lPM.getTotalDuration().toMillis();
						double lPosition = (lCurrent / lTotal) * 100.0;
						cSliderTime.setValue(  lPosition );
					}
				} 
			}); 
		} 
	}
	//*********************************************************

