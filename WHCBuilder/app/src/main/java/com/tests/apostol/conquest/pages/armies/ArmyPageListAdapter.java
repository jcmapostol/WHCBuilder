package com.tests.apostol.conquest.pages.armies;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tests.apostol.conquest.ExpandableAdapter;
import com.tests.apostol.conquest.activities.DraftActivity;
import com.tests.apostol.conquest.databases.DatabaseInterface;
import com.tests.apostol.conquest.ParentObject;
import com.tests.apostol.conquest.R;
import com.tests.apostol.conquest.pages.draft.DraftPage;

import java.util.List;

public class ArmyPageListAdapter extends ExpandableAdapter {
    private DatabaseInterface _db;

    public ArmyPageListAdapter(List<ParentObject> parentList, DatabaseInterface db) {
        super(parentList);
        _db = db;
    }

    @Override
    public RecyclerView.ViewHolder onCreateParentViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_arm, parent, false);
        RecyclerView.ViewHolder vh = new ArmiesParentViewHolder(v);
        return vh;
    }

    @Override
    public RecyclerView.ViewHolder onCreateChildViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.subitem_desc, parent, false);
        RecyclerView.ViewHolder vh = new ArmiesChildViewHolder(v);
        return vh;
    }

    @Override
    public void onBindParent(RecyclerView.ViewHolder holder, int position, Object data) {
        final ParentObject po = (ParentObject) data;
        final ArmiesParentViewHolder deck = (ArmiesParentViewHolder) holder;
        final ArmyVhData wrapper = (ArmyVhData) data;
        deck.name.setText(wrapper.getName());
        deck.name.setTextColor(wrapper.getWarlord().getFaction().getTextColor());
        deck.root.setBackgroundColor(wrapper.getWarlord().getFaction().getColor());
        deck.icon.setImageResource(wrapper.getWarlord().getFaction().getIconId());
        deck.warlordName.setText(wrapper.getWarlord().getName());
        deck.warlordName.setTextColor(wrapper.getWarlord().getFaction().getTextColor());

        deck.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _db.deleteDeck(deck.name.getText().toString());
                removeParent(po);
                Toast.makeText(v.getContext(), "Deck deleted.", Toast.LENGTH_SHORT).show();
            }
        });
        deck.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), DraftActivity.class);

                i.putExtra(DraftPage.SELECTED_WARLORD, wrapper.getWarlord().getName());
                i.putExtra(ArmiesPage.DECK_NAME, wrapper.getName().toString());
                i.putExtra(ArmiesPage.DECK_DESC, wrapper.getDescription());
                i.putExtra(ArmiesPage.IS_EDITING, true);
                v.getContext().startActivity(i);
            }
        });
        deck.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasChildren(po)) {
                    removeChildren(po);
                } else {
                    addChildren(po);
                }
            }
        });
    }

    @Override
    public void onBindChild(RecyclerView.ViewHolder holder, int position, Object data) {
        final ArmiesChildViewHolder desc = (ArmiesChildViewHolder) holder;
        desc.text.setText(data.toString());
    }
}
