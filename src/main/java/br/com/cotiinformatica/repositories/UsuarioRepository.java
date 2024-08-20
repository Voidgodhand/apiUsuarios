package br.com.cotiinformatica.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.cotiinformatica.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

	/*
	 * Método para consultar 1 usuário no banco de dados
	 * através do email informado	 
	*/
	@Query("SELECT u FROM Usuario u WHERE u.email = :email")
	Usuario findByEmail(@Param("email") String email);
	
	/*
	 * Método para consultar 1 usuário no banco de dados
	 * através do enmail e da senha informados
	 */

	@Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.senha = :senha")
	Usuario findByEmailAndSenha(@Param("email") String email, @Param("senha") String senha);
}
