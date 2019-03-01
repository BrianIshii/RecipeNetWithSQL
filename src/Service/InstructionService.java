package Service;

import Entity.Instruction;

public class InstructionService extends EntityService {
  private static final String TABLE_NAME = "Instruction";
  private static final String TABLE_ALIAS = "INS";

  private static InstructionService instance = new InstructionService();

  private InstructionService() {};

  public static InstructionService getInstance() { return instance; }

  public Instruction save(Instruction instruction) {
    if(instruction.getInstrid() == 0) {
      Long instrId = create(instruction);
      if (instrId > 0) {
        instruction.setInstrid(instrId);
        return instruction;
      }
      System.err.println("Unable to create instruction");
    } else {
      throw new RuntimeException("Update instruction not implemented");
    }
    return null;
  }

  private Long create(Instruction instruction) {
    String addInstr = String.format(INSERT_TEMPLATE, getTableName());
    return 0L;
  }

  public String getTableName() {
    return TABLE_NAME;
  }

  public String getTableAlias() {
    return TABLE_ALIAS;
  }
}
