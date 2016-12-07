/*   (C) Copyright 2016, Gimenez Pierre-François
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

package graphOperation;

import java.util.HashSet;
import java.util.Set;

/**
 * Une partition d'un ensemble de variables
 * @author Pierre-François Gimenez
 *
 */

public class Partition
{
	public Set<String>[] ensembles;
	public Set<String> separateur;
	
	@SuppressWarnings("unchecked")
	public Partition()
	{
		ensembles = (Set<String>[]) new HashSet[2];
		for(int i = 0; i < 2; i++)
			ensembles[i] = new HashSet<String>();
		separateur = new HashSet<String>();
	}
	
	@Override
	public String toString()
	{
		String out = "";
		out += "G1 :";
		for(String s : ensembles[0])
			out += " "+s;
		out += "\nG2 :";
		for(String s : ensembles[1])
			out += " "+s;
		out += "\nC :";
		for(String s : separateur)
			out += " "+s;
		return out;
	}
}
