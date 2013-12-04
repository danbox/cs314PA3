

/*
 * @file: UserManager.java
 * @purpose: manages user relationships and actions
 */


import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class UserManager implements Iterable<User>
{
	//Singleton
	private static UserManager me;
	
    //private data members
    private Hashtable<String, User> users;

    //private methods
    protected UserManager()
    {
    	users = new Hashtable<String, User>();
    }

    //public methods
    public static UserManager instance()
    {
    	if (me == null)
    		me = new UserManager();
    	return me;
    }
    
    public Hashtable<String, User> getUsers()
    {
        return users;
    }
    
    public void addUser(User u)
    {
    	users.put(u.getName(), u);
    }
	
	public void expireAllDownloads()
	{
		List<User> all = this.toList();
		for(User u : all)
		{
			u.getLibrary().expireAllDownloads();
		}
	}
    
    public User findUser(String name)
    {
    	return users.get(name);
    }
    
    public List<User> toList() {
    	return new LinkedList<User>(users.values());
    }

	@Override
	public Iterator<User> iterator() {
		return users.values().iterator();
	}

}
