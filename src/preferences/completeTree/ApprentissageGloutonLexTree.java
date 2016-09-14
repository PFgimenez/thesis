package preferences.completeTree;

import java.util.ArrayList;

import compilateurHistorique.Instanciation;
import preferences.heuristiques.HeuristiqueComplexe;

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
 * Apprentissage d'un arbre lexicographique incomplet
 * @author pgimenez
 *
 */

public class ApprentissageGloutonLexTree extends ApprentissageGloutonLexStructure
{
	private int profondeurMax;
	private int seuil;

	public ApprentissageGloutonLexTree(int profondeurMax, int seuil, HeuristiqueComplexe h)
	{
		this.h = h;
		this.profondeurMax = profondeurMax;
		this.seuil = seuil;
	}
	
	private LexicographicStructure apprendRecursif(Instanciation instance, ArrayList<String> variablesRestantes, boolean preferred)
	{
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variablesRestantes);
	
		String var = h.getRacine(historique, variablesTmp, instance);
		
		int pasAssez = 0;
		
		if(variablesTmp.size() < variables.size() - profondeurMax)
			return apprendOrdre(instance, variablesTmp);
		
		/**
		 * Peut-on avoir un split sans risquer de finir avec trop peu d'exemples dans une branche ?
		 */
		for(String val : historique.getValues(var))
		{
			instance.conditionne(var, val);
			int nbInstances = historique.getNbInstances(instance);
			instance.deconditionne(var);
			
			if(nbInstances < seuil) // split impossible !
				pasAssez++;
		}
		
		// Plus assez d'exemples dans aucune branche : ordre lexico
		if(pasAssez == historique.getValues(var).size())// || (pasAssez > 0 && !preferred))
			return apprendOrdre(instance, variablesTmp);

/*		if(pasAssez > 0)
			System.out.println("Pas de split "+pasAssez);
		else
			System.out.println("Split");*/
		
		/**
		 * Split
		 */
		LexicographicTree best = new LexicographicTree(var, historique.nbModalites(var), pasAssez == 0);
		best.setOrdrePref(historique.getNbInstancesToutesModalitees(var, null, true, instance));

		// Si c'était la dernière variable, alors c'est une feuille
		if(variablesTmp.size() == 1)
			return best;
		
		variablesTmp.remove(best.getVar());
		int nbMod = best.getNbMod();
		
		if(pasAssez == 0)
		{
			for(int i = 0; i < nbMod; i++)
			{
				// On conditionne par une certaine valeur
				instance.conditionne(best.getVar(), best.getPref(i));			
				best.setEnfant(i, apprendRecursif(instance, variablesTmp, i == 0));
				instance.deconditionne(best.getVar());
			}
		}
		else
		{
			// Pas de split. On apprend un seul enfant qu'on associe à toutes les branches sortantes.
			LexicographicStructure enfant = apprendRecursif(instance, variablesTmp, true);
			for(int i = 0; i < nbMod; i++)
				best.setEnfant(i, enfant);
		}
		// A la fin, le VDD est conditionné de la même manière qu'à l'appel
		return best;
	}
	
	public LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete)
	{
		return apprendDonnees(filename, entete, -1);
	}
	
	public LexicographicStructure apprendDonnees(ArrayList<String> filename, boolean entete, int nbExemplesMax)
	{
		super.apprendDonnees(filename, entete, nbExemplesMax);
		ArrayList<String> variablesTmp = new ArrayList<String>();
		variablesTmp.addAll(variables);
		struct = apprendRecursif(new Instanciation(), variables, true);
//		System.out.println("Apprentissage fini");
		struct.updateBase(base);
		return struct;
	}
	
	@Override
	public String toString()
	{
		return super.toString()+"-"+profondeurMax;
	}

}
