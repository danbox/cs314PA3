

/*
 * @file: Library.java
 * @purpose: consists of Library properties and actions, including playlist
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;




public class Library implements Iterable<Song>
{
	//private data members
	private List<Song> owned;
	private Map<Song, Pair<Integer, User>> borrowed;
	private List<Song> loaned;
	private List<Pair<User, Song>> borrowRequests;
	private List<Song> downloaded;

	private List<Pair<User, Boolean>> downloadPermissions;
	
	private Map<String, Library> playlists;
	private User owner;
	
	private Dictionary<Song, Queue<User>> waitingList;
	
	private Dictionary<String, Dictionary<String, Pair<borrowSetting, Integer>>> friendBorrowLimit;
	private Dictionary<String, Dictionary<String, Integer>> friendPlayLimit;
	private Song listeningTo;
	
	//public methods
	
	public Library()
	{
		makeLibrary(null);
	}
	
	public Library(List<Song> songs2) {
		makeLibrary(null, songs2);
	}
	
	public Library(User user)
	{
		makeLibrary(user);
	}
	
	private void makeLibrary(Object owner) {
		makeLibrary(owner, new ArrayList<Song>());
	}
        
	
	private void makeLibrary(Object owner, List<Song> songs) {
		this.owned = songs;
		this.loaned = new ArrayList<Song>();
		this.borrowed = new Hashtable<Song, Pair<Integer, User>>();
		this.playlists = new Hashtable<String, Library>();
		this.owner = (User) owner;
		this.downloadPermissions = new ArrayList<Pair<User, Boolean>>();
		
		this.downloaded = new ArrayList<Song>();
		
    	this.waitingList = new Hashtable<Song, Queue<User>>();
		
		this.friendBorrowLimit = new Hashtable<String, Dictionary<String, Pair<borrowSetting, Integer>>>();
		this.friendPlayLimit = new Hashtable<String, Dictionary<String, Integer>>();
		this.borrowRequests = new LinkedList<Pair<User, Song>>();
		
		//Set default borrow limit to 3 borrows
		setDefaultBorrowLimit(3, borrowSetting.LIMIT);
		setDefaultPlayLimit(3);
	}
	
        public void createBorrowRequest(User friend, Song song)
        {
            borrowRequests.add(new Pair<User, Song>(friend, song));
        }
        
        public void removeBorrowRequest(User friend, Song song)
        {
            Pair<User, Song> p = new Pair<User, Song>(friend, song);
            removeBorrowRequest(p);
        }
        
        public void removeBorrowRequest(Pair<User, Song> p)
        {
            borrowRequests.remove(p);
        }
        
        public void acceptBorrowRequest(User friend, Song song)
        {
            sendBorrow(friend, song);
            removeBorrowRequest(friend, song);
        }
        
        public void acceptBorrowRequest(Pair<User, Song> p)
        {
            sendBorrow(p.fst, p.snd);
            removeBorrowRequest(p);
        }
        
        public List<Pair<User, Song>> getBorrowRequests()
        {
            return borrowRequests;
        }
        
	public User getOwner() 
	{
		return this.owner;
	}
        
        public Map<String, Library> getPlayLists()
        {
            return playlists;
        }
        

	public void setDefaultBorrowLimit(int limit, borrowSetting setting)
	{
		setBorrowLimit("default", "default", limit, setting);
	}
	public void setDefaultPlayLimit(int limit)
	{
		setPlayLimit("default","default", limit);
	}
	public void setSongDefaultBorrowLimit(Song song, int limit, borrowSetting setting)
	{
		setBorrowLimit("default", song.getName(), limit, setting);
	}
	public void setSongDefaultPlayLimit(Song song, int limit)
	{
		setPlayLimit("default", song.getName(), limit);
	}
	public void setFriendDefaultBorrowLimit(User friend, int limit, borrowSetting setting)
	{
		setBorrowLimit(friend.getName(), "default", limit, setting);
	}
	public void setFriendDefaultPlayLimit(User friend, int limit)
	{
		setPlayLimit(friend.getName(), "default", limit);
	}
	public Pair<borrowSetting, Integer> getDefaultBorrowLimit()
	{
		return getSongBorrowLimit("default","default");
	}
	public int getDefaultPlayLimit()
	{
		return getSongPlayLimit("default","default");
	}
	public void setBorrowLimit(User friend, Song song, int limit, borrowSetting setting)
	{
		setBorrowLimit(friend.getName(), song.getName(), limit, setting);
	}
	
	public void setBorrowLimit(String friendName, String songName, int limit, borrowSetting setting) 
	{
		if (getBorrowMap(friendName) == null)
				createBorrowMap(friendName);
		getBorrowMap(friendName).put(songName, new Pair<borrowSetting, Integer>(setting, limit));
	}
	public void setPlayLimit (User friend, Song song, int limit)
	{
		setPlayLimit(friend.getName(), song.getName(),limit);
	}
	public void setPlayLimit (String friendName, String songName, int limit)
	{
		if (getPlayLimitMap(friendName) == null)
				createPlayLimitMap(friendName);
		getPlayLimitMap(friendName).put(songName, limit);
	}
	public Dictionary<String, Pair<borrowSetting, Integer>>  getBorrowMap(User friend)
	{
		return getBorrowMap(friend.getName());
	}
	
	public Dictionary<String, Pair<borrowSetting, Integer>> getBorrowMap(String userName)
	{
		if (this.friendBorrowLimit.get(userName) == null)
			createBorrowMap(userName);
		
		return this.friendBorrowLimit.get(userName);		
	}
	private Dictionary<String,Integer> getPlayLimitMap(User friend)
	{
		return getPlayLimitMap(friend.getName());
	}
	private Dictionary<String,Integer> getPlayLimitMap(String userName)
	{
		if (this.friendPlayLimit.get(userName) == null)
			createPlayLimitMap(userName);
		return this.friendPlayLimit.get(userName);
		
	}
	private void createBorrowMap(String userName)
	{
		this.friendBorrowLimit.put(userName, new Hashtable<String, Pair<borrowSetting, Integer>>());
	}
	private void createPlayLimitMap(String userName)
	{
		this.friendPlayLimit.put(userName, new Hashtable<String,Integer>());
	}
	public Pair<borrowSetting, Integer> getSongBorrowLimit(User user, Song song)
	{
		Pair<borrowSetting, Integer> limit = getSongBorrowLimit(user.getName(), song.getName());
		
		//If not set, get the default for that friend
		if (limit == null) {
			limit = getSongBorrowLimit(user.getName(), "default");
			
			//If that friend doesn't have a default, get the default for the song
			if (limit == null) {
				limit = getSongBorrowLimit("default", song.getName());
				
				//If the song doesn't have a default, get the default for the library
				if (limit == null) {
					limit = getDefaultBorrowLimit();
				}
			}
		}
		
		return limit;
	}
	
	public Pair<borrowSetting, Integer> getSongBorrowLimit(String userName, String songName)
	{
		if (getBorrowMap(userName).get(songName) == null)
			createSongLimit(userName, songName);
		
		return getBorrowMap(userName).get(songName);
	}
	
	private void createSongLimit(String userName, String songName) 
	{
		Pair<borrowSetting, Integer> defaults = getDefaultBorrowLimit();
		getBorrowMap(userName).put(songName, new Pair<borrowSetting, Integer>(defaults.fst, defaults.snd));
	}
	
	public int getSongPlayLimit(User user, Song song) 
	{
		return getSongPlayLimit(user.getName(), song.getName());
	}
	
	public int getSongPlayLimit(String userName, String songName) 
	{
		Dictionary<String, Integer> dict = this.friendPlayLimit.get(userName);
		
		if (dict == null) {
			dict = this.friendPlayLimit.get("default");
			
		}
		if (dict.get(songName) != null){
			return dict.get(songName);
		}
		else {
			if (dict.get("default") != null) return dict.get("default");
			else return this.friendPlayLimit.get("default").get("default");
		}
			
	}

	public boolean getDownloadPermission(User user)
	{
		
		for(Pair<User, Boolean> pair : this.downloadPermissions)
		{
			if(pair.fst.equals(user))
			{
				return pair.snd;
			}
		}
		this.downloadPermissions.add(new Pair<User, Boolean>(user, true));
		return true;
	}
	
	public void setDownloadPermission(User user, boolean perm)
	{
		for(Pair<User, Boolean> pair : this.downloadPermissions)
		{
			if(pair.fst.equals(user))
			{
				pair.snd = perm;
			}
		}
		this.downloadPermissions.add(new Pair<User, Boolean>(user, perm));
	}

	//add song to user library
	public void addSong(Song a)
	{
		if (!this.contains(a))
			owned.add(a);
	}
	
	public void addSongs(Collection<Song> songs)
	{
		for (Song s : songs)
			if (!this.contains(s))
				owned.add(s);
	}
	
	public Song getOwnedSong(String songName, String artist)
	{
		for(int i = 0; i < owned.size(); i++)
		{
			if(owned.get(i).get("name").equalsIgnoreCase(songName) && 
			   owned.get(i).get("artist").equalsIgnoreCase(artist)   )
			{
				return owned.get(i);
			}
		}
		
		return null;
			
	}
	public User getOwnerofBorrowed(Song song) {
		return this.borrowed.get(song).snd;
	}

	public int getPlaysLeftOfBorrowed(Song song)
	{
		return this.borrowed.get(song).fst;
	}

	public void setPlaysToZero(Song song)
	{
		this.borrowed.get(song).fst = 0;
	}
	
	public Song getBorrowedSong(String songName, String artist)
	{
		List<Song> borrowed = this.borrowed();
		for(int i = 0; i < borrowed.size(); i++)
		{
			if(borrowed.get(i).get("name").equalsIgnoreCase(songName) && 
			   borrowed.get(i).get("artist").equalsIgnoreCase(artist)   )
			{
				return borrowed.get(i);
			}
		}
		
		return null;
			
	}
	//remove a song from the user library
	public void removeSong(Song b)
	{
		for (int i = 0; i < owned.size(); i++){
			if(owned.get(i).isEqual(b)){
				owned.remove(i);
			}
		}
	}
	
	public void addBorrowedSong(Song song, int limit, User from)
	{
		this.borrowed.put(song, new Pair<Integer, User>(limit, from));
	}
	
	public void removeBorrowedSong(Song song)
	{
		this.borrowed.remove(song);
	}
	
	public void sendBorrow(User destUser, Song song)
	{
		if (isLoaned(song)||this.isPlayingSong()) {
			if (this.waitingList.get(song) == null)
				this.waitingList.put(song, new LinkedList<User>());
			this.waitingList.get(song).add(destUser);
		}

		else {
			Pair<borrowSetting, Integer> curr = getSongBorrowLimit(destUser, song);
			curr.snd--;
			this.setBorrowLimit(destUser, song, curr.snd, curr.fst);
			this.loaned.add(song);
			
			int limit = getSongPlayLimit(destUser, song);
			
			destUser.getLibrary().addBorrowedSong(song, limit, this.owner);
		}
	}
	
	public boolean hasWaitingList(Song s)
	{
		return this.waitingList.get(s) != null;
	}
	
	public Queue<User> getWaitingListUsers(Song s)
	{
		return this.waitingList.get(s);
	}
	
	public boolean returnBorrow(User borrower, Song song)
	{
		Queue<User> q = this.waitingList.get(song);
		
		borrower.getLibrary().removeBorrowedSong(song);
		
		if (q != null) {
			User next = q.remove();
			
			int limit = getSongPlayLimit(next, song);
			
			next.getLibrary().addBorrowedSong(song, limit, this.owner);
			
			if (q.isEmpty())
				this.waitingList.remove(song);
		}  else {
			this.loaned.remove(song);
			return true;
		}
		
		return false;
	}
	
	//create a playlist for the user based on a selection of songs
	public void createPlaylist(String name, List<Song> songs)
	{
		if (this.playlists.get(name) != null) 
			this.playlists.get(name).addSongs(songs);
		else
			this.playlists.put(name, new Library(songs));
	}
	
        public void removePlaylist(String name)
        {
            this.playlists.remove(name);
        }
	public void play(Song song)
	{
		if (this.listeningTo != null)
			this.stop();
		if(!(isBorrowed(song)||isOwned(song))||isLoaned(song)) return;
		if (isBorrowed(song)) {
			int limit = this.borrowed.get(song).fst;
			User from = this.borrowed.get(song).snd;
			
			this.borrowed.put(song, new Pair<Integer, User>(--limit, from));
		}	
		
		this.listeningTo = song;
	}
	
	public void stop()
	{
		//check if borrowed
		if (listeningTo == null) return;
		if (isBorrowed(listeningTo)){
			if (this.borrowed.get(listeningTo).fst == 0) 
			{
				User from = this.borrowed.get(listeningTo).snd;
			
				from.getLibrary().returnBorrow(this.owner, this.listeningTo);
			}
		}
		//check if song was added to waitlist while listening
		Queue<User> q = this.waitingList.get(listeningTo);

		
		if (q != null) {
			User next = q.remove();
			
			int limit = getSongPlayLimit(next, listeningTo);
			
			next.getLibrary().addBorrowedSong(listeningTo, limit, this.owner);
			this.loaned.add(listeningTo);
			if (q.isEmpty())
				this.waitingList.remove(listeningTo);
		}
		this.listeningTo = null;
	}
	
	public boolean contains(Song song) {
		return toList().contains(song);
	}
	
	//display the user's library based on the string value(artist, song ,album)
	public List<Song> search(String query)
	{
		List<Song> result = new LinkedList<Song>();
		
		query = query.toLowerCase();
		
		for (Song s : toList()) {
			if (s.getName().toLowerCase().contains(query) || s.get("artist").toLowerCase().contains(query) || s.get("album").toLowerCase().contains(query))
				result.add(s);
		}
		
		return result;
	}	
	
	public List<Song> toList() {
		List<Song> result = new LinkedList<Song>();
		
		result.addAll(this.owned);
		result.addAll(this.borrowed.keySet());
		
		result.removeAll(this.loaned);
		
		return result;
	}
	
	public List<Song> toSortedList(String sortBy)
	{
		List<Song> result = new LinkedList<Song>(owned);
		Collections.sort(result,  new Song.SongComparator(sortBy));
		return result;
	}
	
	//Return songs owned by this user
	public List<Song> owned() {
		return owned;
	}
	
	public List<Song> borrowed() {
		return new LinkedList<Song>(borrowed.keySet());
	}
	
	public List<Song> loaned() {
		return loaned;
	}
	
	//check if a song can be borrowed
	public boolean checkIfBorrowable(User friend, Song song)
	{
		Pair<borrowSetting, Integer> limit = getSongBorrowLimit(friend, song);
		
		if (limit.fst != borrowSetting.NO && limit.snd > 0 && !this.hasDownloadedSong(song))
			return true;
		else
			return false;
	}
	public boolean isPlayingSong()
	{
		if(this.listeningTo == null) return false;
		else return true;
	}
	public boolean isAvailableToPlay(Song song)
	{
		//if the song is in possession not loaned and no other song is playing
		if (this.contains(song) && !this.loaned.contains(song) && this.listeningTo == null && !this.hasDownloadedSong(song))
		{
			return true;
		} else return false;
	}

	public List<Song> downloaded()
	{
		return this.downloaded;
	}
	
	public boolean hasDownloadedSong(Song s)
	{
		return this.downloaded.contains(s);
	}

	public boolean isAvailableToDownload(Song song)
	{
		if(this.contains(song) && !this.loaned.contains(song) && this.listeningTo == null && !this.hasDownloadedSong(song))
		{
			if(this.borrowed().contains(song))
			{
				if(this.getOwnerofBorrowed(song).getLibrary().getDownloadPermission(owner))
				{
					return true;
				} else
				{
					return false;
				}
			} else
			{
				return true;
			}
		} else return false;
	}

	public void download(Song s)
	{
		if(isAvailableToDownload(s))
		{
			System.out.println("THIS");
			this.downloaded.add(s);
		}
	}
	
	public void expireDownload(Song s)
	{
		if(this.hasDownloadedSong(s))
		{
			this.downloaded.remove(s);
			
			if(this.isBorrowed(s))
			{
				if(this.getPlaysLeftOfBorrowed(s) == 0)
				{
					this.getOwnerofBorrowed(s).getLibrary().returnBorrow(owner, s);
				}
			}
		}
		
	}

	public void expireAllDownloads()
	{
		for(Song s : this.downloaded)
		{
			if(this.isBorrowed(s))
			{
				if(this.getPlaysLeftOfBorrowed(s) == 0)
				{
					this.getOwnerofBorrowed(s).getLibrary().returnBorrow(owner, s);
				}
			}
		}
		this.downloaded.clear();
	}
	
	public boolean isOwned(Song song)
	{
		return (this.owned.contains(song));
	}
	
	public boolean isBorrowed(Song song)
	{
		return (this.borrowed.get(song) != null);
	}
	
	public boolean isLoaned(Song song)
	{
		return this.loaned.contains(song);
	}
	
	//it.getKey() == name
	//it.getValue() == the playlist
	public Iterator<Map.Entry<String, Library>> playlist_iter() {
		return playlists.entrySet().iterator();
	}
	
	@Override
	public Iterator<Song> iterator() {
		return owned.iterator();
	}

	public Library getPlaylist(String name) {
		return this.playlists.get(name);
	}
        
        public boolean isLIMIT(borrowSetting b)
        {
            return b == borrowSetting.LIMIT;
        }
	
	public enum borrowSetting {
        NO, LIMIT, APPROVE
	}
}
