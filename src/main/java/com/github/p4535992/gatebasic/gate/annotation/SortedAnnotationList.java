package com.github.p4535992.gatebasic.gate.annotation;

import gate.Annotation;

import java.util.Vector;

/**
 * Class for sort the annotation in a list.
 */
public class SortedAnnotationList extends Vector<Annotation> {

    private static final org.slf4j.Logger logger =
            org.slf4j.LoggerFactory.getLogger(SortedAnnotationList.class);

    private static final long serialVersionUID = 15L;

    public SortedAnnotationList() {
        super();
    } // SortedAnnotationList

    public boolean addSortedExclusive(Annotation annot) {
        Annotation currAnot;
        // overlapping check
            /*for (int i=0; i<size(); ++i) {
                currAnot = (Annotation) get(i);
                if(annot.overlaps(currAnot)) {
                    return false;
                } // if
            } // for*/
        for (Object o : this) {
            currAnot = (Annotation) o;
            if (annot.overlaps(currAnot)) return false;
            // if
        } // for
        long annotStart = annot.getStartNode().getOffset();
        long currStart;
        // insert
        for (int i=0; i < size(); ++i) {
            currAnot = get(i);
            currStart = currAnot.getStartNode().getOffset();
            if(annotStart < currStart) {
                super.insertElementAt(annot, i);
                logger.info("Insert start: " + annotStart + " at position: " + i + " size=" + size());
                logger.info("Current start: " + currStart);
                return true;
            } // if
        } // for

        int size = size();
        super.insertElementAt(annot, size);
        logger.info("Insert start: " + annotStart + " at size position: " + size);
        return true;
    } // addSorted
} // SortedAnnotationList
