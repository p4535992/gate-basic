package com.github.p4535992.gatebasic.gate.gate8;

import gate.*;


import java.util.*;

/**
 * Extract the contents of records
 * Semantics of each document of Corpus structure the content into an object
 * Java Keyword For each document from which is extracted a Keyword insert the
 * Keyword in a list to be used later for inclusion in Database.
 * @deprecated use {@link GateAnnotation81Kit} instead.
 * @author 4535992.
 * @version 2015-11-12.
 */
@Deprecated
@SuppressWarnings("unused")
public class GateAnnotation8Kit {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateAnnotation8Kit.class);

    private static GateAnnotation8Kit instance = null;
    protected GateAnnotation8Kit(){}

    public static GateAnnotation8Kit getInstance(){
        if(instance == null) {
            instance = new GateAnnotation8Kit();
        }
        return instance;
    }

    /**
     * Method for get all the content annotated on  all documents in a corpus.
     * @param corpus corpus gate.
     * @param listNameAnnotation list string of names of annotations.
     * @param listNameAnnotationSet list string of name of AnnotationSet sorted.
     * @param firstAndExit if true exists after the first content not empty of a specific annotation.
     * @return map of all the contnet of all annotation of all annotationSet on the document.
     */
     public Map<String,Map<String,Map<String,String>>> getAllAnnotationInfo(Corpus corpus, List<String> listNameAnnotation,List<String> listNameAnnotationSet,boolean firstAndExit) {
         Iterator<Document> iter = corpus.iterator();
         Map<String,Map<String,Map<String,String>>> mapDocs = new HashMap<>();
         Map<String,Map<String,String>> mapAnnSet = new HashMap<>(); //AnnotationSet -> Annotation,Content
         Map<String,String> mapAnn = new HashMap<>();
         while (iter.hasNext()) {
            Document document = iter.next();
            //URL url = document.getSourceUrl();
            //String lang = doc.getFeatures().get("LanguageType").toString();
            int size = mapAnnSet.size();
             //get all the annotationSet and all the annotation...
             if(listNameAnnotationSet==null || listNameAnnotationSet.isEmpty()) {
                 Set<String> setNameAnnotationSet = document.getAnnotationSetNames();
                 listNameAnnotationSet = new ArrayList<>(setNameAnnotationSet);
             }
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
                     mapAnn.put(nameAnnotation,"");
                 }
                 //List<Annotation> listAnnotation = annSet.inDocumentOrder();
                 mapAnnSet.put(nameAnnotationSet,mapAnn);
             }

             //get content from all the annotation in alll the annotaiotn set sorted....
            for (String nameAnnotation : listNameAnnotation) { //for each annotation...
                for(String nameAnnotationSet: listNameAnnotationSet) {//for each annotation set...
                    String content; //empty string
                    content = getSingleAnnotationInfo(document, nameAnnotation, nameAnnotationSet);
                    //get the annotation on the first annotation set is have it without check the other annnotation set...
                    if (!(content == null || content.isEmpty())) {
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
     * @param document the {@link Document}gate.
     * @param nameAnnotation the {@link String} of name of the {@link Annotation}.
     * @param nameAnnotationSet the {@link String} of name of the {@link AnnotationSet}
     * @return the {@link String} of the content of the {@link Annotation}.
     */
    public String getSingleAnnotationInfo(Document document,String nameAnnotation,String nameAnnotationSet) {
        String content ="";
        try {
            AnnotationSet annSet = GateUtils.getAnnotationSetFromDoc(document,nameAnnotationSet);
            //SystemLog.message("Get content of the Annotation " + nameAnnotation + " on the AnnotationSet " + annSet.getName() + "...");
            //content = getContentLongestFromAnnnotationsOnAnnotationSet(document, nameAnnotation, annSet);         
            Annotation newAnn;
            for(Annotation ann: annSet){
                if(ann.getType().equals(nameAnnotation)){
                    newAnn = ann;
                    content = Utils.stringFor(document,newAnn);
                    break;
                }
            }
            if(content == null || content.isEmpty()){
                content ="";
            }else{
                content = cleanHTML(content);
            }          
            //content =  getContentLastSingleAnnotationOnAnnotationSet(document, nameAnnotation, annSet);
        }catch(NullPointerException ne){
            //SystemLog.warning("The AnnotationSet "+nameAnnotationSet+" not have a single annotation for this document to the url: "+ document.getSourceUrl());
        }
        return content;
    }

    /**
     * Method for get the last content of the last annotation from all the same annotations on the same annotationSet.
     * @param doc the {@link Document}gate.
     * @param nameAnnotation the {@link String} of name of the {@link Annotation}.
     * @param annotationSet the {@link AnnotationSet}
     * @return the {@link String} of the content of the {@link Annotation}.
     */
    public String getContentLastSingleAnnotationOnAnnotationSet(Document doc,String nameAnnotation,AnnotationSet annotationSet){
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
     * @param doc the {@link Document}gate.
     * @param nameAnnotation the {@link String} of name of the {@link Annotation}.
     * @param annotationSet the {@link AnnotationSet}
     * @return the {@link String} of the content of the {@link Annotation}.
     */
    public String getContentLongestFromAnnotationsOnAnnotationSet(Document doc,String nameAnnotation,AnnotationSet annotationSet){
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

    /**
     * Method to clean a html text to a string text.
     * @param stringHtml the {@link String} html string of text.
     * @return the {@link String}  text cleaned.
     */
    private String cleanHTML(String stringHtml){
        return stringHtml.replaceAll("\\r\\n|\\r|\\n", " ").trim();
        //.replace("\\n\\r", "").replace("\\n","").replace("\\r","").trim())
    }

}//ManageAnnotationAndContent.java

