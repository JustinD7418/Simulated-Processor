import java.util.LinkedList;
import java.util.Optional;

public class Parser {
	
	private TokenHandler handler;
	
	public Parser(LinkedList<Token> list) {
		handler = new TokenHandler(list);
	}
	
	private boolean acceptSeparators() {//Do this before and after commas
		boolean foundSeparator = false;
		while(handler.matchAndRemove(Token.tokenType.SEPARATOR).isPresent()) {
			foundSeparator = true;
			//System.out.println("Removed separator");
		}
		return foundSeparator;
	}
	
	public String[] parse() throws Exception{
		String[] instructions = new String[1024];
		int currentIndex = 0;
		Instruction newInstruction;
		while(handler.moreTokens() == true) {
			newInstruction = parseStatement();;
			instructions[currentIndex] = newInstruction.toString();
			currentIndex++;
			if(handler.moreTokens() == true && acceptSeparators() == false) {
				throw new Exception("Separator needed for further instruction");
			}
		}
	
		return instructions;
	}
	
	private Instruction parseStatement() throws Exception {
	
		if(handler.matchAndRemove(Token.tokenType.MATH).isPresent()) {
			return parseMath();
		} else if (handler.matchAndRemove(Token.tokenType.BRANCH).isPresent()) {
			return parseBranch();
		} else if (handler.matchAndRemove(Token.tokenType.HALT).isPresent()) {
			return parseHalt();
		} else if (handler.matchAndRemove(Token.tokenType.COPY).isPresent()) {
			return parseCopy();
		} else if (handler.matchAndRemove(Token.tokenType.JUMP).isPresent()) {
			return parseJump();
		} else if (handler.matchAndRemove(Token.tokenType.CALL).isPresent()) {
			return parseCall();
		} else if (handler.matchAndRemove(Token.tokenType.PUSH).isPresent()) {
			return parsePush();
		} else if (handler.matchAndRemove(Token.tokenType.POP).isPresent()) {
			return parsePop();
		} else if (handler.matchAndRemove(Token.tokenType.LOAD).isPresent()) {
			return parseLoad();
		} else if (handler.matchAndRemove(Token.tokenType.STORE).isPresent()) {
			return parseStore();
		} else if (handler.matchAndRemove(Token.tokenType.RETURN).isPresent()) {
			return parseReturn();
		} else if (handler.matchAndRemove(Token.tokenType.PEEK).isPresent()) {
			return parsePeek();
		} else {
			throw new Exception("Invalid token for start of instruction");
		}
		
	}

	private Instruction parseMath() throws Exception {
		String operation = "000";
		String aluOperation = "";
		String[] registers = new String[3];
		if(handler.matchAndRemove(Token.tokenType.ADD).isPresent()) {
			aluOperation = "1110";
		} else if(handler.matchAndRemove(Token.tokenType.SUBTRACT).isPresent()) {
			aluOperation = "1111";
		} else if(handler.matchAndRemove(Token.tokenType.MULTIPLY).isPresent()) {
			aluOperation = "0111";
		} else if(handler.matchAndRemove(Token.tokenType.AND).isPresent()) {
			aluOperation = "1000";
		} else if(handler.matchAndRemove(Token.tokenType.OR).isPresent()) {
			aluOperation = "1001";
		} else if(handler.matchAndRemove(Token.tokenType.XOR).isPresent()) {
			aluOperation = "1010";
		} else if(handler.matchAndRemove(Token.tokenType.NOT).isPresent()) {
			aluOperation = "1011";
		} else if(handler.matchAndRemove(Token.tokenType.SHIFT).isPresent()) {
			if(handler.matchAndRemove(Token.tokenType.LEFT).isPresent()) {
				aluOperation = "1100";
			} else if(handler.matchAndRemove(Token.tokenType.RIGHT).isPresent()) {
				aluOperation = "1101";
			} else {
				throw new Exception("Invalid token for operation type Shift");
			}
		} else {
			throw new Exception("Invalid operation for type MATH");
		}
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[1] == null) {
			throw new Exception("Insufficient registers found");
		}
		
