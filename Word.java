
public class Word {
	
	private Bit[] bits;
	
	Word(int val) {
		bits = new Bit[32];
		for(int i = 0; i < 32; i++) {
			this.setBit(i, new Bit());
		}
		this.set(val);
	}
	
	Bit getBit(int i) {//Returns the value of a certain bit
		return bits[i];
	}
	
	void setBit(int i, Bit value) {//Sets a specific bit to a specified value
		bits[i] = value;
	}
	
	Word and(Word other) {//Performs && on every pair of bits between two words
		Word newWord = new Word(0);
		Bit newBit;
		for(int i = 0; i < 32; i++) {
			newBit = this.getBit(i).and(other.getBit(i));
			newWord.setBit(i, newBit);
		}
		return newWord;
	}
	
	Word or(Word other) {//Performs || on every pair of bits between two words
		Word newWord = new Word(0);
		Bit newBit;
		for(int i = 0; i < 32; i++) {
			newBit = this.getBit(i).or(other.getBit(i));
			newWord.setBit(i, newBit);
		}
		return newWord;
	}

	Word xor(Word other) {//Performs exclusive || on every pair of bits between two words
		Word newWord = new Word(0);
		Bit newBit;
		for(int i = 0; i < 32; i++) {
			newBit = this.getBit(i).xor(other.getBit(i));
			newWord.setBit(i, newBit);
		}
		return newWord;
	}

	Word not() {//Performs ! on every bits on a word
		Word newWord = new Word(0);
		Bit newBit;
		for(int i = 0; i < 32; i++) {
			newBit = this.getBit(i).not();
			newWord.setBit(i, newBit);
		}
		return newWord;
	}
	
	Word rightShift(int amount) {//Shifts the values of bits in a word to the right
		Word newWord = new Word(0);
		int wordIndex = 31 - amount;
		for(int j = 31; j > amount; j--) {
			newWord.setBit(j, this.getBit(wordIndex));
			wordIndex--;
			
		}
		for(int i = amount; i <= 0; i--) {
			newWord.setBit(i, new Bit());
		}
		return newWord;
		
	}
	
	Word leftShift(int amount) {//Shifts the values of bits in a word to the left
		Word newWord = new Word(0);
		int wordIndex = 31;
		for(int j = 31; j > 31 - amount; j--) {
			newWord.setBit(j, new Bit());
		}
		for(int i =  31 - amount; i >= 0; i--) {
			newWord.setBit(i, this.getBit(wordIndex));
			wordIndex--;
		}
		return newWord;
	}
	
	public String toString() {
		String fullWord = "";
		fullWord += this.getBit(0).toString();
		for(int i = 1; i < 32; i++) {
			fullWord += ",";
			fullWord += this.getBit(i).toString();
		}
		return fullWord;
	}
	
	long getUnsigned() {//Returns a word as a number that's too big
		long total = 0;
		int multiplier = 0;
		for(int i = 31; i >= 0; i--) {
			if (this.getBit(i).getValue() == true) {
				multiplier = 1;
			} else {
				multiplier = 0;
			}
			total += (multiplier * Math.pow(2, 31 - i));
		}
		return total;
	}
	
	int getSigned() {//Returns a word as a number
		Word newWord;
		Word mask = new Word(0);
		if(this.getBit(0).getValue()) {//If the negative bit is set
			for(int j = 31; j >= 0; j--) {
				mask.setBit(j, new Bit(true));
			}
			newWord = this.not();
			newWord.increment();
		} else {
			newWord = this;
		}
		int total = 0;
		int multiplier = 0;
		for(int i = 31; i >= 1; i--) {
			if (newWord.getBit(i).getValue() == true) {
				multiplier = 1;
			} else {
				multiplier = 0;
			}
			total += (multiplier * Math.pow(2, 31 - i));
		}
		if(this.getBit(0).getValue()) {//If the negative bit is set
			total *= -1;
		}
		return total;
	}
	
	void copy(Word other) {
		for(int i = 0; i < 32; i ++) {
			if(other.getBit(i).getValue()) {
				this.setBit(i, new Bit(true));
			} else {
				this.setBit(i, new Bit(false));
			}
		}
	}
	
	void set(int value) {
		boolean negative = false;
		for(int j = 31; j >= 0; j--) {
			this.bits[j] = new Bit();
		}
		if (value != 0) {
			if(value < 0) {
				value *=-1;
				negative = true;
			}
			int endIndex = 32;//Start at 32 because 31 is the last position
			int origValue = value;
			while (origValue > 0) {//This finds out how many spaces need to be evaluated
				endIndex--;
				origValue = origValue/2;
			}
			origValue = value;//Reset the original value to go through again
			for(int i = 31; i >= endIndex; i--) {
				Bit newBit = new Bit();
				if(origValue % 2 != 0) {
					newBit.set();
				}
				this.setBit(i, newBit);
				origValue = origValue/2;
			}
		}
		if(negative) {
			Word newWord = this.not();
			newWord.increment();
			this.bits = newWord.bits;
		}
		
	}
	
	void increment() {
		Bit carryIn = new Bit(true);//The "one" we will be adding
		Bit resultBit;
		for(int i = 31; i >= 0; i--) {
			resultBit = this.bits[i].xor(carryIn);
			carryIn = this.bits[i].and(carryIn);
			this.bits[i] = resultBit;
		}
	}
	
	void decrement() {
		Word orgWord = this;
		Word subtraction = new Word(1).not();
		subtraction.increment();
		Bit carryIn = new Bit();
		Bit resultBit;
		for(int i = 31; i >= 0; i--) {
			resultBit = orgWord.getBit(i).xor(subtraction.getBit(i)).xor(carryIn);
			carryIn = orgWord.getBit(i).and(subtraction.getBit(i)).or((orgWord.getBit(i).xor(subtraction.getBit(i)).and(carryIn)));
			this.bits[i] = resultBit;
		}
	}
	
	public boolean isEqual(Word word2) {
		boolean result = true;
		for(int i = 31; i >= 0; i--) {
			if(this.getBit(i).getValue() != word2.getBit(i).getValue()) {
				result = false;
				i = -1;
			}
		}
		return result;
	}

}
