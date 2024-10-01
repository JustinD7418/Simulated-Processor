
public class ALU {

	public Word op1;
	public Word op2;
	public Word result;
	
	public void doOperation(Word operation) throws Exception {
		if(operation.getBit(28).getValue()) {// - 1***
			if(operation.getBit(29).getValue()) {// - 11**
				if(operation.getBit(30).getValue()) {// - 111*
					if(operation.getBit(31).getValue()) {//Subtraction - 1111
						result = this.add2(op1, this.add2(op2.not(), new Word(1)));
					} else {//Addition - 1110
						result = this.add2(op1, op2);
					}
				} else {// - 110*
					int shiftAmount = 0;
					int multiplier = 0;
					for(int i = 31; i > 26; i--) {
						if (op2.getBit(i).getValue() == true) {
							multiplier = 1;
						} else {
							multiplier = 0;
						}
						shiftAmount += (multiplier * Math.pow(2, 31 - i));
					}
					if(operation.getBit(31).getValue()) {//Right Shift - 1101
						result = op1.rightShift(shiftAmount);
					} else {//Left Shift - 1100
						result = op1.leftShift(shiftAmount);
					}
				}
			} else {// - 10**
				if(operation.getBit(30).getValue()) {//Testing if the third bit is set while the second bit is not - 101*
					if(operation.getBit(31).getValue()) {//Not - 1011
						result = op1.not();
					} else {//Xor - 1010
						result = op1.xor(op2);
					}
				} else {// - 100*
					if(operation.getBit(31).getValue()) {//Or - 1001
						result = op1.or(op2);
					} else {//And - 1000
						result = op1.and(op2);
					}
				}
			}
		} else {//The first bit is not set - 0***
			if(operation.getBit(29).getValue()) {// - 01**
				if(operation.getBit(30).getValue()) {// - 011*
					if(operation.getBit(31).getValue()) {//Multiplication - 0111
						result = this.multiply(op1, op2);
					} else {// - 0110
						throw new Exception("Invalid Bit Assortment");
					}
				} else {// - 010*
					if(operation.getBit(31).getValue()) {//Less Than or Equal - 0101
						boolean lte = true;
						Word newWord = this.add2(op1, this.add2(op2.not(), new Word(1)));
						if(newWord.getBit(0).getValue() == false) {
							for(int i = 31; i > 0; i--) {
								if(newWord.getBit(i).getValue()) {
									lte	= false;							
								}
							}
						}
						result = new Word(0);
						if (lte) {
							result.increment();
						}
					} else {//Greater Than - 0100
						boolean gt = true;
						Word newWord = this.add2(op1, this.add2(op2.not(), new Word(1)));
						if(newWord.getBit(0).getValue()) {
							gt = false;
						}
						result = new Word(0);
						if (gt) {
							result.increment();
						}
					}
				}
			} else {// - 00**
				if(operation.getBit(30).getValue()) {// - 001*
					if(operation.getBit(31).getValue()) {//Greater Than or Equal - 0011
						boolean gte = true;
						Word newWord = this.add2(op1, this.add2(op2.not(), new Word(1)));
						if(newWord.getBit(0).getValue()) {
							for(int i = 31; i > 0; i--) {
								if(newWord.getBit(i).getValue() == false) {
									gte	= false;							
								}
							}
						}
						result = new Word(0);
						if (gte) {
							result.increment();
						}
					} else {//Less Than - 0010
						boolean lt = true;
						Word newWord = this.add2(op1, this.add2(op2.not(), new Word(1)));
						if(newWord.getBit(0).getValue() == false) {
							lt = false;
						}
						result = new Word(0);
						if (lt) {
							result.increment();
						}
					}
				} else {// - 000*
					if(operation.getBit(31).getValue()) {//Not Equal - 0001
						boolean nEqual = false;
						for(int i = 31; i >= 0; i--) {
							if((op1.getBit(i).getValue() && op2.getBit(i).getValue() == false) || (op1.getBit(i).getValue() == false && op2.getBit(i).getValue())) {
								nEqual = true;
								i = -1;
							}
						}
						result = new Word(0);
						if (nEqual) {
							result.increment();
						}
					} else {//Equals - 0000
						boolean equal = true;
						for(int i = 31; i >= 0; i--) {
							if((op1.getBit(i).getValue() && op2.getBit(i).getValue() == false) || (op1.getBit(i).getValue() == false && op2.getBit(i).getValue())) {
								equal = false;
								i = -1;
							}
						}
						result = new Word(0);
						if (equal) {
							result.increment();
						}
					}
				}
			}
		}		
	}

	private Word add2(Word word1, Word word2) {
		Word result = new Word(0);
		Bit carryIn = new Bit();
		Bit resultBit = new Bit();
		for(int i = 31; i >= 0; i--) {
			resultBit = word1.getBit(i).xor(word2.getBit(i)).xor(carryIn);
			result.setBit(i, resultBit);
			carryIn = word1.getBit(i).and(word2.getBit(i)).or((word1.getBit(i).xor(word2.getBit(i)).and(carryIn)));
		}
		
		return result;
	}
	
	public Word add4(Word word1, Word word2, Word word3, Word word4) {
		Bit carryIn = new Bit();
		Word result = new Word(0);
		Word sum1 = new Word(0);
		Word sum2 = new Word(0);
		Bit resultBit = new Bit();
		
		for(int i = 31; i >= 0; i--) {
			resultBit = word1.getBit(i).xor(word2.getBit(i)).xor(carryIn);
			sum1.setBit(i, resultBit);
			carryIn = word1.getBit(i).and(word2.getBit(i)).or((word1.getBit(i).xor(word2.getBit(i)).and(carryIn)));
		}
		carryIn = new Bit();
		
		for(int i = 31; i >= 0; i--) {
			resultBit = sum1.getBit(i).xor(word3.getBit(i)).xor(carryIn);
			sum2.setBit(i, resultBit);
			carryIn = sum1.getBit(i).and(word3.getBit(i)).or((sum1.getBit(i).xor(word3.getBit(i)).and(carryIn)));
		}
		carryIn = new Bit();
		
		for(int i = 31; i >= 0; i--) {
			resultBit = sum2.getBit(i).xor(word4.getBit(i)).xor(carryIn);
			result.setBit(i, resultBit);
			carryIn = sum2.getBit(i).and(word4.getBit(i)).or((sum2.getBit(i).xor(word4.getBit(i)).and(carryIn)));
		}
		
		return result;
	}
	
	private Word multiply(Word word1, Word word2) {
		Word result = new Word(0);
		Word multiplyResult = new Word(0);
		Word newWord = new Word(0);
		Bit resultBit = new Bit();
		boolean bitSet = false;
		int mCounter = 0;
		
		for(int i = 31; i >= 0; i--) {
			newWord = new Word(0);
			multiplyResult = new Word(0);
			if(word2.getBit(i).getValue()) {
				for(int k = 31; k >= 0; k--) {
					newWord.setBit(k, new Bit(true));
				}
				bitSet = true;//Added this just to save time on the false bits
			}
			if(bitSet) {
				for(int j = i; j >= 0; j--) {
					resultBit = word1.getBit(j+mCounter).and(newWord.getBit(j+mCounter));//Added mCounter to account for the # of 0s
					multiplyResult.setBit(j, resultBit);
				}
			}
			
			result = this.add2(result, multiplyResult);
			mCounter++;
			bitSet = false;
		}
		return result;
	}
	
}
