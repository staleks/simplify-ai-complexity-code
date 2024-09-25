package com.jatheon.ergo.ai.assistant.service;

import com.jatheon.ergo.ai.assistant.model.QuestionResponse;
import com.jatheon.ergo.ai.assistant.service.error.QuestionServiceException;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OpenAIQuestionService implements QuestionService {

    private final ChatLanguageModel chatLanguageModel;

    @Override
    public QuestionResponse performSearch(String question) throws QuestionServiceException {
        log.info("Performing search for question: {}", question);
        QuestionResponse questionResponse = new QuestionResponse();

        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question to the best of your ability:\n"
                        + "\n"
                        + "Question:\n"
                        + "{{question}}\n");

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);

        Prompt prompt = promptTemplate.apply(variables);

        Response<AiMessage> answer = chatLanguageModel.generate(prompt.toUserMessage());
        questionResponse.setAnswer(answer.content().text());

        return questionResponse;
    }

}
