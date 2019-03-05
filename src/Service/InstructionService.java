package Service;

import Entity.Field;
import Entity.Instruction;
import Entity.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstructionService extends EntityService {
  private static InstructionService ourInstance = new InstructionService();

  public static InstructionService getInstance() {
    return ourInstance;
  }

  private InstructionService() {}

  public List<Instruction> searchByRecipe(Long rid) {
    Instruction temp = new Instruction(rid);

    List<List<Field>> fieldsExtracted =
        executorService.executeSelect(
            Instruction.TABLE_NAME, temp.getFields(), temp.getField("rid"));

    List<Instruction> instructions = new ArrayList<>();
    for (List<Field> fieldGroup : fieldsExtracted) {
      temp = new Instruction(rid);
      Field.applyTo(fieldGroup, temp.getFields(), true);
      temp.setStatus(Status.SYNCED);
      instructions.add(temp);
    }
    instructions.sort((i1, i2) -> (Integer) i1.getValue("step") - (Integer) i2.getValue("step"));
    return instructions;
  }
}
