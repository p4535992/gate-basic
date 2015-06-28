package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.util.log.SystemLog;
import com.github.p4535992.util.string.StringKit;
import gate.*;


import java.util.*;

/**
 * Estrai il contenuto dell annotazioni
 * semantiche da ogni documento del Corpus Struttura il contenuto in un oggetto
 * Java Keyword Per ogni documento da cui Ã¨ estratta una Keyword inseriamo la
 * Keyword in una lista da utilizzare successivamente per lt'inserimento nel
 * database.
 */
@SuppressWarnings("unused")
public class GateAnnotation8Kit {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GateAnnotation8Kit.class);

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
                    String content = getSingleAnnotationInfo(document, nameAnnotation, nameAnnotationSet);
                    //get the annotation on the first annotation set is have it without check the other annnotation set...
                    if (!StringKit.isNullOrEmpty(content)) {
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
                //mapAnn.clear();
            }
             String name = document.getName();
             mapDocs.put(name,mapAnnSet);
        }//while
        return mapDocs;
    }//getKeyword

    /**
     * Method for get the content of annotation with a specific methods.
     * @param document gate document.
     * @param nameAnnotation strin name of the Annotation.
     * @param nameAnnotationSet string name of the AnnotationSet.
     * @return string content of the Annotation.
     */
    public String getSingleAnnotationInfo(Document document,String nameAnnotation,String nameAnnotationSet) {
        String content ="";
        try {
            AnnotationSet annSet = getAnnotationSetFromDoc(nameAnnotationSet, document);
            //SystemLog.message("Get content of the Annotation " + nameAnnotation + " on the AnnotationSet " + annSet.getName() + "...");
            //content = getContentLongestFromAnnnotationsOnAnnotationSet(document, nameAnnotation, annSet);         
            Annotation newAnn;
            for(Annotation ann: annSet){
                if(ann.getType().equals(nameAnnotation)){
                    newAnn = ann;
                    content = getContentFromAnnotation(document,newAnn);
                    break;
                }
            }
            if(StringKit.isNullOrEmpty(content)){
                content ="";
            }else{
                content = StringKit.cleanStringHTML(content);
            }          
            //content =  getContentLastSingleAnnotationOnAnnotationSet(document, nameAnnotation, annSet);
        }catch(NullPointerException ne){
            SystemLog.warning("The AnnotationSet "+nameAnnotationSet+" not have a single annotation for this document to the url: "+ document.getSourceUrl());
        }
        return content;
    }

    public List<Object> getValueOfAnnotationFromDoc(Document doc,String annotatioName){
        List<Object> list = new ArrayList<>();
        // obtain the Original markups annotation set
        AnnotationSet origMarkupsSet = doc.getAnnotations("Original markups");
        // obtain annotations of type â€™aâ€™
        AnnotationSet anchorSet = origMarkupsSet.get("a");
        // iterate over each annotation
        // obtain its features and print the value of href feature
        // System.out.println("iterate over each annotation and obtain its features and print the value of href feature...");
        for (Annotation anchor : anchorSet) {
            //String href = (String) anchor.getFeatures().get("href");
            String valueAnn = (String) anchor.getFeatures().get(annotatioName);
            if(valueAnn != null) {
                //URL uHref=new URL(doc.getSourceUrl(), href);
                // resolving href value against the documentâ€™s url
                if(!(list.contains(valueAnn)))list.add(valueAnn);
            }//if
        }//for anchor
        return list;
    }

    /**
     * Method for get all the contents of the annotations from all the same annotations on the same annotationSet.
     * @param doc gate document.
     * @param nameAnnotation string name of the annotation.
     * @param annotationSet string name of the annotationset.
     * @return list of string where each string is the string content of a  annotation on the document.
     */
    public List<String> getContentAllSingleAnnotationOnAnnotationSet(Document doc,String nameAnnotation,AnnotationSet annotationSet) {
        String content;
        List<String> stringList = new ArrayList<>(); //support list
        List<String> finalList = new ArrayList<>();
        try{
            //Get all type annotations within the stringNameAnnotation set of annotations date.set...
            annotationSet = annotationSet.get(nameAnnotation);
            for(Annotation anAnn: annotationSet){
                content = Utils.stringFor(doc, anAnn);
                //avoid duplicate string or substring on the final list...
                if (!(stringList.contains(content))) {
                    stringList.add(content);
                    for (String s : stringList) {
                        if (!s.contains(content))finalList.add(content);
                    }//for(String s : stringList)
                }//!(stringList.contains(content2)
            }//for
            return finalList;

        } catch (NullPointerException ep) {
            SystemLog.exception(ep);
        }
        return finalList;

    }

    /**
     * Method for get the last content of the last annotation from all the same annotations on the same annotationSet.
     * @param doc gate document.
     * @param nameAnnotation string name of the annotation.
     * @param annotationSet string name of the annotationset.
     * @return  string content fo the annotation.
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
            SystemLog.exception(ep);
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
    public String getContentLongestFromAnnnotationsOnAnnotationSet(Document doc,String nameAnnotation,AnnotationSet annotationSet){
        long begOffset,endOffset;
        long x = 0;
        long y = 0;
        String content="";
        try{
            //Get all type annotations within the stringNameAnnotation set of annotations date.set...
            annotationSet = annotationSet.get(nameAnnotation);
            int i = 0;
            for(Annotation annotation: annotationSet){
                if (annotation == null || StringKit.isNullOrEmpty(Utils.stringFor(doc, annotation))) continue;
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
            if(StringKit.isNullOrEmpty(content)){
                return null;
            }else{
                return content;
            }
        } catch (NullPointerException ep) {
            SystemLog.warning(ep.getMessage());
        }
        return null;
    }

    /**
     * Prende un determinato set di annotazione per un determinato documento.
     * @param nameAnnotationSet nome del set di annotazioni
     * @param doc il GATE Document preso in esame
     * @return il GATE Document preso in esame ma solo la parte coperta dal set di annotazioni
     */
    public AnnotationSet getAnnotationSetFromDoc(String nameAnnotationSet, Document doc) {
        AnnotationSet annSet = doc.getAnnotations(nameAnnotationSet);
        if(annSet.isEmpty()){
            return null;
        }else {
            return annSet;
        }
    }

    /**
     * Method for get specific annotation from specific annotaset from specific document.
     * @param doc gate Document.
     * @param nameAnnotationSet string name of gate AnnotationSet.
     * @param nameAnnotation string name of gate Annotation.
     * @return list of gate annotations.
     */
    public List<Annotation> getAnnotationsFromDoc(Document doc,String nameAnnotationSet,String nameAnnotation){
        AnnotationSet annSet = doc.getAnnotations( nameAnnotationSet);
        if(annSet.isEmpty()){
            return null;
        }else {
            Set<Annotation> newSetAnnotation = new HashSet<>(annSet.get(nameAnnotation));
            return  new ArrayList<>(newSetAnnotation);
        }
    }

    /**
     * Method get the string content marked up from a aspecific annotation.
     * @param doc gate document.
     * @param annotation annotation gate.
     * @return the string content marked up from the gate annotation.
     */
    public String getContentFromAnnotation(Document doc,Annotation annotation){
        return Utils.stringFor(doc, annotation);
    }

    /*public Set<String> countAnnotationsOnTheWebPage(Document doc){
        //codice aggiuntivo utile per avere un'idea del contenuto della pagina org.p4535992.mvc.webapp
        // obtain a map of all named annotation sets
        Set<String> annotTypes= null;
        Map<String, AnnotationSet> namedASes = doc.getNamedAnnotationSets();
        System.out.println("No. of named Annotation Sets:"+ namedASes.size());
        // number of annotations each set contains
        for (String setName : namedASes.keySet()) {
            // annotation set
            AnnotationSet aSet = namedASes.get(setName);
            // number of annotations
            SystemLog.message("No. of Annotations for " + setName + ":" + aSet.size());
            // all annotation types
            annotTypes = aSet.getAllTypes();
            for(String aType : annotTypes) {
                System.out.print(" " + aType + ": " + aSet.get(aType).size()+"||");
            }//for aType
        }//for setName
        return annotTypes;
    }*/
    
    public static <K,V> boolean isMapValueNullOrInexistent(Map<K,V> map,K key){
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

