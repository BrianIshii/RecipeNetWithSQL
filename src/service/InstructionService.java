package service;

import entity.Instruction;
import formatter.Field;
import schema.ResponseSchema;

import java.util.ArrayList;
import java.util.List;

public class InstructionService extends EntityService {
  private static InstructionService ourInstance = new InstructionService();

  public static InstructionService getInstance() {
    return ourInstance;
  }

  private InstructionService() {}

  public List<Instruction> searchByRecipe(Long rid) {
      List<ResponseSchema> response =
        executorService.executeSelect(
                Instruction.TABLE_NAME, Instruction.ENTITY_FIELDS, new Field<Long>(Long.class, "rid", rid, true));

    List<Instruction> instructions = new ArrayList<>();
      Instruction temp;
      for (ResponseSchema res : response) {
      temp = new Instruction(rid);
          res.applyValuesTo(temp, true);
          temp.setSynced();
      instructions.add(temp);
    }
      instructions.sort((i1, i2) -> (Integer) i1.getFieldValue("step") - (Integer) i2.getFieldValue("step"));
    return instructions;
  }

    public void clearRecipeInstructions(Long rid) {
        executorService.executeDelete(Instruction.TABLE_NAME, new Field<>(Long.class, "rid", rid));
    }

}
