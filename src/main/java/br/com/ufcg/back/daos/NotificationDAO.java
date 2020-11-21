package br.com.ufcg.back.daos;

import br.com.ufcg.back.entities.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public interface NotificationDAO<T, ID extends Serializable> extends JpaRepository<Notifications, Long> {

    @Query(value = "Select p.* from Notifications p where p.id_user =:plogin",nativeQuery = true)
    List<Notifications> findByIdUser(@Param("plogin") Long id_user);
}
