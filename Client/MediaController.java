/**
 * Dan Coleman R00151926
 * Distributed Systems Programming Project 32: Media Player 2
 * MediaController : Controller class that deals with the functions of GUI
 */
package Client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import Server.FileObserver;
import Server.FolderObservable;
import Server.WatchFile;
import Server.WatchFileInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaController {
//FXML Tags----------------------	
	@FXML
	ListView listViewLocal;
	
	@FXML
	ListView listViewServer;
	
	@FXML
	Button playButton;
	
	@FXML
	Button loadButton;
	
	@FXML
	Button downloadButton;
	
	@FXML
	MediaView mediaView;
//------------------------------
	
	
	//Used to see if video is playing or paused
	Boolean playing = false;

	//The Path for the directories.
	private String localPath = "src\\ClientFolder";
	private String serverPath = "src\\ServerFolder";
	
	@FXML
	//Starts the code
	public void initialize() {
		
		//Runs code after gui created on UI Thread so it can't add to ListViews until they've been created.
        Platform.runLater(() -> {
            addObservers();
        });

	}
	/**
	 * If no methods are passed in this addObservers runs. It passes in the path of the directory/folder to watch and the list
	 * to keep the content names in
	 */
	public void addObservers(){

        addObservers(listViewServer, serverPath);
        addObservers(listViewLocal, localPath);

    }

	//Runs if addObserver passes in the ListView and String. Starts a thread with an observable list that observes the path passed in

	/*
	This method will take in a list view and a path
	It will create an observable file monitor for the folder
	path provided on its own thread.
	It will then pass the observable list into the list view
	So that when the observable monitor updates its observers
	the listview also reflects the change
	 */
	public void addObservers(ListView lv, String path) {

		ObservableList<String> ol = FXCollections.observableArrayList();
		
		//Makes an Observer for path passed in 
		FolderObservable watchFile = new FolderObservable(path);
		
		Thread thread = new Thread(watchFile);
		
		thread.start();
		
		FileObserver fileObserver = new FileObserver(ol);
		
		//Adds fileObserver to watchfile
		watchFile.addObserver(fileObserver);
		
		lv.setItems(ol);
		
		
		
	}
	
	@FXML 
	//Plays Media File in Player or Pauses it if it's playing
	public void play(ActionEvent openPlayer) {
		
        if(playing == false){
            mediaView.getMediaPlayer().play();
            playing = true;
            playButton.setText("Pause");
        }
        else{
            mediaView.getMediaPlayer().pause();
            playing = false;
            playButton.setText("Play");
        }
	}
	

	/*
	Takes the selection from the ListView, returns a file from the directory
	that corresponds to the string taken from listview, it is then loaded into
	the media view so that the video may play.
	 */
	@FXML
	public void load(ActionEvent back) throws MalformedURLException {

		WatchFileInterface watchFile = new WatchFile(localPath);

        String name = (String) listViewLocal.getSelectionModel().getSelectedItem();

        File returnFile = watchFile.ReturnFile(name);

        Media media = new Media(returnFile.toURI().toURL().toString());

        MediaPlayer mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setAutoPlay(false);

        mediaView.setMediaPlayer(mediaPlayer);

	}

	/*
	Takes a file from the download list view and then
	passes that file into another folder
	 */
	@FXML
	public void download(ActionEvent download) throws IOException {
        WatchFileInterface serverWatchFile = new WatchFile(serverPath);
        WatchFileInterface localWatchFile = new WatchFile(localPath);

        String file = (String) listViewServer.getSelectionModel().getSelectedItem();

        localWatchFile.CopyFile(serverWatchFile.ReturnFile(file));



	}
	
}
