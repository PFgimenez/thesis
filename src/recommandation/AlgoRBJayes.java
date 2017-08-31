package recommandation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.io.xmlbif.XMLBIFReader;

import compilateurHistorique.DatasetInfo;

import java.util.Map;
import java.util.Random;

/*   (C) Copyright 2015, Gimenez Pierre-François
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Algorithme de recommandation avec les réseaux bayésiens qui utilise Jayes
 * @author Pierre-François Gimenez
 *
 */

public class AlgoRBJayes implements AlgoRecoRB
{
	private BayesNet rb;
	private JunctionTreeAlgorithm inferer;
	private Map<BayesNode,String> evidence = new HashMap<BayesNode,String>();
	
	public AlgoRBJayes()
	{
		inferer = new JunctionTreeAlgorithm();
	}
		
	public void describe()
	{
		System.out.println("Bayesian Network with Jayes");
	}

	
	@Override
	public String recommande(String variable, ArrayList<String> possibles)
	{
		double[] beliefsC;
		inferer.setEvidence(evidence);
		beliefsC = inferer.getBeliefs(rb.getNode(variable));

		int best=0;
		double bestproba=-1;
		
		for(int i = 0; i < beliefsC.length; i++)
		{
//			System.out.println(rb.getNode(variable).getOutcomeName(i)+": "+beliefsC[i]);
			if(beliefsC[i] > bestproba && (possibles == null || possibles.contains(rb.getNode(variable).getOutcomeName(i)))){
				bestproba = beliefsC[i];
				best = i;
			}
		}
		return rb.getNode(variable).getOutcomeName(best);
	}

	@Override
	public void setSolution(String variable, String solution)
	{
		try
		{
			// Si cette valeur n'a jamais été observée dans l'ensemble de tests, on ne peut pas la traiter
			rb.getNode(variable).getOutcomeIndex(solution);
			evidence.put(rb.getNode(variable), solution);
		}
		catch(IllegalArgumentException e)
		{}
	}

	@Override
	public void oublieSession()
	{
		evidence.clear();
	}

/*	@Override
	public void apprendContraintes(SALADD contraintes)
	{}*/

	@Override
	public void apprendDonnees(DatasetInfo datasetinfo, ArrayList<String> filename, int nbIter, boolean entete) 
	{}

	public ArrayList<String> getVariables()
	{
		ArrayList<String> out = new ArrayList<String>();
		List<BayesNode> var = rb.getNodes();
		for(BayesNode b : var)
			out.add(b.getName());
		return out;
	}
	
	@Override
	public void termine()
	{}

	@Override
	public void terminePli()
	{}

	public String recommandeGeneration(String variable, ArrayList<String> possibles)
	{
		double[] beliefsC;
		inferer.setEvidence(evidence);
		beliefsC = inferer.getBeliefs(rb.getNode(variable));

		double choix = (new Random()).nextDouble();
		double total = 0;
		double normalisation = 0;
		
		for(int i = 0; i < beliefsC.length; i++)
			if(possibles.contains(rb.getNode(variable).getOutcomeName(i)))
				normalisation += beliefsC[i];
				
		// Si aucun cas n'est rencontré, on renvoie une valeur au hasard (uniformément tirée)
		if(normalisation == 0)
			return possibles.get((new Random()).nextInt(possibles.size()));

		choix = choix * normalisation;

		for(int i = 0; i < beliefsC.length; i++)
			if(possibles.contains(rb.getNode(variable).getOutcomeName(i)))
			{
				total += beliefsC[i];
				if(choix <= total)
					return rb.getNode(variable).getOutcomeName(i);
			}
		System.out.println("Erreur! "+choix+" "+total);
		return null;
	}
	
	public String toString()
	{
		return getClass().getSimpleName();
	}

	public void initHistorique(ArrayList<String> filename, boolean entete)
	{}
	
	@Override
	public void unassign(String variable)
	{
		evidence.remove(rb.getNode(variable));
	}

	@Override
	public void apprendRB(String file)
	{
		File fichier = new File(file);
		InputStream input;
		try {
			input = new FileInputStream(fichier);
			XMLBIFReader reader = new XMLBIFReader(input);			
			rb = reader.read();
			inferer.setNetwork(rb);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
