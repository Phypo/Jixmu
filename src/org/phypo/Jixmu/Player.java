package org.phypo.Jixmu;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.Log;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

//**********************************************************
public class Player extends BorderPane // Player class extend BorderPane 
// in order to divide the media 
// player into regions 
{ 
	static File sDir = new File( "/home/phipo/Musique");

	FileChooser      cFileChooser = null; 
	DirectoryChooser cDirChooser = null; 

	public enum Error { NO_ERROR, MEDIAPLAYER, MALFORMED_URL, MEDIA_UNSUPPORTED, MEDIA_ERROR};



	VBox        cTopBox  = null;
	MenuBar     cMenu    = null; 

	MediaBar    cMedBar     = null;
	CmdBar      cCmdBar     = null;

	Media       cMedia   = null; 
	MediaPlayer cMedPlayer  = null;
	Viewer      cViewer      = null;
	TableRecords cTableRecords    = null;


	enum   RepeatMode{ NO_REPEAT, REPEAT_TRACK, REPEAT_ALL };
	RepeatMode cRepeatMode = RepeatMode.NO_REPEAT;
	RepeatMode getRepeatMode() { return cRepeatMode ;}
	void       setRepeatMode( RepeatMode iRepeatMode) { cRepeatMode= iRepeatMode; }



	public MediaPlayer getPlayer() { return cMedPlayer;} 

	MyRecordMap  cRecords = new MyRecordMap();

	int cCurrentRecordPos = 0;

	//--------------------------------------
	public Player() {

		cTopBox = new VBox(2);
		setTop(cTopBox);  

		cTableRecords = new TableRecords( Player.this );
		cTableRecords.setVisible(false);					
		setCenter( cTableRecords );

		// ==== Menu =====
		cMenu              = new MenuBar(); 
		cTopBox.getChildren().add( cMenu );
		// --=== FILE ===--
		Menu     lMenuFile = new Menu("File"); 

		FxHelper.AddMenuItem( lMenuFile,"Clear", (ActionEvent e) -> { clearAll(); });

		FxHelper.AddMenuSeparator( lMenuFile);

		cMenu.getMenus().add(lMenuFile); 
		initReadFileChooser(lMenuFile  );
		initDirChooser( lMenuFile  );

		//===================
		FxHelper.AddMenuItem( lMenuFile,"Add playlist ...", (ActionEvent e) -> { 
			cFileChooser = new FileChooser();
			cFileChooser.setInitialDirectory(sDir);
			cFileChooser.setTitle( "Adding playlist");
			cFileChooser.getExtensionFilters().add( new ExtensionFilter("Jixmu playlist", "*.jixmu"));
			// A FAIRE showOpenMultipleDialog
			List<File> lFiles = cFileChooser.showOpenMultipleDialog( Main.Instance().getPrimStage()); 
			sDir = cFileChooser.getInitialDirectory();		        
			if (lFiles != null) { 	
				for( File lFile : lFiles) {
					//Platform.runLater(() -> { cTableRecords.readPlayList(lFile, false); });
					cTableRecords.readPlayList(lFile, false);
				}
				cTableRecords.save();
			}
		});


		FxHelper.AddMenuSeparator( lMenuFile);
		//===================
		FxHelper.AddMenuItem( lMenuFile,"Save playlist ...", (ActionEvent e) ->{ 
			cFileChooser = new FileChooser();
			cFileChooser.setInitialDirectory(sDir);
			cFileChooser.setTitle( "Save playlist");
			cFileChooser.getExtensionFilters().add( new ExtensionFilter("Jixmu playlist", "*.jixmu"));
			File lFile = cFileChooser.showSaveDialog( Main.Instance().getPrimStage()); 
			sDir = cFileChooser.getInitialDirectory();		        
			if (lFile != null) { 	
				String lName=null;
				try {
					lName = Conf.RemoveFileExtension( lFile.getCanonicalPath() );
					Log.Dbg( "Save playlist Name : " + lName);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if( lName == null ) {
					Alert lAlert = new Alert(AlertType.ERROR, "Bad file name" );
					lAlert.showAndWait();	   
					return;
				}
				lName += ".jixmu";
				cTableRecords.writePlaylist( lName, false);
			}	
		});	
		//===================

		FxHelper.AddMenuSeparator( lMenuFile);
		//===================
		FxHelper.AddMenuItem(lMenuFile, "Quit", (ActionEvent)->{ 
			cTableRecords.save();				
			Conf.SaveIni();
			System.exit(0);					
		});	

		// --=== VIEW ===--
		Menu     lMenuView= new Menu("View");
		cMenu.getMenus().add(lMenuView); 

		FxHelper.AddMenuItem( lMenuView, "Records", (ActionEvent e) -> {
			if( cTableRecords.isVisible() ) {
				cTableRecords.setVisible(false);
			} else {
				cTableRecords.setVisible(true);					
			}
		});

		cTableRecords = new TableRecords(this);
		cTableRecords.setVisible(true);					
		setCenter( cTableRecords );


		cTableRecords.load();
		//--------------------------------------


		// ==== Menu =====


		// ======== Find media to play ===============

		cMedBar = new MediaBar(this);
		cCmdBar = new CmdBar(this);
		cTopBox.getChildren().addAll(cCmdBar, cMedBar); // Setting the MediaBar at bottom 
		//		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
		
		if( Conf.sAutoPlay) {
			play( cTableRecords.getCurrentRecord(), Conf.sCurrentMediaTime );
		}

	}
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	void setVolume(double iVol) {
		if( iVol < 0 ) iVol = 0;
		else if( iVol > 1) iVol =1;

		Conf.sVolume = iVol;
		if( getPlayer() !=null )
			getPlayer().setVolume( Conf.sVolume );		
	}
	//--------------------------------------
	double getVolume() { return Conf.sVolume;}
	//--------------------------------------
	void setBalance(double iBal) {
		if( iBal < -1 ) iBal = -1;
		else if( iBal > 1) iBal =1;

		Conf.sBalance = iBal;
		if( getPlayer() !=null )
			getPlayer().setBalance( Conf.sBalance );		
	}
	double getBalance() { return Conf.sBalance; }
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	/*
	MyRecord addFile2Records( File iFile ) {
		MyRecord lRecord = new MyRecord( iFile );
		return add2Records( lRecord );
	}
	//--------------------------------------
	MyRecord add2Records( MyRecord iRecord ) {

		cTableRecords.addLine( iRecord);	
		return iRecord;
	}*/
	//--------------------------------------

	//--------------------------------------
	void initReadFileChooser( Menu iMenu ) {
		cFileChooser = new FileChooser();
		cFileChooser.setInitialDirectory(sDir);
		cFileChooser.setTitle( "Adding file");
		cFileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.m4a"),
				new ExtensionFilter("Video Files", "*.mp4"));
		//         new ExtensionFilter("All Files", "*.*"));

		FxHelper.AddMenuItem(iMenu,"Add file ...",(ActionEvent e) -> { 
			List<File> lFiles = cFileChooser.showOpenMultipleDialog( Main.Instance().getPrimStage()); 
			sDir = cFileChooser.getInitialDirectory();		        
			if (lFiles != null) { 
				for( File lFile : lFiles) {					
					cTableRecords.addFile(lFile );
				}				
				cTableRecords.writeSize2Foot("");
				cTableRecords.save();				
			} 
		});
	}
	//--------------------------------------
	void initDirChooser( Menu iMenu ) {
		cDirChooser = new DirectoryChooser();
		cDirChooser.setInitialDirectory(sDir);
		cDirChooser.setTitle( "Adding folder ...");

		FxHelper.AddMenuItem(iMenu,"Add folder", (ActionEvent e) ->{ 

			Log.Dbg( "Player lItemOpen.setOnAction ");
			File lFile = cDirChooser.showDialog( Main.Instance().getPrimStage()); 
			sDir = cDirChooser.getInitialDirectory();		        
			Log.Dbg(sDir.getAbsolutePath());
			// Choosing the file to play 
			if (lFile != null) { 
				//play( addFile2Records(lFile ) );
				cTableRecords.addFile(lFile );
				Platform.runLater(() -> { cTableRecords.save(); });
				cTableRecords.writeSize2Foot("");
			} 
		});
	}
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	public void clearAll() {
		pause();
		cMedPlayer = null;
		cTableRecords.clearAll();
	}
	//--------------------------------------
	public void play( MyRecord iRecord, double iPos ) {
		if( iRecord == null ) {
			next();
			return;
		} 

		URI iURI = iRecord.cURI;
		try {
			cMedia  = new Media(iURI.toURL().toExternalForm());
		} catch (MalformedURLException e) {
			iRecord.setError( Error.MALFORMED_URL, e.getMessage());
			// TODO Auto-generated catch block
			String lErr = e.getMessage();
			Alert lAlert = new Alert(AlertType.ERROR, lErr );
			lAlert.showAndWait();	   
			return;
		}
		catch (MediaException e ){
			iRecord.setError( Error.MEDIA_UNSUPPORTED, e.getMessage());
			String lErr = e.getMessage();
			Alert lAlert = new Alert(AlertType.ERROR, lErr );
			lAlert.showAndWait();	   
			return ;
		}
		catch (Exception e ){
			iRecord.setError( Error.MEDIA_ERROR, e.getMessage());
			String lErr = e.getMessage();
			Alert lAlert = new Alert(AlertType.ERROR, lErr );
			lAlert.showAndWait();	   		
			return ;
		}


		//ObservableMap<String,Object> 	cMedia.getCssMetaData();
		//ObservableList<Track> 	getTracks()

		if( cMedPlayer != null){
			cMedPlayer.stop();
			//cMedPlayer.release();
		}


		cMedPlayer = new MediaPlayer(cMedia);

		cMedPlayer.setOnError( ()-> {
			System.out.println("Media error occurred: " + cMedPlayer.getError());
			iRecord.setError( Error.MEDIAPLAYER, cMedPlayer.getError().toString() );			
		});

		cMedPlayer.setVolume(Conf.sVolume);
		cMedPlayer.setBalance(Conf.sBalance);
		cMedPlayer.setMute(Conf.sMute);

		StringBuilder lStr = new StringBuilder();
		lStr.append( iRecord.getName() );
		lStr.append(  " ");
		lStr.append( cMedPlayer.getTotalDuration().toMinutes() + "mn ");
		lStr.append(  " ");
		lStr.append( cMedia.getWidth() );
		lStr.append(  "x");
		lStr.append( cMedia.getHeight() );
		cCmdBar.setInfo( lStr.toString());
/* A FAIRE il faudrait sauvegarder le Record courant dans le .ini - pour eviter une desyncro des fichiers
		if( iPos != 0 ) {
			Log.Dbg( "Play start=" + iPos  );
			Duration lDur = new Duration( iPos );
			cMedPlayer.setStartTime( lDur ); 
		}
		*/
		cMedBar.newMedia();

		if( iRecord.getExtension().equalsIgnoreCase("mp4") ) {
			if( cViewer == null ) {
				cViewer = new Viewer( this );				
			}

			cViewer.newMedia( cMedia, cMedPlayer );
			cViewer.show(true);

		} else {
			if( cViewer != null ) {
				cViewer.show( false );
			}
		}

		cMedPlayer.play();
	} 
	//--------------------------------------	
	public void mute() {
		if( cMedPlayer != null ) {			
			cMedPlayer.setMute(Conf.sMute);
		}
	}
	//--------------------------------------	
	public void pause() {
		if( cMedPlayer != null ) {			
			cMedPlayer.pause();
			cCmdBar.pause();
		}
	}
	//--------------------------------------	
	public void play() {
		if( cMedPlayer != null ) {			
			cMedPlayer.play();
			cCmdBar.play();
		}else {
			next();
		}
	}
	//--------------------------------------	
	public void next() {		
		MyRecord lRecord = null;
		
		for( int i=0; i< 100; i++) { // en cas d'eereur on essaye 100 x
				lRecord = cTableRecords.getNextRecord();
			} 			
			if( lRecord != null && lRecord.onError() == false) {
				play( lRecord, 0 );
				return;
			}
			else {
				next();
			}
	}
	//--------------------------------------	
	public void previous() {
		MyRecord lRecord = null;

		for( int i=0; i< 100; i++) {
				lRecord = cTableRecords.getPreviousRecord();
			}
		if( lRecord != null && lRecord.onError() == false) {
				play( lRecord, 0 );
				return;
			}
		else {
			previous();
		}
	}
	//--------------------------------------	
	public void endOfTrack() {
		Log.Dbg("endOfTrack" );

		if(  getRepeatMode() == Player.RepeatMode.REPEAT_TRACK) {
			getPlayer().seek( getPlayer().getStartTime()); 
			getPlayer().play();
		} else {
			next();
		}
	}
} 
//**********************************************************

