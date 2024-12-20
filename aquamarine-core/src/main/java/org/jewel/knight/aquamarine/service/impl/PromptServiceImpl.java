package org.jewel.knight.aquamarine.service.impl;

import com.sun.webkit.network.URLs;
import org.jewel.knight.aquamarine.service.FileService;
import org.jewel.knight.aquamarine.service.PromptService;
import javafx.util.Pair;
import org.jewel.knight.aquamarine.vo.FileVO;
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
import org.springframework.util.DigestUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

    @Value("classpath:/prompt/suggest.st")
    private Resource suggestPromptResource;

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

    private static final Map<String, FileVO> IDS_CACHE = new HashMap<>();

    @Override
    public String preface(String text) {
        return chat(text, prefacePromptResource);
    }


    @Override
    public String rag(String text) {

        Message systemMessage = getSystemMessage(text);
        UserMessage userMessage = new UserMessage("`" + text + "`");
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        ChatResponse generate = azureOpenAiChatClient.call(prompt);
        return generate.getResult().getOutput().getContent();

    }

    @Override
    public String suggest(String text) {
        return chat(text, suggestPromptResource);
    }

    @Override
    public void initData() {
        if (!isInit) {

            simpleVectorStore = new SimpleVectorStore(embeddingClient);

            Pair<String, File[]> files = fileService.getFiles(path);
            for (File file : files.getValue()) {
                FileUrlResource fileUrlResource;
                try {
                    URL url = file.toURI().toURL();
                    fileUrlResource = new FileUrlResource(url);
                    TextReader textReader = new TextReader(fileUrlResource);
                    List<Document> documents = textReader.get();
                    simpleVectorStore.add(documents);

                    // 设置元数据
                    String md5Digest = DigestUtils.md5DigestAsHex(fileUrlResource.getInputStream());
                    FileVO fileVO = new FileVO(md5Digest, getIds(documents));
                    IDS_CACHE.put(url.toExternalForm(), fileVO);
                } catch (Exception e) {
//                    throw new RuntimeException(e);

                }

            }
            isInit = true;
        }

    }

    @Override
    public void reloadData(String path) {
        File file = new File(path);
        try {
            URL url = file.toURI().toURL();

            FileVO fileVO = IDS_CACHE.get(url.toExternalForm());

            // 首次
            if (fileVO == null) {
                FileUrlResource fileUrlResource = new FileUrlResource(url);
                TextReader textReader = new TextReader(fileUrlResource);
                List<Document> documents = textReader.get();
                simpleVectorStore.add(documents);
                String md5Digest = DigestUtils.md5DigestAsHex(fileUrlResource.getInputStream());
                fileVO = new FileVO(md5Digest, getIds(documents));
                IDS_CACHE.put(url.toExternalForm(), fileVO);
            } else {
                FileUrlResource fileUrlResource = new FileUrlResource(url);
                String md5Digest = DigestUtils.md5DigestAsHex(fileUrlResource.getInputStream());
                if (!fileVO.getMd5().equals(md5Digest)) {
                    simpleVectorStore.delete(fileVO.getIds());
                    TextReader textReader = new TextReader(fileUrlResource);
                    List<Document> documents = textReader.get();
                    simpleVectorStore.add(documents);
                    fileVO.setMd5(md5Digest);
                    fileVO.setIds(getIds(documents));
                    IDS_CACHE.put(url.toExternalForm(), fileVO);

                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getIds(List<Document> documents) {
        List<String> list = new ArrayList<>(documents.size());
        for (Document document : documents) {
            list.add(document.getId());
        }
        return list;
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

    private String chat(String text, Resource resource) {
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(resource);

        Message message = systemPromptTemplate.createMessage(Map.of("documents", text));

        UserMessage userMessage = new UserMessage(text);
        Prompt prompt = new Prompt(List.of(message, userMessage));
        ChatResponse generate = azureOpenAiChatClient.call(prompt);
        return generate.getResult().getOutput().getContent();
    }


}
