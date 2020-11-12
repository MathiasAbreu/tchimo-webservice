package br.com.ufcg.back.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.ufcg.back.entities.Usuario;

import java.io.Serializable;
import java.util.Optional;

@Repository
public interface UsuariosDAO<T, ID extends Serializable> extends JpaRepository<Usuario, Long> {

    @Query(value = "SELECT usuario FROM Usuario usuario WHERE usuario.email LIKE %:email%")
    Optional<Usuario> findByEmail(@Param("email") String email);
}
