package br4cp;

import java.util.ArrayList;

public class Heuristique9 implements Heuristique {

	@Override
	public void reordoner(int[][] contraintes,
			Ordonnancement ord) {
		ArrayList<Var> listeTemp=new ArrayList<Var>();
		ord.constGraphAdj(contraintes);
	//constNbContraintes(contraintes);
	
	int score[]=new int[ord.size];
	int max=-1;
	int varmax=-1;
	
	for(int curr=0; curr<ord.size; curr++){
		for(int i=0; i<ord.size; i++){
			if(score[i]>max){
				max=score[i];
				varmax=i;
			}
		}
		listeTemp.add(ord.variables.get(varmax));
		
		score[varmax]=-1;		//faut plus qu'elle ressorte
		//mise a jours de score
		for(int i=0; i<ord.size; i++){
			if(score[i]!=-1 && ord.graphAdj[varmax][i]>0){
				//recherche de l'arite max
				score[i]+=ord.graphAdj[varmax][i]-1;
				//score[i]+=1;
			}
		}
		
		max=-1;
		varmax=-1;
			
	}
	for(int i=0; i<listeTemp.size(); i++)
		ord.variables.set(i, listeTemp.get(i));
	}
	
}
