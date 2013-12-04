

/*
 * @file: User.java
 * @purpose: consists of User properties and actions
 */


import java.util.LinkedList;
import java.util.List;



public class User
{
    //private data members
	private String username;
	private String password;
	private Library mylibrary;
	private List<User> invites;
	private List<User> myfriends;
	private List<String> notifications;
	private PermType libpermview;
	
	//public methods
	
	public User(String name, String password) {
		this.username = name;
		this.password = password;
		
		this.invites = new LinkedList<User>();
		this.myfriends = new LinkedList<User>();

		this.notifications = new LinkedList<String>();
		
		this.mylibrary = new Library(this);
	}

	public void addNotification(String notification)
	{
		notifications.add(notification);
	}

	public List<String> getNotifications()
	{
		return notifications;
	}
	
	public boolean hasNotifications()
	{
		return !notifications.isEmpty();
	}

	public void clearNotifications()
	{
		notifications.clear();
	}

	//accept an invite from the user(string) if invite exist
	public void acceptInvite(User friend)
	{
		if (this.invites.contains(friend)) {
			if (!this.isFriendsWith(friend)) {
				this.myfriends.add(friend);
				friend.getFriends().add(this);
				//this.getLibrary().
			}
			
			this.invites.remove(friend);
		}
	}
	
	//add invite to list of invites
	public void addInvite(User a) 
	{
		if (!this.invites.contains(a))
			this.invites.add(a);
	}
        
        public void removeInvite(User a)
        {
//            if(this.invites.contains(a))
                this.invites.remove(a);
        }
        
        public void removeFriend(User a)
        {
            if(this.isFriendsWith(a))
            {
                this.myfriends.remove(a);
            }
        }
	
	public void sendInvite(User b)
	{
		b.addInvite(this);
	}
	
	public void setPerm(PermType p) {
		this.libpermview = p;
	}
	
	public PermType getPerm() {
		return this.libpermview;
	}
	
	public void setPublic() {
		setPerm(PermType.ALL);
	}
	
	public void setFriendsOnly() {
		setPerm(PermType.FRIENDS);
	}
	
	public void setPrivate() {
		setPerm(PermType.NONE);
	}
	
	public String getName() {
		return username;
	}
	
	public boolean checkPassword(String pwd) {
		return checkPassword(false, pwd);
	}
	
	public boolean checkPassword(Boolean hashed, String hash) {
		if (hashed) {
//			if (hash.equals(hash(this.password)))
				return true;
		}
		else 
			if (hash.equals(this.password))
				return true;
		return false;
	}
	
	public List<User> getFriends() {
		return this.myfriends;
	}
	
	public List<String> getFriendList() {
		List<String> result = new LinkedList<String>();
		for (User f : this.myfriends)
			result.add(f.getName());
		
		return result;
	}
        
        public List<User> getInvites()
        {
            return this.invites;
        }
	
	public Library getLibrary() {
		return this.mylibrary;
	}
	
	public String getUsername(){
		return this.username;
	}

    public void addFriend(User friend)
    {
        if(!isFriendsWith(friend))
        {
            myfriends.add(friend);
        }
    }
	
	public boolean isFriendsWith(User other)
	{
		return (this.myfriends.contains(other));
	}
	
	public boolean isEqual(Object o) {
		if (User.class.isInstance(o)) {
			if(this.username.equalsIgnoreCase(((User) o).getName())) 
				return true;
		}
		return false;
	}
        
        @Override public String toString()
        {
            return username;
        }
	
}
