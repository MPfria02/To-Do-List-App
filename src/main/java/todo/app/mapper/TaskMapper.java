package todo.app.mapper;

import todo.app.logic.Task;
import todo.app.logic.TaskDTO;

public class TaskMapper {
	
    public static TaskDTO toDTO(Task task) {
        return new TaskDTO(task.getEntityId(), task.getTitle(), task.getDescription());
    }

    public static Task toEntity(TaskDTO taskDTO, Long userId) {
        Task task = new Task(taskDTO.getTitle(), taskDTO.getDescription());
        task.setUserId(userId);
        return task;
    }

}
