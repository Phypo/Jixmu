package org.phypo.Jixmu;


import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import org.phypo.PPg.PPgFX.FxHelper;

import javafx.event.ActionEvent; 
import javafx.geometry.Pos; 
import javafx.scene.media.MediaPlayer; 
import javafx.scene.media.MediaPlayer.Status; 

//*********************************************************
public class CmdBar extends HBox {
	
	
	Button cPlayButton      = new Button(">");
	Button cPreviousButton  = new Button("|<<"); 
	Button cNextButton      = new Button(">>|"); 
	Button cDestroyButton   = new Button(""); 
	Button cInfoMedia       = new Button("");
	Button cCopyBestOF      = null;

	
	Player cPlayer = null;
	
	//--------------------------------------------------------------------------

	public CmdBar( Player iPlayer  ) {
		cPlayer = iPlayer;		

		if( Conf.sDirCopyBestOf != null && Conf.sDirCopyBestOf.length()>0) {
			cCopyBestOF = new Button("");
			cCopyBestOF.setPrefWidth(60); 
			cCopyBestOF.setMinWidth(50); 
			FxHelper.SetButtonImage( cCopyBestOF, Conf.sIconeCpy2BestOf );
			cCopyBestOF.setOnAction( (ActionEvent e) -> { cPlayer.copyCurrent2BestOf(); });
			getChildren().add(cCopyBestOF);
		}
		
		getChildren().addAll( cPreviousButton, cPlayButton, cNextButton, cInfoMedia, cDestroyButton);
	
		cPlayButton.setPrefWidth(40); 
		cPlayButton.setMinWidth(40); 

		cPreviousButton.setPrefWidth(60); 
		cPreviousButton.setMinWidth(50); 
		cPreviousButton.setOnAction((ActionEvent e) -> { cPlayer.previous(); }); 								
		
		cNextButton.setPrefWidth(60); 
		cNextButton.setMinWidth(50); 
		cNextButton.setOnAction( (ActionEvent e) -> { cPlayer.next(); }); 		
		
		
		cDestroyButton.setPrefWidth(60); 
		cDestroyButton.setMinWidth(50); 
		FxHelper.SetButtonImage( cDestroyButton, Conf.sIconeDestroy );
		cDestroyButton.setOnAction( (ActionEvent e) -> { cPlayer.destroyCurrent(); }); 								
		
		cInfoMedia.setPrefWidth(800); 
		cInfoMedia.setMinWidth(200 );
		cInfoMedia.setOnAction( (ActionEvent e) -> {cPlayer.scrollToCurrent();	}); 	
		
		

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
