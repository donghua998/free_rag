package com.example.quanxiankongzhi.kb.service.impl;
import com.example.quanxiankongzhi.kb.dto.SearchResultVO;
import com.example.quanxiankongzhi.kb.service.VectorStoreService;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.grpc.SearchResults;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
// 在现有import后面添加
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.index.CreateIndexParam;
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorStoreServiceImpl implements VectorStoreService {
    private final MilvusServiceClient milvusClient;
    private static final String COLLECTION_NAME = "kb_chunks";
    private static final int VECTOR_DIM = 768;
    /**
     * 启动时自动创建 Collection（如果不存在）
     */
    @Override
    @PostConstruct
    public void init(){
        boolean hasCollection = false;
        try{
            hasCollection = milvusClient.hasCollection(
                    HasCollectionParam.newBuilder()
                            .withCollectionName(COLLECTION_NAME)
                            .build()
            ).getData();
        }catch (Exception e){
            log.warn("检查Collection失败: {}", e.getMessage());
        }
        if (!hasCollection){
            createCollection();
        }else{
            log.info("Milvus Collection '{}' 已存在", COLLECTION_NAME);
        }
    }
    /**
     * 插入向量
     */
    @Override
    public void insert(Long chunkId, Long docId, Long kbId, String text, List<Float> vector){
        List<InsertParam.Field> fields = new ArrayList<>();
        fields.add(new InsertParam.Field("chunk_id", Arrays.asList(chunkId)));
        fields.add(new InsertParam.Field("doc_id", Arrays.asList(docId)));
        fields.add(new InsertParam.Field("kb_id", Arrays.asList(kbId)));
        fields.add(new InsertParam.Field("text", Arrays.asList(text)));
        fields.add(new InsertParam.Field("vector", Arrays.asList(vector)));
        InsertParam param = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();
        milvusClient.insert(param);
    }
    /**
     * 向量检索
     */
    @Override
    public List<SearchResultVO> search(List<Float> queryVector, Long kbId, int topK){
        String filter = "kb_id == " + kbId;
        SearchParam param = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("vector")
                .withVectors(Arrays.asList(queryVector))
                .withExpr(filter)
                .withTopK(topK)
                .withMetricType(MetricType.IP)
                .withParams("{\"nprobe\":16}")
                .withOutFields(Arrays.asList("chunk_id", "text"))
                .build();
        R<SearchResults> result = milvusClient.search(param);
        List<SearchResultVO> results = new ArrayList<>();
        if (result.getData() != null && result.getData().getResults() != null){
            // 获取字段数据
            Map<String, List<?>> fieldDataMap = new HashMap<>();
            result.getData().getResults().getFieldsDataList().forEach(fieldsData ->{
                String fieldName = fieldsData.getFieldName();
                if ("chunk_id".equals(fieldName)){
                    fieldDataMap.put("chunk_id", fieldsData.getScalars().getLongData().getDataList());
                }else if("text".equals(fieldName)){
                    fieldDataMap.put("text", fieldsData.getScalars().getStringData().getDataList());
                }
            });
            // 获取分数
            List<Float> scores = result.getData().getResults().getScoresList();
            // 组装结果

            List<Long> chunkIds = (List<Long>) fieldDataMap.getOrDefault("chunk_id", new ArrayList<>());
            List<String> texts = (List<String>) fieldDataMap.getOrDefault("text", new ArrayList<>());
            for (int i = 0; i < chunkIds.size(); i++){
                SearchResultVO dto = new SearchResultVO();
                dto.setChunkId(chunkIds.get(i));
                dto.setText(texts.get(i));
                dto.setScore(scores.get(i));
                results.add(dto);
            }
        }
        return results;
    }
    private void createCollection(){
        FieldType idField = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();
        FieldType chunkIdField = FieldType.newBuilder()
                .withName("chunk_id")
                .withDataType(DataType.Int64)
                .build();
        FieldType docIdField = FieldType.newBuilder()
                .withName("doc_id")
                .withDataType(DataType.Int64)
                .build();
        FieldType kbIdField = FieldType.newBuilder()
                .withName("kb_id")
                .withDataType(DataType.Int64)
                .build();
        FieldType textField = FieldType.newBuilder()
                .withName("text")
                .withDataType(DataType.VarChar)
                .withMaxLength(65535)
                .build();
        FieldType vectorField = FieldType.newBuilder()
                .withName("vector")
                .withDataType(DataType.FloatVector)
                .withDimension(VECTOR_DIM)
                .build();
        CreateCollectionParam param = CreateCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withDescription("知识库文档分块向量")
                .addFieldType(idField)
                .addFieldType(chunkIdField)
                .addFieldType(docIdField)
                .addFieldType(kbIdField)
                .addFieldType(textField)
                .addFieldType(vectorField)
                .build();
        milvusClient.createCollection(param);
        log.info("创建Milvus Collection: {}", COLLECTION_NAME);
        createIndex();
    }
    private void createIndex(){
        try{
            milvusClient.createIndex(
                    CreateIndexParam.newBuilder()
                            .withCollectionName(COLLECTION_NAME)
                            .withFieldName("vector")
                            .withIndexType(IndexType.IVF_FLAT)
                            .withMetricType(MetricType.IP)
                            .withExtraParam("{\"nlist\":128}")
                            .build()
            );
            log.info("创建Milvus Index: {}", COLLECTION_NAME);
        }catch (Exception e){
            log.warn("创建Milvus Index失败: {}", e.getMessage());
        }
    }
}
