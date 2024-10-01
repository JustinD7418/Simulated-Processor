
public class Instruction3R extends Instruction{

	private String op;
	private int registerSource1;
	private int registerSource2;
	private int registerDest;
	private String ALUOp;
	private int immVal;
	
	Instruction3R(String opCode, int rs1, int rs2, int rd, String alu, int imm){
		op = opCode;
		registerSource1 = rs1;
		registerSource2 = rs2;
		registerDest = rd;
		ALUOp = alu;
		immVal = imm;
	}
	
	Instruction3R(String opCode, int rs1, int rs2, int rd, int imm){
		op = opCode;
		registerSource1 = rs1;
		registerSource2 = rs2;
		registerDest = rd;
		ALUOp = "0000";
		immVal = imm;
	}
	
	private String registerToBinary(int num) {
		String output = "";
		int origValue = num;
				
		for(int i = 0; i < 5; i++) {
			String bit = "0";
			if(origValue % 2 != 0) {
				bit = "1";
			}
			output = bit + output;
			origValue = origValue/2;
		}
		return output;
	}
	
	private String immediateValueToBinary() {
		String output = "";
		String value = "";
		int origValue = immVal;
		int endIndex = 8;
		
		while (origValue > 0) {//This finds out how many spaces need to be evaluated
			endIndex--;
			origValue = origValue/2;
		}
		origValue = immVal;//Reset the original value to go through again
		for(int i = 0; i < endIndex; i++) {
			output += "0";
		}
				
		for(int i = endIndex; i < 8; i++) {
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
		
		return immediateValueToBinary() + registerToBinary(registerSource1) + registerToBinary(registerSource2) + ALUOp + registerToBinary(registerDest) + op + "10";
	}
	
}
