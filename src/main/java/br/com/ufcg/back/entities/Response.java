package br.com.ufcg.back.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Response {

    private Long id_notification;
    private boolean procedure;

    @JsonCreator
    public Response(Long id_notification, boolean procedure) {

        super();
        this.id_notification = id_notification;
        this.procedure = procedure;
    }

    public Long getId_notification() {
        return id_notification;
    }

    public void setId_notification(Long id_notification) {
        this.id_notification = id_notification;
    }

    public boolean isProcedure() {
        return procedure;
    }

    public void setProcedure(boolean procedure) {
        this.procedure = procedure;
    }
}
