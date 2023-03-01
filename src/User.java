package src;
import src.Enums.Permission;
import src.Enums.Role;

public class User {
    private final String name;
    private final Role role;
    private final String department;
    private final String password;

    public User(String name, Role role, String department, String password) {
        this.name = name;
        this.role = role;
        this.department = department;
        this.password = password;
    }

    public User(String name, Role role, String password) {
        this(name, role, null, password);
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public String getDepartment() {
        return department;
    }

    public String getPassword() {
        return password;
    }
}
