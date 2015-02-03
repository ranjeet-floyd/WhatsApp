package in.istore.bitblue.app.pojo;

public class Admin {
    private String Name;
    private String Email;

    public Admin() {
    }

    public Admin(String name, String email) {
        Name = name;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
