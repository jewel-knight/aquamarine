package com.impactcn.aquamarine.service.impl;

import com.impactcn.aquamarine.service.FileService;
import com.impactcn.aquamarine.service.PromptService;
import javafx.util.Pair;
import org.springframework.ai.azure.openai.AzureOpenAiChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author impactCn
 * @date 2024/1/3 23:12
 */
@Service
public class PromptServiceImpl implements PromptService {

    @Value("classpath:/prompt/preface.st")
    private Resource prefacePromptResource;

    @Value("classpath:/prompt/system-qa.st")
    private Resource systemQaPromptResource;

    @Value("${file.path}")
    private String path;

    @Autowired
    private AzureOpenAiChatClient azureOpenAiChatClient;

    @Autowired
    private EmbeddingClient embeddingClient;

    @Autowired
    private FileService fileService;

    private SimpleVectorStore simpleVectorStore = null;

    private boolean isInit = false;

    @Override
    public String preface(String text) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(prefacePromptResource);

        Message message = systemPromptTemplate.createMessage(Map.of("documents", text));

        UserMessage userMessage = new UserMessage(text);
        Prompt prompt = new Prompt(List.of(message, userMessage));
        ChatResponse generate = azureOpenAiChatClient.call(prompt);
        return generate.getResult().getOutput().getContent();
    }


    @Override
    public String rag(String text) {

        Message systemMessage = getSystemMessage(text);
        UserMessage userMessage = new UserMessage("`" + text + "`");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse generate = azureOpenAiChatClient.call(prompt);
        return generate.getResult().getOutput().getContent();

    }

    private Message getSystemMessage(String text) {
        SearchRequest searchRequest = SearchRequest.query(text)
                .withTopK(3)
                .withSimilarityThreshold(0.7);

        List<Document> similarDocuments = simpleVectorStore.similaritySearch(searchRequest);
        String documents = similarDocuments.stream().map(Document::getContent).collect(Collectors.joining("\n"));
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(this.systemQaPromptResource);
        return systemPromptTemplate.createMessage(Map.of("documents", documents, "message", text));
    }

    public void initData() {
        if (!isInit) {

            simpleVectorStore = new SimpleVectorStore(embeddingClient);

            Pair<String, File[]> files = fileService.getFiles(path);
            for (File file : files.getValue()) {
                FileUrlResource fileUrlResource;
                try {
                    fileUrlResource = new FileUrlResource(file.toURI().toURL());
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }
                TextReader textReader = new TextReader(fileUrlResource);
                simpleVectorStore.add(textReader.get());
            }
            isInit = true;
        }

    }

}
