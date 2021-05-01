package org.phypo.Jixmu;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import org.phypo.PPg.PPgFX.DataViewObj;
import org.phypo.PPg.PPgUtils.Log;


//***********************************
public class MyRecord implements DataViewObj{

	static protected   long    sCurrentNumOrder=1;

	protected   long    cNumOrder=1;
	protected   long    cNumRandom=1;
	protected   URI     cURI;
	protected   String  cPath="";
	protected   String  cName="";
	protected   String  cInfo="";
	protected   long    cSize=0;
	protected   String  cExtension="";
	protected   Player.Error cError= Player.Error.NO_ERROR;
	protected   String  cStrError=null;  


	public long     getOrder()            { return cNumOrder; }
	public long     getRandom()           { return cNumRandom; }
	public String   getUri()              { return cURI.getRawPath();   }
	public String   getName()             { return cName;   }
	public String   getPath()             { return cPath;   }
	public String   getInfo()             { return cInfo;   }
	public String   getExtension()        { return cExtension;   }
	public String   getStrError()         { return cStrError;   }
	public Player.Error getError()        { return cError;   }
	public boolean  onError()             { return cError != Player.Error.NO_ERROR;}


	//----------------------------
	void setError( Player.Error iError, String iStrError ) {
		cStrError = new String( iStrError );
		cError    = iError;
	}
	//----------------------------
	public String getSize() {

		if(cSize < 1000 ){
			return "o "+cSize ;
		}
		else
			if(cSize < 1000000 ){
				long lSize =  cSize/(100);
				float  lSizeF = (float) ((lSize)/10.0);
				return "ko "+lSizeF ;
			}
			else 
				if(cSize < 1000000000 ){
					long lSize =  cSize/(100*1000);
					float  lSizeF = (float) ((lSize)/10.0);
					return "_Mo "+lSizeF ;
				}
				else 
				{
					long lSize =  cSize/(100*1000*1000);
					float  lSizeF = (float) ((lSize)/10.0);
					return "_Go "+lSizeF;
				}
	}
	//----------------------------
	void randomize() { cNumRandom = Main.Instance().getRandomLong();}

	//----------------------------
	public MyRecord( File iFile ) { //URI iUri) {
		this( iFile, 0);	
	}	
	//----------------------------
	void set( MyRecord iObj ) {
		cNumOrder  = iObj.cNumOrder;
		cNumRandom = iObj.cNumRandom;
		cURI       = iObj.cURI;
		cPath      = iObj.cPath;
		cName      = iObj.cName;
		cInfo      = iObj. cInfo;
		cSize      = iObj.cSize;
		cExtension = iObj. cExtension;
		cError     = iObj.cError;
		cStrError  = iObj.cStrError;  
	
	}
	//----------------------------
	public MyRecord( File iFile, long iRandom  ) { //URI iUri) {

		if( iRandom == 0) {
			randomize();
		} else {
			cNumRandom = iRandom;
		}

		cNumOrder = sCurrentNumOrder++;


		cURI = iFile.toURI();

		cName = iFile.getName();
		cPath = iFile.getAbsolutePath(); //getCanonicalPath();
		int lIndex = cName.lastIndexOf('.');
		if( lIndex > 0) {
			cExtension = cName.substring( lIndex + 1);
		}

		try {
			cSize = Files.size(iFile.toPath());
			cInfo = "" + cSize;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.Dbg2( "MyRecord URI:" + cURI) ;
	}
	//----------------------------
	@Override
	public String getObjName() {
		return "Record";
	}
	@Override
	public Long getKey() {
		return cNumOrder;
	}
	@Override
	public void setFrom(DataViewObj iObj) {
		set( (MyRecord) iObj);		
	}	  

	//----------------------------

}
//***********************************

