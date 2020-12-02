package org.phypo.Jixmu;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;


import java.io.IOException;
import java.nio.file.Files;
import org.phypo.PPg.PPgFX.FxHelper;
import org.phypo.PPg.PPgFX.TableFX;
import org.phypo.PPg.PPgFX.TableFxHelper;
import org.phypo.PPg.PPgUtils.Log;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
	public HashMap<String,MyRecord> cRecords = new HashMap<>();

	//--------------------------------------------
	int      cCurrentRecordPos = -1;
	MyRecord cCurrentRecord = null;

	//-----------------------------

	@Override
	public MyRecord addLine( MyRecord iObj ) {

		MyRecord lTmp = cRecords.get(iObj);
		if( lTmp != null) return lTmp;
		cRecords.put( iObj.getPath(), iObj );
		super.addLine(iObj);
		return iObj;
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

		cCurrentRecord    = iRecord;
		cCurrentRecordPos = iPosItem;
		clearAndSelect( iPosItem);
		return cCurrentRecord;
	}
	//-----------------------------
	// calling by double-click
	MyRecord setCurrentRecord( MyRecord iRecord ) {
		return setCurrentRecord( iRecord,getIndexOf( iRecord) );
	}
	//--------------------------------------------
	MyRecord getNextRecord() {

		if( cCurrentRecord == null ) {
			return setCurrentRecord( null, -1 );
		}

		cCurrentRecordPos = getIndexOf( cCurrentRecord); // il peut y avoir eu des sorts ou autre //Lent?

		return setCurrentRecord( getItem( cCurrentRecordPos+1 ), cCurrentRecordPos+1 );
	}
	//--------------------------------------------
	MyRecord getPreviousRecord() {

		if( cCurrentRecord == null ) {
			return setCurrentRecord( null, -1 );
		}

		cCurrentRecordPos = getIndexOf( cCurrentRecord); // il peut y avoir eu des sorts ou autre //Lent?

		return setCurrentRecord( getItem( cCurrentRecordPos-1 ), cCurrentRecordPos-1 );
	}
	//--------------------------------------------
	public TableRecords( Player iPlayer ) {

		cPlayer = iPlayer;
		setSelectionMode(SelectionMode.MULTIPLE);
		addAutoMenu( MENU_SELECTION );

		TableColumn<MyRecord,String>     lColStr ;

		lColStr = addColumn( "Name",         "Name");		
		lColStr = addColumn( "Size",         "Size");		
		lColStr = addColumn( "Extension",    "Extension");		
		lColStr = addColumn( "Error",        "StrError");		
		lColStr = addColumn( "Path",         "Path");		
		lColStr = addColumn( "Information",  "Info");		


		setOnDragOver(new EventHandler<DragEvent>(){
			public void handle(DragEvent iEv) {
				Log.Dbg("setOnDragOver");		      		         
				if (iEv.getDragboard().hasFiles()) {
					iEv.acceptTransferModes(TransferMode.ANY); 
				}
				iEv.consume();
			}
		});

		setOnDragDropped(new EventHandler<DragEvent>(){		
			public void handle(DragEvent iEv) {
				List<File> lFiles = iEv.getDragboard().getFiles();
				Log.Dbg("Got " + lFiles.size() + " files");		 
				for( File lFile : lFiles ) {
					addFile( lFile );
				}
				TableFxHelper.AutoResizeColumns(TableRecords.this.getTableView());

				iEv.consume();
			}
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
	public MyRecord addFile( File iFile ) {
		if( iFile.canRead() == false ) return null; 

		if( iFile.isDirectory() ) 	return addDirectory( iFile );


		String lName = iFile.getPath();
		Log.Dbg( "TableRecords addFile path:" +  lName );

		/*
		int lIndex = lName.lastIndexOf('.');

		if( lIndex <= 0 ) return null;

		String lExtension = lName.substring( lIndex + 1);

		Log.Dbg( "TableRecords addFile extension:" + lExtension  );
		*/
/*
				if( lExtension.compareToIgnoreCase( "mp3") != 0 
				&& lExtension.compareToIgnoreCase(  "mp4") != 0
			    && lExtension.compareToIgnoreCase(  "m4a") != 0) { 
		return false;
		}
		*/
		 
		MyRecord lRecord = new MyRecord( iFile );
		addLine( lRecord);	
		
		if( cCurrentRecordPos == -1 ) {
			cCurrentRecordPos = 0;
		}
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
		FxHelper.AddMenuItem( iMenu, "Remove selection",  new EventHandler<ActionEvent>() {
			//=========================
			public void handle( ActionEvent iEv) {		

				MyRecord lFind = removeSelectedLineObject();
				if( lFind != null ) {
					cRecords.remove( lFind.getPath() );
				}
			}		    
		});
		return true;
	}
	//-----------------------------
	public void doubleClick( MouseEvent iEv, MyRecord iRecord, int iPosItem  ) {
		if( setCurrentRecord( iRecord, iPosItem) != null )
			cPlayer.play(iRecord);
	}
}
//***********************************
