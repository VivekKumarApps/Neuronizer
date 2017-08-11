package de.djuelg.neuronizer.presentation.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.fernandocejas.arrow.optional.Optional;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.djuelg.neuronizer.R;
import de.djuelg.neuronizer.domain.executor.impl.ThreadExecutor;
import de.djuelg.neuronizer.presentation.presenters.AddHeaderPresenter;
import de.djuelg.neuronizer.presentation.presenters.DisplayTodoListPresenter;
import de.djuelg.neuronizer.presentation.presenters.impl.DisplayTodoListPresenterImpl;
import de.djuelg.neuronizer.presentation.ui.custom.FlexibleRecyclerView;
import de.djuelg.neuronizer.presentation.ui.custom.FragmentInteractionListener;
import de.djuelg.neuronizer.presentation.ui.dialog.Dialogs;
import de.djuelg.neuronizer.presentation.ui.flexibleadapter.TodoListItemViewModel;
import de.djuelg.neuronizer.storage.TodoListRepositoryImpl;
import de.djuelg.neuronizer.threading.MainThreadImpl;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;

import static de.djuelg.neuronizer.presentation.ui.Constants.KEY_TITLE;
import static de.djuelg.neuronizer.presentation.ui.Constants.KEY_UUID;
import static de.djuelg.neuronizer.presentation.ui.custom.Animations.fadeIn;
import static de.djuelg.neuronizer.presentation.ui.custom.Animations.fadeOut;
import static de.djuelg.neuronizer.presentation.ui.custom.AppbarTitle.changeAppbarTitle;

/**
 * Activities that contain this fragment must implement the
 * {@link FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoListFragment extends Fragment implements View.OnClickListener, DisplayTodoListPresenter.View, AddHeaderPresenter.View,
        FlexibleAdapter.OnItemSwipeListener {

    private static final int SWIPE_LEFT_TO_EDIT = 4;
    private static final int SWIPE_RIGHT_TO_DELETE = 8;

    @Bind(R.id.fab_add_header) FloatingActionButton mFabHeader;
    @Bind(R.id.fab_menu) FloatingActionMenu mFabMenu;
    @Bind(R.id.fab_menu_header) FloatingActionButton mFabHeaderMenu;
    @Bind(R.id.fab_menu_item) FloatingActionButton mFabItemMenu;
    @Bind(R.id.todo_list_recycler_view) FlexibleRecyclerView mRecyclerView;
    @Bind(R.id.todo_list_empty_recycler_view) RelativeLayout mEmptyView;

    private DisplayTodoListPresenter mPresenter;
    private FragmentInteractionListener mListener;
    private FlexibleAdapter<AbstractFlexibleItem> mAdapter;
    private String uuid;
    private String title;

    public TodoListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static TodoListFragment newInstance(String uuid, String title) {
        TodoListFragment fragment = new TodoListFragment();
        Bundle args = new Bundle();
        args.putString(KEY_UUID, uuid);
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            uuid = bundle.getString(KEY_UUID);
            title = bundle.getString(KEY_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_todo_list, container, false);

        ButterKnife.bind(this, view);
        mFabHeader.setHideAnimation(fadeOut());
        mFabHeader.setShowAnimation(fadeIn());
        mFabMenu.setMenuButtonHideAnimation(fadeOut());
        mFabMenu.setMenuButtonShowAnimation(fadeIn());
        mFabHeader.setOnClickListener(this);
        mFabHeaderMenu.setOnClickListener(this);
        mFabItemMenu.setOnClickListener(this);
        changeAppbarTitle(getActivity(), title);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // create a presenter for this view
        mPresenter = new DisplayTodoListPresenterImpl(
                ThreadExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                this,
                new TodoListRepositoryImpl()
        );

        // let's load list when the app resumes
        mPresenter.loadTodoList(uuid);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.syncTodoList(mAdapter.getHeaderItems());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTodoListLoaded(List<AbstractFlexibleItem> items) {
        mAdapter = new FlexibleAdapter<>(items);
        mRecyclerView.setupRecyclerView(mEmptyView, mAdapter, mFabMenu);
        mRecyclerView.setupFlexibleAdapter(this, mAdapter);
    }

    @Override
    public void onTodoListReloaded(List<AbstractFlexibleItem> items) {
        mAdapter.updateDataSet(items, true);
    }

    @Override
    public void onClick(View view) {
        // Currently there is only FAB
        switch (view.getId()) {
            case R.id.fab_add_header:
            case R.id.fab_menu_header:
                Dialogs.showAddHeaderDialog(this, uuid);
                break;
            case R.id.fab_menu_item:
                mListener.onAddItem(uuid);
                break;
            default:
                break;
        }
    }

    @Override
    public void onHeaderAdded() {
        mPresenter.loadTodoList(uuid);
    }

    @Override
    public void onItemSwipe(int position, int direction) {
        switch (direction) {
            case SWIPE_LEFT_TO_EDIT:
                editItem(position);
                break;
            case SWIPE_RIGHT_TO_DELETE:
                break;
        }
    }

    private void editItem(int position) {
        Optional<TodoListItemViewModel> vm = Optional.fromNullable((TodoListItemViewModel) mAdapter.getItem(position));
        if (vm.isPresent()) mListener.onEditItem(uuid, vm.get().getItem().getUuid());
    }

    @Override
    public void onActionStateChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        // Nothing to do
    }
}
