package br.com.ufcg.back;

import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest
public class TchimoTests {

	@Test
	public void testaGetsESetsEntidades() {
		Usuario usuario1 = new Usuario(1L, "g@gmail", "123", "Gilmar");
		Turma t1 = new Turma("T01", "FS", "ES", 3, 10, 2);
		Grupo grupo1 = new Grupo(1, "g@gmail");

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
		Grupo grupo1 = new Grupo(1L, "g@gmail");

		t2.adicionaGrupo(grupo1);

		try {
			t2.removeUserFromGroup(1L,"g@gmail");
		} catch (UserNotFoundException e) {
			e.printStackTrace();
		} catch (GroupNotFoundException e) {
			e.printStackTrace();
		}
		t2.addUser(usuario1);
		assertTrue(t2.verificaSeUsuarioJaPertece("g@gmail"));
		t2.removeUser("g@gmail");

		assertEquals(0, t2.quantidadeGruposNaTurma());
		t2.addQGrupo();
		assertEquals(1, t2.quantidadeGruposNaTurma());
		assertFalse(t2.verificaSeUsuarioJaPertece("ggmail"));

	}
}



