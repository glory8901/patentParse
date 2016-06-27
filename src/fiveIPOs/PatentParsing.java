package fiveIPOs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.XMLReader;
import utils.file.FilesFilter;
import utils.file.FolderRecursion;
import utils.file.FolderUtils;

public class PatentParsing {

	public static void main(String[] args) throws Exception {
		XMLReader xmlReader = new XMLReader();
		List<File> folderList = new ArrayList<File>();
		List<File> folderCleared = new ArrayList<File>();
		List<File> allfiles = new ArrayList<File>();

		// FR-new
		folderList = FolderRecursion
				.getAllFoldersList("H:\\20160610\\临时解压文件\\FR");
		folderCleared = FilesFilter.filter(folderList, "FR_FRNEW", null);
		allfiles = FolderUtils.getFiles(folderCleared);
		System.out.println(allfiles.get(0).getName());
		// read
		xmlReader.readXmlAndWrite(allfiles, "out/xml_new.csv", "DesignApplication",
				"PublicationDate", "RegistrationNumber",
				"DesignCurrentStatusCode", "ViewDetails>View>ViewNumber");

		// FR-amd
		folderCleared = FilesFilter.filter(folderList, "FR_FRAMD", null);
		allfiles = FolderUtils.getFiles(folderCleared);
		System.out.println(allfiles.get(0).getName());
		// read
		xmlReader.readXmlAndWrite(allfiles, "out/xml_amd.csv", "DesignApplication",
				"PublicationDate", "RegistrationNumber",
				"DesignCurrentStatusCode", "ViewDetails>View>ViewNumber");
		
		// JP-sgm
		SGMReader sgmReader = new SGMReader();
		sgmReader.readSGMAndWrite();
		
		//JP-lst
		LstReader lstReader = new LstReader();
		lstReader.readLstAndWrite();
	}

}
