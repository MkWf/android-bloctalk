package io.bloc.android.bloctalk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.api.DataSource;
import io.bloc.android.bloctalk.api.model.ConversationItem;

/**
 * Created by Mark on 3/11/2015.
 */
public class ConversationNavigationAdapter extends RecyclerView.Adapter<ConversationNavigationAdapter.ViewHolder> {
    @Override
    public ConversationNavigationAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conversation_navigation_item, viewGroup, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ConversationNavigationAdapter.ViewHolder itemAdapterViewHolder, int index) {
        DataSource sharedDataSource = BlocTalkApplication.getSharedDataSource();
        itemAdapterViewHolder.update(sharedDataSource.getConvos().get(index));
    }

    @Override
    public int getItemCount() {
        return BlocTalkApplication.getSharedDataSource().getConvos().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        ConversationItem convoItem;

        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.conversation_navigation_item_name);

            itemView.setOnClickListener(this);
        }

        void update(ConversationItem conversationItem){
            convoItem = conversationItem;
            name.setText(conversationItem.getName());
        }

        @Override
        public void onClick(View v) {

        }
    }
}
