package de.djuelg.neuronizer.presentation.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.djuelg.neuronizer.R;
import jp.wasabeef.richeditor.RichEditor;

import static de.djuelg.neuronizer.presentation.ui.Constants.EDITOR_ABOUT_CSS;
import static de.djuelg.neuronizer.presentation.ui.Constants.EDITOR_FONT_SIZE;
import static de.djuelg.neuronizer.presentation.ui.custom.view.AppbarCustomizer.changeAppbarTitle;

public class ImprintFragment extends Fragment {

    @BindView(R.id.richEditor_imprint) RichEditor richEditor;
    private Unbinder mUnbinder;

    public ImprintFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ImprintFragment newInstance() {
        return new ImprintFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_imprint, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        changeAppbarTitle(getActivity(), R.string.imprint);

        richEditor.setHtml(getString(R.string.imprint_html));
        richEditor.loadCSS(EDITOR_ABOUT_CSS);
        richEditor.setPadding(24, 0, 24, 0);
        richEditor.setEditorFontSize(EDITOR_FONT_SIZE);
        richEditor.setInputEnabled(false);
        richEditor.setFocusable(false);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
