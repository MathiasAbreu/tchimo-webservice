package br.com.ufcg.back.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.ufcg.back.entities.Usuario;

@Repository
public interface UsuariosDAO extends JpaRepository<Usuario, Long> {
    Usuario save(Usuario course);

    @Query(value="Select t from Usuario t where t.id=:plogin")
    Usuario findByID(@Param("plogin") Long id);
}
