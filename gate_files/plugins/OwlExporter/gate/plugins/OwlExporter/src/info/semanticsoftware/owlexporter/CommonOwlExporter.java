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

import java.util.HashMap;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.model.OWLIndividual;

public class CommonOwlExporter {
	private final HashMap<String, OWLIndividual> docIndividuals = new HashMap<String, OWLIndividual>();
	private final HashMap<Integer, OWLIndividual> domainIndividuals = new HashMap<Integer, OWLIndividual>();
	private final HashMap<Integer, OWLIndividual> nlpIndividuals = new HashMap<Integer, OWLIndividual>();
	
	public OWLIndividual getDocIndividual(String key) {
		for(Iterator itTypes = docIndividuals.values().iterator(); itTypes.hasNext();) {
			System.out.println(itTypes.next());				
		}
		return this.docIndividuals.get(key);		
	}
	
	public void addDocIndividual(String key, OWLIndividual docIndividual) {
		this.docIndividuals.put(key, docIndividual);		
	}
	
	public OWLIndividual getIndividual(int key, OntologyType oT) {		
		if(oT.getOntologyType() == OntologyType.domain.getOntologyType())
			return this.domainIndividuals.get(key);
		else
			return this.nlpIndividuals.get(key);
	}
	
	public void addIndividual(int key, OWLIndividual npIndividual, OntologyType oT) {
		if(oT.getOntologyType() == OntologyType.domain.getOntologyType())
			this.domainIndividuals.put(key, npIndividual);
		else
			this.nlpIndividuals.put(key, npIndividual);		
	}

        /**
        * A sequence diagram can accept all classifiers. It will add them as a new 
        * Classifier Role with that classifier as a base. All other accepted figs 
        * are added as is.
        * @param objectToAccept
        * @return true if the diagram can accept the object, else false
        * @see org.argouml.uml.diagram.ui.UMLDiagram#doesAccept(java.lang.Object)
        */
        public boolean doesAccept(Object objectToAccept) {
 		return false;
	}

 	public boolean doesAccept1(Object objectToAccept) {
		return false;
       }
}
