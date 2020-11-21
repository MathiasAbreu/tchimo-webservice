package br.com.ufcg.back.daos;

import br.com.ufcg.back.entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public interface NotificationDAO<T, ID extends Serializable> extends JpaRepository<Notifications, Long> {

}
