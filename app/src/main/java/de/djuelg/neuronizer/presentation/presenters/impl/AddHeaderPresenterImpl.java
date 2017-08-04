package de.djuelg.neuronizer.presentation.presenters.impl;

import de.djuelg.neuronizer.domain.executor.Executor;
import de.djuelg.neuronizer.domain.executor.MainThread;
import de.djuelg.neuronizer.domain.interactors.todolist.AddHeaderInteractor;
import de.djuelg.neuronizer.domain.interactors.todolist.impl.AddHeaderInteractorImpl;
import de.djuelg.neuronizer.domain.repository.TodoListRepository;
import de.djuelg.neuronizer.presentation.exception.ParentNotFoundException;
import de.djuelg.neuronizer.presentation.presenters.AddHeaderPresenter;
import de.djuelg.neuronizer.presentation.presenters.base.AbstractPresenter;

/**
 * Created by djuelg on 16.07.17.
 */

public class AddHeaderPresenterImpl extends AbstractPresenter implements AddHeaderPresenter, AddHeaderInteractor.Callback {

    private View mView;
    private TodoListRepository mTodoListRepository;

    public AddHeaderPresenterImpl(Executor executor, MainThread mainThread,
                                  View view, TodoListRepository todoListRepository) {
        super(executor, mainThread);
        mView = view;
        mTodoListRepository = todoListRepository;
    }

    @Override
    public void resume() {
        // Nothing to do
    }

    @Override
    public void pause() {
        // Nothing to do
    }

    @Override
    public void stop() {
        // Nothing to do
    }

    @Override
    public void destroy() {
        // Nothing to do
    }

    @Override
    public void onHeaderAdded() {
        mView.headerAdded();
    }

    @Override
    public void onParentNotFound() {
        throw new ParentNotFoundException("Cannot add header without parent");
    }

    @Override
    public void addHeader(String title, String parentTodoListUuid) {
        // initialize the interactor
        AddHeaderInteractor interactor = new AddHeaderInteractorImpl(
                mExecutor,
                mMainThread,
                this,
                mTodoListRepository,
                title,
                0,
                parentTodoListUuid
        );

        // run the interactor
        interactor.execute();
    }
}