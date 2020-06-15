package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomTaskCommentService implements TaskCommentService {

    @Override
    public TaskComment findById(Long taskCommentId) {
        return null;
    }

    @Override
    public List<TaskComment> findAllByTaskId(Long taskId) {
        return null;
    }

    @Override
    public TaskComment createComment(TaskComment taskComment) {
        return null;
    }

    @Override
    public void delete(Long taskCommentId) {

    }
}
