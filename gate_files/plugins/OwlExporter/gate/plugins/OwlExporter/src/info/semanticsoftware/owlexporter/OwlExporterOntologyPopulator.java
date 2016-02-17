/*
OwlExporter --  http://www.semanticsoftware.info/owlexporter

This file is part of the OwlExporter architecture.

Copyright (C) 2009, 2010, 2011 Semantic Software Lab, http://www.semanticsoftware.info
        René Witte
        Ninus Khamis

The OwlExporter  architecture is free software: you can
redistribute and/or modify it under the terms of the GNU Affero General
Public License as published by the Free Software Foundation, either
version 3 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package info.semanticsoftware.owlexporter;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLClass;
import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSDatatype;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.DocumentContent;
import gate.FeatureMap;
import gate.creole.ANNIEConstants;
import gate.jape.constraint.RegExpFindPredicate;
import gate.util.InvalidOffsetException;
import info.semanticsoftware.owlexporter.exception.PopulatorException;

public abstract class OwlExporterOntologyPopulator {
	private OwlExporterOntology owlExporterOntology;
	protected final String OWLEXPORTCLASSDOMAIN = "OwlExportClassDomain";
    protected final String OWLEXPORTRELATIONDOMAIN = "OwlExportRelationDomain";
    protected final String OWLEXPORTCLASSNLP = "OwlExportClassNLP";
    protected final String OWLEXPORTRELATIONNLP = "OwlExportRelationNLP";
    protected final String OWLEXPORTRELATIONDOMAINNLP = "OwlExportRelationDomainNLP";
    protected final String CHAINID = "IDs";
    protected final String SENTANNOTATION = "Sentence";
    protected final String CLASSNAME = "className";    
    protected final String DOMAINID = "domainId";
    protected final String RANGEID = "rangeId";
    protected final String PROPERTYNAME = "propertyName";
    protected final String REPRESENTATIONID = "representationId";
    protected final String INSTANCENAME = "instanceName";
    protected static final String NO_TOKEN_MSG = "There is no token annotation!" ;
	
    protected static final String COREFCHAIN = "corefChain";	
	private Set allChainIDs;
    private Set containedIDs;
    private Set chainIDsInSent;    
    private Set returnSet;
    
		
	protected void setOwlExporterOntology(OwlExporterOntology owlExporterOntology) {
		this.owlExporterOntology = owlExporterOntology;		
	}
	
	protected OwlExporterOntology getOwlExporterOntology() {
		return this.owlExporterOntology;		
	}
	
	protected AnnotationSet getInputAs() throws PopulatorException {
		AnnotationSet inputAs = (this.owlExporterOntology.getOwlExporter().getInputASName() == null) ?
				this.owlExporterOntology.getOwlExporter().getDocument().getAnnotations() :
					this.owlExporterOntology.getOwlExporter().getDocument().getAnnotations(
							this.owlExporterOntology.getOwlExporter().getInputASName());		 
		return inputAs;
	}
	
	protected AnnotationSet getRelationAs(OntologyType oT) throws PopulatorException {
		AnnotationSet relationAs = null;
		
		try {
			if(oT.getOntologyType() == OntologyType.domain.getOntologyType()) {
				relationAs = this.getInputAs().get(this.OWLEXPORTRELATIONDOMAIN);
			}
			else if(oT.getOntologyType() == OntologyType.nlp.getOntologyType()) {
				relationAs = this.getInputAs().get(this.OWLEXPORTRELATIONNLP);
			}			
			else if(oT.getOntologyType() == OntologyType.domainnlp.getOntologyType()) {
				relationAs = this.getInputAs().get(this.OWLEXPORTRELATIONDOMAINNLP);
			}
			else relationAs = null;
		}
		catch(PopulatorException aEx) {
			return null;
		}
	
		return relationAs;
	}		
	
	protected DocumentContent getDocContent() throws PopulatorException {
		return this.owlExporterOntology.getOwlExporter().getDocument().getContent();
	}
	
	protected boolean hasProcessed(JenaOWLModel model, URL sourceUrl) {
      // Any instance of a "Document" class with a "sourceUrl" datatype property
      // matching the passed in sourceUrl argument have already been processed.
		try {			
			RDFSNamedClass c = model.getRDFSNamedClass("Document");
			if(c!=null) {
				Collection<RDFSClass> i = c.getInstances(true);				
				for(Iterator<RDFSClass> instanceIt = i.iterator(); instanceIt.hasNext();) {
					OWLIndividual ind = (OWLIndividual) instanceIt.next();				
					
					Collection<RDFProperty> p = ind.getRDFProperties();					
					for(Iterator<RDFProperty> propIt = p.iterator(); propIt.hasNext();) {
						RDFProperty prop = propIt.next();
						
						
						
						if(prop.getLocalName().compareToIgnoreCase("sourceUrl")==0) {							
							if(ind.getPropertyValue(prop).toString().
									compareToIgnoreCase(sourceUrl.getFile())==0)
                        return true;
						}
									
								
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;	
	}
    
    protected void populateOntology(CommonOwlExporter cOE, OntologyType oT) throws PopulatorException {   	
		AnnotationSet inputAs;
		try {
			if(oT.getOntologyType() == OntologyType.domain.getOntologyType())
				inputAs = this.getInputAs().get(this.OWLEXPORTCLASSDOMAIN);
			else
				inputAs = this.getInputAs().get(this.OWLEXPORTCLASSNLP);
				
    	}
    	catch(PopulatorException aEx) {
    		return;
    	}    	
    			
		OWLDatatypeProperty idProp;		
		try {		
			idProp = this.createDataTypeProperty(
					this.getOwlExporterOntology(), "idPropOf", 
					this.getOwlExporterOntology().getOwlModel().getXSDint());					
		}
		catch (Exception e) {
			throw new PopulatorException("Error: populateOntology  " + e);				
		}
		
		for(Iterator itTypes = inputAs.iterator(); itTypes.hasNext();) {
			Annotation ann = (Annotation) itTypes.next();
			
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Annotation ID: " + ann.getId());
			
			final FeatureMap annFeat = ann.getFeatures();
			if(annFeat.get(this.CLASSNAME) == null) {
            System.err.println("Annotation <"+ ann.getType() +"> is missing the "+ this.CLASSNAME +" feature.");
            continue;
         }
			
			OWLNamedClass owlClass = this.getOwlExporterOntology().getOwlModel().
							getOWLNamedClass(annFeat.get(this.CLASSNAME).toString());
			
         // Best effort population, so ignore exportable annotations with
         // unrecognized ontology classes.
			if(owlClass == null) {
				System.err.println("Concept " + annFeat.get(this.CLASSNAME).toString() + " does not exist.");
            continue;
			}				
							
			
			try {				
				idProp.addUnionDomainClass((RDFSClass)owlClass);
			}
			catch (Exception e) {
				throw new PopulatorException("Error: populateOntology " + e);				
			}		
			
			String instanceName = annFeat.get(INSTANCENAME).toString();
			Assert.assertNotNull(instanceName);
			
			final Integer id = (Integer) annFeat.get(this.REPRESENTATIONID);				
			
			instanceName = this.getOwlExporterOntology().stringValid(instanceName);
			instanceName = instanceName.replaceAll(" ", "_");
			
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Instance name: " + instanceName);
			
			try {
				final OWLIndividual individual = owlClass.createOWLIndividual(
				   (owlClass.getLocalName().toString().compareToIgnoreCase("Document")==0) ?
               instanceName : id + "_" + instanceName
            );
				
				individual.setPropertyValue(idProp, id);
				this.ontoAddDataypeProperty(id, owlClass, individual);				
				cOE.addIndividual(id, individual, oT);				 
			}
			catch(IllegalArgumentException iaEx) {
				throw new PopulatorException("The OwlExporter has already processed entites from corpus: " +
						this.getOwlExporterOntology().getOwlExporter().getDocumentFileName() +
						". Please trySelectWithRowMap another corpus to process or remove the existing output ontology.");
			}		 									
		}	
	}
    
    protected void ontoAddDataypeProperty(Integer id, OWLNamedClass owlClass, OWLIndividual individual) {
    	if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
    		System.out.println("OWLExporter Message: Running the Onto add datatype property");
		
		
		Annotation idAnn = (this.getOwlExporterOntology().getOwlExporter().getInputASName() == null) ? 
				this.getOwlExporterOntology().getOwlExporter().getDocument().getAnnotations().get(id) : 
					this.getOwlExporterOntology().getOwlExporter().getDocument().getAnnotations(
							this.getOwlExporterOntology().getOwlExporter().getInputASName()).get(id);	
				
				if(idAnn == null) return;
				
				
				
				Collection<OWLNamedClass> owlSuperClasses =  owlClass.getSuperclasses(false);
				Collection<OWLDatatypeProperty> owlClassProp;
				Collection<OWLDatatypeProperty> owlSuperClassProp;
				FeatureMap idAnnFeat = idAnn.getFeatures();				
				Set key = idAnnFeat.keySet();				
				
				owlClassProp = (Collection<OWLDatatypeProperty>) owlClass.getUnionDomainProperties(false);		
				
				Iterator<OWLNamedClass> owlSuperClass = owlSuperClasses.iterator();
				
				while(owlSuperClass.hasNext()) {
					//OWLNamedClass vs. OWLMaxCardintality "Crashed when running the organism pipeline"
					OWLClass namedClass = (OWLClass) owlSuperClass.next();
					
					owlSuperClassProp = (Collection<OWLDatatypeProperty>)					
							//namedClass.getUnionDomainProperties(true);
							namedClass.getUnionDomainProperties(false);
					owlClassProp.addAll(owlSuperClassProp);
				}
				
				
								
				Iterator<OWLDatatypeProperty> oIt = owlClassProp.iterator();
				
				try {
				while(oIt.hasNext()) {
						RDFProperty k = (RDFProperty)oIt.next();		
						
						if(key.contains(k.getBrowserText()))							
							individual.setPropertyValue(k, idAnnFeat.get(k.getBrowserText()).toString());
					}
				}
				catch(IllegalArgumentException iarEx) {
					System.out.println(iarEx.getMessage());
				}				
	}
    
    protected void addObjectProperty(JenaOWLModel owlModel, OntologyType oT) throws PopulatorException {
		if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
			System.out.println("OWLExporter Message: Running the Onto add property");
		
		AnnotationSet relationAs = this.getRelationAs(oT);
		
		try {
			if ((relationAs == null|| relationAs.isEmpty()))
				return;		
		}		
		catch(Exception e) {
			e.printStackTrace();
			throw new PopulatorException("Error: ontoAddProperty " + e);
		}	
		
		Iterator itRelations = relationAs.iterator();
		Annotation relationAnn;
		String relationName, relDomainStr, relRangeStr= "", currRelRangeStr="", owlDomainStr, owlRangeStr, annDomainStr, annRangeStr;
		Integer annDomainId, annRangeId;
		OWLObjectProperty relProperty;
		RDFSClass owlDomainClass = null, owlRangeClass = null;
		
		while(itRelations.hasNext()) {
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("******************************************************************");
			
			relationAnn = (Annotation)itRelations.next();
			relationName = (String)relationAnn.getFeatures().get(this.PROPERTYNAME);
			
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Relation name : " + relationName);
			
			if(relationName == null) {
				System.out.println("OWLExporter Warning: The property name in OwlExportRelation is null!");
				continue;
			}			
			
			relProperty = owlModel.getOWLObjectProperty(relationName);					
			
			if(relProperty == null) {
				System.out.println("OWLExporter Warning: The property name " + relationName + " in OwlExportRelation is incorrect!");
				continue;
			}
			
			Collection relDomainCol = relProperty.getUnionDomain(true);
			Collection relRangeCol = relProperty.getUnionRangeClasses();
			Iterator relDomainIt = relDomainCol.iterator();
			Iterator relRangeIt = relRangeCol.iterator();			
			
			while(relDomainIt.hasNext()) {
				relDomainStr = ((RDFSClass)relDomainIt.next()).getName();
				
				while(relRangeIt.hasNext()) {
					relRangeStr = ((RDFSClass)relRangeIt.next()).getName();
				}
				
				owlDomainClass = owlModel.getRDFSNamedClass(relDomainStr);
				owlRangeClass = owlModel.getRDFSNamedClass(relRangeStr);
											
				Collection domainInstances = owlDomainClass.getInstances(true);
				Collection rangeInstances = owlRangeClass.getInstances(true);
				
				annDomainId = (Integer)relationAnn.getFeatures().get(
						this.DOMAINID);
				annRangeId = (Integer)relationAnn.getFeatures().get(
						this.RANGEID);
				
				try {				
					annDomainStr = this.getTokenContentById(annDomainId, oT);
					annRangeStr =  this.getTokenContentById(annRangeId, oT);			
				}
				catch(Exception ex) {
					throw new PopulatorException("Error: addProperty " + ex);
				}			
				
				
				for(Iterator domainIt = domainInstances.iterator(); domainIt.hasNext() &&
					annDomainStr!=null && annRangeStr!=null;) {
						OWLIndividual domainInd = (OWLIndividual) domainIt.next();
						owlDomainStr = domainInd.getName();						
						//I don't like this document hack.
						if(owlDomainStr.contains(annDomainId.toString()) ||
								owlDomainStr.contains(this.getOwlExporterOntology().getOwlExporter().getDocument().getName())) {
							for(Iterator rangeIt = rangeInstances.iterator(); rangeIt.hasNext();) {
								OWLIndividual rangeInd = (OWLIndividual)rangeIt.next();
								
								owlRangeStr = rangeInd.getName();
								
								if(owlRangeStr.contains(annRangeId.toString())) {									
									if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
										System.out.println("OWLExporter Message: Exporting Relation " +
												domainInd.getLocalName() + " " + relProperty.getLocalName() + " " + rangeInd.getLocalName());
								
									domainInd.addPropertyValue(relProperty, rangeInd);
									
									if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
										System.out.println("Exporting " + domainInd.getLocalName() + " " +
												relProperty + " " + rangeInd.getLocalName());
								}
							}
						}
				}
			}
		}
	}
    
    protected void addCoreferences(CommonOwlExporter cOE) throws PopulatorException {
    	AnnotationSet inputAs, corefAs;    
    	Annotation corefSentAnn = null;
    	DocumentContent docContent = null;
    	Integer npId = null;
    	
    	try {
    		inputAs = this.getInputAs().get(this.OWLEXPORTCLASSDOMAIN);
    		corefAs = this.getInputAs();
    		docContent = this.getDocContent();
    	}
    	catch(PopulatorException aEx) {
    		return;
    	}    	
    			
		OWLDatatypeProperty corefStr, corefSent;
		
		if (this.getOwlExporterOntology().getOwlExporter().getDomainExists() ||
				this.getOwlExporterOntology().getOwlExporter().getNLPExists()) {
			corefStr = this.getOwlExporterOntology().getOwlModel().
					getOWLDatatypeProperty("corefStringWithId");
			corefSent = this.getOwlExporterOntology().getOwlModel().
					getOWLDatatypeProperty("corefSentenceWithId");			
		}
		else {			
			corefStr = this.getOwlExporterOntology().getOwlModel().
					createOWLDatatypeProperty("corefStringWithId");
		    corefStr.setRange(this.getOwlExporterOntology().getOwlModel().
		    		getXSDstring());
		    
		    corefSent =  this.getOwlExporterOntology().getOwlModel().
		    		createOWLDatatypeProperty("corefSentenceWithId");
		    corefSent.setRange(this.getOwlExporterOntology().getOwlModel().
		    		getXSDstring());
		}	    
	    
	    HashMap corefSentenceAnnID = new HashMap();
	    
	    corefSentenceAnnID = getCorefidTosentidHashmap(corefSentenceAnnID);
	    
				
		for(Iterator itTypes = inputAs.iterator(); itTypes.hasNext();) {			
			Annotation ann = (Annotation) itTypes.next();	
			
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Annotation ID: " + ann.getId());
			
			FeatureMap annFeat = ann.getFeatures();
			
			if(annFeat.get(this.CLASSNAME)==null) continue;
			
			OWLNamedClass owlClass = this.getOwlExporterOntology().getOwlModel().
					getOWLNamedClass(annFeat.get(this.CLASSNAME).toString());
			
			try {
				corefStr.addUnionDomainClass(owlClass);
				corefSent.addUnionDomainClass(owlClass);				
			}
			catch (Exception e) {
				throw new PopulatorException
				("Error: populateCoreferenceOntology " + e);
			}			
			
			npId = (Integer) annFeat.get(this.REPRESENTATIONID);
			
			OWLIndividual nPIndividual = cOE.getIndividual(npId, OntologyType.domain);
			
			Integer chainId = (Integer)annFeat.get(this.COREFCHAIN);
						
			if(chainId!=null) {
				if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
					System.out.println("OWLExporter Message: running chain process...");
				
				Annotation corefAnn = corefAs.get(chainId);
				FeatureMap corefFeat = corefAnn.getFeatures();
				ArrayList corefList = (ArrayList)corefFeat.get(
						this.CHAINID);
				
				if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
					System.out.println("OWLExporter Message: corefList: " + corefList);
							
				if(corefList.size() > 1) {					
					if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
						System.out.println("OWLExporter Message: corefList Size: " + corefList.size());					
					
					for(Iterator corefIt = corefList.iterator(); corefIt.hasNext(); ) {
						
						Integer corefId = (Integer)corefIt.next();
						
						if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
							System.out.println("OWLExporter Message: corefId: " + corefId);
						
						Annotation corefInstAnn = corefAs.get(corefId);		
						
						String corefInstStr = "";
						
						try {
							corefInstStr = 
								docContent.getContent(corefInstAnn.getStartNode().getOffset(),
										corefInstAnn.getEndNode().getOffset()).toString();
						}
						catch(InvalidOffsetException ioEx) {
							throw new PopulatorException("Invalid offset exeption! "  + ioEx);			
						}
						
						corefInstStr = this.getOwlExporterOntology().stringValid(corefInstStr);
						
						if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
							System.out.println("OWLExporter Message: corefInstStr: " + corefInstStr);
						
						corefSentAnn = corefAs.get((Integer)(corefSentenceAnnID.get(corefId)));
						
						if (corefSentAnn == null) {
							System.out.println("Warning: No sentence was found containing annotation with id : " + 
									corefId + " ('" + corefInstStr + "')" );							
							continue;
						}
						
						String corefSentStr = "";
						
						try {
							corefSentStr =  docContent.getContent(
									corefSentAnn.getStartNode().getOffset(),
									corefSentAnn.getEndNode().getOffset()).toString();
						}
						catch(InvalidOffsetException ioEx) {
							throw new PopulatorException("Error:" + ioEx);						
						}
						
						corefSentStr = corefSentStr.replaceAll("&#xA;", "");
						corefSentStr = this.getOwlExporterOntology().stringValid(corefSentStr);						
						
						nPIndividual.addPropertyValue(
								corefStr, corefId.toString() + "_" +
								this.getOwlExporterOntology().stringValid(corefInstStr));
						nPIndividual.addPropertyValue(
								corefSent,corefId.toString() + "_" +
								this.getOwlExporterOntology().stringValid(corefSentStr));
					}
				}
			}
		}		
    }
    
    protected void addCoreferenceProperty(JenaOWLModel owlModel, OntologyType oT) throws PopulatorException {
		if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag()) System.out.println("OWLExporter Message: Running the ontoAddPropterty...");

		AnnotationSet relationAs = this.getRelationAs(oT);
		
		try {
			if (relationAs == null || relationAs.isEmpty() && this.getOwlExporterOntology().getOwlExporter().getDebugFlag()) {
				System.out.println("No Coref Chains Available to Export.");
				return;
			}
				
		}		
		catch(Exception e) {
			e.printStackTrace();
			throw new PopulatorException("Error: addCoreferenceProperty " + e);
		}

		
		
		Iterator itRelations = relationAs.iterator();
		Annotation relationAnn;
		String relationName, relDomainStr, relRangeStr= "", currRelRangeStr="", owlDomainStr, owlRangeStr, annDomainStr, annRangeStr;
		Integer annDomainId, annRangeId;
		OWLObjectProperty relProperty;
		OWLNamedClass owlDomainClass, owlRangeClass;
		   
		while(itRelations.hasNext()) {
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("*******************************************************************************");
			
			relationAnn = (Annotation)itRelations.next();
			relationName = (String)relationAnn.getFeatures().get(
					this.PROPERTYNAME);
			   
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: relationName : " + relationName);
			
			if(relationName == null) {
				System.out.println("OWLExporter Warning: The property name in OwlExportRelation is null!");
				continue;
			}			
			
			relProperty = owlModel.getOWLObjectProperty(relationName);
			
			if(relProperty == null) {
				System.out.println("OWLExporter Warning: The property name " + relationName + " in OwlExportRelation is incorrect!");
				continue;
			}
			
			Collection relDomainCol = relProperty.getUnionDomain(false);
			Collection relRangeCol = relProperty.getUnionRangeClasses();
			Iterator relDomainIt = relDomainCol.iterator();
			Iterator relRangeIt = relRangeCol.iterator();
			
			while(relDomainIt.hasNext()) {
				relDomainStr= ((RDFSClass)relDomainIt.next()).getName();			
				
				while(relRangeIt.hasNext()) {
					relRangeStr = ((RDFSClass)relRangeIt.next()).getName();					
				}
				
				owlDomainClass = owlModel.getOWLNamedClass(relDomainStr);
				owlRangeClass = owlModel.getOWLNamedClass(relRangeStr);
				
				Collection domainInstances = owlDomainClass.getInstances(false);
				Collection rangeInstances = owlRangeClass.getInstances(false);
				
				annDomainId = (Integer)relationAnn.getFeatures().get(
						this.DOMAINID);
				annRangeId = (Integer)relationAnn.getFeatures().get(
						this.RANGEID);
				annDomainStr = this.getTokenContentById(annDomainId, this.getOntologyType());
				annRangeStr =  this.getTokenContentById(annRangeId, this.getOntologyType());
				
				
				for(Iterator domainIt = domainInstances.iterator();domainIt.hasNext() &&
				annDomainStr==null &&
				annRangeStr==null;) {
					OWLIndividual domainInd = (OWLIndividual) domainIt.next();
					owlDomainStr = domainInd.getName();
					
					Collection corefDom = domainInd.getPropertyValues(
							owlModel.getOWLDatatypeProperty("corefStringWithId"));
					
					for(Iterator corefDomIt = corefDom.iterator();corefDomIt.hasNext();) {
						String corefDomStr = (String)corefDomIt.next();
						
						if(corefDomStr.contains(annDomainId.toString())) {
							for(Iterator rangeIt = rangeInstances.iterator();rangeIt.hasNext();) {
								OWLIndividual rangeInd = (OWLIndividual)rangeIt.next();
								owlRangeStr = rangeInd.getName();
								Collection corefRan = rangeInd.getPropertyValues(
										owlModel.getOWLDatatypeProperty("corefStringWithId"));
								
								for(Iterator corefRanIt = corefRan.iterator();corefRanIt.hasNext();) {
									String corefRanStr = (String)corefRanIt.next();
									if(corefRanStr.contains(annRangeId.toString()))
										domainInd.addPropertyValue(relProperty, rangeInd);
								}
							}
						}
					}
				}
				
				for(Iterator domainIt = domainInstances.iterator();domainIt.hasNext() && 
				annDomainStr!=null&&annRangeStr==null;) {
					OWLIndividual domainInd = (OWLIndividual) domainIt.next();
					owlDomainStr = domainInd.getName();
					
					if(owlDomainStr.contains(annDomainId.toString())) {
						for(Iterator rangeIt = rangeInstances.iterator();rangeIt.hasNext();) {
							OWLIndividual rangeInd = (OWLIndividual)rangeIt.next();
							owlRangeStr = rangeInd.getName();
							
							Collection corefRan = rangeInd.getPropertyValues(
									owlModel.getOWLDatatypeProperty("corefStringWithId"));
							for(Iterator corefRanIt = corefRan.iterator();corefRanIt.hasNext();) {
								String corefRanStr = (String)corefRanIt.next();
								
								if(corefRanStr.contains(annRangeId.toString()))
									domainInd.addPropertyValue(relProperty, rangeInd);
							}
						}
					}
				}
				
				
				for(Iterator domainIt = domainInstances.iterator();domainIt.hasNext() &&
				annDomainStr==null &&
				annRangeStr!=null;) {
					OWLIndividual domainInd = (OWLIndividual) domainIt.next();
					owlDomainStr = domainInd.getName();
					
					Collection corefDom = domainInd.getPropertyValues(
							owlModel.getOWLDatatypeProperty("corefStringWithId"));
					
					for(Iterator corefDomIt = corefDom.iterator();corefDomIt.hasNext();) {
						String corefDomStr = (String)corefDomIt.next();
						
						if(corefDomStr.contains(annDomainId.toString())) {
							for(Iterator rangeIt = rangeInstances.iterator();rangeIt.hasNext();) {
								OWLIndividual rangeInd = (OWLIndividual)rangeIt.next();
								owlRangeStr = rangeInd.getName();
								if(owlRangeStr.contains(annRangeId.toString()))
									domainInd.addPropertyValue(relProperty, rangeInd);
							}
						}
					}
				}
			}
		}
    }  
    
	protected HashMap getCorefidTosentidHashmap(HashMap newMap) throws PopulatorException {		
	 	AnnotationSet inputAs;
	 	
    	try {
    		inputAs = this.getInputAs();
    	}
    	catch(PopulatorException aEx) {
    		return null;
    	}
    	
    	System.out.println("OWLExporter Message: Now producing the Hashmap (single)...");	
				
		if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
			System.out.println("OWLExporter Message: SENT_ANNOTATION_TYPE: " +
					this.SENTANNOTATION);
		
		AnnotationSet allSentAnn = inputAs.get(this.SENTANNOTATION);
		Annotation chainAnn, sentAnno;
		AnnotationSet containedAnnotationSetInSentence;
		ArrayList chainIDs;
		Integer sentID, chainElementID;
		
		this.allChainIDs = new HashSet();
		this.containedIDs = new HashSet();
		this.chainIDsInSent = new HashSet();
		this.returnSet = new HashSet();
		
		Iterator corefChainIt =
			this.getOwlExporterOntology().getOwlExporter().getCorefChainList().iterator();
		
		if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
			System.out.println("OWLExporter Message: corefChainList:" + this.getOwlExporterOntology().getOwlExporter().getCorefChainList());
		
		while(corefChainIt.hasNext()) {
			String chainStr = (String)corefChainIt.next();
			AnnotationSet allChainAnn = inputAs.get(chainStr);
			
			if(allChainAnn==null) {
				System.out.println("OWLExporter Message Warning: no chain elements for '" + chainStr + "'" ); 
				continue;
			}
			
			for(Iterator i = allChainAnn.iterator();i.hasNext();) {
				if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
					System.out.println("OWLExporter Message: First Iterator");
				chainAnn = (Annotation)i.next();
				chainIDs = (ArrayList)chainAnn.getFeatures().get(
						this.CHAINID);
				if(chainIDs==null) System.out.println("OWLExporter Warning: Chain ID is null");
				this.allChainIDs.addAll(chainIDs);
			}
		}
		
		if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
			System.out.println("OWLExporter Message: All Chain IDs :" + this.allChainIDs);
		
		for(Iterator i = allSentAnn.iterator(); i.hasNext();) {
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Second Iterator");
			sentAnno = (Annotation)i.next();
			sentID = sentAnno.getId();
			containedAnnotationSetInSentence = inputAs.getContained(sentAnno.getStartNode().getOffset(), sentAnno.getEndNode().getOffset());
			this.containedIDs.clear();
			this.containedIDs.addAll(this.getIDsFromAnnotationSet(containedAnnotationSetInSentence));
			
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Contained IDs: " + this.containedIDs);
			this.chainIDsInSent.clear();
			this.chainIDsInSent.addAll(this.allChainIDs);
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Chain IDs in sentence before retain all: " + this.chainIDsInSent);
			this.chainIDsInSent.retainAll(this.containedIDs);
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Chain IDs in sent after retain all: " + this.chainIDsInSent);
			
			for(Iterator j = this.chainIDsInSent.iterator();j.hasNext();) {
				chainElementID = (Integer)j.next();
				if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
					System.out.println("OWLExporter Message: Chain element ID: " + chainElementID +" sentID: " + sentID);
				newMap.put(chainElementID, sentID);
			}
		}
		
		System.out.println("Hashmap single ready.");
		return newMap;		
    }
	
	protected Set getIDsFromAnnotationSet(AnnotationSet as) throws PopulatorException {
		this.returnSet.clear();
		
		for(Iterator i = as.iterator();i.hasNext();)
			this.returnSet.add(((Annotation)i.next()).getId());
		return this.returnSet;
	}
	
	protected void addDomainNLPProperty(JenaOWLModel domainOwlModel, OntologyType dOT,
			JenaOWLModel nlpOwlModel, OntologyType nOT,
			OntologyType oT) throws PopulatorException {
		if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
			System.out.println("OWLExporter Message: Running the Onto add property");
		
		AnnotationSet relationAs = this.getRelationAs(oT);
		
		try {
			if ((relationAs == null|| relationAs.isEmpty()))
				return;		
		}		
		catch(Exception e) {
			e.printStackTrace();
			throw new PopulatorException("Error: ontoAddProperty " + e);
		}	
		
		Iterator itRelations = relationAs.iterator();
		Annotation relationAnn;
		String relationName, relDomainStr, relRangeStr= "", currRelRangeStr="", owlDomainStr, owlRangeStr, annDomainStr, annRangeStr;
		Integer annDomainId, annRangeId;
		OWLObjectProperty relProperty;
		RDFSClass owlDomainClass = null, owlRangeClass = null;
		
		while(itRelations.hasNext()) {
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("******************************************************************");
			
			relationAnn = (Annotation)itRelations.next();
			relationName = (String)relationAnn.getFeatures().get(this.PROPERTYNAME);
			
			if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
				System.out.println("OWLExporter Message: Relation name : " + relationName);
			
			if(relationName == null) {
				System.out.println("OWLExporter Warning: The property name in OwlExportRelation is null!");
				continue;
			}			
			
			relProperty = domainOwlModel.getOWLObjectProperty(relationName);					
			
			if(relProperty == null) {
				System.out.println("OWLExporter Warning: The property name " + relationName + " in OwlExportRelation is incorrect!");
				continue;
			}
			
			Collection relDomainCol = relProperty.getUnionDomain(true);
			Collection relRangeCol = relProperty.getUnionRangeClasses();
			Iterator relDomainIt = relDomainCol.iterator();
			Iterator relRangeIt = relRangeCol.iterator();			
			
			while(relDomainIt.hasNext()) {
				relDomainStr = ((RDFSClass)relDomainIt.next()).getName();
				
				while(relRangeIt.hasNext()) {
					relRangeStr = ((RDFSClass)relRangeIt.next()).getName();					
				}
				
				owlDomainClass = domainOwlModel.getRDFSNamedClass(relDomainStr);
				owlRangeClass = nlpOwlModel.getRDFSNamedClass(relRangeStr);
											
				Collection domainInstances = owlDomainClass.getInstances(true);
				Collection rangeInstances = owlRangeClass.getInstances(true);
				
								
				annDomainId = (Integer)relationAnn.getFeatures().get(
						this.DOMAINID);
				annRangeId = (Integer)relationAnn.getFeatures().get(
						this.RANGEID);
				
				try {				
					annDomainStr = this.getTokenContentById(annDomainId, dOT);
					annRangeStr =  this.getTokenContentById(annRangeId, nOT);			
				}
				catch(Exception ex) {
					throw new PopulatorException("Error: addProperty " + ex);
				}
				
				//System.out.println("Domain: " + owlDomainClass.getName() + " " + domainInstances.size() + " " + annDomainStr);
				//System.out.println("Range: " + owlRangeClass.getName() + " " + rangeInstances.size() + " " + annRangeStr);
					
				
				for(Iterator domainIt = domainInstances.iterator(); domainIt.hasNext() &&
					annDomainStr!=null && annRangeStr!=null;) {
						OWLIndividual domainInd = (OWLIndividual) domainIt.next();
						owlDomainStr = domainInd.getName();					
						
						if(owlDomainStr.contains(annDomainId.toString())) {
							//System.out.println(annDomainId.toString() + " " + domainInd.getName());
							
							for(Iterator rangeIt = rangeInstances.iterator(); rangeIt.hasNext();) {
								OWLIndividual rangeInd = (OWLIndividual)rangeIt.next();
								owlRangeStr = rangeInd.getName();															
								
								if(owlRangeStr.contains(annRangeId.toString())) {
									//System.out.println(annRangeId.toString() + " " + rangeInd.getName());
									
									if(this.getOwlExporterOntology().getOwlExporter().getDebugFlag())
										System.out.println("OWLExporter Message: Exporting Relation " +
												domainInd.getLocalName() + " " + relProperty.getLocalName() + " " + rangeInd.getLocalName());								
														
										
										domainInd.addPropertyValue(relProperty, rangeInd);									
								}
							}
						}		
				}				
			}		
		}
	}
	
	protected String getTokenContentById(Integer id, OntologyType oT) throws PopulatorException {
    	if(this.owlExporterOntology.getOwlExporter().getDebugFlag())
    		System.out.println("Running getTokenContentByID..., id from the owlexportrelation : " + id);
    	
    	AnnotationSet classAs = null;
    	
    	if(oT.getOntologyType() == OntologyType.domain.getOntologyType())
    		classAs = this.getInputAs().get(this.OWLEXPORTCLASSDOMAIN);
    	else
    		classAs = this.getInputAs().get(this.OWLEXPORTCLASSNLP);
    		
    	Iterator classAsIt = classAs.iterator();   	
    	
    	while(classAsIt.hasNext()) {
    		Annotation classAnn = (Annotation)classAsIt.next();
    		Integer reprId = (Integer)(classAnn.getFeatures().get("representationId"));
    		try {    			
    			if(reprId == id)
    				/*System.out.println(oT.getOntologyType() + " " + reprId + " " + id + " " +
    						this.owlExporterOntology.getMyUtils().getTokenContentInAnnotation(
    	    						this.owlExporterOntology.getOwlExporter().getDocument() , classAnn));*/
    			
    				return this.getTokenContentInAnnotation(
    						this.owlExporterOntology.getOwlExporter().getDocument() , classAnn);    			
    		}
    		catch(Exception e) {
    			throw new PopulatorException("Error : getTokenContentById " + e);
    		}
    		
    	}
    	
    	return null;
    }
    
    protected OWLObjectProperty createObjectProperty(OwlExporterOntology oEO, 
    		OWLNamedClass domain, OWLNamedClass range, String value) throws PopulatorException {
    	OWLObjectProperty objectType;	
						
		if(this.getOwlExporterOntology().getOwlExporter().getDomainExists() ||
				this.getOwlExporterOntology().getOwlExporter().getNLPExists()) {			
			objectType = oEO.getOwlModel().getOWLObjectProperty(value);
		}
		else {			
		    
			objectType = oEO.getOwlModel().createOWLObjectProperty(value);
			objectType.setDomain(domain);
			objectType.setRange(range);  
		}
		
		return objectType;
    }   
    
    protected OWLObjectProperty createObjectProperty(OwlExporterOntology oEO, 
    		OWLNamedClass domain, String value) throws PopulatorException {
    	OWLObjectProperty objectType;	
						
    	if(this.getOwlExporterOntology().getOwlExporter().getDomainExists() ||
				this.getOwlExporterOntology().getOwlExporter().getNLPExists()) {			
			objectType = oEO.getOwlModel().getOWLObjectProperty(value);
		}
		else {			
		    
			objectType = oEO.getOwlModel().createOWLObjectProperty(value);
		    objectType.setDomain(domain);		      
		}
		
		return objectType;
    }
    
    protected OWLDatatypeProperty createDataTypeProperty(OwlExporterOntology oEO,
    		String value, RDFSDatatype range) throws PopulatorException {
    	OWLDatatypeProperty dataType;	
						
    	if(this.getOwlExporterOntology().getOwlExporter().getDomainExists() ||
				this.getOwlExporterOntology().getOwlExporter().getNLPExists()) {			
			dataType = oEO.getOwlModel().getOWLDatatypeProperty(value);
		}
		else {			
		    
			dataType = oEO.getOwlModel().createOWLDatatypeProperty(value);		    
		    dataType.setRange(range);  
		}
		
		return dataType;
    }
    
    /**
     * Get the concatencated String from the included token annotations.
     * Because We might do some post processings with the tokens, such as the number combination,
     * sometimes the concatenated string of those tokens might be different with the orginal
     * content that is directly extracted from the document body.
     *
     * @param doc The gate.Document instance that comes with the DEFAULT annotation set
     * @param anno The gate.Annotation instance
     * @return The String of the concatenated tokens within the annoation span
     * @throws Exception When there is no token within the given annotation span or something else.
     */
    public static String getTokenContentInAnnotation( Document doc , Annotation anno ) throws Exception {
  	
      return  getTokenContentInAnnotation( doc.getAnnotations() , anno );
  	
    }
    
    /**
     * Get the concatencated String from the token annotations covered
     * by the specified annotation.
     * Because We might do some post processings with the tokens, such as the number combination,
     * sometimes the concatenated string of those tokens might be different with the orginal
     * content that is directly extracted from the document body.
     *
     * @param inputAS The given input AnnotationSet
     * @param anno The gate.Annotation instance
     * @return The String of the concatenated tokens within the annoation span
     * @throws Exception When there is no token within the given annotation span or something else.
     */
    public static String getTokenContentInAnnotation( AnnotationSet inputAS , Annotation anno )
        throws Exception{
        return getTokenContentInSpan( inputAS , 
  				    anno.getStartNode().getOffset(),
  				    anno.getEndNode().getOffset());
    }
    
    /**
     * Get the concatencated String from the tokens covered by 
     * the specified span.
     * Because We might do some post processings with the tokens, such as the number combination,
     * sometimes the concatenated string of those tokens might be different with the orginal
     * content that is directly extracted from the document body.
     *
     * @param inputAS The given input AnnotationSet
     * @param spanStartOffset The starting offset of the span
     * @param spanEndOffset The endding offset of the span
     * @return The String of the concatenated tokens within the annoation span
     * @throws Exception When there is no token within the given annotation span or something else.
     */
    public static String getTokenContentInSpan( AnnotationSet inputAS , 
  					      Long spanStartOffset, Long spanEndOffset)
        throws Exception {

      if( spanStartOffset.compareTo( spanEndOffset ) >= 0 )
        return "" ;

      List<Annotation> listTokenAndSpace = getTokenSpaceAnnoListInSpan( inputAS,spanStartOffset,spanEndOffset,false);
      
      String content = getTokenContentFromTokenSpaceList( listTokenAndSpace , true );

      return content ;
    }
    
    /** returns a sorted list **/
    public static List<Annotation> getTokenSpaceAnnoListInSpan( AnnotationSet inputAS , 
  						  Long spanStartOffset, Long spanEndOffset, boolean sort ){
      if( spanStartOffset.compareTo( spanEndOffset ) >= 0 )
        return null ;
      final HashSet<String> set = new HashSet<String>() ;
      set.add( gate.creole.ANNIEConstants.TOKEN_ANNOTATION_TYPE );
      set.add( gate.creole.ANNIEConstants.SPACE_TOKEN_ANNOTATION_TYPE );

      AnnotationSet tokens = inputAS.get( set  );
      if( tokens == null || tokens.isEmpty() ){
  	//throw new Exception( NO_TOKEN_MSG  );
        return new ArrayList<Annotation>() ;
      }

      tokens = tokens.get( spanStartOffset,
  			 spanEndOffset );
      if( tokens == null && tokens.size() == 0 ){
        //throw new Exception( NO_TOKEN_WITHIN_MSG );
        return new ArrayList<Annotation>() ;
      }
  	
      List<Annotation> list = new ArrayList<Annotation>( tokens );

      if( sort ) sortAnnotations(list);
      return list ;
    }
    
    public static String getTokenContentFromTokenSpaceList( List<Annotation> listTokenAndSpace , boolean useDocSpace)
    throws Exception {
  
  if( listTokenAndSpace == null )
    return "";

  sortAnnotations( listTokenAndSpace );

  Iterator iter = listTokenAndSpace.iterator() ;
	
  String content = "";
	
  final String space = " ";

  Annotation tmp = null ;
  FeatureMap features = null ;
  String string = null ;
  String annoType = null ;

  //find the first token, omit all the preceding spacetokens
  do{
    if( iter.hasNext() == false ){
	throw new Exception(NO_TOKEN_MSG );
    }

    tmp = (Annotation) iter.next() ;
    annoType = tmp.getType() ;

    // continue while the obtained annotation is spacetoken
  }while( annoType.equals( ANNIEConstants.SPACE_TOKEN_ANNOTATION_TYPE ) == true );

  // now we get the first token
  features = tmp.getFeatures() ;
  string = (String) features.get( ANNIEConstants.TOKEN_STRING_FEATURE_NAME );
  content = content + string ;

  int prevAnnoType = 0 ;

  while( iter.hasNext() ){
    tmp = (Annotation) iter.next() ;
    annoType = tmp.getType() ;

    // if we encounter one space token, add one space to the complete content
    if( annoType.equals( ANNIEConstants.SPACE_TOKEN_ANNOTATION_TYPE ) == true ){
	  if( prevAnnoType == 0 ){
	      content = content + space ;
	      prevAnnoType = 1 ;
	  }
    }else{
	// add the string of the token to the complete content
	features = tmp.getFeatures() ;
	string = (String) features.get( ANNIEConstants.TOKEN_STRING_FEATURE_NAME );

	if( useDocSpace ){
	  prevAnnoType = 0 ;
	  content = content + string ;
	}else{
	  prevAnnoType = 0 ;
	  content = content + space + string ;
	}
    }
  }

  // return the complete string
  return content ;
}
    
    /** 
     * Sort the given AnnotationSet that contains some annotations of the same type, using the gate.util.OffsetComparator
     *
     * @param annoList The AnnotationSet object that to be sorted.
     * @return One java.util.List object that contains that sorted annotation(s).
     */
    public static void sortAnnotations( List<Annotation> annoList ){
  	
      // sort all tokens by start offset
      Collections.sort( annoList , new gate.util.OffsetComparator() );
  	
    }

       
    protected abstract OntologyType getOntologyType();
	protected abstract String getPrefix();
	protected abstract String getFileEnding();				
}
