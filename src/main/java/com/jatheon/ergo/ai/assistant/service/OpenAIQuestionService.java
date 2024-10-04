package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.inference.QuestionResponse;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Slf4j
@RequiredArgsConstructor
public class OpenAIQuestionService implements QuestionService {
    private static final String TEST_METADATA_SOURCE = "s3://simplify-ai-complexity/Levi9-How_to_transform_data_into_value_with_GenerativeAI.pdf";

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatLanguageModel chatLanguageModel;

    @Override
    public QuestionResponse performSearch(final String question) throws QuestionServiceException {
        // Embed the question
        Response<Embedding> queryEmbedding = embeddingModel.embed(question);

        // Find relevant embeddings in embedding store by semantic similarity
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant(queryEmbedding.content(), 10, 0.8);
        for (int i = 0; i < relevantEmbeddings.size(); i++) {
            EmbeddingMatch<TextSegment> embeddingMatch = relevantEmbeddings.get(i);
            log.info("Match [score: {}, text: {}]", embeddingMatch.score(), embeddingMatch.embedded().text());
        }

        // Create a prompt for the model that includes question and relevant embeddings
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question to the best of your ability:\n"
                        + "\n"
                        + "Question:\n"
                        + "{{question}}\n"
                        + "\n"
                        + "Base your answer on the following information:\n"
                        + "{{information}}");

        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", information);

        Prompt prompt = promptTemplate.apply(variables);

        String answer = chatLanguageModel.generate(prompt.toUserMessage().text());
        // See an answer from the model
        log.debug("Answer: {}", answer);
        return QuestionResponse.builder().answer(answer).build();

        /**
        // Embed the question
        Response<Embedding> queryEmbedding = embeddingModel.embed(question);
        Embedding embedding = queryEmbedding.content();


        log.info("--- Find Relevant Embeddings ---");
        // Perform the search
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings = embeddingStore.findRelevant(embedding,
                10, 0.7);
        log.info("Relevant embeddings - size: {}", relevantEmbeddings.size());


        /**
        List<RecommendationItem> recommendationItems = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> match : relevantEmbeddings) {
            RecommendationItem recommendationItem = new RecommendationItem();
            recommendationItem.setEmbeddingId(match.embeddingId());
            recommendationItem.setText(match.embedded().text());
            recommendationItem.setScore(match.score());
            recommendationItem.setResourceId(match.embedded().metadata("messageId"));
            recommendationItem.setLink(match.embedded().metadata("archive"));
            recommendationItems.add(recommendationItem);
        }
        Collections.sort(recommendationItems);

        log.info("Retrieve content - STARTED");
        Function<Query, Filter> filterByAttachment =
                (query) -> metadataKey("source").isEqualTo(TEST_METADATA_SOURCE);
        Query query = new Query("What is the phone number for Levi9 contact?");
        Embedding embeddedQuery = embeddingModel.embed(query.text()).content();
        log.debug("Vector is: [size: {}]", embeddedQuery.dimension());
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(10)
                .minScore(0.7)
                .build();

        // Define custom prompt template
        PromptTemplate promptTemplate = PromptTemplate.from(
                "You are a helpful AI assistant. Use the following pieces of context to answer the user's question. " +
                        "If you don't know the answer, just say that you don't know. Don't try to make up an answer.\n" +
                        "Context:\n" +
                        "{{context}}\n" +
                        "Human: {{question}}\n" +
                        "AI: "
        );

        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("context", information);

        Prompt prompt = promptTemplate.apply(variables);

        /**
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        ConversationalRetrievalChain conversationalRetrievalChain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .retrievalAugmentor(DefaultRetrievalAugmentor.builder()
                        .contentInjector(DefaultContentInjector.builder()
                                .promptTemplate(promptTemplate)
                                .build())
                        .build())
                .build();
        String answer = conversationalRetrievalChain.execute(query.text());
        log.info("Answer: {}", answer);
        log.info("Retrieve content - ENDED");

        String answer = "";

        log.info("--- Chat Model AI Generation ---");
        try {
            answer = chatLanguageModel.generate(prompt.toUserMessage().text());
            log.debug("Answer: {}", answer);
            return QuestionResponse.builder().answer(answer).build();
        } catch (Exception ex) {
            log.error("Error generating answer", ex);
            throw new QuestionServiceException("Error generating answer", ex);
        }
        **/
    }

}
