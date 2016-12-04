package origin;
/*
This class is used to store the location information(i.e. the row and column index) of the element in
the map.
e.g. the row and column index of the pokemon of in the map 
  
 */

public class Location{
	private int row;
	private int column;
	
	public Location(int row,int column){
		this.row = row;
		this.column = column;
	}
	
	public Location(Location l){
		this.row = l.row;
		this.column = l.column;
	}
	
	public int getRow(){
		return row;
	}
	
	public int getColumn(){
		return column;
	}
	
	
	public void reset(int row,int column){
		this.row = row;
		this.column = column;
	}
	//hashcode() is used to set up hash map of pokemons and stations
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + row;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (column != other.column)
			return false;
		if (row != other.row)
			return false;
		return true;
	}
	
	
	//used to print path to the output file with format:<row,column>
	public String toString(){
		return "<" + row + "," + column + ">"; 
	}

}
