package br.com.ufcg.back.services.comparators;

import br.com.ufcg.back.entities.Notifications;

import java.util.Comparator;

public class ComparatorNotificationsByDate implements Comparator<Notifications> {

    @Override
    public int compare(Notifications notification01, Notifications notification02) {
        return (notification01.getCreationDate().compareTo(notification02.getCreationDate()));
    }
}
