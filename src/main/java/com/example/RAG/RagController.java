package com.example.RAG;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class RagController {

    private final ChatModel chatModel;
    private final VectorStore vectorStore;
    public RagController(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    private String prompt = """
            Your task is to answer questions about Serbian road and traffic safety law.
            Use the information from the DOCUMENTS section to provide accurate answers. If unsure or if the
            answer isn't found in the DOCUMENT section, simply state that you do not know the answer.
            
            QUESTION:
            {input}
            
            DOCUMENTS:
            {documents}
            
            """;

    @GetMapping
    public String simplify(@RequestParam(
            value = "question", defaultValue = "\"List all the provisions of the Road Traffic Safety Law"
    )String question) {

        PromptTemplate template = new PromptTemplate(prompt);
        Map<String,Object> promptParameters = new HashMap<>();
        promptParameters.put("input", question);
        promptParameters.put("documents", findSimilarData(question));

        return chatModel
                .call(template.create(promptParameters))
                .getResult()
                .getOutput()
                .getText();
    }

    private String findSimilarData(String question) {
        List<Document> documents = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(question)
                .topK(7)
                .build()
        );
        return documents
                .stream()
                .map(Document::getText)
                .collect(Collectors.joining());
    }


}
