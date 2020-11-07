package br.com.ufcg.back.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.ufcg.back.entities.Turma;

import java.io.Serializable;

@Repository
public interface TurmasDAO<T, ID extends Serializable> extends JpaRepository<Turma, String> {
    Turma save(Turma course);

    @Query(value="Select t from Turma t where t.id=:plogin")
    Turma findByID(@Param("plogin") String id);
}
