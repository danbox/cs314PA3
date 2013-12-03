

/*
 * @file: MusicManager.java
 * @purpose: manages music relationships and actions
 */


import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;



public class MusicManager 
{
	//Singleton
	private static MusicManager me;

    //private data members
	private UserManager users;
    private Library globalLibrary;

    //private methods
    
    protected MusicManager() {
    	users = UserManager.instance();
    	globalLibrary = new Library();
    }
    
	//public methods
    
    public static MusicManager instance()
    {
    	if (me == null)
    		me = new MusicManager();
    	return me;
    }

    public Library getGlobalLibrary() {
        return globalLibrary;
    }
    
	//returns a dictionary of songs, each with a list of friends who own that song.
	public Dictionary<Song, List<User>> browseFriendsMusic(User user)
	{
		Hashtable<Song, List<User>> result = new Hashtable<Song, List<User>>();
		
		for (User f : user.getFriends()) {
			for(Song s : f.getLibrary()) {
				updateUserList(result, f, s);
			}
		}
		
		return result;
	}
	
	//search for songs based on a friend, returns a list of songs that the friend owns
	public List<Song> searchFriendsMusic(User user, User friend)
	{
		return searchFriendsMusic(user, friend, "name");
	}
	public List<Song> searchFriendsMusic(User user, User friend, String sortBy)
	{
		if (user.isFriendsWith(friend)) {
			List<Song> result = friend.getLibrary().owned();
			Collections.sort(result, new Song.SongComparator(sortBy));
			return result;
		}
		return null;
	}
	
	public Set<Map.Entry<Song, List<User>>> searchFriendsMusic(User user, String query)
	{
		Hashtable<Song, List<User>> result = new Hashtable<Song, List<User>>();
		
		for (User f : user.getFriends())
			for (Song s : f.getLibrary().search(query))
				updateUserList(result, f, s);
		
		return result.entrySet();
	}
	
	private List<User> updateUserList(Dictionary<Song, List<User>> r, User f, Song s) {
		List<User> userList = r.get(s);
		if ( userList == null) {
			userList = new LinkedList<User>();
			userList.add(f);
			r.put(s, userList);
		} else userList.add(f);
		
		return userList;
	}
	
	//search for friends based on a song, returns a list of friends
	public List<User> searchFriendsMusic(User user, Song song)
	{
		return searchForUsersBySong(user.getFriends(), song);
	}
	
	//search for users friend or not based on a song, returns a list of Users
	public List<User> searchMusic(Song song)
	{
		return searchForUsersBySong(users, song);
	}
	
	private List<User> searchForUsersBySong(Iterable<User> coll, Song song)
	{
		List<User> result = new LinkedList<User>();
		
		for (User u : coll) {
			if (u.getLibrary().contains(song))
				result.add(u);
		}
		
		return result;
	}
	
	//add a song to a user's library
	public void addSong(User user, Song song)
	{
		user.getLibrary().addSong(song);
	}
	
	//remove a song from a user's library
	public void removeSong(User user, Song song)
	{
		user.getLibrary().removeSong(song);
	}
	
	//creates a playlist for a user based on a selection of songs
	public void createPlaylist(User user, List<Song> songs)
	{
		createPlaylist(user, "untitled", songs);
	}
	
	public void createPlaylist(User user, String plname, List<Song> songs)
	{
		user.getLibrary().createPlaylist(plname, songs);
	}
	
	public void addToPlaylist(User user, String name, List<Song> songs)
	{
		Library playlist = user.getLibrary().getPlaylist(name);
		
		if (playlist == null)
			createPlaylist(user, name, songs);
		else
			playlist.addSongs(songs);
	}
	
	//get a list of songs from user library based on artist, song, or album (string)
	public List<Song> getList(User user, String query)
	{
		return user.getLibrary().search(query);
	}
	
	
	//add a user library to the global library
	public void addToLib(Library lib)
	{
		for(Song s : lib)
			globalLibrary.addSong(s);
	}
	
	public void borrowToLib(User source, User dest, Song song)
    {
		
		Library srcLib = source.getLibrary();
		
		//Song is owned
    	if (srcLib.isOwned(song)) {
    		//User has permission
    		if (srcLib.checkIfBorrowable(dest, song))
    			//Song isn't already borrowed
    			srcLib.sendBorrow(dest, song);
//    		else
//    			No permission
    	}	
    }
	
	public void playSong(User user, Song song)
	{
		user.getLibrary().play(song);
	}
	
	public void stopSong(User user)
	{
		user.getLibrary().stop();
	}
	
	//take back a borrowed song from the users, Song argument is from the user's library
	public void takeBack(User borrower, Song song)
	{
		if(borrower.getLibrary().isBorrowed(song)) {
			if(borrower.getLibrary().isPlayingSong()) borrower.getLibrary().stop();
			
			User owner = borrower.getLibrary().getOwnerofBorrowed(song);
			
			owner.getLibrary().returnBorrow(borrower, song);
		}
		//else song not borrowed
	}
}

