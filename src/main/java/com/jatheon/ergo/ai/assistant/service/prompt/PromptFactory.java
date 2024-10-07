package com.jatheon.ergo.ai.assistant.service.prompt;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.embedding.EmbeddingMatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public final class PromptFactory {

    private PromptFactory() {
        throw new AssertionError("This class should not be instantiated.");
    }

    /**
     * Create a prompt for the given question and relevant embeddings.
     * @param question
     * @param embeddingMatches
     * @return {@link Prompt}
     */
    public static Prompt ragPrompt(final String question, final List<EmbeddingMatch<TextSegment>> embeddingMatches) {
        // Create a prompt for the model that includes question and relevant embeddings
        String RAG_PROMPT_TEMPLATE = "You are a helpful AI assistant. Use the following pieces of context to answer the user's question. "
                + "If you don't know the answer, just say that you don't know. Don't try to make up an answer.\n"
                + "Answer the following question to the best of your ability:\n"
                + "{{question}}\n"
                + "\n"
                + "Context:\n"
                + "{{context}}\n";
        PromptTemplate promptTemplate = PromptTemplate.from(RAG_PROMPT_TEMPLATE);
        String context = embeddingMatches.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("context", context);
        return promptTemplate.apply(variables);
    }

}
