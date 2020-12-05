package org.phypo.Jixmu;

import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.Log;

import javafx.application.Platform; 
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.geometry.Insets; 
import javafx.geometry.Pos;
import javafx.scene.control.Button; 
import javafx.scene.control.Label; 
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
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
		
		cSliderVolume.setValue( cPlayer.getVolume() );
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
					cPlayer.setVolume(cSliderVolume.getValue()); 
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
		
		setAlignment(Pos.CENTER);

		cPlayer = iPlayer;
		
		setAlignment(Pos.CENTER); // setting the HBox to center 
		setPadding(new Insets(5, 10, 5, 10)); 
		
		cSliderVolume.setValue( cPlayer.getVolume() );
		cSliderBalance.setValue(cPlayer.getBalance());

		cSliderVolume.setPrefWidth(70); 
		cSliderVolume.setMinWidth(30); 
		
		cSliderBalance.setPrefWidth(70); 
		cSliderBalance.setMinWidth(30); 

		Label  cLabelBalance  = new Label("Balance: "); 

		HBox.setHgrow(cSliderTime, Priority.ALWAYS); 
//	cPlayButton.setPrefWidth(30); 

	//	getChildren().add(cPlayButton); // cPlayButton 
		getChildren().addAll( lToggleM, lToggleR, lToggleA, cSliderTime, cLabelVolume, cSliderVolume, cLabelBalance, cSliderBalance);		
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
					}
			}); 
		} 
	}
	//*********************************************************

