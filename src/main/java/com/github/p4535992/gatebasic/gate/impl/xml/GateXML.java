package com.github.p4535992.gatebasic.gate.impl.xml;

import com.github.p4535992.gatebasic.gate.annotation.SortedAnnotationList;
import gate.*;
import gate.corpora.RepositioningInfo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 4535992 on 23/12/2015.
 * @author 4535992.
 * @version 2015-12-23.
 */
public class GateXML{

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(GateXML.class);

    private static GateXML instance = null;
    protected GateXML(){}

    public static GateXML getInstance(){
        if(instance == null) {
            instance = new GateXML();
        }
        return instance;
    }

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
