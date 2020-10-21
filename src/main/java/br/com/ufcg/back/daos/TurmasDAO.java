package br.com.ufcg.back.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.ufcg.back.entities.Turma;

@Repository
public interface TurmasDAO extends JpaRepository<Turma, Long> {
    Turma save(Turma course);

    @Query(value="Select t from Turma t where t.id=:plogin")
    Turma findByID(@Param("plogin") Long id);
}
