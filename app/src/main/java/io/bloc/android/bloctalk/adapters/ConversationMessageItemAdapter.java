package io.bloc.android.bloctalk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.api.DataSource;
import io.bloc.android.bloctalk.api.model.MessageItem;

import static java.text.DateFormat.SHORT;
import static java.text.DateFormat.getDateTimeInstance;

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
    }

    @Override
    public int getItemCount() {
        return BlocTalkApplication.getSharedDataSource().getMsgs().size();
    }

    class ItemAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView body;
        TextView timestamp;
        ImageView sender;
        MessageItem item;
        ProgressBar progressBar;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            body = (TextView) itemView.findViewById(R.id.conversation_message_item_body);
            sender = (ImageView) itemView.findViewById(R.id.conversation_message_item_sender_indicator);
            timestamp = (TextView) itemView.findViewById(R.id.conversation_message_item_sender_timestamp);
        }


        void update(MessageItem messageItem){
            item = messageItem;

            RelativeLayout.LayoutParams
                    lpBody = (RelativeLayout.LayoutParams)body.getLayoutParams();
            RelativeLayout.LayoutParams
                    lpSender = (RelativeLayout.LayoutParams)sender.getLayoutParams();
            RelativeLayout.LayoutParams
                    lpTime = (RelativeLayout.LayoutParams)timestamp.getLayoutParams();

            if(messageItem.getType() == MessageItem.OUTGOING_MSG){
                lpBody.addRule(RelativeLayout.LEFT_OF, sender.getId());

                lpSender.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                lpTime.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                sender.setImageResource(R.mipmap.conversation_message_item_outgoing);
            }else{
                lpBody.addRule(RelativeLayout.RIGHT_OF, sender.getId());

                lpSender.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                lpTime.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                if(messageItem.getRead() == 0){
                    sender.setImageResource(R.mipmap.conversation_message_item_incoming_unread);
                    //BlocTalkApplication.getSharedDataSource().updateMsgReadStatus(messageItem.getId());
                }
                else{
                    sender.setImageResource(R.mipmap.conversation_message_item_incoming);
                }
            }

            body.setText(messageItem.getBody());
            body.setLayoutParams(lpBody);

            body.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            sender.getLayoutParams().height = body.getMeasuredHeight() + 10;
            sender.setLayoutParams(lpSender);

            timestamp.setLayoutParams(lpTime);


            if(messageItem.getTime().equals("Sending...")){
                //Do nothing
            }else{
                DateFormat formatter = getDateTimeInstance(SHORT, SHORT);
                formatter.setLenient(false);

                Date date = new Date(Long.parseLong(messageItem.getTime()));
                formatter.format(date);

                timestamp.setText(date.toString());
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
