
public class Processor {

	private Word PC;
	private Word SP;
	private Bit halted = new Bit();
	private Word currentInstruction;
	private ALU alu = new ALU();
	private Word ALUFuncCode;
	private Word opCode;
	Bit[] registerOp = new Bit[2];
	private Word immediateValue;
	private Word registerDest;
	private Word registerSource1Location;
	private Word registerSource2Location;
	public Word r0, r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20, 
	r21, r22, r23, r24, r25, r26, r27, r28, r29, r30, r31;
	private Word executionOutput;
	private Bit booleanResult;
	public static int clockCycle;
	private InstructionCache cache;
	private L2Cache cacheL2;
	
	Processor(){
		PC = new Word(0);
		SP = new Word(1024);
		r0 = new Word(0);
		r1 = new Word(0);
		r2 = new Word(0);
		r3 = new Word(0);
		r4 = new Word(0);
		r5 = new Word(0);
		r6 = new Word(0);
		r7 = new Word(0);
		r8 = new Word(0);
		r9 = new Word(0);
		r10 = new Word(0);
		r11 = new Word(0);
		r12 = new Word(0);
		r13 = new Word(0);
		r14 = new Word(0);
		r15 = new Word(0);
		r16 = new Word(0);
		r17 = new Word(0);
		r18 = new Word(0);
		r19 = new Word(0);
		r20 = new Word(0);
		r21 = new Word(0);
		r22 = new Word(0);
		r23 = new Word(0);
		r24 = new Word(0);
		r25 = new Word(0);
		r26 = new Word(0);
		r27 = new Word(0);
		r28 = new Word(0);
		r29 = new Word(0);
		r30 = new Word(0);
		r31 = new Word(0);
		cache = new InstructionCache();
		cacheL2 = new L2Cache();
	}
	
	void run() throws Exception{
		clockCycle = 0;
		while(halted.getValue() == false) {
			fetch();
			decode();
			execute();
			store();
		}
		System.out.println(clockCycle);
	}
	
	
	private void fetch() throws Exception {//L2 Cache Version
		currentInstruction = cache.readCache(PC);
		if(currentInstruction == null) {//Cache miss in main cache
			currentInstruction = cacheL2.readCache(PC);
			if(currentInstruction == null) {//Cache miss in L2
				Word PCOffset = new Word(0);
				PCOffset.copy(PC);
				cache.setAddress(PCOffset);
				for(int i = 0; i < 8; i++) {
					cache.setCache(MainMemory.read(PCOffset), i);
					PCOffset.increment();
				}
				clockCycle += 50;
				cacheL2.setAddress(1, PCOffset);
				for(int i = 0; i < 8; i++) {
					cacheL2.setCache(1, MainMemory.read(PCOffset), i);
					PCOffset.increment();
				}
				cacheL2.setAddress(2, PCOffset);
				for(int i = 0; i < 8; i++) {
					cacheL2.setCache(2, MainMemory.read(PCOffset), i);
					PCOffset.increment();
				}
				cacheL2.setAddress(3, PCOffset);
				for(int i = 0; i < 8; i++) {
					cacheL2.setCache(3, MainMemory.read(PCOffset), i);
					PCOffset.increment();
				}
				cacheL2.setAddress(4, PCOffset);
				for(int i = 0; i < 8; i++) {
					cacheL2.setCache(4, MainMemory.read(PCOffset), i);
					PCOffset.increment();
				}
				clockCycle += 350;
				currentInstruction = cache.readCache(PC);
				PC.increment();
			} else {//cache hit in L2
				cache.setAddress(cacheL2.getHitAddress());
				for(int i = 0; i < 8; i++) {
					cache.setCache(cacheL2.getHitBlock()[i], i);
				}
				clockCycle += 50;
				PC.increment();
			}
		} else {//Cache hit in main cache
			clockCycle += 10;
			PC.increment();
		}
		
	}
	
	
	/*
	private void fetch() throws Exception {//Main Cache Only Version
		currentInstruction = cache.readCache(PC);
		if(currentInstruction == null) {//Cache miss in main cache
			Word PCOffset = new Word(0);
			PCOffset.copy(PC);
			cache.setAddress(PCOffset);
			for(int i = 0; i < 8; i++) {
				cache.setCache(MainMemory.read(PCOffset), i);
				PCOffset.increment();
			}
			clockCycle += 50;
			
			currentInstruction = cache.readCache(PC);
			PC.increment();
		} else {
			clockCycle += 10;
			PC.increment();
		}
	}
	*/
	
