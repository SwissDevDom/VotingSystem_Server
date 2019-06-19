package ch.fhnw.kvanc.server.web;

import javax.validation.constraints.NotNull;

/**
 * VoteDTO
 *
 * Dieses DTO umfasst die Antwort auf die Frage und kann entweder "true" oder
 * "false" sein. Das DTO wird vom Spring Framework für die Deserialiserung des JSON-Requests
 * eingesetzt.
 */
public class VoteDTO {
    @NotNull
    private Boolean vote;

    /**
     * @param vote the vote to set
     */
    public void setVote(Boolean vote) {
        this.vote = vote;
    };

    public Boolean getVote() {
        return vote;
    }

}