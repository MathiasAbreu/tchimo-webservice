package br.com.ufcg.back;

import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TchimoTests {

	@Test
	public void testaAtributosUsuarios() {
		//Assert para objeto vazio
		Usuario usuarioNulo = new Usuario();
		assertEquals(null, usuarioNulo.getEmail());
		assertEquals(null, usuarioNulo.getPassword());
		assertEquals(null, usuarioNulo.getName());

		Usuario usuarioEmTeste = new Usuario(1L, "anne@gmail.com", "123456", "Anne");
		// Asserts para @email, @password e @name
		assertEquals("anne@gmail.com", 	usuarioEmTeste.getEmail());
		assertEquals("123456", 			usuarioEmTeste.getPassword());
		assertEquals("Anne", 			usuarioEmTeste.getName());

		// Modifica atributos passíveis de mudança por meio de metodos set
		usuarioEmTeste.setEmail("gilmar@gmail.com");
		usuarioEmTeste.setPassword("654321");
		usuarioEmTeste.setName("Gilmar");

		// Asserts para @email, @password e @name
		assertEquals("gilmar@gmail.com", 	usuarioEmTeste.getEmail());
		assertEquals("654321", 				usuarioEmTeste.getPassword());
		assertEquals("Gilmar", 				usuarioEmTeste.getName());

		// Asserts para métedos de addTurma e removeTurma
		Turma turmaEmTeste = new Turma("T01", "FS", "ES", 3, 10, 2);
		usuarioEmTeste.addTurma(turmaEmTeste);
		boolean contemTurma = usuarioEmTeste.getMembersTurma().contains(turmaEmTeste);
		assertTrue(contemTurma);

	}
	@Test
	public void testaAtributosTurma() {
		//Assert para objeto vazio
		Turma turmaNula = new Turma();
		assertEquals(null, 	turmaNula.getName());
		assertEquals(null, 	turmaNula.getFormationStrategy());
		assertEquals(null, 	turmaNula.getEndingStrategy());
		assertEquals(0.0, 	turmaNula.getQuantityOfGroups());


		Turma turmaEmTeste = new Turma("T01", "FS", "ES", 3, 10, 2);

		// Asserts para @name, @creationDate, @formationStrategy, @endingStrategy, @quantityOfGroups, @endTime e @minutes
		long creationDate = ((new Date()).getTime() / 1000L);
		long endDate = creationDate + ((10 * 3600) + (2 * 60));

		assertEquals("T01", turmaEmTeste.getName());
		assertEquals(creationDate, 	turmaEmTeste.getCreationDate());
		assertEquals("FS", 	turmaEmTeste.getFormationStrategy());
		assertEquals("ES", 	turmaEmTeste.getEndingStrategy());
		assertEquals(3, 	turmaEmTeste.getQuantityOfGroups());
		assertEquals(endDate, 		turmaEmTeste.getEndDate());

		// Assert para @manager
		Usuario monitor = new Usuario(1L, "anne@gmail.com", "123456", "Anne");
		turmaEmTeste.setManager(monitor);
		assertEquals(monitor, turmaEmTeste.getManager());

		Usuario professor = new Usuario(2L, "rohit@gmail.com", "654321", "Rohit");

		// Modifica atributos passíveis de mudança por meio de metodos set
		turmaEmTeste.setName("Turma 1");
		turmaEmTeste.setManager(professor);

		// Asserts para @name e @manager
		assertEquals("Turma 1", turmaEmTeste.getName());
		assertEquals(professor, 		turmaEmTeste.getManager());

	}

	@Test
	public void testaAtributosGrupo() {
		//Assert para objeto vazio
		Grupo grupoNulo = new Grupo();
		assertEquals(null, 	grupoNulo.getIdGroup());
		assertEquals(null, 	grupoNulo.getEmailManager());
		assertEquals(0, 	grupoNulo.amountOfMembers());

		Usuario aluno1 = new Usuario(1L, "aluno1@ccc.ufcg.edu.br", "123456", "Aluno 1");
		Grupo grupoEmTeste = new Grupo(1, "aluno1@ccc.ufcg.edu.br",1L);
		// Assert para @idGroup e @emailManager
		assertEquals(1, 						grupoEmTeste.getIdGroup());
		assertEquals("aluno1@ccc.ufcg.edu.br", 	grupoEmTeste.getEmailManager());
		assertEquals(1, 						grupoEmTeste.amountOfMembers());

		Usuario aluno2 = new Usuario(2L, "aluno2@ccc.ufcg.edu.br", "123456", "Aluno 2");
		Usuario aluno3 = new Usuario(3L, "aluno3@ccc.ufcg.edu.br", "123456", "Aluno 3");

		// Asserts para métedos de addUser e removeUser
		try {
			grupoEmTeste.addUser(2L);
		} catch (UserAlreadyExistException e) {
			e.printStackTrace();
		}
		try {
			grupoEmTeste.addUser(3L);
		} catch (UserAlreadyExistException e) {
			e.printStackTrace();
		}
		assertEquals(3, grupoEmTeste.amountOfMembers());
	}

	@Test
	public void testaFuncionalidadesTurma() {
		// Asserts para métedos de addTurma e removeTurma
		Turma turmaEmTeste = new Turma("T01", "FS", "ES", 3, 10, 2);

	}

	@Test
	public void testaGetsESetsEntidades() {
		Usuario usuario1 = new Usuario(1L, "g@gmail", "123", "Gilmar");
		Turma t1 = new Turma("T01", "FS", "ES", 3, 10, 2);
		Grupo grupo1 = new Grupo(1, "g@gmail",1L);

		t1.addUser(usuario1);
		usuario1.addTurma(t1);

		t1.setId("test");
		assertEquals("test", t1.getId());
		t1.setManager(usuario1);
		assertEquals(usuario1, t1.getManager());
		assertEquals("T01", t1.getName());
		t1.setName(" ");
		assertEquals(" ", t1.getName());
		assertEquals("FS", t1.getFormationStrategy());

		assertEquals("ES", t1.getEndingStrategy());
		assertEquals(1, t1.getIntegrantes().size());
		assertEquals(3, t1.getQuantityOfGroups());
		assertEquals(((new Date()).getTime() / 1000L) + ((10 * 3600) + (2 * 60)), t1.getEndDate());

		assertEquals(1L, usuario1.getIdUser());
		assertEquals(1, usuario1.getMembersTurma().size());
	}
	@Test
	public void testaContrutorVazioEntidades() {
		Usuario usuarioVazio = new Usuario();
		Turma tVazia = new Turma();
		Grupo grupoVazio = new Grupo();
	}
	@Test
	public void testaMetodosEntidades() {
		Usuario usuario1 = new Usuario(1L, "g@gmail", "123", "Gilmar");
		Turma t2 = new Turma("T01", "FS", "ES", 3, 10, 2);
		Grupo grupo1 = new Grupo(1L, "g@gmail",1L);

		t2.adicionaGrupo(grupo1);

		try {
			t2.removeUserFromGroup(1L,1L,"g@gmail");
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		} catch (GroupNotFoundException e) {
			e.printStackTrace();
		}
		t2.addUser(usuario1);
		Assertions.assertTrue(t2.verificaSeUsuarioJaPertece("g@gmail"));
		t2.removeUser("g@gmail");

		assertEquals(0, t2.quantidadeGruposNaTurma());
		t2.addQGrupo();
		assertEquals(1, t2.quantidadeGruposNaTurma());
		assertFalse(t2.verificaSeUsuarioJaPertece("ggmail"));

	}
}



