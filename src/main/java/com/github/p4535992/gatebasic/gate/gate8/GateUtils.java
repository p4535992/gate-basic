package com.github.p4535992.gatebasic.gate.gate8;

import com.github.p4535992.gatebasic.gate.annotation.SortedAnnotationList;
import gate.*;
import gate.corpora.DocumentStaxUtils;
import gate.corpora.RepositioningInfo;
import gate.creole.ResourceInstantiationException;
import gate.creole.tokeniser.DefaultTokeniser;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
            logger.warn("AnnotationSet -> "+nameAnnotationSet+ " not have any Annotation for the current document.");
            return null;
        }else {
            return annSet;
        }
    }

    /**
     * Method to get all the AnnotationSet from a Document gate.
     * @param doc the {@link Document} of gate.
     * @return the the {@link List} of {@link AnnotationSet} of GATE founded.
     */
    public static List<AnnotationSet> getAnnotationSetsFromDoc(Document doc) {
        Set<String> annSetNames = doc.getAnnotationSetNames();
        if(annSetNames.isEmpty()){
            logger.warn("Document -> "+doc.getName()+ " not have any AnnotationSet for the current document.");
            return null;
        }else {
            List<AnnotationSet> listAnnSet = new ArrayList<>();
            for(String annSetName : annSetNames){
                listAnnSet.add(getAnnotationSetFromDoc(doc,annSetName));
            }
            return listAnnSet;
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
            logger.warn("Document -> "+doc.getName()+ " not have any Annotation for the current document.");
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
            logger.warn("AnnotationSet -> "+nameAnnotationSet+ " not have any Annotation for the current document.");
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
            logger.warn("AnnotationSet -> "+annSet.getName()+ " not have any Annotation for the current document.");
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
        // obtain annotations of type â€™aâ€™ AnnotationSet anchorSet = origMarkupsSet.get("a")
        AnnotationSet anchorSet = origMarkupsSet.get(annotationName);
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
            org.apache.commons.io.FileUtils.write(outputFile, document.toXml(document.getAnnotations().get(types), true), StandardCharsets.UTF_8);
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

    //============================================
    // SUPPORT FOR XML
    //============================================

    /**
     * Method to convert every gate document in a coprus in xml files with all the annotations you gate in them
     * @param corpus corpus  of gate.
     * @param addAnnotTypesRequired list of annotation.
     * @param directory file directory where store the html and xml file of the gate documents.
     * @throws IOException error.
     */
    public void createXMLFileForEachDoc(Corpus corpus, List<String> addAnnotTypesRequired, File directory) throws IOException{
        // for each document, get an XML document with the person,location,MyGeo names added
        //String pathDir = FileUtilities.getPath(directory);
        Iterator<Document> iter = corpus.iterator();
        int count = 0;
        String startTagPart_1 = "<span GateID=\"";
        String startTagPart_2 = "\" title=\"";
        String startTagPart_3 = "\" style=\"background:Red;\">";
        String endTag = "</span>";
        String[] tags = new String[]{startTagPart_1,startTagPart_2,startTagPart_3,endTag};
        while(iter.hasNext()) {
            Document doc = iter.next();
            AnnotationSet defaultAnnotSet = doc.getAnnotations();
            Set<String> annotTypesRequired = new HashSet<>();
            for (String s : addAnnotTypesRequired) {
                annotTypesRequired.add(s);
                logger.info(s);
            }
            // annotTypesRequired.add("Person");
            // annotTypesRequired.add("Location");
            Set<Annotation> newSetAnnotation = new HashSet<>(defaultAnnotSet.get(annotTypesRequired));
            FeatureMap features = doc.getFeatures();
            String originalContent = (String)features.get(GateConstants.ORIGINAL_DOCUMENT_CONTENT_FEATURE_NAME);
            RepositioningInfo info = (RepositioningInfo)features.get(GateConstants.DOCUMENT_REPOSITIONING_INFO_FEATURE_NAME);
            ++count;
            ///GENERAZIONE DEI DOCUMENTI
            //String nameGateDocument0 = doc.getName();
            //String fileName0 = "("+count+")"+nameGateDocument0+".html";
            if(!directory.exists()) {
                //create file if not exists...
                if(!directory.createNewFile()){
                    logger.warn("Can't create the file "+directory.getAbsolutePath());
                }
            }
            logger.info("File write to the path : '" + directory.getAbsolutePath() + "'");
            //after the controller is execute....
            if(originalContent != null && info != null) {
                logger.info("OrigContent and reposInfo existing. Generate file...");
                Iterator<Annotation> it = newSetAnnotation.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();
                while(it.hasNext()) {
                    currAnnot = it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while
                /*StringBuilder editableContent = new StringBuilder(originalContent);
                long insertPositionEnd;
                long insertPositionStart;*/
                // insert anotation tags backward
                logger.info("Unsorted annotations count: " + newSetAnnotation.size());
                logger.info("Sorted annotations count: " + sortedAnnotations.size());
                /*if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
                    for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                        currAnnot = sortedAnnotations.get(i);
                        insertPositionStart = currAnnot.getStartNode().getOffset();
                        insertPositionStart = info.getOriginalPos(insertPositionStart);
                        insertPositionEnd = currAnnot.getEndNode().getOffset();
                        insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
                        if(insertPositionEnd != -1 && insertPositionStart != -1) {
                            editableContent.insert((int)insertPositionEnd, endTag);
                            editableContent.insert((int)insertPositionStart, startTagPart_3);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getType());
                            editableContent.insert((int)insertPositionStart, startTagPart_2);
                            editableContent.insert((int)insertPositionStart,
                                    currAnnot.getId().toString());
                            editableContent.insert((int)insertPositionStart, startTagPart_1);
                        } // if
                    } // for
                }// if size*/
                StringBuilder editableContent =
                        supportCreateXMLWithInfo(newSetAnnotation,sortedAnnotations,tags,originalContent,info);
                try (FileWriter writer = new FileWriter(directory)) {
                    writer.write(editableContent.toString());
                }
            } // if - should generate
            else if (originalContent != null) {
                logger.info("OrigContent existing. Generate file...");

                Iterator<Annotation> it = newSetAnnotation.iterator();
                Annotation currAnnot;
                SortedAnnotationList sortedAnnotations = new SortedAnnotationList();

                while(it.hasNext()) {
                    currAnnot = it.next();
                    sortedAnnotations.addSortedExclusive(currAnnot);
                } // while

               /* StringBuilder editableContent = new StringBuilder(originalContent);
                long insertPositionEnd;
                long insertPositionStart;*/
                // insert anotation tags backward
                logger.info("Unsorted annotations count: " + newSetAnnotation.size());
                logger.info("Sorted annotations count: " + sortedAnnotations.size());
                /*if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
                    for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                        currAnnot = sortedAnnotations.get(i);
                        insertPositionStart = currAnnot.getStartNode().getOffset();
                        insertPositionEnd = currAnnot.getEndNode().getOffset();
                        if(insertPositionEnd != -1 && insertPositionStart != -1) {
                            editableContent.insert((int)insertPositionEnd, endTag);
                            editableContent.insert((int)insertPositionStart, startTagPart_3);
                            editableContent.insert((int)insertPositionStart,currAnnot.getType());
                            editableContent.insert((int)insertPositionStart, startTagPart_2);
                            editableContent.insert((int) insertPositionStart, currAnnot.getId().toString());
                            editableContent.insert((int)insertPositionStart, startTagPart_1);
                        } // if
                    } // for
                }//if size*/
                StringBuilder editableContent = supportCreateXML(newSetAnnotation,sortedAnnotations,tags,originalContent);
                try (FileWriter writer = new FileWriter(directory)) {
                    writer.write(editableContent.toString());
                }
            }
            else {
                logger.info("Content : " + doc.getContent().toString());
                logger.info("Repositioning: " + info);
            }

            String xmlDocument = doc.toXml(newSetAnnotation, false);
            String fileName = "("+count+")"+doc.getName()+".xml";
            //File dir2 = new File ("gate_files");
            File actualFile = new File (fileName);
            try (FileWriter writer2 = new FileWriter(actualFile)) {
                writer2.write(xmlDocument);
            }
        }
    }

    private StringBuilder supportCreateXML(Set<Annotation> newSetAnnotation,SortedAnnotationList sortedAnnotations,
                                           String[] tags, String originalContent){
        StringBuilder editableContent = new StringBuilder(originalContent);
        long insertPositionEnd;
        long insertPositionStart;
        if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
            for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                Annotation currAnnot = sortedAnnotations.get(i);
                insertPositionStart = currAnnot.getStartNode().getOffset();
                insertPositionEnd = currAnnot.getEndNode().getOffset();
                supportSupportXML(editableContent,insertPositionStart,insertPositionEnd,tags,currAnnot);
            } // for
        }//if size
        return editableContent;
    }

    private StringBuilder supportCreateXMLWithInfo(Set<Annotation> newSetAnnotation,SortedAnnotationList sortedAnnotations,
                                                   String[] tags, String originalContent,RepositioningInfo info) {
        StringBuilder editableContent = new StringBuilder(originalContent);
        long insertPositionEnd;
        long insertPositionStart;
        if(newSetAnnotation.size()>0 && sortedAnnotations.size()>0){
            for(int i=sortedAnnotations.size()-1; i>=0; --i) {
                Annotation currAnnot = sortedAnnotations.get(i);
                insertPositionStart = currAnnot.getStartNode().getOffset();
                insertPositionStart = info.getOriginalPos(insertPositionStart);
                insertPositionEnd = currAnnot.getEndNode().getOffset();
                insertPositionEnd = info.getOriginalPos(insertPositionEnd, true);
                supportSupportXML(editableContent,insertPositionStart,insertPositionEnd,tags,currAnnot);
            } // for
        }// if size
        return editableContent;
    }

    private StringBuilder supportSupportXML(StringBuilder editableContent,long insertPositionStart,long insertPositionEnd,String[] tags,Annotation currAnnot){
        if(insertPositionEnd != -1 && insertPositionStart != -1) {
            editableContent.insert((int)insertPositionEnd, tags[4]);
            editableContent.insert((int)insertPositionStart, tags[3]);
            editableContent.insert((int)insertPositionStart,currAnnot.getType());
            editableContent.insert((int)insertPositionStart, tags[2]);
            editableContent.insert((int) insertPositionStart, currAnnot.getId().toString());
            editableContent.insert((int)insertPositionStart, tags[1]);
        } // if
        return editableContent;
    }



}
