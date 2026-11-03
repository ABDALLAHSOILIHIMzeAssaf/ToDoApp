package org.ldv.todoapp.controller

import jakarta.servlet.http.HttpSession
import org.ldv.todoapp.controller.form.TacheForm
import org.ldv.todoapp.model.entity.Tache
import org.ldv.todoapp.model.entity.Utilisateur
import org.ldv.todoapp.model.repository.TacheRepository
import org.ldv.todoapp.model.repository.UtilisateurRepository
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class MesTachesController(
    private val tacheRepository: TacheRepository,
    private val utilisateurRepository: UtilisateurRepository
) {

    @GetMapping("/mes-taches")
    fun mesTaches(
        model: Model,
        session: HttpSession
    ): String {

        // Récupération de l'identifiant de l'utilisateur actif
        val utilisateurId =
            session.getAttribute("utilisateurId") as? Long

        // Récupération des tâches de l'utilisateur actif
        val taches = if (utilisateurId != null) {
            tacheRepository.findByUtilisateur_IdUtilisateur(utilisateurId)
        } else {
            emptyList()
        }

        // Transmission de la liste des tâches au modèle
        model.addAttribute("taches", taches)

        // Affichage du template mes-taches.html
        return "mes-taches"
    }

    @GetMapping("/mes-taches/creer")
    fun formulaireCreation(
        model: Model,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        if (rechercherUtilisateurActif(session) == null) {
            redirectAttributes.addFlashAttribute("messageErreur", "Sélectionnez un utilisateur avant de créer une tâche.")
            return "redirect:/mes-taches"
        }

        model.addAttribute("tacheForm", TacheForm())
        return "formulaire-tache"
    }

    @PostMapping("/mes-taches/creer")
    fun creerTache(
        @ModelAttribute("tacheForm") formulaire: TacheForm,
        bindingResult: BindingResult,
        session: HttpSession,
        redirectAttributes: RedirectAttributes
    ): String {
        val utilisateur = rechercherUtilisateurActif(session)
        if (utilisateur == null) {
            redirectAttributes.addFlashAttribute("messageErreur", "Sélectionnez un utilisateur avant de créer une tâche.")
            return "redirect:/mes-taches"
        }

        // Les contraintes HTML facilitent la saisie mais peuvent être contournées.
        if (formulaire.titre.isBlank()) {
            bindingResult.rejectValue("titre", "titre.obligatoire", "Le titre est obligatoire.")
        } else if (formulaire.titre.length > 255) {
            bindingResult.rejectValue("titre", "titre.longueur", "Le titre ne doit pas dépasser 255 caractères.")
        }

        // BindingResult contient aussi les éventuelles erreurs de conversion de date.
        if (bindingResult.hasErrors()) {
            return "formulaire-tache"
        }

        // Le propriétaire vient de la session. L'identifiant, la date de création,
        // l'état initial et la référence de démonstration ne viennent pas du formulaire.
        val tache = Tache(
            titre = formulaire.titre.trim(),
            description = formulaire.description.trim().ifBlank { null },
            dateEcheance = formulaire.dateEcheance,
            utilisateur = utilisateur
        )
        tacheRepository.save(tache)

        redirectAttributes.addFlashAttribute("messageSucces", "La tâche a bien été créée.")
        return "redirect:/mes-taches"
    }

    private fun rechercherUtilisateurActif(session: HttpSession): Utilisateur? {
        val utilisateurId = session.getAttribute("utilisateurId") as? Long ?: return null
        val utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null)
        if (utilisateur == null) {
            session.removeAttribute("utilisateurId")
        }
        return utilisateur
    }
}
