package io.bloc.android.bloctalk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        TextView bodyIn;
        TextView timestampIn;
        ImageView senderIn;

        TextView bodyOut;
        TextView timestampOut;
        ImageView senderOut;

        RelativeLayout rlIn;
        RelativeLayout rlOut;

        MessageItem item;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            bodyIn = (TextView) itemView.findViewById(R.id.conversation_message_item_body_incoming);
            senderIn = (ImageView) itemView.findViewById(R.id.conversation_message_item_indicator_incoming);
            timestampIn = (TextView) itemView.findViewById(R.id.conversation_message_item_timestamp_incoming);

            bodyOut = (TextView) itemView.findViewById(R.id.conversation_message_item_body_outgoing);
            senderOut = (ImageView) itemView.findViewById(R.id.conversation_message_item_indicator_outgoing);
            timestampOut = (TextView) itemView.findViewById(R.id.conversation_message_item_timestamp_outgoing);

            rlIn = (RelativeLayout) itemView.findViewById(R.id.rl_conversation_msg_item_incoming);
            rlOut = (RelativeLayout) itemView.findViewById(R.id.rl_conversation_msg_item_outgoing);
        }



        void update(MessageItem messageItem){
            item = messageItem;

            if(messageItem.getType() == MessageItem.OUTGOING_MSG){
                rlIn.setVisibility(View.GONE);
                rlOut.setVisibility(View.VISIBLE);

                RelativeLayout.LayoutParams lpSenderOut = (RelativeLayout.LayoutParams)senderOut.getLayoutParams();

                senderOut.setImageResource(R.mipmap.conversation_message_item_outgoing);

                bodyOut.setText(messageItem.getBody());

                bodyOut.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                senderOut.getLayoutParams().height = bodyOut.getMeasuredHeight() + 10;
                senderOut.setLayoutParams(lpSenderOut);
            }else{
                rlIn.setVisibility(View.VISIBLE);
                rlOut.setVisibility(View.GONE);

                RelativeLayout.LayoutParams lpSenderIn = (RelativeLayout.LayoutParams)senderIn.getLayoutParams();

                bodyIn.setText(messageItem.getBody());

                bodyIn.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                senderIn.getLayoutParams().height = bodyIn.getMeasuredHeight() + 10;
                senderIn.setLayoutParams(lpSenderIn);

                if(messageItem.getRead() == 0){
                    senderIn.setImageResource(R.mipmap.conversation_message_item_incoming_unread);
                }
                else{
                    senderIn.setImageResource(R.mipmap.conversation_message_item_incoming);
                }
            }

            if(messageItem.getTime().equals("Sending...")){
                //Do nothing
            }else{
                DateFormat formatter = getDateTimeInstance(SHORT, SHORT);
                formatter.setLenient(false);

                Date date = new Date(Long.parseLong(messageItem.getTime()));
                formatter.format(date);

                timestampIn.setText(date.toString());
                timestampOut.setText(date.toString());
            }
        }

        @Override
        public void onClick(View v) {

        }
    }
}
