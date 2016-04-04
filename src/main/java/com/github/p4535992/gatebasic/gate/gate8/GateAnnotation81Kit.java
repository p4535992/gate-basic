package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.object.MapAnnotation;
import com.github.p4535992.gatebasic.object.MapAnnotationSet;
import com.github.p4535992.gatebasic.object.MapContent;
import com.github.p4535992.gatebasic.object.MapDocument;
import javax.annotation.Nullable;
import gate.*;

import java.util.*;

/**
 * Extract the contents of records
 * Semantics of each document of Corpus structure the content into an object
 * Java Keyword For each document from which is extracted a Keyword insert the
 * Keyword in a list to be used later for inclusion in Database.
 * @author 4535992.
 * @version 2015-11-12.
 */
@SuppressWarnings("unused")
public class GateAnnotation81Kit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateAnnotation81Kit.class);

    private static GateAnnotation81Kit instance = null;
    protected GateAnnotation81Kit(){}

    public static GateAnnotation81Kit getInstance(){
        if(instance == null) {
            instance = new GateAnnotation81Kit();
        }
        return instance;
    }

    /**
     * Method for get all the content annotated on  all documents in a corpus.
     * @param corpus the {@link Corpus} of {@link Document} gate.
     * @param listNameAnnotation the {@link List} of {@link String}  of names of annotations.
     * @param listNameAnnotationSet the {@link List} of {@link String} of name of AnnotationSet sorted.
     * @param firstAndExit the {@link Boolean} is true if exists after the first content not empty of a specific annotation.
     * @return the {@link MapDocument} of map of all the content of all annotation of all annotationSet on the document.
     */
     public MapDocument getAllAnnotationInfo(
             Corpus corpus,@Nullable List<String> listNameAnnotationSet,
             @Nullable List<String> listNameAnnotation, boolean firstAndExit) {
         Iterator<Document> iter = corpus.iterator();
         MapDocument mapDocs = new MapDocument();
         MapAnnotationSet mapAnnSet = new MapAnnotationSet(); //AnnotationSet -> Annotation,Content
         MapAnnotation mapAnn = new MapAnnotation();
         while (iter.hasNext()) {
            Document document = iter.next();
            //URL url = document.getSourceUrl();
            //String lang = doc.getFeatures().get("LanguageType").toString();
            //int size = mapAnnSet.size();
             //get all the annotationSet and all the annotation...
             if(listNameAnnotationSet==null || listNameAnnotationSet.isEmpty()) {
                 Set<String> setNameAnnotationSet = document.getAnnotationSetNames();
                 listNameAnnotationSet = new ArrayList<>(setNameAnnotationSet);
             }
             /*
             Map<String,AnnotationSet> mapAnnotationSet = document.getNamedAnnotationSets();
             for(String nameAnnotationSet : listNameAnnotationSet){
                 if(isMapValueNullOrInexistent(mapAnnotationSet,nameAnnotationSet)){
                     continue;
                 }
                 AnnotationSet annSet =  mapAnnotationSet.get(nameAnnotationSet);
                 if(listNameAnnotation==null || listNameAnnotation.isEmpty()) {
                     Set<String> setNameAnnotation = annSet.getAllTypes();
                     listNameAnnotation = new ArrayList<>(setNameAnnotation);
                 }
                 for(String nameAnnotation: listNameAnnotation){
                     //set a empty string for avoid the NullPointerException...
                     mapAnn.put(nameAnnotation,new MapContent(""));
                 }
                 //List<Annotation> listAnnotation = annSet.inDocumentOrder();
                 mapAnnSet.put(nameAnnotationSet,mapAnn);
             }
             */

             //get content from all the annotation in alll the annotaiotn set sorted....
            for (String nameAnnotation : listNameAnnotation) { //for each annotation...
                for(String nameAnnotationSet: listNameAnnotationSet) {//for each annotation set...
                    MapContent content; //empty string
                    content = getSingleAnnotationInfo(document,  nameAnnotationSet,nameAnnotation);
                    //get the annotation on the first annotation set is have it without check the other annnotation set...
                    if (!content.isEmpty()) {
                        if(firstAndExit) {
                            //found it the annotation on this annotationSet...
                            mapAnn.put(nameAnnotation,content);
                            mapAnnSet.put(nameAnnotationSet, mapAnn);
                            //to the next annnotation...
                            break;
                        }else{
                            //update value on map....
                            mapAnn.put(nameAnnotation,content);
                            mapAnnSet.put(nameAnnotationSet, mapAnn);
                        }
                    }
                }//for each annotationset
            }//for each annotation...
             String name = document.getName();
             mapDocs.put(name,mapAnnSet);
        }//while
        return mapDocs;
    }//getKeyword

    /**
     * Method for get the content of annotation with a specific methods.
     * @param document the {@link Document} gate.
     * @param nameAnnotation the {@link String} name of the Annotation.
     * @param nameAnnotationSet the {@link String} name of the AnnotationSet.
     * @return the {@link MapContent} string content of the Annotation.
     */
    public MapContent getSingleAnnotationInfo(Document document,String nameAnnotationSet,String nameAnnotation) {
        MapContent content = new MapContent("");
        try {
            AnnotationSet annSet = GateUtils.getAnnotationSetFromDoc(document,nameAnnotationSet);
            //SystemLog.message("Get content of the Annotation " + nameAnnotation + " on the AnnotationSet " + annSet.getName() + "...");
            //content = getContentLongestFromAnnnotationsOnAnnotationSet(document, nameAnnotation, annSet);         
            if(annSet != null){
                Annotation newAnn;
                for (Annotation ann : annSet) {
                    if (ann.getType().equals(nameAnnotation)) {
                        newAnn = ann;
                        content.setContent(Utils.stringFor(document, newAnn));
                        break;
                    }
                }
                if(content.isEmpty()){
                    logger.warn("AnnotationSet -> "+nameAnnotationSet+" -> "+nameAnnotation+" is empty for this document to the url: "+ document.getSourceUrl());
                }
            }
            //Not very sure about that
           /* else{
                List<String> contents = GateUtils.getContentFromDoc(document, nameAnnotationSet,nameAnnotation);
                if(contents.get(0) != null || !contents.get(0).isEmpty()){
                    content.setContent(contents.get(0));
                }
            }*/
            if(content.isEmpty())content.setContent("");
            //content =  getContentLastSingleAnnotationOnAnnotationSet(document, nameAnnotation, annSet);
        }catch(NullPointerException ne){
            logger.warn("AnnotationSet -> "+nameAnnotationSet+": is empty for this document to the url: "+ document.getSourceUrl());
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        return content;
    }

    /**
     * Method for get the content of annotation with a specific methods.
     * @param document the {@link Document} gate.
     * @param nameAnnotation the {@link Annotation} name of the Annotation.
     * @param nameAnnotationSet the {@link AnnotationSet} name of the AnnotationSet.
     * @return the {@link String} string content of the Annotation.
     */
    public String getSingleAnnotationInfo(Document document,AnnotationSet nameAnnotationSet,Annotation nameAnnotation) {
        return getSingleAnnotationInfo(document,nameAnnotationSet.getName(),nameAnnotation.getType()).getContent();
    }

    /**
     * Method for get the content of annotation with a specific methods.
     * @param document the {@link Document} gate.
     * @param nameAnnotation the {@link Annotation} name of the Annotation.
     * @param nameAnnotationSet the {@link AnnotationSet} name of the AnnotationSet.
     * @return the {@link String} string content of the Annotation.
     */
    public String getSingleAnnotationInfoString(Document document,String nameAnnotationSet,String nameAnnotation) {
        return getSingleAnnotationInfo(document,nameAnnotationSet,nameAnnotation).getContent();
    }

    /**
     * Method for get all the content annotated on  all documents in a corpus.
     * @param corpus corpus gate.
     * @param listNameAnnotation list string of names of annotations.
     * @param listNameAnnotationSet list string of name of AnnotationSet sorted.
     * @param firstAndExit if true exists after the first content not empty of a specific annotation.
     * @return map of all the contnet of all annotation of all annotationSet on the document.
     * @deprecated use {@link #getAllAnnotationInfo(Corpus, List, List, boolean)} instead.
     */
    @Deprecated
    public Map<String,Map<String,Map<String,String>>> getAllAnnotationInfoString(
            Corpus corpus,List<String> listNameAnnotationSet, List<String> listNameAnnotation,boolean firstAndExit) {
        return getAllAnnotationInfo(corpus,listNameAnnotationSet,listNameAnnotation,firstAndExit).getMap2();
    }


    /**
     * Method for get the last content of the last annotation from all the same annotations on the same annotationSet.
     * @param doc gate document.
     * @param nameAnnotation string name of the annotation.
     * @param annotationSet string name of the annotationset.
     * @return  string content fo the annotation.
     */
    public String getContentLastSingleAnnotationOnAnnotationSet(Document doc,AnnotationSet annotationSet,String nameAnnotation){
        int x = 0;
        int y;
        String content="";
        try{
            //Get all type annotations within the stringNameAnnotation set of annotations date.set...
            annotationSet = annotationSet.get(nameAnnotation);
            for(Annotation anAnn: annotationSet)
                if (anAnn.getType().equals(nameAnnotation)) {
                    if (x == 0) {
                        x = anAnn.getId();
                        content = Utils.stringFor(doc, anAnn);
                    }
                    if (x > 0) {
                        y = anAnn.getId();
                        if (y > x) {
                            content = Utils.stringFor(doc, anAnn);
                        }
                    }//if(i >0)
                }
            //for
            return content;
        } catch (NullPointerException ep) {
            logger.info(ep.getMessage(), ep);
        }
        return null;
    }

    /**
     * Method for get the most longest content merged from all the same annotations on the same annotationSet.
     * @param doc gate document.
     * @param nameAnnotation string name of the annotation.
     * @param annotationSet string name of the annotationset.
     * @return string most longest content from the same annotations.
     */
    public String getContentLongestFromAnnotationsOnAnnotationSet(Document doc,AnnotationSet annotationSet,String nameAnnotation){
        long begOffset,endOffset;
        long x = 0;
        long y = 0;
        String content="";
        try{
            //Get all type annotations within the stringNameAnnotation set of annotations date.set...
            annotationSet = annotationSet.get(nameAnnotation);
            int i = 0;
            for(Annotation annotation: annotationSet){
                String annotationForDoc = Utils.stringFor(doc, annotation);
                if (annotationForDoc == null || annotationForDoc.isEmpty()) continue;
                if(annotation.getType().equals(nameAnnotation)) {
                    begOffset = annotation.getStartNode().getOffset().intValue();
                    endOffset = annotation.getEndNode().getOffset().intValue();
                    if (i == 0) {
                        x = begOffset;
                        y = endOffset;
                    } else {
                        if (x > begOffset) {
                            x = begOffset;
                        }
                        if (y < endOffset) {
                            y = endOffset;
                        }
                    }
                    content = Utils.stringFor(doc, x, y);
                    //content = doc.getContent().toString().substring(x, y);
                    if (i == annotationSet.size()) {
                        break;
                    }//if
                }
                i++;
            }
            if(content == null || content.isEmpty()){
                return null;
            }else{
                return content;
            }
        } catch (NullPointerException ep) {
            logger.info(ep.getMessage(),ep);
        }
        return null;
    }


     public Set<String> countAnnotationsOnDocument(Document doc){
        //codice aggiuntivo utile per avere un'idea del contenuto della pagina org.p4535992.mvc.webapp
        // obtain a map of all named annotation sets
        Set<String> annotTypes= null;
        Map<String, AnnotationSet> namedASes = doc.getNamedAnnotationSets();
        logger.info("No. of named Annotation Sets:"+ namedASes.size());
        // number of annotations each set contains
        for (String setName : namedASes.keySet()) {
            // annotation set
            AnnotationSet aSet = namedASes.get(setName);
            // number of annotations
            logger.info("No. of Annotations for " + setName + ":" + aSet.size());
            // all annotation types
            annotTypes = aSet.getAllTypes();
            for(String aType : annotTypes) {
               logger.info(" " + aType + ": " + aSet.get(aType).size()+"||");
            }//for aType
        }//for setName
        return annotTypes;
    }



    private <K,V> boolean isMapValueNullOrInexistent(Map<K,V> map,K key){
        V value = map.get(key);
        if (value != null) {
            return false;
        } else {
            // Key might be present...
            if (map.containsKey(key)) {
                // Okay, there's a key but the value is null
                return true;
            }
            // Definitely no such key
            return true;

        }
    }



}//ManageAnnotationAndContent.java

