package com.tests.apostol.conquest.pages.deckbuilder;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.tests.apostol.conquest.Card;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.databases.ConquestDbHelper;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.pages.armies.ArmiesPage;
import com.tests.apostol.conquest.pages.draft.DraftPage;

public class DetailsPage extends Fragment {
    private EditText _name;
    private EditText _desc;
    private TextInputLayout _nameroot;
    private TextInputLayout _descroot;
    private ImageView _image;
    private DatabaseInterface _db;
    private boolean _isEditing;
    private String _originalName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.draft_page_det, container, false);
        setRetainInstance(true);

        _db = new DatabaseInterface(getContext(), new ConquestDbHelper(getContext()));
        Card warlord = _db.getCardsByClause(getArguments().getString(DraftPage.SELECTED_WARLORD)).get(0);

        _name = (EditText) v.findViewById(R.id.draft_page_det_name);
        _desc = (EditText) v.findViewById(R.id.draft_page_det_desc);
        _image = (ImageView) v.findViewById(R.id.draft_page_det_warlord);
        _image.setImageResource(warlord.getImageId());
        _nameroot = (TextInputLayout) v.findViewById(R.id.draft_page_det_nameroot);
        _descroot = (TextInputLayout) v.findViewById(R.id.draft_page_det_descroot);

        Bundle b = getArguments();
        _originalName = b.getString(ArmiesPage.DECK_NAME);
        String desc = b.getString(ArmiesPage.DECK_DESC);
        _isEditing = b.getBoolean(ArmiesPage.IS_EDITING);

        if (_originalName != null)
            _name.setText(_originalName);

        if (desc != null)
            _desc.setText(desc);

        _name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!_isEditing && !_name.getText().toString().equals(_originalName) && _db.checkDeckExists(_name.getText().toString())) {
                        _nameroot.setError("Deck name already exists. This deck won't be saved.");
                    } else if (_name.getText().toString().length() == 0) {
                        _nameroot.setError("Must set deck name. This deck won't be saved.");
                    } else {
                        _nameroot.setError("");
                    }
                }
            }
        });

        return v;
    }

    public String getName() { return _name.getText().toString(); }
    public String getDescription() { return _desc.getText().toString(); }
}