package io.bloc.android.bloctalk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.api.DataSource;
import io.bloc.android.bloctalk.api.model.Conversation;

/**
 * Created by Mark on 3/8/2015.
 */
public class ConversationItemAdapter extends RecyclerView.Adapter<ConversationItemAdapter.ItemAdapterViewHolder> {
    @Override
    public ConversationItemAdapter.ItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conversation_item, viewGroup, false);
        return new ItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ConversationItemAdapter.ItemAdapterViewHolder itemAdapterViewHolder, int index) {
        DataSource sharedDataSource = BlocTalkApplication.getSharedDataSource();
        itemAdapterViewHolder.update(sharedDataSource.getConvos().get(index));
    }

    @Override
    public int getItemCount() {
        return BlocTalkApplication.getSharedDataSource().getConvos().size();
    }

    class ItemAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView name;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.conversation_item_name);
        }

        void update(Conversation conversationItem){
            name.setText(conversationItem.getName());
        }
    }
}
