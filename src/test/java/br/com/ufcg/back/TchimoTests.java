package br.com.ufcg.back;

import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Notifications;
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
	public void testaAtributosDoUsuarios() {
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

	}
	@Test
	public void testaAtributosDaTurma() {
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
	public void testaAtributosDoGrupo() {
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
	public void testaAtributosDasNotificacoes() {
		//Assert para objeto vazio
		Notifications notificacoesNulo = new Notifications();
		assertEquals(null, 	notificacoesNulo.getId());
		assertEquals(null, 	notificacoesNulo.getId_user());
		assertEquals(null, 	notificacoesNulo.getId_turma());
		assertEquals(null, 	notificacoesNulo.getCreationDate());

	}

	@Test
	public void testaCadastrarUsuario() {
		Usuario usuario1 = new Usuario(1L, "aluno1@ccc.ufcg.edu.br", "123456", "Anne");
		Usuario usuario2 = new Usuario(1L, "aluno1@ccc.ufcg.edu.br", "654321", "Gilmar");
		Usuario usuario3 = new Usuario(2L, "aluno3@ccc.ufcg.edu.br", "654321", "Gilmar");

		// Assert para usuario com mesmo email
		assertTrue(usuario1.equals(usuario2));
		assertFalse(usuario1.equals(usuario3));
	}

	@Test
	public void testaCadastrarTurma() {
		Turma turma1 = new Turma("T01", "FS", "ES", 3, 10, 2);
		turma1.setId("T01");

		Turma turma2 = new Turma("T01", "FS", "ES", 3, 10, 2);
		turma2.setId("T01");

		Turma turma3 = new Turma("T01", "FS", "ES", 3, 10, 2);
		turma3.setId("T03");

		// Assert para turmas com mesmo parametros de construcao
		assertTrue(turma1.equals(turma2));
		assertFalse(turma1.equals(turma3));

		Usuario usuario1 = new Usuario(1L, "aluno1@ccc.ufcg.edu.br", "123456", "Anne");
		Usuario usuario2 = new Usuario(2L, "aluno2@ccc.ufcg.edu.br", "654321", "Gilmar");

		// Fluxo de cadastro de turma para usuario1
		usuario1.addTurma(turma1);
		usuario1.addTurma(turma2);

		turma1.setManager(usuario1);

		assertFalse(turma1.verificaSeUsuarioJaPertece("aluno1@ccc.ufcg.edu.br"));
		assertFalse(turma2.verificaSeUsuarioJaPertece("aluno1@ccc.ufcg.edu.br"));
		turma1.addUser(usuario1);
		turma2.addUser(usuario1);
		assertTrue(turma1.verificaSeUsuarioJaPertece("aluno1@ccc.ufcg.edu.br"));
		assertTrue(turma2.verificaSeUsuarioJaPertece("aluno1@ccc.ufcg.edu.br"));

		// Fluxo de cadastro de turma para usuario1
		turma1.addUser(usuario2);
		turma2.addUser(usuario2);
		assertTrue(turma1.verificaSeUsuarioJaPertece("aluno2@ccc.ufcg.edu.br"));
		assertTrue(turma2.verificaSeUsuarioJaPertece("aluno2@ccc.ufcg.edu.br"));

	}

	@Test
	public void testaCadastrarGrupo() {
		Grupo grupo1 = new Grupo(1, "aluno1@ccc.ufcg.edu.br",1L);
		Grupo grupo2 = new Grupo(1, "aluno1@ccc.ufcg.edu.br",1L);
		Grupo grupo3 = new Grupo(3, "aluno3@ccc.ufcg.edu.br",3L);

		// Assert para grupos como mesmos parametros de construcao
		assertTrue(grupo1.equals(grupo2));
		assertFalse(grupo1.equals(grupo3));

	}

	@Test
	public void testaAcessarDadosTurma() {
	}

	@Test
	public void testaAcessarDadosGrupo() {
	}

	@Test
	public void testaSairDeTurma() {
		Turma turma1 = new Turma("T01", "FS", "ES", 3, 10, 2);
		Turma turma2 = new Turma("T01", "FS", "ES", 3, 10, 2);

		Usuario usuario1 = new Usuario(1L, "aluno1@ccc.ufcg.edu.br", "123456", "Anne");
		Usuario usuario2 = new Usuario(2L, "aluno2@ccc.ufcg.edu.br", "654321", "Gilmar");

		// Fluxo de cadastro de turma para usuario1
		usuario1.addTurma(turma1);
		usuario1.addTurma(turma2);

		assertFalse(turma1.verificaSeUsuarioJaPertece("aluno1@ccc.ufcg.edu.br"));
		assertFalse(turma2.verificaSeUsuarioJaPertece("aluno1@ccc.ufcg.edu.br"));
		assertEquals(null, turma1.removeUser("aluno1@ccc.ufcg.edu.br"));
		assertEquals(null, turma2.removeUser("aluno1@ccc.ufcg.edu.br"));

		// Fluxo de cadastro de turma para usuario1
		turma1.addUser(usuario2);
		turma2.addUser(usuario2);
		assertTrue(turma1.verificaSeUsuarioJaPertece("aluno2@ccc.ufcg.edu.br"));
		assertTrue(turma2.verificaSeUsuarioJaPertece("aluno2@ccc.ufcg.edu.br"));

		turma1.removeUser("aluno2@ccc.ufcg.edu.br");
		turma2.removeUser("aluno2@ccc.ufcg.edu.br");
		assertFalse(turma1.verificaSeUsuarioJaPertece("aluno2@ccc.ufcg.edu.br"));
		assertFalse(turma2.verificaSeUsuarioJaPertece("aluno2@ccc.ufcg.edu.br"));
	}

	@Test
	public void testaSairDeGrupo() {
	}

	@Test
	public void testaJuntarGrupos() {
	}

	@Test
	public void testaDistribuirUsuariosEmGruposManual() {
	}

	@Test
	public void testaDistribuirUsuariosEmGruposTimer() {
	}
}



