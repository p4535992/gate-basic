package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.util.log.SystemLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 4535992 on 25/06/2015.
 * @author 4535992
 * @version 2015-11-12
 */
@SuppressWarnings("unused")
public class GateSupport {

    private Map<String,String> mapAnnotation;
    private Map<String,Map<String,String>> mapAnnotationSet;
    private Map<String,Map<String,Map<String,String>>> mapDocs;
    private String content;

    public Map<String, String> getMapAnnotation() {
        return mapAnnotation;
    }

    public void setMapAnnotation(Map<String, String> mapAnnotation) {
        this.mapAnnotation = mapAnnotation;
    }

    public Map<String, Map<String, String>> getMapAnnotationSet() {
        return mapAnnotationSet;
    }

    public void setMapAnnotationSet(Map<String, Map<String, String>> mapAnnotationSet) {
        this.mapAnnotationSet = mapAnnotationSet;
    }

    public Map<String, Map<String, Map<String, String>>> getMapDocs() {
        return mapDocs;
    }

    public void setMapDocs(Map<String, Map<String, Map<String, String>>> mapDocs) {
        this.mapDocs = mapDocs;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private static GateSupport instance = null;

    protected GateSupport(){}

    protected GateSupport(Map<String,Map<String,Map<String,String>>> mapDocs) {
        this.mapAnnotation = new HashMap<>();
        this.mapAnnotationSet = new HashMap<>();
        this.mapDocs = new HashMap<>();
        this.mapDocs = mapDocs;
    }

    public static GateSupport getInstance(Map<String,Map<String,Map<String,String>>> mapDocs){
        if(instance == null) {
            instance = new GateSupport(mapDocs);
        }
        return instance;
    }

    public static GateSupport getInstance(Map<String,Map<String,Map<String,String>>> mapDocs,boolean isNull){
        if(isNull) instance = null;
        return  getInstance(mapDocs);
    }

    public static GateSupport getInstance(){
        if(instance == null) {
            instance = new GateSupport();
        }
        return instance;
    }

    public static GateSupport getInstance(boolean isNull){
        if(isNull) instance = null;
        return getInstance();
    }

    public static void setInstance(GateSupport instance) {
        GateSupport.instance = instance;
    }

    public Map<String,Map<String,String>> getDocument(Integer index,Map<String,Map<String,Map<String,String>>> mapDocs){
        this.mapDocs = mapDocs;
        return getDocument(index);
    }

    public Map<String,Map<String,String>> getDocument(Integer index){
        if(index > mapDocs.size()){
             SystemLog.warning("The index:"+index+" on the map of the documents you try to get not exists on this map of the result of GATE!!!");
             return null;
        }
        List<Map<String,Map<String,String>>> list = new ArrayList<>(mapDocs.values());
        this.mapAnnotationSet = list.get(index);
        return list.get(index);
    }

    public Map<String,Map<String,String>> getDocument(String nameDocument,Map<String,Map<String,Map<String,String>>> mapDocs){
        this.mapDocs = mapDocs;
        return getDocument(nameDocument,false);
    }

    public Map<String,Map<String,String>> getDocument(String nameDocument){
        return getDocument(nameDocument,false);
    }

    public Map<String,Map<String,String>> getDocument(String nameDocument,boolean ignorecase){
        for(Map.Entry<String,Map<String,Map<String,String>>> entryDocs: mapDocs.entrySet()){
            if(ignorecase){
                if(entryDocs.getKey().equalsIgnoreCase(nameDocument)){
                    this.mapAnnotationSet = mapDocs.get(entryDocs.getKey());
                    return entryDocs.getValue();
                }
            }else{
                if(entryDocs.getKey().equals(nameDocument)){
                    this.mapAnnotationSet = mapDocs.get(entryDocs.getKey());
                    return entryDocs.getValue();
                }
            }

        }
        SystemLog.warning("The document with the name:"+nameDocument+" not exists on this map of the result of GATE!!!");
        return null;
    }

    public Map<String,String> getAnnotationSet(Integer index,Map<String,Map<String,String>> mapAnnotationSet ){
        this.mapAnnotationSet = mapAnnotationSet;
        return getAnnotationSet(index);
    }

    public Map<String,String> getAnnotationSet(Integer index){
        if(index > mapAnnotationSet.size()){
            SystemLog.warning("The index on the map of the annotationSets you try to get not exists on this map of the result of GATE!!!");
            return null;
        }
        List<Map<String,String>> list = new ArrayList<>(mapAnnotationSet.values());
        this.mapAnnotation = list.get(index);
        return list.get(index);
    }


    public Map<String,String> getAnnotationSet(String nameAnnotationSet,Map<String,Map<String,String>> mapAnnotationSet ){
        this.mapAnnotationSet = mapAnnotationSet;
        return getAnnotationSet(nameAnnotationSet,false);
    }

    public Map<String,String> getAnnotationSet(String nameAnnotationSet){
        return getAnnotationSet(nameAnnotationSet,false);
    }

    public Map<String,String> getAnnotationSet(String nameAnnotationSet,boolean ignorecase){
        for(Map.Entry<String,Map<String,String>> entryDocs: mapAnnotationSet.entrySet()){
            if(ignorecase){
                if(entryDocs.getKey().equalsIgnoreCase(nameAnnotationSet)){
                    this.mapAnnotation = mapAnnotationSet.get(entryDocs.getKey());
                    return entryDocs.getValue();
                }
            }else{
                if(entryDocs.getKey().equals(nameAnnotationSet)){
                    this.mapAnnotation = mapAnnotationSet.get(entryDocs.getKey());
                    return entryDocs.getValue();
                }
            }

        }
        SystemLog.warning("The annotationSet with the name:"+nameAnnotationSet+" not exists on this map of the result of GATE!!!");
        return null;
    }

    public String getAnnotation(Integer index,Map<String,String> mapAnnotation ){
        this.mapAnnotation = mapAnnotation;
        return getAnnotation(index);
    }

    public String getAnnotation(Integer index){
        if(index > mapAnnotation.size()){
            SystemLog.warning("The index:"+index+" on the map of the annotations you try to get not exists on this map of the result of GATE!!!");
            return null;
        }
        List<String> list = new ArrayList<>(mapAnnotation.values());
        return list.get(index);
    }

    public String getAnnotation(String nameAnnotation,Map<String,String> mapAnnotation ) {
        this.mapAnnotation = mapAnnotation;
        return getAnnotation(nameAnnotation,false);
    }

    public String getAnnotation(String nameAnnotation) {
        return getAnnotation(nameAnnotation,false);
    }


    public String getAnnotation(String nameAnnotation,boolean ignorecase){
        for(Map.Entry<String,String> entryAnn: mapAnnotation.entrySet()){
            if(ignorecase){
                if(entryAnn.getKey().equalsIgnoreCase(nameAnnotation)){
                    return entryAnn.getValue();
                }
            }else{
                if(entryAnn.getKey().equals(nameAnnotation)){
                    return entryAnn.getValue();
                }
            }

        }
        SystemLog.warning("The annotation with the name:"+nameAnnotation+" not exists on this map of the result of GATE!!!");
        return null;
    }

    public String getContent(String nameDocument,String nameAnnotationSet,String nameAnnotation){
        try {
            return getAnnotation(nameAnnotation, getAnnotationSet(nameAnnotationSet, getDocument(nameDocument, mapDocs)));
        }catch(NullPointerException ne){
            SystemLog.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists!");
            return null;
        }
    }

    public String getContent(Integer indexDoc,String nameAnnotationSet,String nameAnnotation){
        try{
            return getAnnotation(nameAnnotation, getAnnotationSet(nameAnnotationSet, getDocument(indexDoc, mapDocs)));
        }catch(NullPointerException ne){
            SystemLog.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists!");
            return null;
        }
    }

    public String getContent(Integer indexDoc,Integer indexAnnSet,String nameAnnotation){
        try {
            return getAnnotation(nameAnnotation, getAnnotationSet(indexAnnSet, getDocument(indexDoc, mapDocs)));
        }catch(NullPointerException ne){
            SystemLog.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists!");
            return null;
        }
    }

    public String getContent(Integer indexDoc,Integer indexAnnSet,Integer indexAnn){
        try {
            return getAnnotation(indexAnn, getAnnotationSet(indexAnnSet, getDocument(indexDoc, mapDocs)));
        }catch(NullPointerException ne){
            SystemLog.error("Some parameter of input is wrong or this combination document/annotationSet/annotation not exists!");
            return null;
        }

    }


}
