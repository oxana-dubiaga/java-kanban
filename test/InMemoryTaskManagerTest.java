import service.Managers;
import service.TaskManager;


public class InMemoryTaskManagerTest extends AbstractTaskManagerTest {

    @Override
    protected TaskManager createTaskManager() {
        return Managers.getDefault();
    }

}