		if(registers[2] == null) {//If the Instruction is 2R
			return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), aluOperation, 0);
		} else {//If the Instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), aluOperation, 0);
		}
	}
	
	
	private Instruction parseBranch() throws Exception {
		String operation = "001";
		String aluOp = "";
		String[] registers = new String[3];
		int immVal;
		if(handler.matchAndRemove(Token.tokenType.EQUAL).isPresent()) {
			aluOp = "0000";
		} else if(handler.matchAndRemove(Token.tokenType.UNEQUAL).isPresent()) {
			aluOp = "0001";
		} else if(handler.matchAndRemove(Token.tokenType.GREATER).isPresent()) {
			aluOp = "0100";
		} else if(handler.matchAndRemove(Token.tokenType.GREATEROREQUAL).isPresent()) {
			aluOp = "0011";
		} else if(handler.matchAndRemove(Token.tokenType.LESS).isPresent()) {
			aluOp = "0010";
		} else if(handler.matchAndRemove(Token.tokenType.LESSOREQUAL).isPresent()) {
			aluOp = "0101";
		} else {
			throw new Exception("Invalid operation for type MATH");
		}
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		} else {
			throw new Exception("Immediate value not present for BRANCH instruction");
		}
		
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[1] == null) {
			throw new Exception("Insufficient registers found");
		}
		
		if(registers[2] == null) {//If the Instruction is 2R
			return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), aluOp, immVal);
		} else {//If the Instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), aluOp, immVal);
		}
	}
	
	private Instruction parseHalt() {
		return new InstructionNoR("000", 0);
	}
	
	private Instruction parseCopy() throws Exception {
		String operation = "000";
		String[] registers = new String[3];
		int immVal = 0;
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		} else {
			throw new Exception("Immediate value not present for COPY instruction");
		}
		
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[1] != null) {
			throw new Exception("Too many registers present for COPY instruction");
		}
		
		return new InstructionDestOnly(operation, Integer.parseInt(registers[0]), immVal);
	}
	
	private Instruction parseJump() throws Exception {
		String operation = "001";
		String[] registers = new String[3];
		int immVal;
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		} else {
			throw new Exception("Immediate value not present for JUMP instruction");
		}
		
		registers = getRegisters();
		
		if(registers[1] != null) {
			throw new Exception("Too many registers present for JUMP instruction");
		}
		
		if(registers[0] == null) {//NoR
			return new InstructionNoR(operation, immVal);
		} else {//DestOnly
			return new InstructionDestOnly(operation,Integer.parseInt(registers[0]), immVal);
		}
		
	}
	
	private Instruction parseCall() throws Exception {
		String operation = "010";
		String aluOp = "";
		String[] registers = new String[3];
		int immVal;
		if(handler.matchAndRemove(Token.tokenType.EQUAL).isPresent()) {
			aluOp = "0000";
		} else if(handler.matchAndRemove(Token.tokenType.UNEQUAL).isPresent()) {
			aluOp = "0001";
		} else if(handler.matchAndRemove(Token.tokenType.GREATER).isPresent()) {
			aluOp = "0100";
		} else if(handler.matchAndRemove(Token.tokenType.GREATEROREQUAL).isPresent()) {
			aluOp = "0011";
		} else if(handler.matchAndRemove(Token.tokenType.LESS).isPresent()) {
			aluOp = "0010";
		} else if(handler.matchAndRemove(Token.tokenType.LESSOREQUAL).isPresent()) {
			aluOp = "0101";
		} else {
			throw new Exception("Invalid operation for type MATH");
		}
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		} else {
			throw new Exception("Immediate value not present for CALL instruction");
		}
		
		registers = getRegisters();
		
		if(registers[2] != null) {//If the instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), aluOp, immVal);
		} else {
			if(registers[1] != null) {//If the instruction is 2R
				return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), aluOp, immVal);
			} else {
				if(registers[0] != null) {//If the instruction is 2R
					return new InstructionDestOnly(operation, Integer.parseInt(registers[0]), aluOp, immVal);
				} else {//If the instruction NoR
					return new InstructionNoR(operation, immVal);
				}
			}
		}
	}
	
	private Instruction parsePush() throws Exception {
		String operation = "011";
		String aluOp = "";
		String[] registers = new String[3];
		int immVal;
		
		if(handler.matchAndRemove(Token.tokenType.ADD).isPresent()) {
			aluOp = "1110";
		} else if(handler.matchAndRemove(Token.tokenType.SUBTRACT).isPresent()) {
			aluOp = "1111";
		} else if(handler.matchAndRemove(Token.tokenType.MULTIPLY).isPresent()) {
			aluOp = "0111";
		} else if(handler.matchAndRemove(Token.tokenType.AND).isPresent()) {
			aluOp = "1000";
		} else if(handler.matchAndRemove(Token.tokenType.OR).isPresent()) {
			aluOp = "1001";
		} else if(handler.matchAndRemove(Token.tokenType.XOR).isPresent()) {
			aluOp = "1010";
		} else if(handler.matchAndRemove(Token.tokenType.NOT).isPresent()) {
			aluOp = "1011";
		} else if(handler.matchAndRemove(Token.tokenType.SHIFT).isPresent()) {
			if(handler.matchAndRemove(Token.tokenType.LEFT).isPresent()) {
				aluOp = "1100";
			} else if(handler.matchAndRemove(Token.tokenType.RIGHT).isPresent()) {
				aluOp = "1101";
			} else {
				throw new Exception("Invalid token for operation type Shift");
			}
		} else {
			throw new Exception("Invalid operation for type MATH");
		}
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		} else {
			throw new Exception("Immediate value not present for PUSH instruction");
		}
		
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[2] != null) {//If the instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), aluOp, immVal);
		} else {
			if(registers[1] != null) {//If the instruction is 2R
				return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), aluOp, immVal);
			} else {//If the instruction is DestOnly
				return new InstructionDestOnly(operation, Integer.parseInt(registers[0]), aluOp, immVal);
			}
		}
	}
	
	private Instruction parsePop() throws Exception {
		String operation = "110";
		String[] registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[1] != null) {
			throw new Exception("Too many registers present for POP instruction");
		}
		
		return new InstructionDestOnly(operation, Integer.parseInt(registers[0]), 0);
	}
	
	private Instruction parseLoad() throws Exception {
		String operation = "100";
		int immVal;
		String[] registers;
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		} else {
			throw new Exception("Immediate value not present for LOAD instruction");
		}
		
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[2] != null) {//If the instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), immVal);
		} else {
			if(registers[1] != null) {//If the instruction is 2R
				return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), immVal);
			} else {//If the instruction is DestOnly
				return new InstructionDestOnly(operation, Integer.parseInt(registers[0]), immVal);
			}
		}
	}
	
	private Instruction parseStore() throws Exception {
		String operation = "101";
		int immVal = 0;
		String[] registers;
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		}
		
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[2] != null) {//If the instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), immVal);
		} else {
			if(registers[1] != null) {//If the instruction is 2R
				return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), immVal);
			} else {//If the instruction DestOnly
				return new InstructionDestOnly(operation, Integer.parseInt(registers[0]), immVal);
			}
		}
	}
	
	private Instruction parseReturn() {
		String operation = "100";
		return new InstructionNoR(operation, 0);
	}
	
	private Instruction parsePeek() throws Exception {
		String operation = "110";
		int immVal = 0;
		String[] registers;
		
		if(handler.peek(0).getTokenType() == Token.tokenType.NUMBER) {
			immVal = Integer.parseInt(handler.peek(0).getValue());
			handler.matchAndRemove(Token.tokenType.NUMBER);
		}
		
		registers = getRegisters();
		
		if(registers[0] == null) {
			throw new Exception("No registers found");
		}
		
		if(registers[1] == null) {
			throw new Exception("Insufficient registers found");
		}
		
		if(registers[2] != null) {//If the instruction is 3R
			return new Instruction3R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), Integer.parseInt(registers[2]), immVal);
		} else {//If the instruction is 2R
			return new Instruction2R(operation, Integer.parseInt(registers[0]), Integer.parseInt(registers[1]), immVal);
		}
	}
	
	private String[] getRegisters() throws Exception {
		String[] registers = new String[3];
		int i = 0;
		String numText;
		while(handler.moreTokens() == true && handler.peek(0).getTokenType() == Token.tokenType.REGISTER) {
			if(i > 2) {
				throw new Exception("Too many registers present");
			}
			numText = "";
			if(Integer.parseInt(handler.peek(0).getValue()) > 31) {
				throw new Exception("Invalid register number");
			}
			numText = handler.peek(0).getValue();
			handler.matchAndRemove(Token.tokenType.REGISTER);
			registers[i] = numText;
			i++;
		}
		return registers;
	}
	
}
