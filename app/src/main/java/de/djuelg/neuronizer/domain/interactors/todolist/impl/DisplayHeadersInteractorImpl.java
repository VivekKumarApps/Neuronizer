package de.djuelg.neuronizer.domain.interactors.todolist.impl;

import com.fernandocejas.arrow.collections.Lists;

import de.djuelg.neuronizer.domain.executor.Executor;
import de.djuelg.neuronizer.domain.executor.MainThread;
import de.djuelg.neuronizer.domain.interactors.base.AbstractInteractor;
import de.djuelg.neuronizer.domain.interactors.todolist.DisplayHeadersInteractor;
import de.djuelg.neuronizer.domain.model.todolist.TodoListHeader;
import de.djuelg.neuronizer.domain.repository.Repository;

/**
 * Created by djuelg on 09.07.17.
 */
public class DisplayHeadersInteractorImpl extends AbstractInteractor implements DisplayHeadersInteractor {

    private final Callback callback;
    private final Repository repository;
    private final String todoListUuid;

    public DisplayHeadersInteractorImpl(Executor threadExecutor, MainThread mainThread,
                                        Callback callback, Repository repository, String todoListUuid) {
        super(threadExecutor, mainThread);
        this.callback = callback;
        this.repository = repository;
        this.todoListUuid = todoListUuid;
    }

    private void postHeader(final Iterable<TodoListHeader> headers) {
        mMainThread.post(new Runnable() {
            @Override
            public void run() {
                callback.onHeadersRetrieved(Lists.newArrayList(headers));
            }
        });
    }

    @Override
    public void run() {
        Iterable<TodoListHeader> headers = repository.todoList().getHeadersOfTodoListId(todoListUuid);
        postHeader(headers);
    }
}
