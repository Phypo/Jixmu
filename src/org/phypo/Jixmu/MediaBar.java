package org.phypo.Jixmu;

import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.Log;

import javafx.application.Platform; 
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets; 
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox; 
import javafx.scene.layout.Priority; 
import javafx.scene.media.MediaPlayer; 

//*********************************************************

public class MediaBar extends HBox { 

	Slider cSliderTime    = new Slider(); 
	Slider cSliderVolume  = new Slider(0,1,0.5); 
	Slider cSliderBalance = new Slider(-1,1,0 );
	
	Button cPlayButton    = new Button("||"); 
	Label  cLabelVolume   = new Label(""); 
	Label  cLabelBalance  = new Label(""); 
	Label  cLabelDuration = new Label("0000000"); 

	Player cPlayer        = null;
	
	//--------------------------------------------------------------------------
	public void newMedia( ) {
		
		cSliderVolume.setValue( Math.sqrt(cPlayer.getVolume()) );
		cSliderBalance.setValue(cPlayer.getBalance());

		cPlayer.getPlayer().currentTimeProperty().addListener( (Observable ov) -> { 
				updatesValues(); 
		}); 
		
		cPlayer.getPlayer().setOnEndOfMedia(
                () -> { cPlayer.endOfTrack(); });						
		
		
		cSliderTime.valueProperty().addListener( (Observable ov) ->{ 
				if (cSliderTime.isPressed()) { 
					cPlayer.getPlayer().seek(cPlayer.getPlayer().getMedia().getDuration().multiply(cSliderTime.getValue() / 100)); 
				} 
		}); 

		cSliderVolume.valueProperty().addListener((Observable ov)-> { 
				if (cSliderVolume.isPressed()) { 
					cPlayer.setVolume(cSliderVolume.getValue()*cSliderVolume.getValue()); 
			        Conf.SaveIni();
					Platform.runLater(new Runnable() { public void run() { Conf.SaveIni();}});
				} 
		}); 

		cSliderBalance.valueProperty().addListener( (Observable ov) ->{ 
				if (cSliderBalance.isPressed()) { 
					double lVal = cSliderBalance.getValue();
					Log.Dbg( "Balance:" + lVal );
					cPlayer.setBalance( lVal ); 
					Platform.runLater(new Runnable() { public void run() { Conf.SaveIni();}});
				} 
		}); 
	}
	//--------------------------------------------------------------------------
	public MediaBar(Player iPlayer ) {
		
		ToggleButton lToggleM = FxHelper.CreateToggle( "", "Mute", Conf.sIconeMute );
		lToggleM.setSelected(Conf.sMute);
		lToggleM.setOnAction( (ActionEvent e)->{
	        Conf.sMute = lToggleM.isSelected();	
			Log.Dbg( "Toggle Mute" + lToggleM.isSelected() + Conf.sMute);	
	        cPlayer.mute();
	        Conf.SaveIni();
		});
		
		ToggleButton lToggleR = FxHelper.CreateToggle( "",  "Repeat all",  Conf.sIconeRepeatAll );
		lToggleR.setSelected(Conf.sRepeatAll);
		lToggleR.setOnAction( (ActionEvent e)->{
	        Conf.sRepeatAll = lToggleR.isSelected();	
			Log.Dbg( "Toggle Repeat" + lToggleR.isSelected() + Conf.sRepeatAll);	
	        Conf.SaveIni();
		});
		
		ToggleButton lToggleA = FxHelper.CreateToggle( "",  "Random",  Conf.sIconeRandom );
		lToggleA.setSelected(Conf.sRandom );
		lToggleA.setOnAction( (ActionEvent e)->{
	        Conf.sRandom = lToggleA.isSelected();		        
	        Log.Dbg( "Toggle random" + lToggleA.isSelected() + " " +  Conf.sRandom );	
	        Conf.SaveIni();
		});
		
	//	setAlignment(Pos.CENTER);

		cPlayer = iPlayer;
		
	//	setAlignment(Pos.CENTER); // setting the HBox to center 
		setPadding(new Insets(5, 5, 5, 5)); 

		
		cSliderVolume.setValue( Math.sqrt(cPlayer.getVolume()));
		cSliderBalance.setValue(cPlayer.getBalance());

		cSliderTime.setMaxWidth(8000); 
		cSliderTime.setPrefWidth(800); 
		cSliderTime.setMinWidth(200); 

		cSliderVolume.setMaxWidth(200); 
		cSliderVolume.setPrefWidth(100); 
		cSliderVolume.setMinWidth(80); 
		
		cSliderBalance.setMaxWidth(160); 
		cSliderBalance.setPrefWidth(80); 
		cSliderBalance.setMinWidth(50); 

		cLabelBalance.setGraphic(new ImageView( Conf.sIconeLR));
		cLabelVolume.setGraphic(new ImageView( Conf.sIconeVolume));

		HBox.setHgrow(cSliderTime, Priority.ALWAYS); 
//	cPlayButton.setPrefWidth(30); 
		cLabelDuration.setMaxWidth(200); 
		cLabelDuration.setPrefWidth(110); 
		cLabelDuration.setMinWidth(80); 

	//	getChildren().add(cPlayButton); // cPlayButton 
		getChildren().addAll( lToggleM, lToggleR, lToggleA, cSliderTime, cLabelVolume, cSliderVolume, cLabelBalance, cSliderBalance, cLabelDuration);		
	} 
	//--------------------------------------------------------------------------
	protected void updatesValues() {	
			Platform.runLater(()-> { 		
					// update the position when media run

					MediaPlayer lPM = cPlayer.getPlayer();
					if( lPM != null ) {
				
						double lCurrent = lPM.getCurrentTime().toMillis();
						double lTotal   = lPM.getTotalDuration().toMillis();
						double lPosition = (lCurrent / lTotal) * 100.0;
	//					Log.Dbg( "updatesValues " + lTotal + " " + lCurrent + "->"+ lPosition);
						
						cSliderTime.setValue(  lPosition );
						Conf.sCurrentMediaTime = lCurrent;
						cLabelDuration.setText( (int)(lCurrent/1000) + "/" + (int)(lTotal/1000)+ "s" );
					}
			}); 
		} 
	}
	//*********************************************************

