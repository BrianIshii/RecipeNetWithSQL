package RecipeNet.service;

import RecipeNet.entity.Instruction;
import RecipeNet.exception.ExecutorException;
import RecipeNet.schema.Field;
import RecipeNet.schema.ResponseSchema;

import java.util.ArrayList;
import java.util.List;

public class InstructionService extends EntityService {
  private static InstructionService ourInstance = new InstructionService();

  public static InstructionService getInstance() {
    return ourInstance;
  }

  private InstructionService() {}

  public List<Instruction> searchByRecipe(Long rid) throws ExecutorException {
    List<ResponseSchema> response =
        executorService.executeSelect(
            Instruction.TABLE_NAME,
            Instruction.ENTITY_FIELDS,
            new Field<Long>(Long.class, Instruction.RID, rid, true));

    List<Instruction> instructions = new ArrayList<>();
    Instruction temp;
    for (ResponseSchema res : response) {
      temp = new Instruction(rid);
      res.applyValuesTo(temp, true);
      temp.setSynced();
      instructions.add(temp);
    }
    // TODO add sort query functionality
    instructions.sort(
        (i1, i2) -> (Integer) i1.getFieldValue("step") - (Integer) i2.getFieldValue("step"));
    return instructions;
  }

  /**
   * Deletes all Instructions that contain the given rid.
    * @param rid
   * @throws ExecutorException
   */
  public void deleteRecipeInstructions(Long rid) throws ExecutorException {
    executorService.executeDelete(
        Instruction.TABLE_NAME, new Field<>(Long.class, Instruction.RID, rid));
  }
}
