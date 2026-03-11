package com.example.RAG;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {
    private final ChatModel chatModel;
    private final VectorStore vectorStore;


    public RagController(ChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }
}
