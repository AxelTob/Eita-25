package src;
import src.Enums.Permission;
import src.Enums.Role;

public class User {
    private final String name;
    private final Role role;
    private final String department;

    public User(String name, Role role, String department) {
        this.name = name;
        this.role = role;
        this.department = department;
    }

    public User(String name, Role role) {
        this(name, role, null);
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
}
