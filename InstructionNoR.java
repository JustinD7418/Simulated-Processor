
public class InstructionNoR extends Instruction{

	private String op;
	private int immVal;
	
	InstructionNoR(String opCode, int imm){
		op = opCode;
		immVal = imm;
	}
	
	private String immediateValueToBinary() {
		String output = "";
		String value = "";
		int origValue = immVal;
		int endIndex = 27;
		
		while (origValue > 0) {//This finds out how many spaces need to be evaluated
			endIndex--;
			origValue = origValue/2;
		}
		origValue = immVal;//Reset the original value to go through again
		for(int i = 0; i < endIndex; i++) {
			output += "0";
		}
				
		for(int i = endIndex; i < 27; i++) {
			String bit = "0";
			if(origValue % 2 != 0) {
				bit = "1";
			}
			value = bit + value;
			origValue = origValue/2;
		}
		output += value;
		
		return output;
	}

	@Override
	public String toString() {
		return immediateValueToBinary() + op + "00";
	}
	
}
