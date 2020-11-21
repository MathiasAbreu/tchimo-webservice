package br.com.ufcg.back.services.comparators;

import br.com.ufcg.back.entities.Notification;

import java.util.Comparator;

public class ComparatorNotificationsByDate implements Comparator<Notification> {

    @Override
    public int compare(Notification notification01, Notification notification02) {
        return (notification01.getCreationDate().compareTo(notification02.getCreationDate()));
    }
}
