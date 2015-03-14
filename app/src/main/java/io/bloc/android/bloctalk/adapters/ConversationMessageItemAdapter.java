package io.bloc.android.bloctalk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.api.DataSource;
import io.bloc.android.bloctalk.api.model.MessageItem;

/**
 * Created by Mark on 3/10/2015.
 */
public class ConversationMessageItemAdapter extends RecyclerView.Adapter<ConversationMessageItemAdapter.ItemAdapterViewHolder> {
    @Override
    public ItemAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int index) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conversation_message_item, viewGroup, false);
        return new ItemAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ConversationMessageItemAdapter.ItemAdapterViewHolder holder, int index) {
        DataSource sharedDataSource = BlocTalkApplication.getSharedDataSource();
        holder.update(sharedDataSource.getMsgs().get(index));
        //holder.update();
    }

    @Override
    public int getItemCount() {
        return BlocTalkApplication.getSharedDataSource().getMsgs().size();
        //return 5;
    }

    class ItemAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView body;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            body = (TextView) itemView.findViewById(R.id.conversation_message_item_body);

        }

        void update(MessageItem messageItem){
            body.setText(messageItem.getBody());
            //body.setText("Hey!");
        }

        void update(){
            body.setText("Hey!");
        }

        @Override
        public void onClick(View v) {

        }
    }
}
