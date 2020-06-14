package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;

import java.util.List;

public interface TaskCommentService {
    List<TaskComment> findAllByTaskId(Long taskId);
}
