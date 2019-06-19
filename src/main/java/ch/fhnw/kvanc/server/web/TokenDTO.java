package ch.fhnw.kvanc.server.web;

/**
 * TokenDTO
 *
 * Dieses DTO wird sowohl bei der Registrierung vom Request und als auch bei
 * der Response verwendet. Das TokenDTO umfasst zwei Properties:
 * - token (String):
 * wird vom Server generiert und in der Response gesetzt. Es dient als
 * eindeutige Identifikation bei allen folgenden Requests.
 *
 * -email (String):
 * wird für die Registrierung im Request eingesetzt. Die Email muss
 * innerhalb der Applikation eindeutig sein.
 */
public class TokenDTO {
    private String token;
    private String email;

    public TokenDTO() {
    }
    
    public TokenDTO(String token, String email) {
        this.token = token;
        this.email = email;
    }
    
    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
}