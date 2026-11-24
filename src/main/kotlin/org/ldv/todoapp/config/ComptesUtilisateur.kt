package org.ldv.todoapp.config
import org.ldv.todoapp.model.repository.UtilisateurRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ComptesUtilisateur(
    private val utilisateurRepository: UtilisateurRepository
) : UserDetailsService {
    @Transactional(readOnly = true)
    override fun loadUserByUsername(email: String): UserDetails {
        val utilisateur = utilisateurRepository.findByEmail(email) ?: throw UsernameNotFoundException("Compte indisponible")
        val empreinte = utilisateur.empreinteMotDePasse ?: throw UsernameNotFoundException("Compte indisponible")
        val role = utilisateur.role ?: throw UsernameNotFoundException("Compte indisponible")
        return User.withUsername(utilisateur.email)
            .password(empreinte)
            .authorities(role.nom)
            .build()
    }
}