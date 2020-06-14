package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.TaskComment;
import at.ac.tuwien.sepm.groupphase.backend.service.TaskCommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomTaskCommentService implements TaskCommentService {
    @Override
    public List<TaskComment> findAllByTaskId(Long taskId) {
        return null;
    }
}
