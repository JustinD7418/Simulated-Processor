
public class Bit {

	private boolean value;
	
	Bit(){
		value = false;
	}
	
	Bit(boolean val){
		this.set(val);
	}
	
	void set(boolean val) {//Sets the value of the bit
		value = val;
	}
	
	void toggle() {//Sets the value of the bit to its opposite
		if(value == true) {
			value = false;
		} else {
			value = true;
		}
	}
	
	void set() {//Sets the value of a bit to true
		value = true;
	}
	
	void clear() {//Sets the value of a bit to false
		value = false;
	}
	
	boolean getValue() {//Returns the current value
		return value;
	}
	
	Bit and(Bit other) {//Performs the && operator
		Bit newBit = new Bit();
		newBit.clear();
		if(this.getValue()) {//Checking if the first bit is true
			if(other.getValue()) {//Checking if the second bit is true
				newBit.set();//If both are true, set the new bit to true
			}
		}
		return newBit;
	}
	
	Bit or(Bit other) {//Performs the || operator
		Bit newBit = new Bit();
		newBit.clear();
		if(this.getValue()) {//Checking if the first bit is true
			newBit.set();//If the first bit is true, set the new bit to true
		} else {
			if(other.getValue()) {//If the first bit is false, check if the second bit is true
				newBit.set();//If the second bit is true, set the new bit to true
			}
		}
		
		return newBit;
	}
	
	Bit xor(Bit other) {//Performs the exclusive || operator
		Bit newBit = new Bit();
		newBit.clear();
		if(this.getValue()) {//Checking if the first bit is true
			newBit.set();//If the first bit is true, set the new bit to true
			if(other.getValue()) {//Checking if the second bit is true
				newBit.clear();//If both are true, set the new bit to false
			}
		} else {
			if(other.getValue()) {//If the first bit is false, check if the second bit is true
				newBit.set();//If only the second bit is true, set the new bit to true
			}
		}
		return newBit;
	}
	
	Bit not() {//Performs the ! operator
		Bit newBit = new Bit();
		if(this.getValue()) {//If the original bit is true
			newBit.clear();//Set the new bit to false
		} else {//If the original bit is false
			newBit.toggle();//Set the new bit to true
		}
		return newBit;
	}
	
	public String toString() {//Returns the current value as a string
		if(getValue()) {
			return "t";
		} else {
			return "f";
		}
	}
	
}
