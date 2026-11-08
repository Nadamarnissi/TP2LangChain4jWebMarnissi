
package ma.emsi.marnissi.web;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import ma.emsi.marnissi.llm.LlmClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("bb")
@ViewScoped
public class Bb implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private LlmClient llmClient;

    private String question = "";
    private String role = "assistant";

    // Historique affiché dans l'interface Web.
    private final List<Echange> historique = new ArrayList<>();

    public void envoyer() {

        if (question == null || question.isBlank()) {
            return;
        }

        String texteQuestion = question.trim();

        try {
            // Appliquer le rôle système au début de la conversation.
            if (historique.isEmpty()) {
                llmClient.setSystemRole(getInstructionRole());
            }

            // Envoyer la question à Gemini avec LangChain4j.
            String reponse = llmClient.chat(texteQuestion);

            historique.add(new Echange(texteQuestion, reponse));
            question = "";

        } catch (RuntimeException e) {
            historique.add(new Echange(
                    texteQuestion,
                    "Erreur : impossible d'obtenir une réponse de Gemini."
            ));

            question = "";
        }
    }

    public void nouvelleConversation() {

        // Effacer les messages affichés.
        historique.clear();
        question = "";

        // Réinitialiser la mémoire conversationnelle de Gemini.
        // Le nouveau rôle sera appliqué au prochain message.
        llmClient.setSystemRole(null);
    }

    private String getInstructionRole() {

        if ("traducteur".equals(role)) {
            return "Tu es un traducteur professionnel. "
                    + "Traduis tous les messages de l'utilisateur "
                    + "en anglais, sans explication supplémentaire.";
        }

        return "Tu es un assistant IA utile et précis. "
                + "Réponds aux questions en français.";
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Echange> getHistorique() {
        return historique;
    }

    // Représente un échange dans l'historique du chat.
    public static class Echange implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String question;
        private final String reponse;

        public Echange(String question, String reponse) {
            this.question = question;
            this.reponse = reponse;
        }

        public String getQuestion() {
            return question;
        }

        public String getReponse() {
            return reponse;
        }
    }
}
