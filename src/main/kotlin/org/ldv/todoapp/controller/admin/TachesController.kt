package org.ldv.todoapp.controller.admin

import jakarta.servlet.http.HttpSession
import org.ldv.todoapp.controller.form.TacheAdminForm
import org.ldv.todoapp.model.entity.Tache
import org.ldv.todoapp.model.entity.Utilisateur
import org.ldv.todoapp.model.repository.TacheRepository
import org.ldv.todoapp.model.repository.UtilisateurRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class TachesController(
    private val tacheRepository: TacheRepository,
    private val utilisateurRepository: UtilisateurRepository
) {
    @GetMapping("/admin/taches")
    fun taches(model: Model, session: HttpSession, redirectAttributes: RedirectAttributes): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        model.addAttribute("listeTaches", tacheRepository.findAllAvecUtilisateur())
        return "admin/taches"
    }

    @GetMapping("/admin/taches/creer")
    fun formulaireCreation(model: Model, session: HttpSession, redirectAttributes: RedirectAttributes): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        model.addAttribute("tacheForm", TacheAdminForm())
        preparerFormulaire(model)
        return "admin/formulaire-tache"
    }

    @PostMapping("/admin/taches/creer")
    fun creerTache(
        @ModelAttribute("tacheForm") formulaire: TacheAdminForm,
        bindingResult: BindingResult,
        model: Model,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        val proprietaire = validerFormulaire(formulaire, bindingResult)
        if (bindingResult.hasErrors()) {
            preparerFormulaire(model)
            return "admin/formulaire-tache"
        }

        val tache = Tache(
            titre = formulaire.titre.trim(),
            description = formulaire.description.trim().ifBlank { null },
            dateEcheance = formulaire.dateEcheance,
            terminee = formulaire.terminee,
            utilisateur = requireNotNull(proprietaire)
        )
        tacheRepository.save(tache)
        redirectAttributes.addFlashAttribute("messageSucces", "La tâche a bien été créée.")
        return "redirect:/admin/taches"
    }

    @GetMapping("/admin/taches/{id}/modifier")
    fun formulaireModification(
        @PathVariable("id") id: Long,
        model: Model,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        val tache = tacheRepository.findByIdAvecUtilisateur(id)
        if (tache == null) return signalerTacheAbsente(redirectAttributes)

        val formulaire = TacheAdminForm().apply {
            titre = tache.titre
            description = tache.description.orEmpty()
            dateEcheance = tache.dateEcheance
            terminee = tache.terminee
            utilisateurId = tache.utilisateur?.idUtilisateur
        }
        model.addAttribute("tacheForm", formulaire)
        preparerFormulaire(model, id)
        return "admin/formulaire-tache"
    }

    @PostMapping("/admin/taches/{id}/modifier")
    fun modifierTache(
        @PathVariable("id") id: Long,
        @ModelAttribute("tacheForm") formulaire: TacheAdminForm,
        bindingResult: BindingResult,
        model: Model,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        val tache = tacheRepository.findByIdAvecUtilisateur(id)
        if (tache == null) return signalerTacheAbsente(redirectAttributes)

        val proprietaire = validerFormulaire(formulaire, bindingResult)
        if (bindingResult.hasErrors()) {
            preparerFormulaire(model, id)
            return "admin/formulaire-tache"
        }

        // Modifier la ligne existante conserve sa date de création et referenceDemo.
        tache.titre = formulaire.titre.trim()
        tache.description = formulaire.description.trim().ifBlank { null }
        tache.dateEcheance = formulaire.dateEcheance
        tache.terminee = formulaire.terminee
        tache.utilisateur = requireNotNull(proprietaire)
        tacheRepository.save(tache)

        redirectAttributes.addFlashAttribute("messageSucces", "La tâche a bien été modifiée.")
        return "redirect:/admin/taches"
    }

    @GetMapping("/admin/taches/{id}/supprimer")
    fun confirmationSuppression(
        @PathVariable("id") id: Long,
        model: Model,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        val tache = tacheRepository.findByIdAvecUtilisateur(id)
        if (tache == null) return signalerTacheAbsente(redirectAttributes)

        model.addAttribute("tacheASupprimer", tache)
        return "admin/confirmation-suppression-tache"
    }

    @PostMapping("/admin/taches/{id}/supprimer")
    fun supprimerTache(
        @PathVariable("id") id: Long,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        // Le rôle est revérifié même si la confirmation a été affichée auparavant.
        if (!estAdministrateur(session)) return refuserAcces(redirectAttributes)

        val tache = tacheRepository.findByIdAvecUtilisateur(id)
        if (tache == null) return signalerTacheAbsente(redirectAttributes)

        tacheRepository.delete(tache)
        redirectAttributes.addFlashAttribute("messageSucces", "La tâche a bien été supprimée.")
        return "redirect:/admin/taches"
    }

    private fun preparerFormulaire(model: Model, id: Long? = null) {
        model.addAttribute("proprietaires", utilisateurRepository.findAllAvecRole())
        if (id != null) model.addAttribute("idTache", id)
    }

    private fun validerFormulaire(formulaire: TacheAdminForm, bindingResult: BindingResult): Utilisateur? {
        if (formulaire.titre.isBlank()) {
            bindingResult.rejectValue("titre", "titre.obligatoire", "Le titre est obligatoire.")
        } else if (formulaire.titre.length > 255) {
            bindingResult.rejectValue("titre", "titre.longueur", "Le titre ne doit pas dépasser 255 caractères.")
        }

        // Une erreur de conversion d'identifiant est déjà présente dans BindingResult.
        if (bindingResult.hasFieldErrors("utilisateurId")) return null
        val proprietaire = formulaire.utilisateurId?.let { utilisateurRepository.findById(it).orElse(null) }
        if (proprietaire == null) {
            bindingResult.rejectValue("utilisateurId", "utilisateur.invalide", "Sélectionnez un propriétaire existant.")
        }
        return proprietaire
    }

    private fun estAdministrateur(session: HttpSession): Boolean {
        val id = session.getAttribute("utilisateurId") as? Long ?: return false
        val utilisateur = utilisateurRepository.findByIdAvecRole(id)
        if (utilisateur == null) session.removeAttribute("utilisateurId")
        return utilisateur?.role?.nom == "ROLE_ADMIN"
    }

    private fun refuserAcces(redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addFlashAttribute("messageErreur", "L'accès à la gestion des tâches est réservé aux administrateurs.")
        return "redirect:/mes-taches"
    }

    private fun signalerTacheAbsente(redirectAttributes: RedirectAttributes): String {
        redirectAttributes.addFlashAttribute("messageErreur", "Cette tâche est introuvable.")
        return "redirect:/admin/taches"
    }
}
