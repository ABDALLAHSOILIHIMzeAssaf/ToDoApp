package org.ldv.todoapp.controller.admin

import jakarta.servlet.http.HttpSession
import org.ldv.todoapp.controller.form.UtilisateurForm
import org.ldv.todoapp.model.entity.Role
import org.ldv.todoapp.model.entity.Utilisateur
import org.ldv.todoapp.model.repository.RoleRepository
import org.ldv.todoapp.model.repository.UtilisateurRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UtilisateursController(
    private val utilisateurRepository: UtilisateurRepository,
    private val roleRepository: RoleRepository
) {
    @GetMapping("/admin/utilisateurs")
    fun utilisateurs(model: Model, session: HttpSession, redirectAttributes: RedirectAttributes): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)
        model.addAttribute("listeUtilisateurs", utilisateurRepository.findAllAvecRole())
        return "admin/utilisateurs"
    }

    @GetMapping("/admin/utilisateurs/creer")
    fun formulaireCreation(model: Model, session: HttpSession, redirectAttributes: RedirectAttributes): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)
        model.addAttribute("utilisateurForm", UtilisateurForm())
        preparerFormulaire(model)
        return "admin/formulaire-utilisateur"
    }

    @PostMapping("/admin/utilisateurs/creer")
    fun creerUtilisateur(
        @ModelAttribute("utilisateurForm") formulaire: UtilisateurForm,
        bindingResult: BindingResult, model: Model,
        session: HttpSession, redirectAttributes: RedirectAttributes
    ): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)
        val role = validerFormulaire(formulaire, bindingResult)
        if (!bindingResult.hasErrors()) {
            val utilisateur = Utilisateur(nom = formulaire.nom.trim(), prenom = formulaire.prenom.trim(),
                email = formulaire.email.trim(), role = requireNotNull(role))
            if (enregistrer(utilisateur, bindingResult)) {
                redirectAttributes.addFlashAttribute("messageSucces", "L'utilisateur a bien été créé.")
                return "redirect:/admin/utilisateurs"
            }
        }
        preparerFormulaire(model)
        return "admin/formulaire-utilisateur"
    }

    @GetMapping("/admin/utilisateurs/{id}/modifier")
    fun formulaireModification(@PathVariable("id") id: Long, model: Model,
        session: HttpSession, redirectAttributes: RedirectAttributes): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)
        val utilisateur = utilisateurRepository.findByIdAvecRole(id)
            ?: return signalerUtilisateurAbsent(redirectAttributes)
        model.addAttribute("utilisateurForm", UtilisateurForm().apply {
            nom = utilisateur.nom
            prenom = utilisateur.prenom
            email = utilisateur.email
            roleId = utilisateur.role?.idRole
        })
        preparerFormulaire(model, id)
        return "admin/formulaire-utilisateur"
    }

    @PostMapping("/admin/utilisateurs/{id}/modifier")
    fun modifierUtilisateur(@PathVariable("id") id: Long,
        @ModelAttribute("utilisateurForm") formulaire: UtilisateurForm,
        bindingResult: BindingResult, model: Model,
        session: HttpSession, redirectAttributes: RedirectAttributes): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)
        val utilisateur = utilisateurRepository.findByIdAvecRole(id)
            ?: return signalerUtilisateurAbsent(redirectAttributes)
        val role = validerFormulaire(formulaire, bindingResult, id)
        if (!bindingResult.hasErrors()) {
            // La ligne existante conserve son identifiant, referenceDemo et ses tâches.
            utilisateur.nom = formulaire.nom.trim()
            utilisateur.prenom = formulaire.prenom.trim()
            utilisateur.email = formulaire.email.trim()
            utilisateur.role = requireNotNull(role)
            if (enregistrer(utilisateur, bindingResult)) {
                redirectAttributes.addFlashAttribute("messageSucces", "L'utilisateur a bien été modifié.")
                // Un administrateur peut changer son propre rôle : son accès cesse immédiatement.
                return if (session.getAttribute("utilisateurId") == id && role.nom != "ROLE_ADMIN")
                    "redirect:/mes-taches" else "redirect:/admin/utilisateurs"
            }
        }
        preparerFormulaire(model, id)
        return "admin/formulaire-utilisateur"
    }

    private fun preparerFormulaire(model: Model, id: Long? = null) {
        model.addAttribute("roles", roleRepository.findAll().sortedBy { it.nom })
        if (id != null) model.addAttribute("idUtilisateur", id)
    }

    private fun validerFormulaire(formulaire: UtilisateurForm, erreurs: BindingResult, id: Long? = null): Role? {
        for ((champ, valeur) in listOf("nom" to formulaire.nom, "prenom" to formulaire.prenom)) {
            if (valeur.isBlank()) erreurs.rejectValue(champ, "$champ.obligatoire", "Ce champ est obligatoire.")
            else if (valeur.length > 100) erreurs.rejectValue(champ, "$champ.longueur", "100 caractères maximum.")
        }
        val email = formulaire.email.trim()
        if (email.length > 255 || !Regex("^[^\\s@]+@[^\\s@]+$").matches(email)) {
            erreurs.rejectValue("email", "email.invalide", "Saisissez une adresse électronique valide (255 caractères maximum).")
        } else {
            val existant = utilisateurRepository.findByEmail(email)
            if (existant != null && existant.idUtilisateur != id)
                erreurs.rejectValue("email", "email.unique", "Cette adresse électronique est déjà utilisée.")
        }
        if (erreurs.hasFieldErrors("roleId")) return null
        val role = formulaire.roleId?.let { roleRepository.findById(it).orElse(null) }
        if (role == null) erreurs.rejectValue("roleId", "role.invalide", "Sélectionnez un rôle existant.")
        return role
    }

    private fun enregistrer(utilisateur: Utilisateur, erreurs: BindingResult): Boolean {
        return try {
            // La contrainte UNIQUE reste la protection finale, y compris en cas de requêtes simultanées.
            utilisateurRepository.saveAndFlush(utilisateur)
            true
        } catch (exception: DataIntegrityViolationException) {
            val existant = utilisateurRepository.findByEmail(utilisateur.email)
            if (existant != null && existant.idUtilisateur != utilisateur.idUtilisateur)
                erreurs.rejectValue("email", "email.unique", "Cette adresse électronique est déjà utilisée.")
            else erreurs.reject("utilisateur.enregistrement", "Enregistrement impossible. Vérifiez les données et le rôle sélectionné.")
            false
        }
    }

    private fun estAdministrateur(session: HttpSession): Boolean {
        val id = session.getAttribute("utilisateurId") as? Long ?: return false
        val utilisateur = utilisateurRepository.findByIdAvecRole(id)
        if (utilisateur == null) session.removeAttribute("utilisateurId")
        return utilisateur?.role?.nom == "ROLE_ADMIN"
    }

    private fun refuserAcces(redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addFlashAttribute("messageErreur", "L'accès à la gestion des utilisateurs est réservé aux administrateurs.")
        return "redirect:/mes-taches"
    }

    private fun signalerUtilisateurAbsent(redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addFlashAttribute("messageErreur", "Cet utilisateur est introuvable.")
        return "redirect:/admin/utilisateurs"
    }
}
