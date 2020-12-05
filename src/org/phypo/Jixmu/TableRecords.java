package org.phypo.Jixmu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;

import org.phypo.PPg.PPgFX.AppliFx;
import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgFX.TableFX;
import org.phypo.PPg.PPgFX.TableFxHelper;

import org.phypo.PPg.PPgUtils.Log;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

//***********************************
public class TableRecords  extends  TableFX<MyRecord>{


	Player      cPlayer=null;
	//	MyRecordMap cMap;
	public HashMap<String,MyRecord>   cRecords = new HashMap<>();
	public TreeMap<Long,MyRecord>     cRandoms = new TreeMap<>();
	//--------------------------------------------
	//int      cCurrentRecordPos = -1;
	MyRecord cCurrentRecord = null;

	//-----------------------------
	@Override
	public MyRecord addLine( MyRecord iObj ) {
		
		if( cRecords.containsKey( iObj.getPath()))
				return null;
				
		cRecords.put( iObj.getPath(), iObj );			
		cRandoms.put( iObj.getRandom(), iObj);
		super.addLine(iObj);
		return iObj;
	}
	//-----------------------------
	void clearAll() {
		getContainer().clear();
		cRecords.clear();
		cRandoms.clear();
		cCurrentRecord = null;
		save();
	}
	//-----------------------------
	MyRecord getCurrentRecord() {
		return cCurrentRecord;
	}
	//-----------------------------
	// calling by double-click
	MyRecord setCurrentRecord( MyRecord iRecord, int iPosItem ) {
		if( iRecord == null ) {
			if( iPosItem == -1 ) {
				if( size() > 0 ) {
					iPosItem = 0;
					iRecord = getItem( 0);
				}
				else {
					return cCurrentRecord;
				}
			}
		}

		cCurrentRecord = iRecord;
		clearAndSelect( iPosItem);
		scrollToIndex( iPosItem);
		return cCurrentRecord;
	}
	//-----------------------------
	void randomize() {
		cRandoms.clear();
		for( MyRecord lRec : getContainer() ) {
			lRec.randomize();
			cRandoms.put( lRec.getRandom(), lRec);
		}
	}	
	//-----------------------------
	// calling by double-click
	MyRecord setCurrentRecord( MyRecord iRecord ) {
		return setCurrentRecord( iRecord, getIndexOf( iRecord) );
	}
	//--------------------------------------------
	MyRecord setCurrentRecord(  String iStr) {
		MyRecord lRecord = cRecords.get(iStr);
		return setCurrentRecord( lRecord );		
	}
	//--------------------------------------------
	MyRecord getNextRecord(boolean oReturnFlagStop) {
		oReturnFlagStop =false;
		
		if( cCurrentRecord == null ) {
			return setCurrentRecord( null, -1 );
		}
		Log.Dbg( "getNextRecord Conf.sRandom:"  + Conf.sRandom );
	
		if( Conf.sRandom == false ) {
			Log.Dbg( "getNextRecord sequential");
			int lCurrentRecordPos = getIndexOf( cCurrentRecord); // il peut y avoir eu des sorts ou autre //Lent?
			MyRecord lRecord = getItem( lCurrentRecordPos+1 );
			if( lRecord == null ) {
				oReturnFlagStop = true; 
			}
		return setCurrentRecord( lRecord, lCurrentRecordPos+1);
		}
		
		Log.Dbg( "getNextRecord random");
		Map.Entry<Long,MyRecord> lEntry = cRandoms.higherEntry( cCurrentRecord.getRandom() );
		
		if( lEntry == null ) {
			oReturnFlagStop = true; 
			lEntry = cRandoms.firstEntry();
			if( lEntry == null ) {			
				return setCurrentRecord( null, -1 );
			}
		}
		return  setCurrentRecord( lEntry.getValue());
	}
	//--------------------------------------------
	MyRecord getPreviousRecord() {

		if( cCurrentRecord == null ) {
			return setCurrentRecord( null, -1 );
		}

		Log.Dbg( "getPreviousRecord Conf.sRandom:"  + Conf.sRandom );
		
		if( Conf.sRandom == false ) {
			Log.Dbg( "getPreviousRecord sequential");
			int lCurrentRecordPos = getIndexOf( cCurrentRecord); // il peut y avoir eu des sorts ou autre //Lent?
			MyRecord lRecord = getItem( lCurrentRecordPos-1 );
			if( lRecord == null ) {
				lRecord = getItem( size()-1 ); // la fin
			}
			return setCurrentRecord( lRecord, lCurrentRecordPos-1);
		} 
		Log.Dbg( "getPreviousRecord random");
		Map.Entry<Long,MyRecord> lEntry = cRandoms.lowerEntry(cCurrentRecord.getRandom());
		
		if( lEntry == null ) {			
			lEntry = cRandoms.lastEntry();// la fin
			if( lEntry == null ) {			
				return setCurrentRecord( null, -1 );
			}
		}
		
		return setCurrentRecord( lEntry.getValue());
	}
	//--------------------------------------------
	public TableRecords( Player iPlayer ) {

		cPlayer = iPlayer;
		setSelectionMode(SelectionMode.MULTIPLE);
		addAutoMenu( MENU_SELECTION );

		TableColumn<MyRecord,String>     lColStr ;

		lColStr = addColumn( "Order",        "Order");	
		lColStr = addColumn( "Name",         "Name");		
		lColStr = addColumn( "Size",         "Size");	
		lColStr = addColumn( "Extension",    "Extension");		
		//lColStr = addColumn( "Random",       "Random");	
		lColStr = addColumn( "Path",         "Path");		
//		lColStr = addColumn( "Information",  "Info");		
		lColStr = addColumn( "Error",        "StrError");		


		setOnDragOver( (DragEvent iEv) -> {
				Log.Dbg("setOnDragOver");		      		         
				if (iEv.getDragboard().hasFiles()) {
					iEv.acceptTransferModes(TransferMode.ANY); 
				}
				iEv.consume();
		});

		setOnDragDropped( (DragEvent iEv) -> {
				List<File> lFiles = iEv.getDragboard().getFiles();
				Log.Dbg("Got " + lFiles.size() + " files");		 
				for( File lFile : lFiles ) {
					addFile( lFile );
				}
				TableFxHelper.AutoResizeColumns(TableRecords.this.getTableView());
				
				writeSize2Foot("");
				Platform.runLater(() -> { save(); });
				
				iEv.consume();
		});
	}
	//--------------------------------------------
	public MyRecord addDirectory( File iFile ) {
		MyRecord lRecord = null;

		try {
			DirectoryStream<Path> lPaths = Files.newDirectoryStream( iFile.toPath() );
			//		lPaths.forEach( lPath ->  addFile( lPath.toFile()) );
			for( Path lPath : lPaths ) {
				lRecord = addFile( lPath.toFile());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
		return lRecord;
	}
	//--------------------------------------------
	public MyRecord addFile( String iStr ) {
		return addFile( new File( iStr ));
	}
	//--------------------------------------------
	public MyRecord addFile( File iFile ) {
		if( iFile.canRead() == false ) return null; 

		if( iFile.isDirectory() ) 	return addDirectory( iFile );


		String lName = iFile.getPath();
		Log.Dbg2( "TableRecords addFile path:" +  lName );

		int lIndex = lName.lastIndexOf('.');
		if( lIndex <= 0 ) return null;		
		String lExtension = lName.substring( lIndex + 1);
		if( lExtension == null | lExtension.length() == 0)
			return null;
		lExtension = lExtension.toLowerCase();

		if( Conf.IsGoodExtension(lExtension) == false ) {
			Log.Dbg( "TableRecords addFile bad file extension:" + lExtension  );
			return null;
		}

		MyRecord lRecord = new MyRecord( iFile );
		addLine( lRecord);	

		return lRecord;
	}
	/*
	//--------------------------------------------
	public void setRecordsMap( MyRecordMap iMap  ) {
		clearLines();			
		addLines( iMap.cRecords.values());
		Log.Dbg( "setRecordsMap " + iMap.cRecords.values().size() );		
	}
	 */
	public boolean addPopupMenuItems( ContextMenu iMenu, MouseEvent iEv) {
		FxHelper.AddMenuItem( iMenu, "Remove selection", ( ActionEvent iAEv) -> {		

				MyRecord lFind = removeSelectedLineObject();
				if( lFind != null ) {
					Log.Dbg( "addPopupMenuItems Remove line :" + lFind.getPath() );
					cRecords.remove( lFind.getPath() );
				}
		});
		return true;
	}
	//-----------------------------
	public void doubleClick( MouseEvent iEv, MyRecord iRecord, int iPosItem  ) {
		if( setCurrentRecord( iRecord, iPosItem) != null )
			cPlayer.play(iRecord, 0);
	}
	//-----------------------------
	//-----------------------------
	//-----------------------------	
	public boolean save() { return writePlaylist( Conf.sFileSavePlayList, true); }
	public boolean load() { return readPlayList( Conf.sFileSavePlayList, true);  }
	//-----------------------------	
	public boolean writePlaylist( String iFilename, boolean iFlagWriteCurrent ) {
		if( iFilename == null || iFilename.length() == 0)
			return false;

		File lFile= new File( iFilename );
		return writePlaylist( lFile,iFlagWriteCurrent );	
	}
	//-----------------------------	
	public boolean writePlaylist( File iFile, boolean iFlagWriteCurrent  ) {
		try {
			PrintStream lFout = new PrintStream( new FileOutputStream( iFile ) );
			return writePlaylist( lFout, iFlagWriteCurrent);
		}	catch(Exception e ) { Log.Err( e.toString() );
		e.printStackTrace();
		}
		return false;
	}
	final static String sPlayListSectionTag="[PLAYLIST]";  
	final static String sCurrentTag="Current=";  
	final static String sFilesTag="Files[]=";
			
	//-----------------------------	
	// A FAIRE : mettre des final String pour les chaines 

	public boolean writePlaylist( PrintStream iOut, boolean iFlagWriteCurrent  ){
		iOut.println( "[APPLICATION]");
		iOut.println( "Name="+AppliFx.GetAppliName() );
		iOut.println( "Version="+AppliFx.GetVersion() ); 		
		iOut.println( sPlayListSectionTag ); 
		
		if( iFlagWriteCurrent && cCurrentRecord != null ) {
			iOut.println( sCurrentTag +cCurrentRecord.getPath()  ); 
		}
			
		iOut.println( sFilesTag ); 
		ObservableList<MyRecord> lList = getContainer();
		for( MyRecord lRecord : lList ) {
			iOut.println( lRecord.getPath());
		}
		return true;
	}
	//------------------------------------------------------
	// Calling by read
	boolean readPlayList(String iFilename, boolean iFlagReadCurrent ) {
		if( iFilename == null || iFilename.length() == 0)
			return false;
		File lFile= new File( iFilename );
		return readPlayList( lFile, iFlagReadCurrent);	
	}
	//-----------------------------	
	public boolean readPlayList( File iFile, boolean iFlagReadCurrent ) {
		try {
			InputStreamReader lReader = new InputStreamReader(new FileInputStream(iFile  ));
			readPlayList( lReader,iFlagReadCurrent );
		}	catch(Exception e ) { Log.Err( e.toString() );
		e.printStackTrace();
		}
		return false;
	} 	
	//-----------------------------	
	public boolean readPlayList(  InputStreamReader pIStream, boolean iFlagReadCurrent ){
		BufferedReader lBufread = new BufferedReader( pIStream );

		String lCurrent = null;
		String lStr;
		try {
			String  lSection="";
			boolean lHeader=true;
			while( (lStr =lBufread.readLine()) != null) {
				Log.Dbg2( "TableRecords read - readline:" + lStr);
				
				if( lHeader ) {
					if( lStr.charAt(0) == '[' ) {
						lSection = lStr;				
						Log.Dbg2( "TableRecords read - Section:" + lSection );
					}
					else {
						if( lSection.equals(sPlayListSectionTag )) {	
							Log.Dbg2("TableRecords read - Read Section  Playlist");
							if( lStr.equals( sFilesTag)){
								lHeader = false;
								Log.Dbg2( "TableRecords read - Header end");
							}
							else if( lStr.startsWith( sCurrentTag )) {
								lCurrent = lStr.substring(sCurrentTag.length());							
								Log.Dbg2( "TableRecords read - Current :"+lCurrent);
							}
						}
					}
				}
				else {
					Log.Dbg2("TableRecords read - Adding file:" + lStr );
					addFile( lStr);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		if( iFlagReadCurrent && lCurrent != null )
			setCurrentRecord( lCurrent );
		
		writeSize2Foot("");
		return true;
	}
}
//***********************************
