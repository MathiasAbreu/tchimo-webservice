package br.com.ufcg.back.daos;

import br.com.ufcg.back.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface GruposDAO<T, ID extends Serializable> extends JpaRepository<Grupo, Long> {

}
