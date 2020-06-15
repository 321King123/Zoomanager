package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;

import java.util.List;

public interface TaskCommentService {

    TaskComment findById(Long taskCommentId);

    List<TaskComment> findAllByTaskId(Long taskId);

    TaskComment createComment(TaskComment taskComment);

    void delete(Long taskCommentId);
}
