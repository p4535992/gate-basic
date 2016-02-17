package com.github.p4535992.gatebasic.gate.gate8;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Utils;
import gate.corpora.DocumentStaxUtils;
import org.apache.commons.io.FileUtils;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 4535992 on 12/02/2016.
 * @author 4535992.
 * Just a mirror class for {@link Utils} class of gate.
 */
public class GateUtils {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateUtils.class);

    /**
     * Method get the string content marked up from a aspecific annotation.
     * @param doc the {@link Document} gate.
     * @param annotation the {@link Annotation} gate.
     * @return the {@link String} content marked up from the gate annotation.
     */
    public String getContent(Document doc,Annotation annotation){
        return Utils.stringFor(doc, annotation);
    }

    /**
     * Method get the string content marked up from a aspecific annotation.
     * @param doc the {@link Document} gate.
     * @param annotationSet the {@link AnnotationSet} gate.
     * @return the {@link String} content marked up from the gate annotation.
     */
    public String getContent(Document doc, AnnotationSet annotationSet){
        return Utils.stringFor(doc, annotationSet);
    }

    /**
     * Prende un determinato set di annotazione per un determinato documento.
     * @param nameAnnotationSet nome del set di annotazioni
     * @param doc il GATE Document preso in esame
     * @return il GATE Document preso in esame ma solo la parte coperta dal set di annotazioni
     */
    public static AnnotationSet getAnnotationSetFromDoc(String nameAnnotationSet, Document doc) {
        AnnotationSet annSet = doc.getAnnotations(nameAnnotationSet);
        if(annSet.isEmpty()){
            logger.warn("The AnnotationSet "+nameAnnotationSet+ " not have any Annotation for the current document.");
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
    public static List<Annotation> getAnnotationsFromDoc(Document doc, String nameAnnotationSet, String nameAnnotation){
        AnnotationSet annSet = doc.getAnnotations( nameAnnotationSet);
        if(annSet.isEmpty()){
            return null;
        }else {
            Set<Annotation> newSetAnnotation = new HashSet<>(annSet.get(nameAnnotation));
            return  new ArrayList<>(newSetAnnotation);
        }
    }

    /**
     * Method to get the String contet from a gate Document in a specific Annotation type.
     * OLD_NAME: getValueOfAnnotationFromDoc
     * @param doc the {@link Document} of gate.
     * @param annotationName the {@link Annotation} of gate.
     * @return the {@link List} of {@link Object} Feature.
     */
    public static List<String> getContentFromDoc(Document doc,String annotationName){
        List<String> list = new ArrayList<>();
        // obtain the Original markups annotation set
        AnnotationSet origMarkupsSet = doc.getAnnotations("Original markups");
        // obtain annotations of type â€™aâ€™
        AnnotationSet anchorSet = origMarkupsSet.get("a");
        // iterate over each annotation
        // obtain its features and print the value of href feature
        // System.out.println("iterate over each annotation and obtain its features and print the value of href feature...");
        for (Annotation anchor : anchorSet) {
            //String href = (String) anchor.getFeatures().get("href");
            String valueAnn = String.valueOf(anchor.getFeatures().get(annotationName));
            if(valueAnn != null) {
                //URL uHref=new URL(doc.getSourceUrl(), href);
                // resolving href value against the documentâ€™s url
                if(!(list.contains(valueAnn)))list.add(valueAnn);
            }//if
        }//for anchor
        return list;
    }

    public static List<String> getContentFromDoc(Document doc,String annotationSet,String annotationName){
        List<String> list = new ArrayList<>();
        // obtain the Original markups annotation set
        AnnotationSet origMarkupsSet = doc.getAnnotations(annotationSet);
        // obtain annotations of type â€™aâ€™
        AnnotationSet anchorSet = origMarkupsSet.get("a");
        // iterate over each annotation
        // obtain its features and print the value of href feature
        // System.out.println("iterate over each annotation and obtain its features and print the value of href feature...");
        for (Annotation anchor : anchorSet) {
            //String href = (String) anchor.getFeatures().get("href");
            String valueAnn =  String.valueOf(anchor.getFeatures().get(annotationName));
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
     * OLD_NAME: getContentAllSingleAnnotationOnAnnotationSet.
     * @param doc gate document.
     * @param nameAnnotation string name of the annotation.
     * @param annotationSet string name of the annotationset.
     * @return list of string where each string is the string content of a  annotation on the document.
     */
    public static List<String> getContentFromDoc(Document doc,AnnotationSet annotationSet,String nameAnnotation) {
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
                    }
                }
            }
            return finalList;
        } catch (NullPointerException ignored) {}
        return finalList;
    }

    /**
     * Method to write the document gate as XML.
     * href: http://stackoverflow.com/questions/19677972/read-annotated-data-from-gate-datastore
     * @param document the {@link Document} to write.
     * @param outputFile the {@link File} where write the annotations.
     * @return the {@link File} written.
     */
    public File writeDocument(Document document,File outputFile){
        try {
            DocumentStaxUtils.writeDocument(document, outputFile);
        } catch (XMLStreamException | IOException e) {
            logger.error(e.getMessage(),e);
        }
        return outputFile;
    }

    /**
     * Method to write the annotations as inline XML.
     * href: http://stackoverflow.com/questions/19677972/read-annotated-data-from-gate-datastore
     * @param document the {@link Document} to write.
     * @param annotationsToSearch the {@link List} of {@link String} to write.
     * @param outputFile the {@link File} where write the annotations.
     * @return the {@link File} written.
     */
    public File writeAnnotations(Document document,List<String> annotationsToSearch,File outputFile){
        Set<String> types = new HashSet<>();
        for(String ann: annotationsToSearch){
            types.add(ann); // and whatever others you're interested in
        }
        try {
            FileUtils.write(outputFile, document.toXml(document.getAnnotations().get(types), true));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return outputFile;
    }




}
