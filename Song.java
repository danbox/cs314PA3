

/*
 * @file: Song.java
 * @purpose: consists of song meta data
 */


import java.util.Comparator;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Song
{
    //private data members
	private String name;
	private Metadata data;
	private int downloadCount;
	private int playedCount;
	private int borrowedCount;
	
	//public methods
	

	public Song(String name, String artist) {
		this.name = name;
		
		data = new Metadata();
        data.put("artist", artist);
		data.put("name", name);
	}
	
	public Song(String name, String[][] arr) {
		this.name = name;
		
		data = new Metadata(arr);
		data.put("name", name);
	}
	
	//TODO: Throw an error when there is no name;
	public Song(String[][] arr) {
		data = new Metadata(arr);
		
		this.name = data.get("name");
	}
	
	public Song(Metadata m) {
		this.data = m;
		
		this.name = data.get("name");
	}
	
	public String toString() {
		return this.name +  "\t" + data.toString();
	}
	
	public String get(String key) {
		if (key.equalsIgnoreCase("name"))
			return this.name;
		return this.data.get(key);
	}

	public void incrementDownloadedCount()
	{
		this.downloadCount++;
	}

	public void incrementPlayedCount()
	{ 
		this.playedCount++;
	}

	public void incrementBorrowedCount()
	{
		this.borrowedCount++;
	}

	public int getDownloadedCount()
	{
		return this.downloadCount;
	}

	public int getPlayedCount()
	{
		return this.playedCount;
	}

	public int getBorrowedCount()
	{
		return this.borrowedCount;
	}
	public String getName() {
		return this.name;
	}
        
	public int getYear() {
		return Integer.valueOf(this.data.get("year"));
	}
	
	public Metadata getMetaData() {
		return data;
	}
	
	public boolean isEqual(Object a){
		if (Song.class.isInstance(a)) {
			Song b = (Song) a;
			if (b.getName() == this.name &&	b.getMetaData() == this.data)
				return true;
		}
		return false;
	}
	
	public static class SongComparator implements Comparator<Song> {
		public String sortBy;
		
		public SongComparator(String query) {
			this.sortBy = query;
		}
		
		public int compare(Song s1, Song s2) {
			if (sortBy.equalsIgnoreCase("name"))
				return s1.getName().compareToIgnoreCase(s2.getName());
			else if (sortBy.equalsIgnoreCase("artist"))
				return s1.get("artist").compareToIgnoreCase(s2.get("artist"));
			else if (sortBy.equalsIgnoreCase("album"))
				return s1.get("album").compareToIgnoreCase(s2.get("album"));
			else if(sortBy.equalsIgnoreCase("played"))
				return new Integer(s1.getPlayedCount()).compareTo(s2.getPlayedCount());
			else if(sortBy.equalsIgnoreCase("borrowed"))
				return new Integer(s1.getBorrowedCount()).compareTo(s2.getBorrowedCount());
			else if(sortBy.equalsIgnoreCase("downloaded"))
				return new Integer(s1.getDownloadedCount()).compareTo(s2.getBorrowedCount());
			else
				return 0;
		}
		
		public boolean equals(Object o) {
			if (SongComparator.class.isInstance(o))
				return (((SongComparator) o).sortBy.equals(this.sortBy));
			return false;
		}
	}
}
