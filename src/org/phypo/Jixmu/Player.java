package org.phypo.Jixmu;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;

import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgUtils.Log;

import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

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

	enum  ProgressMode { SEQUENTIAL, RANDOM };
	ProgressMode cProgressMode = ProgressMode.SEQUENTIAL;
	ProgressMode getProgressMode() { return cProgressMode; }
	void         setProgressMode( ProgressMode iProgressMode)  {  cProgressMode =   iProgressMode;}


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
		cMenu.getMenus().add(lMenuFile); 
		initReadFileChooser(lMenuFile  );
		initDirChooser( lMenuFile  );
		
		FxHelper.AddMenuItem(lMenuFile, "Quit", new EventHandler<ActionEvent>(){ 
				public void handle(ActionEvent e){
					System.exit(0);					
				}});

		// --=== VIEW ===--
		Menu     lMenuView= new Menu("View");
		cMenu.getMenus().add(lMenuView); 

		FxHelper.AddMenuItem( lMenuView, "Records", new EventHandler<ActionEvent>(){ 

			public void handle(ActionEvent e){
				if( cTableRecords.isVisible() ) {
					cTableRecords.setVisible(false);
				} else {
					cTableRecords.setVisible(true);					
				}
			}});

		cTableRecords = new TableRecords(this);
		cTableRecords.setVisible(true);					
		setCenter( cTableRecords );

		//--------------------------------------


		// ==== Menu =====


		// ======== Find media to play ===============

		cMedBar = new MediaBar(this);
		cCmdBar = new CmdBar(this);
		cTopBox.getChildren().addAll(cCmdBar, cMedBar); // Setting the MediaBar at bottom 
		//		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
	}
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------
	private double cVolume = 0.5;
	//--------------------------------------
	void setVolume(double iVol) {
		if( iVol < 0 ) iVol = 0;
		else if( iVol > 1) iVol =1;

		cVolume = iVol;
		if( getPlayer() !=null )
			getPlayer().setVolume( cVolume );		
	}
	//--------------------------------------
	double getVolume() { return cVolume;}
	//--------------------------------------
	private double cBalance = 0;
	//--------------------------------------
	void setBalance(double iBal) {
		if( iBal < -1 ) iBal = -1;
		else if( iBal > 1) iBal =1;
	
		cBalance = iBal;
		if( getPlayer() !=null )
			getPlayer().setBalance( cBalance );		
	}
	double getBalance() { return cBalance; }
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
	void initReadFileChooser( Menu iMenu ) {
		cFileChooser = new FileChooser();
		cFileChooser.setInitialDirectory(sDir);
		cFileChooser.setTitle( "Choose media file");

		FxHelper.AddMenuItem(iMenu,"Open file", new EventHandler<ActionEvent>(){ 

			public void handle(ActionEvent e){ 

				Log.Dbg( "Player lItemOpen.setOnAction ");
				File lFile = cFileChooser.showOpenDialog( Main.Instance().getPrimStage()); 
				sDir = cFileChooser.getInitialDirectory();		        
				Log.Dbg(sDir.getAbsolutePath());
				// Choosing the file to play 
				if (lFile != null) { 
					//play( addFile2Records(lFile ) );
					play( cTableRecords.addFile(lFile ));
				} 
			}
		});
	}
	//--------------------------------------
	void initDirChooser( Menu iMenu ) {
		cDirChooser = new DirectoryChooser();
		cDirChooser.setInitialDirectory(sDir);
		cDirChooser.setTitle( "Choose directory");

		FxHelper.AddMenuItem(iMenu,"Open directory", new EventHandler<ActionEvent>(){ 

			public void handle(ActionEvent e){ 

				Log.Dbg( "Player lItemOpen.setOnAction ");
				File lFile = cDirChooser.showDialog( Main.Instance().getPrimStage()); 
				sDir = cDirChooser.getInitialDirectory();		        
				Log.Dbg(sDir.getAbsolutePath());
				// Choosing the file to play 
				if (lFile != null) { 
					//play( addFile2Records(lFile ) );
					play( cTableRecords.addFile(lFile ));
				} 
			}
		});
	}
	//--------------------------------------
	//--------------------------------------
	//--------------------------------------

	public void play( MyRecord iRecord ) {
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

		cMedPlayer.setVolume(cVolume);
		cMedPlayer.setBalance(cBalance);

		StringBuilder lStr = new StringBuilder();
		lStr.append( iRecord.getName() );
		lStr.append(  " ");
		lStr.append( cMedPlayer.getTotalDuration().toMinutes() + "mn ");
		lStr.append(  " ");
		lStr.append( cMedia.getWidth() );
		lStr.append(  "x");
		lStr.append( cMedia.getHeight() );
		cCmdBar.setInfo( lStr.toString());

		cMedBar.newMedia();

		//	if( cMedia.getWidth() > 0 ) {
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
		}
	}
	//--------------------------------------	
	public void next() {		
		MyRecord lRecord = null;

		for( int i=0; i< 100; i++) {
			if( getProgressMode() == ProgressMode.SEQUENTIAL) {
				lRecord = cTableRecords.getNextRecord();
			} 			
			if( lRecord != null && lRecord.onError() == false) {
				play( lRecord );
				return;
			}
			else 
				next();
		}
	}
	//--------------------------------------	
	public void previous() {
		MyRecord lRecord = null;

		for( int i=0; i< 100; i++) {
			if( getProgressMode() == ProgressMode.SEQUENTIAL) {
				lRecord = cTableRecords.getPreviousRecord();
			}
			if( lRecord != null && lRecord.onError() == false) {
				play( lRecord );
				return;
			}
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