	/*
	private void fetch() throws Exception {//No Cache version
		currentInstruction = MainMemory.read(PC);
		clockCycle += 300;
		PC.increment();
	}
	*/
	
	private void decode() {
		registerOp = new Bit[2];
		Bit newBit = new Bit();
		booleanResult = new Bit();
		immediateValue = new Word(0);
		registerDest = new Word(0);
		registerSource1Location = new Word(0);
		registerSource2Location = new Word(0);
		opCode = new Word(0);
		
		alu.op1 = null;
		alu.op2 = null;
		alu.result = null;
		
		for(int i = 31; i > 26; i--) {
			opCode.setBit(i, currentInstruction.getBit(i));
		}
		
		registerOp[1] = opCode.getBit(31);
		registerOp[0] = opCode.getBit(30);
		
		if(registerOp[1].getValue()) {//If the bits are X1	
			if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register
				//Getting the Immediate value from the instruction
				for(int j = 12; j >= 0; j--) {
					newBit = new Bit();
					newBit = currentInstruction.getBit(j);
					if(newBit.getValue()) {
						newBit = new Bit(true);
					} else {
						newBit = new Bit();
					} 
					immediateValue.setBit(31 - (12 - j), newBit);
				}
				//Getting the register destination value
				for(int j = 26; j > 21; j--) {
					registerDest.setBit(j + 5, currentInstruction.getBit(j));
				}
				//Getting the register source value
				for(int k = 17; k > 12; k--) {
					registerSource1Location.setBit(k + 14, currentInstruction.getBit(k));
				}
			} else { //If the bits are 01 - 1R Dest Only
				//Getting the Immediate value from the instruction
				for(int i = 17; i >= 0; i--) {
					newBit = new Bit();
					newBit = currentInstruction.getBit(i);
					if(newBit.getValue()) {
						newBit = new Bit(true);
					} else {
						newBit = new Bit();
					} 
					immediateValue.setBit(31 - (17 - i), newBit);
				}
				//Getting the register destination value
				for(int j = 26; j > 21; j--) {
					registerDest.setBit(j + 5, currentInstruction.getBit(j));
				}
			}
		} else {//If the bits are X0
			if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
				//Getting the Immediate value from the instruction
				for(int i = 7; i >= 0; i--) {
					newBit = new Bit();
					newBit = currentInstruction.getBit(i);
					if(newBit.getValue()) {
						newBit = new Bit(true);
					} else {
						newBit = new Bit();
					} 
					immediateValue.setBit(31 - (7 - i), newBit);
				}
				//Getting the register destination value
				for(int j = 26; j > 21; j--) {
					registerDest.setBit(j + 5, currentInstruction.getBit(j));
				}
				
				//Getting the 2nd register source value
				for(int k = 17; k > 12; k--) {
					registerSource2Location.setBit(k + 14, currentInstruction.getBit(k));
				}
				//Getting the 1st register source value
				for(int l = 12; l > 7; l--) {
					registerSource1Location.setBit(l + 19, currentInstruction.getBit(l));
				}
			} else {//If the bits are 00 - 0R No Register
				//Getting the Immediate value from the instruction 
				for(int j = 26; j >= 0; j--) {
					newBit = new Bit();
					newBit = currentInstruction.getBit(j);
					if(newBit.getValue()) {
						newBit = new Bit(true);
					} else {
						newBit = new Bit();
					} 
					immediateValue.setBit(31 - (26 - j), newBit);
				}
			}
		}
		
	}

	private void execute() throws Exception {
		ALUFuncCode = new Word(0);
		executionOutput = new Word(0);
		
		//Getting the ALU operation
		for(int i = 21; i > 17; i--) {
			ALUFuncCode.setBit(i + 10, currentInstruction.getBit(i));
		}
		
		if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == false) {// 000 - Math
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					alu.op1 = getRegister(registerDest);
					alu.op2 = getRegister(registerSource1Location);
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else { //If the bits are 01 - 1R Dest Only
					
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerSource2Location);
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else {//If the bits are 00 - 0R No Register
					halted.set();
				}
			}
		} else if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == true){// 001 - Branch
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerDest);
					alu.doOperation(ALUFuncCode);
					if(alu.result.getBit(31).getValue()) {
						booleanResult.set();
						alu.op1 = PC;
						alu.op2 = immediateValue;
						ALUFuncCode = new Word(14);// This sets the word to ...1110 or Addition
						alu.doOperation(ALUFuncCode);
						executionOutput = alu.result;
					} else {
						booleanResult.clear();
					}
				} else { //If the bits are 01 - 1R Dest Only
					ALUFuncCode = new Word(14);
					alu.op1 = PC;
					alu.op2 = immediateValue;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerSource2Location);
					alu.doOperation(ALUFuncCode);
					if(alu.result.getBit(31).getValue()) {
						booleanResult.set();
						alu.op1 = PC;
						alu.op2 = immediateValue;
						ALUFuncCode = new Word(14);
						alu.doOperation(ALUFuncCode);
						executionOutput = alu.result;
					} else {
						booleanResult.clear();
					}
				} else {//If the bits are 00 - 0R No Register
					executionOutput = immediateValue;
				}
			}
		} else if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == true && opCode.getBit(29).getValue() == false){// 010 - Call
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerDest);
					alu.doOperation(ALUFuncCode);
					if(alu.result.getBit(31).getValue()) {
						booleanResult.set();
						alu.op1 = PC;
						alu.op2 = immediateValue;
						ALUFuncCode = new Word(14);
						alu.doOperation(ALUFuncCode);
						executionOutput = alu.result;
					} else {
						booleanResult.clear();
					}
				} else { //If the bits are 01 - 1R Dest Only
					ALUFuncCode = new Word(14);
					alu.op1 = PC;
					alu.op2 = immediateValue;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerSource2Location);
					alu.doOperation(ALUFuncCode);
					if(alu.result.getBit(31).getValue()) {
						booleanResult.set();
						alu.op1 = getRegister(registerDest);
						alu.op2 = immediateValue;
						ALUFuncCode = new Word(14);
						alu.doOperation(ALUFuncCode);
						executionOutput = alu.result;
					} else {
						booleanResult.clear();
					}
				} else {//If the bits are 00 - 0R No Register
					executionOutput = immediateValue;
				}
			}
		} else if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == true && opCode.getBit(29).getValue() == true){// 011 - Push
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					alu.op1 = getRegister(registerDest);
					alu.op2 = getRegister(registerSource1Location);
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else { //If the bits are 01 - 1R Dest Only
					alu.op1 = getRegister(registerDest);
					alu.op2 = immediateValue;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerSource2Location);
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else {//If the bits are 00 - 0R No Register
					throw new Exception("Unused Operation Exception");
				}
			}
		} else if(opCode.getBit(27).getValue() == true && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == false){// 100 - Load/Return
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = immediateValue;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else { //If the bits are 01 - 1R Dest Only
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerDest);
					alu.op2 = immediateValue;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerSource2Location);
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else {//If the bits are 00 - 0R No Register
					//Return
				}
			}
		} else if(opCode.getBit(27).getValue() == true && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == true) {// 101 - Store
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerDest);
					alu.op2 = immediateValue;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else { //If the bits are 01 - 1R Dest Only
					//No execution Needed
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerDest);
					alu.op2 = getRegister(registerSource1Location);
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else {//If the bits are 00 - 0R No Register
					throw new Exception("Unused Operation Exception");
				}
			}
		} else if(opCode.getBit(27).getValue() == true && opCode.getBit(28).getValue() == true && opCode.getBit(29).getValue() == false) {// 110 - Peek/Pop/Interrupt
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(immediateValue);
					alu.doOperation(ALUFuncCode);
					
					alu.op1 = SP;
					alu.op2 = alu.result;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else { //If the bits are 01 - 1R Dest Only
					//Nothing needs to be done
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					ALUFuncCode = new Word(14);
					alu.op1 = getRegister(registerSource1Location);
					alu.op2 = getRegister(registerSource2Location);
					alu.doOperation(ALUFuncCode);
					
					alu.op1 = SP;
					alu.op2 = alu.result;
					alu.doOperation(ALUFuncCode);
					executionOutput = alu.result;
				} else {//If the bits are 00 - 0R No Register
					
				}
			}
		} else {
			throw new Exception("Invalid Opcode");
		}
		
		if(alu.result != null) {//Checking if an ALU operation was used
			if(ALUFuncCode.getBit(28).getValue() == false && ALUFuncCode.getBit(29).getValue() && ALUFuncCode.getBit(30).getValue() && ALUFuncCode.getBit(31).getValue()) {
				//Checking if the ALU operation is multiplication
				clockCycle += 10;
			} else {
				clockCycle += 2;
			}
		}
		
	}

	private void store() throws Exception {		
		if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == false) {// 000 - Math
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register
					setRegister(registerDest, executionOutput);
				} else { //If the bits are 01 - 1R Dest Only
					setRegister(registerDest, immediateValue);
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					setRegister(registerDest, executionOutput);
				} else {//If the bits are 00 - 0R No Register
					
				}
			}
		} else if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == true){// 001 - Branch
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					if(booleanResult.getValue()) {
						PC = executionOutput;
					} 
				} else { //If the bits are 01 - 1R Dest Only
					PC = executionOutput;
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					if(booleanResult.getValue()) {
						PC = executionOutput;
					} 
				} else {//If the bits are 00 - 0R No Register
					PC = executionOutput;
				}
			}
		} else if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == true && opCode.getBit(29).getValue() == false){// 010 - Call
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					if(booleanResult.getValue()) {
						SP.decrement();
						MainMemory.write(SP, PC);
						PC = executionOutput;
					} 		
				} else { //If the bits are 01 - 1R Dest Only
					SP.decrement();
					MainMemory.write(SP, PC);
					PC = executionOutput;
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					if(booleanResult.getValue()) {
						SP.decrement();
						MainMemory.write(SP, PC);
						PC = executionOutput;
					} 
				} else {//If the bits are 00 - 0R No Register
					SP.decrement();
					MainMemory.write(SP, PC);
					PC = executionOutput;
				}
			}
		} else if(opCode.getBit(27).getValue() == false && opCode.getBit(28).getValue() == true && opCode.getBit(29).getValue() == true){// 011 - Push
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					SP.decrement();
					MainMemory.write(SP, executionOutput);
				} else { //If the bits are 01 - 1R Dest Only
					SP.decrement();
					MainMemory.write(SP, executionOutput);
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					SP.decrement();
					MainMemory.write(SP, executionOutput);
				} else {//If the bits are 00 - 0R No Register
					//Should have returned an exception in execute
				}
			}
		} else if(opCode.getBit(27).getValue() == true && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == false){// 100 - Load/Return
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					setRegister(registerDest, MainMemory.read(executionOutput));
				} else { //If the bits are 01 - 1R Dest Only
					setRegister(registerDest, MainMemory.read(executionOutput));
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					setRegister(registerDest, MainMemory.read(executionOutput));
				} else {//If the bits are 00 - 0R No Register
					PC = MainMemory.read(SP);
					SP.increment();
				}
			}
		} else if(opCode.getBit(27).getValue() == true && opCode.getBit(28).getValue() == false && opCode.getBit(29).getValue() == true) {// 101 - Store
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					MainMemory.write(executionOutput, getRegister(registerSource1Location));
				} else { //If the bits are 01 - 1R Dest Only
					MainMemory.write(getRegister(registerDest), immediateValue);
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					MainMemory.write(executionOutput, getRegister(registerSource2Location));
				} else {//If the bits are 00 - 0R No Register
					//Should have returned an exception in Execute()
				}
			}
		} else if(opCode.getBit(27).getValue() == true && opCode.getBit(28).getValue() == true && opCode.getBit(29).getValue() == false) {// 110 - Peek/Pop/Interrupt
			if(registerOp[1].getValue()) {//If the bits are X1
				if(registerOp[0].getValue()) {//If the bits are 11 - 2R 2 Register	
					setRegister(registerDest, MainMemory.read(executionOutput));
				} else { //If the bits are 01 - 1R Dest Only
					setRegister(registerDest, MainMemory.read(SP));
					SP.increment();
				}
			} else {//If the bits are X0
				if(registerOp[0].getValue()) {//If the bits are 10 - 3R 3 Register
					setRegister(registerDest, MainMemory.read(executionOutput));
				} else {//If the bits are 00 - 0R No Register
					
				}
			}
		} else {
			throw new Exception("Invalid Opcode");
		}
		r0 = new Word(0);
	}
	
	Word getRegister(Word regDestValue){
		if(regDestValue.getBit(31).getValue()) {// XXXX1
			if(regDestValue.getBit(30).getValue()) {// XXX11
				if(regDestValue.getBit(29).getValue()) {// XX111
					if(regDestValue.getBit(28).getValue()) {// X1111
						if(regDestValue.getBit(27).getValue()) {// 11111
							return r31;
						} else {// 01111
							return r15;
						}
					} else {// X0111
						if(regDestValue.getBit(27).getValue()) {// 10111
							return r23;
						} else {// 00111
							return r7;
						}
					}
				} else {// XX011
					if(regDestValue.getBit(28).getValue()) {// X1011
						if(regDestValue.getBit(27).getValue()) {// 11011
							return r27;
						} else {// 01011
							return r11;
						}
					} else {// X0011
						if(regDestValue.getBit(27).getValue()) {// 10011
							return r19;
						} else {// 00011
							return r3;
						}
					}
				}
			} else {// XXX01
				if(regDestValue.getBit(29).getValue()) {// XX101
					if(regDestValue.getBit(28).getValue()) {// X1101
						if(regDestValue.getBit(27).getValue()) {// 11101
							return r29;
						} else {// 01101
							return r13;
						}
					} else {// X0101
						if(regDestValue.getBit(27).getValue()) {// 10101
							return r21;
						} else {// 00101
							return r5;
						}
					}
				} else {// XX001
					if(regDestValue.getBit(28).getValue()) {// X1001
						if(regDestValue.getBit(27).getValue()) {// 11001
							return r25;
						} else {// 01001
							return r9;
						}
					} else {// X0001
						if(regDestValue.getBit(27).getValue()) {// 10001
							return r17;
						} else {// 00001
							return r1;
						}
					}
				}
			}
		} else {// XXXX0
			if(regDestValue.getBit(30).getValue()) {// XXX10
				if(regDestValue.getBit(29).getValue()) {// XX110
					if(regDestValue.getBit(28).getValue()) {// X1110
						if(regDestValue.getBit(27).getValue()) {// 11110
							return r30;
						} else {// 01110
							return r14;
						}
					} else {// X0110
						if(regDestValue.getBit(27).getValue()) {// 10110
							return r22;
						} else {// 00110
							return r6;
						}
					}
				} else {// XX010
					if(regDestValue.getBit(28).getValue()) {// X1010
						if(regDestValue.getBit(27).getValue()) {// 11010
							return r26;
						} else {// 01010
							return r10;
						}
					} else {// X0010
						if(regDestValue.getBit(27).getValue()) {// 10010
							return r18;
						} else {// 00010
							return r2;
						}
					}
				}
			} else {// XXX00
				if(regDestValue.getBit(29).getValue()) {// XX100
					if(regDestValue.getBit(28).getValue()) {// X1100
						if(regDestValue.getBit(27).getValue()) {// 11100
							return r28;
						} else {// 01100
							return r12;
						}
					} else {// X0100
						if(regDestValue.getBit(27).getValue()) {// 10100
							return r20;
						} else {// 00100
							return r4;
						}
					}
				} else {// XX000
					if(regDestValue.getBit(28).getValue()) {// X1000
						if(regDestValue.getBit(27).getValue()) {// 11000
							return r24;
						} else {// 01000
							return r8;
						}
					} else {// X0000
						if(regDestValue.getBit(27).getValue()) {// 10000
							return r16;
						} else {// 00000
							return r0;
						}
					}
				}
			}
		}
	}
	
	void setRegister(Word regDestValue, Word newWord){
		if(regDestValue.getBit(31).getValue()) {// XXXX1
			if(regDestValue.getBit(30).getValue()) {// XXX11
				if(regDestValue.getBit(29).getValue()) {// XX111
					if(regDestValue.getBit(28).getValue()) {// X1111
						if(regDestValue.getBit(27).getValue()) {// 11111
							r31 = newWord;
						} else {// 01111
							r15 = newWord;
						}
					} else {// X0111
						if(regDestValue.getBit(27).getValue()) {// 10111
							r23 = newWord;
						} else {// 00111
							r7 = newWord;
						}
					}
				} else {// XX011
					if(regDestValue.getBit(28).getValue()) {// X1011
						if(regDestValue.getBit(27).getValue()) {// 11011
							r27 = newWord;
						} else {// 01011
							r11 = newWord;
						}
					} else {// X0011
						if(regDestValue.getBit(27).getValue()) {// 10011
							r19 = newWord;
						} else {// 00011
							r3 = newWord;
						}
					}
				}
			} else {// XXX01
				if(regDestValue.getBit(29).getValue()) {// XX101
					if(regDestValue.getBit(28).getValue()) {// X1101
						if(regDestValue.getBit(27).getValue()) {// 11101
							r29 = newWord;
						} else {// 01101
							r13 = newWord;
						}
					} else {// X0101
						if(regDestValue.getBit(27).getValue()) {// 10101
							r21 = newWord;
						} else {// 00101
							r5 = newWord;
						}
					}
				} else {// XX001
					if(regDestValue.getBit(28).getValue()) {// X1001
						if(regDestValue.getBit(27).getValue()) {// 11001
							r25 = newWord;
						} else {// 01001
							r9 = newWord;
						}
					} else {// X0001
						if(regDestValue.getBit(27).getValue()) {// 10001
							r17 = newWord;
						} else {// 00001
							r1 = newWord;
						}
					}
				}
			}
		} else {// XXXX0
			if(regDestValue.getBit(30).getValue()) {// XXX10
				if(regDestValue.getBit(29).getValue()) {// XX110
					if(regDestValue.getBit(28).getValue()) {// X1110
						if(regDestValue.getBit(27).getValue()) {// 11110
							r30 = newWord;
						} else {// 01110
							r14 = newWord;
						}
					} else {// X0110
						if(regDestValue.getBit(27).getValue()) {// 10110
							r22 = newWord;
						} else {// 00110
							r6 = newWord;
						}
					}
				} else {// XX010
					if(regDestValue.getBit(28).getValue()) {// X1010
						if(regDestValue.getBit(27).getValue()) {// 11010
							r26 = newWord;
						} else {// 01010
							r10 = newWord;
						}
					} else {// X0010
						if(regDestValue.getBit(27).getValue()) {// 10010
							r18 = newWord;
						} else {// 00010
							r2 = newWord;
						}
					}
				}
			} else {// XXX00
				if(regDestValue.getBit(29).getValue()) {// XX100
					if(regDestValue.getBit(28).getValue()) {// X1100
						if(regDestValue.getBit(27).getValue()) {// 11100
							r28 = newWord;
						} else {// 01100
							r12 = newWord;
						}
					} else {// X0100
						if(regDestValue.getBit(27).getValue()) {// 10100
							r20 = newWord;
						} else {// 00100
							r4 = newWord;
						}
					}
				} else {// XX000
					if(regDestValue.getBit(28).getValue()) {// X1000
						if(regDestValue.getBit(27).getValue()) {// 11000
							r24 = newWord;
						} else {// 01000
							r8 = newWord;
						}
					} else {// X0000
						if(regDestValue.getBit(27).getValue()) {// 10000
							r16 = newWord;
						} else {// 00000
							r0 = newWord;
						}
					}
				}
			}
		}
	}

}