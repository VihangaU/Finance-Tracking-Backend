package financeTracking.app.model;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 50)
    private String password;

    @DBRef
    private Set<Role> roles = new HashSet<>();

    @NotBlank
    private String currency;

    private List<String> preferredCurrencies;

    public User(String username, String email, String password, String currency, Set<Role> roles, List<String> preferredCurrencies) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.currency = currency;
        this.roles = roles;
        this.preferredCurrencies = preferredCurrencies;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<String> getPreferredCurrencies() {
        return preferredCurrencies;
    }

    public void setPreferredCurrencies(List<String> preferredCurrencies) {
        this.preferredCurrencies = preferredCurrencies;
    }

}
