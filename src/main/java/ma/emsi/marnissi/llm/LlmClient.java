package ma.emsi.marnissi.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String systemRole;

    // Ces objets sont recréés après une éventuelle désérialisation CDI.
    private transient Assistant assistant;
    private transient ChatMemory chatMemory;

    public LlmClient() {
        initialiserAssistant();
    }

    private void initialiserAssistant() {

        // Lire la clé Gemini depuis les propriétés JVM de Payara.
        String apiKey = System.getProperty("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "La variable GEMINI_KEY est absente."
            );
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // Conserver les 20 derniers messages.
        chatMemory = MessageWindowChatMemory.withMaxMessages(20);

        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void setSystemRole(String systemRole) {

        this.systemRole = systemRole;

        // Changer de rôle implique de réinitialiser le contexte.
        chatMemory.clear();

        if (systemRole != null && !systemRole.isBlank()) {
            chatMemory.add(SystemMessage.from(systemRole));
        }
    }

    public String chat(String question) {

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException(
                    "La question ne peut pas être vide."
            );
        }

        return assistant.chat(question);
    }

    @Serial
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {

        input.defaultReadObject();

        // Reconstruire le service après une restauration de la vue.
        // L'historique précédent n'est pas conservé dans ce cas.
        initialiserAssistant();

        if (systemRole != null && !systemRole.isBlank()) {
            chatMemory.add(SystemMessage.from(systemRole));
        }
    }
}
