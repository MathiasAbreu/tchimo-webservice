package br.com.ufcg.back.daos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.ufcg.back.entities.Usuario;

import java.io.Serializable;

@Repository
public interface UsuariosDAO<T, ID extends Serializable> extends JpaRepository<Usuario, String> {

}
