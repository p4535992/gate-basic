package com.github.p4535992.gatebasic.gate.gate8;

import gate.*;
import gate.corpora.DocumentStaxUtils;
import gate.creole.ResourceInstantiationException;
import gate.creole.tokeniser.DefaultTokeniser;
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
//@@TODO NEED SOME IMPROVEMENT AND INTEGRATION WITH THE PROJECT
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
     * Method to get a AnnotationSet from a Document gate.
     * @param nameAnnotationSet the {@link String} name of the AnnotationSet.
     * @param doc the {@link Document} of gate.
     * @return the {@link AnnotationSet} of GATE founded.
     */
    public static AnnotationSet getAnnotationSetFromDoc(Document doc,String nameAnnotationSet) {
        AnnotationSet annSet = doc.getAnnotations(nameAnnotationSet);
        if(annSet.isEmpty()){
            logger.warn("The AnnotationSet "+nameAnnotationSet+ " not have any Annotation for the current document.");
            return null;
        }else {
            return annSet;
        }
    }

    /**
     * Method to get a specific List of Annotation from a AnnotationSet in the gate Document.
     * @param doc the {@link Document of GATE}.
     * @return the {@link List} of {@link Annotation} of GATE founded.
     */
    public static List<Annotation> getAnnotationsFromDoc(Document doc) {
        AnnotationSet annSet = doc.getAnnotations();
        if(annSet.isEmpty()){
            logger.warn("The Document "+doc.getName()+ " not have any Annotation for the current document.");
            return null;
        }else {
            //return annSet;
            return toAnnotations(annSet);
        }
    }

    /**
     * Method to get a specific List of Annotation from a AnnotationSet in the gate Document.
     * @param doc the {@link Document of GATE}
     * @param nameAnnotationSet the {@link String} name of the {@link AnnotationSet}.
     * @return the {@link List} of {@link Annotation} of GATE founded.
     */
    public static List<Annotation> getAnnotationsFromDoc(Document doc,String nameAnnotationSet) {
        AnnotationSet annSet = doc.getAnnotations(nameAnnotationSet);
        if(annSet.isEmpty()){
            logger.warn("The AnnotationSet "+nameAnnotationSet+ " not have any Annotation for the current document.");
            return null;
        }else {
            //return annSet;
            return toAnnotations(annSet);
        }
    }

    /**
     * Method for get specific annotation from specific annotaset from specific document.
     * Method to get a specific List of Annotation from a AnnotationSet in the gate Document.
     * @param doc the {@link Document of GATE}
     * @param nameAnnotationSet the {@link String} name of the {@link AnnotationSet}.
     * @param nameAnnotation the {@link String} name of the {@link Annotation}.
     * @return the {@link List} of {@link Annotation} of GATE founded.
     */
    public static List<Annotation> getAnnotationsFromDoc(Document doc, String nameAnnotationSet, String nameAnnotation){
        AnnotationSet annSet = doc.getAnnotations( nameAnnotationSet);
        if(annSet.isEmpty()){
            logger.warn("The AnnotationSet "+annSet.getName()+ " not have any Annotation for the current document.");
            return null;
        }else {
            return toAnnotations(annSet,nameAnnotation);
        }
    }

    /**
     * Method to get the String contet from a gate Document in a specific Annotation type.
     * OLD_NAME: getValueOfAnnotationFromDoc
     * @param doc the {@link Document} of gate.
     * @param annotationName the {@link Annotation} of gate.
     * @return the {@link List} of {@link Object} Feature of GATE founded.
     */
    public static List<String> getContentFromDoc(Document doc,String annotationName){
       return getContentFromDoc(doc,"Original markups",annotationName);
    }

    public static List<String> getContentFromDoc(Document doc,String annotationSet,String annotationName){
        List<String> list = new ArrayList<>();
        // obtain the Original markups annotation set
        AnnotationSet origMarkupsSet = doc.getAnnotations(annotationSet);
        // obtain annotations of type â€™aâ€™
        AnnotationSet anchorSet = origMarkupsSet.get("a");
        // iterate over each annotation
        // obtain its features and print the value of href feature
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
            org.apache.commons.io.FileUtils.write(outputFile, document.toXml(document.getAnnotations().get(types), true));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return outputFile;
    }

    public static String generateSym() {
        return Gate.genSym();
    }

    public static List<Annotation> toAnnotations(AnnotationSet annotationSet){
        Set<Annotation> newSetAnnotation = new HashSet<>(annotationSet.get());
        return new ArrayList<>( newSetAnnotation);
    }

    public static List<Annotation> toAnnotations(AnnotationSet annotationSet,Annotation annotation){
        Set<Annotation> newSetAnnotation = new HashSet<>(annotationSet.get(annotation.getType()));
        return new ArrayList<>( newSetAnnotation);
    }

    public static List<Annotation> toAnnotations(AnnotationSet annotationSet,String nameAnnotation){
        Set<Annotation> newSetAnnotation = new HashSet<>(annotationSet.get(nameAnnotation));
        return new ArrayList<>( newSetAnnotation);
    }

    public static List<Annotation> toAnnotations(AnnotationSet annotationSet,List<String> listAnnotations){
        Set<Annotation> newSetAnnotation = new HashSet<>(annotationSet.get(new HashSet<>(listAnnotations)));
        return new ArrayList<>( newSetAnnotation);
    }

    public static Annotation toAnnotation(AnnotationSet annotationSet,Integer indexAnnotation){
        return annotationSet.get(indexAnnotation);
    }

    public static Object toFeature(Annotation annotation,String annotationName){
        return annotation.getFeatures().get(annotationName);
    }

    public static Object toFeature(Annotation annotation){
        return annotation.getFeatures().get(annotation.getType());
    }

    public static ProcessingResource createDefaultTokenizer(Document doc) throws ResourceInstantiationException {
        //create a default tokeniser
        FeatureMap params = Factory.newFeatureMap();
        params = Factory.newFeatureMap();
        params.put(DefaultTokeniser.DEF_TOK_DOCUMENT_PARAMETER_NAME, doc);
        ProcessingResource tokeniser = (ProcessingResource) Factory.createResource(
                "gate.creole.tokeniser.DefaultTokeniser", params
        );
        return tokeniser;
    }

    /*
     If you need to duplicate other resources, use the two-argument
     Factory.duplicate, passing the ctx as the second parameter, to preserve object graph
     two calls to Factory.duplicate(r, ctx) for the same resource r in the same context ctx will return the same duplicate.
     calls to the single argument Factory.duplicate(r) or to the
     two-argument version with different contexts will return different duplicates.
     Can call the default duplicate algorithm (bypassing the CustomDuplication check) via Factory.defaultDuplicate
     it is safe to call defaultDuplicate(this, ctx), but calling duplicate(this, ctx) from within its own custom
     duplicate will cause infinite recursion!  
     */
    public Resource duplicate(Factory.DuplicationContext ctx, List<ProcessingResource> prList)throws ResourceInstantiationException {
        // duplicate this controller in the default way - this handles subclasses nicely
        Controller c = (Controller) Factory.defaultDuplicate((Resource) this, ctx);
        // duplicate each of our PRs
        List<ProcessingResource> newPRs = new ArrayList<>();
        for(ProcessingResource pr : prList) {
            newPRs.add((ProcessingResource)Factory.duplicate(pr, ctx));
        }
        // and set this duplicated list as the PRs of the copy
        c.setPRs(newPRs);
        return c;
    }

    /*
     * @return a {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer}
     * so that placeholders are correctly populated
     * @throws Exception exception if the file is not found or cannot be opened or read
     */
    /*@Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws Exception {
        PropertySourcesPlaceholderConfigurer propConfig = new PropertySourcesPlaceholderConfigurer();
        org.springframework.core.io.Resource[] resources = new UrlResource[]
                {new UrlResource("file:${user.home}/.config/api.properties")};
        propConfig.setLocations(resources);
        propConfig.setIgnoreResourceNotFound(true);
        propConfig.setIgnoreUnresolvablePlaceholders(true);
        return propConfig;
    }*/




}
