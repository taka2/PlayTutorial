package controllers;
 
import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;
 
@With(Secure.class)
public class Admin extends Controller {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byEmail", Security.connected()).first();
            renderArgs.put("user", user.fullname);
        }
    }
 
    public static void index() {
	    String user = Security.connected();
	    List<Post> posts = Post.find("author.email", user).fetch();
	    render(posts);
    }
	 
	public static void save(Long id, String title, String content, String tags) {
	    Post post;
	    if(id == null) {
	        // Create post
	        User author = User.find("byEmail", Security.connected()).first();
	        post = new Post(author, title, content);
	    } else {
	        // Retrieve post
	        post = Post.findById(id);
	    	// Check
	    	
	        // Edit
	        post.title = title;
	        post.content = content;
	        post.tags.clear();
	    }
	    // Set tags list
	    for(String tag : tags.split("\\s+")) {
	        if(tag.trim().length() > 0) {
	            post.tags.add(Tag.findOrCreateByName(tag));
	        }
	    }
	    // Validate
	    validation.valid(post);
	    if(validation.hasErrors()) {
	        render("@form", post);
	    }
	    // Save
	    post.save();
	    index();
	}

	public static void form(Long id) {
	    if(id != null) {
	        //Post post = Post.findById(id);
	    	User author = User.find("byEmail", Security.connected()).first();
	    	Post post = Post.findByIdAndAuthor(id, author);

	    	if(post == null) {
	    		index();
	    	}
	        render(post);
	    }
	    render();
	}
}