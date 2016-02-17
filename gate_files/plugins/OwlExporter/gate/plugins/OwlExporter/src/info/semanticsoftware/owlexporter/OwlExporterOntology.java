/*
OwlExporter --  http://www.semanticsoftware.info/owlexporter

This file is part of the OwlExporter architecture.

Copyright (C) 2009, 2010, 2011, 2012, 2014 Semantic Software Lab, http://www.semanticsoftware.info
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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import edu.stanford.smi.protege.util.URIUtilities;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protege.exception.OntologyLoadException;
import info.semanticsoftware.owlexporter.exception.ApplicationException;
import gate.util.Files;


public class OwlExporterOntology{	
	protected final String EXPORT_FORMAT = "OWL";
    protected final String EXPORT_EXT = "owl";    
    protected final String LANGXMLABBREV = "RDF/XML-ABBREV";    
    	
    private OwlExporter owlExporter;	
	private JenaOWLModel owlModel;	
	private String exportFilePathStr;
	private String namespace;
	private ImportHelper importHelper;
	
	
	public OwlExporterOntology(OwlExporter owlExporter) throws ApplicationException {
		setOwlExporter(owlExporter);		
	}
	
	protected void setOwlExporter(OwlExporter owlExporter) {
		this.owlExporter = owlExporter;		
	}
	
	protected OwlExporter getOwlExporter() {
		return this.owlExporter;
	}
	
	protected void setOwlModel(JenaOWLModel owlModel) {
		this.owlModel = owlModel;		
	}
	
	protected JenaOWLModel getOwlModel() {
		return this.owlModel;
	}
	
	protected void setExportFilePathStr(String exportFilePathStr) {
		this.exportFilePathStr = exportFilePathStr;		
	}
	
	protected String getExportFilePathStr() {
		return this.exportFilePathStr;
	}
	
	protected void setImportHelper(ImportHelper importHelper) {
		this.importHelper = importHelper; 				
	}
	
	protected ImportHelper getImportHelper() {
		return this.importHelper;
	}    
    
    protected void setOntologyNamespace(String namespace) {    	
		this.namespace = namespace;
	}
	
	protected String getOntologyNamespace() {
		return this.namespace;
	}   
	
	protected String stringValid(String input) {
	       input = input.replaceAll( "\t|\n|\r|\f|\u0085|\u2028|\u2029", " " );
	       input = input.replaceAll("&","&amp;");
	       input = input.replaceAll("<","&lt;");
	       input = input.replaceAll(">","&gt;");
	       input = input.replaceAll("'","&apos;");
	       input = input.replaceAll("\"","&quot;");
	       input = input.replaceAll("[$]|[%]","_");
	       input = input.replaceAll(":","_");	       
	       input = input.replaceAll("[<]|[>]", "");
	       return input;
	}
	
	
	protected void createOntology(String fileEnding, OntologyType oT) throws ApplicationException{
		JenaOWLModel owlModel = null;	
		try {
		    if( ((oT.getOntologyType() == OntologyType.domain.getOntologyType()) && this.getOwlExporter().getDomainExists())
			|| ((oT.getOntologyType() == OntologyType.nlp.getOntologyType()) && this.getOwlExporter().getNLPExists()))
		    {
			// load existing domain ontology from file	
			InputStream exportFIS = null;
			try {
			    if(oT.getOntologyType() == OntologyType.domain.getOntologyType()) {
				URL exportURL = new URL("file:" + this.getFormattedFilePath(this.getOwlExporter().getDomainExportFilepathStr(), fileEnding + "."));
				exportFIS = new FileInputStream(Files.fileFromURL(exportURL));
				this.setOwlModel(ProtegeOWL.createJenaOWLModelFromInputStream(exportFIS));
			    } else {
				URL exportURL = new URL("file:" +this.getOwlExporter().getNLPExportFilepathStr());
				exportFIS = new FileInputStream(Files.fileFromURL(exportURL));
				this.setOwlModel(ProtegeOWL.createJenaOWLModelFromInputStream(exportFIS));
			    }
			} 
			catch(Exception e) {
			    e.printStackTrace();
			    throw new ApplicationException("Error: createOntology [loading] " + e);
			}
			finally {
			    if (exportFIS != null) exportFIS.close();
			}
		    }
		    else {
			String uri = "";
			
			if(oT.getOntologyType() == OntologyType.domain.getOntologyType())
			    uri = this.getOwlExporter().getImportDomainOntology().toString();
			else
			    uri = this.getOwlExporter().getImportNLPOntology().toString();
			
			owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri);
			this.setOwlModel(owlModel);
		    }
		
		if (this.getOwlExporter().getMultiOwlExport())
		    setImportHelper(new ImportHelper(owlModel));
		}
		catch(Exception e) {
		    e.printStackTrace();
		    throw new ApplicationException("Error: createOntology " + e);
		}
	}
	
	
	protected void saveOntology(String fileEnding, OntologyType oT) throws ApplicationException {
	    Collection errors = new ArrayList();
	    OutputStream owlModelFOS = null;
	    try {			
		if(oT.getOntologyType() == OntologyType.domain.getOntologyType()) {
		    this.setExportFilePathStr(this.getFormattedFilePath(this.getOwlExporter().getDomainExportFilepathStr(), fileEnding + "."));
		}
		else {
		    this.setExportFilePathStr(this.getOwlExporter().getNLPExportFilepathStr());			  
		}
		URL owlModelURL = new URL("file:" + this.getExportFilePathStr());
 		owlModelFOS = new FileOutputStream(Files.fileFromURL(owlModelURL));
		this.getOwlModel().save(owlModelFOS, this.LANGXMLABBREV, errors);
		if(oT.getOntologyType() == OntologyType.domain.getOntologyType())
		    this.addSameAs(this.getExportFilePathStr());		
	    }
	    catch(Exception e) {
		throw new ApplicationException("Error: saveOntology " + e);
	    }
	    finally {
		try {
		    if (owlModelFOS != null) owlModelFOS.close();
		} catch (Exception fileEx) {
		    throw new ApplicationException("Close Error : saveOntologoy " + fileEx);
		}
	    }
	}
	
	protected String getFormattedFilePath(String filePath, String replacement) {
		return filePath.replaceAll("\\.", replacement);
	}
	
	protected void addSameAs(String filePath) throws ApplicationException {		
		JenaOWLModel owlModel;
		InputStream owlModelFIS = null;
		try {
		    try { 
			URL owlModelURL = new URL("file:" + filePath );
			owlModelFIS = new FileInputStream( Files.fileFromURL(owlModelURL));
			owlModel = ProtegeOWL.createJenaOWLModelFromInputStream(owlModelFIS);
		    }
		    catch(OntologyLoadException olEx) {
			throw new ApplicationException("Error : addSameAsFunction " + olEx);
		    }
		    finally{
			if (owlModelFIS != null) owlModelFIS.close();
		    }
		}
		catch(Exception fileEx) {
		    throw new ApplicationException("File Error : addSameAsFunction " + fileEx);
		}

		if(owlModel == null) return;
		 
		Collection classes = owlModel.getUserDefinedOWLNamedClasses();
		 
		for(Iterator it = classes.iterator(); it.hasNext();) {
		    RDFSClass cls = (RDFSClass) it.next();
		    
		    if(cls.getBrowserText().contains("Document") || cls.getBrowserText().contains("Sentence")) continue;
			 
		    Collection instances = cls.getInstances(false);
			 
		    for(Iterator jt = instances.iterator(); jt.hasNext();) {
			OWLIndividual individual = (OWLIndividual) jt.next();
			Collection sameIndCol = individual.getSameAs();
			
			String indStr = individual.getBrowserText();
			Collection instances2 = cls.getInstances(false);
			
			for(Iterator kt = instances2.iterator(); kt.hasNext();) {
			    OWLIndividual individual2 =  (OWLIndividual) kt.next();
			    if(sameIndCol.contains(individual2)) continue;
			    
			    String indStr2 = individual2.getBrowserText();
			    if(indStr.equals(indStr2)) continue;
			    
			    String expression = "[\\d]+[\\_]";
			    Pattern p = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);   
			    
			    String tempIndStr = indStr.replaceFirst(p.pattern(), "");
			    String tempIndStr2 = indStr2.replaceFirst(p.pattern(), "");
			    
			    if(tempIndStr.compareToIgnoreCase(tempIndStr2)==0) {
				individual.addSameAs(individual2);
			    }
			}
		    }
		}
		
		Collection errors = new ArrayList();
		if(this.getOwlExporter().getDebugFlag())
		    System.out.println("OWL Exporter Message: saving the second time in order to add the sameAs function.");
		OutputStream owlModelFOS = null;
		try {
		    URL owlModelURL = new URL("file:" + filePath );
		    owlModelFOS = new FileOutputStream(Files.fileFromURL(owlModelURL));
		    owlModel.save( owlModelFOS, LANGXMLABBREV, errors);
		}
		catch (Exception fileEx) {
		    throw new ApplicationException("Save Error : addSameAsFunction " + fileEx);
		}
		finally {
		    try {
			if (owlModelFOS != null) owlModelFOS.close();
		    } catch (Exception fileEx) {
			throw new ApplicationException("Close Error : addSameAsFunction " + fileEx);
		    }
		}
	}
	
	protected void addNamespace(String prefix) throws ApplicationException {
		try {
			String namespace = this.getOwlModel().
					getNamespaceManager().getDefaultNamespace();			
			String newBase =  namespace.substring(0,namespace.length()-1) + "/" + prefix;						
			OWLUtil.renameOntology(this.getOwlModel(),
					this.getOwlModel().getDefaultOWLOntology(), newBase);
			this.getOwlModel().
					getTripleStoreModel().getTopTripleStore().setOriginalXMLBase(newBase);								
		}
		catch(Exception e) {			
			throw new ApplicationException("Error : addNamespace " + e); 
		}		
	}
	
	
	//Find something better
	public void importOntology(OwlExporterOntology ontology, String prefix) throws ApplicationException {
		try {			
			//if you want to use a custom prefix for the namespace of the imported ontology, uncomment the following line
			//owlExporterOntologyPopulator.getOwlExporterOntology().getOwlModel().getNamespaceManager().setDefaultNamespace(this.getOwlModel().getNamespaceManager().getDefaultNamespace().replace("#","") + "/" + prefix + "#");
			//owlExporterOntologyPopulator.getOwlExporterOntology().getOwlModel().getNamespaceManager().setPrefix(new URI(this.getOwlModel().getNamespaceManager().getDefaultNamespace().replace("#","") + "/" + prefix + "#"), prefix);
			
			URI importUri = URIUtilities.createURI(ontology.getExportFilePathStr());			
			if(importUri!=null) {
				this.getImportHelper().addImport(importUri);
				this.getImportHelper().importOntologies();								
			}
		}
		catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
			throw new ApplicationException("Error: importOntology " + e);			
		}
	}	
}
