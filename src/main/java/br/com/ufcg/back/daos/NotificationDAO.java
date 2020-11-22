package br.com.ufcg.back.daos;

import br.com.ufcg.back.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface NotificationDAO<T, ID extends Serializable> extends JpaRepository<Notification, Long> {

    @Query(value = "Select p.* from Notifications p where p.id_user =:plogin",nativeQuery = true)
    List<Notification> findByIdUser(@Param("plogin") Long id_user);
}
