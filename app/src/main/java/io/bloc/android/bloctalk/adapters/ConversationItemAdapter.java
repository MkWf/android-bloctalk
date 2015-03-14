package io.bloc.android.bloctalk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.bloc.android.bloctalk.BlocTalkApplication;
import io.bloc.android.bloctalk.R;
import io.bloc.android.bloctalk.api.DataSource;
import io.bloc.android.bloctalk.api.model.ConversationItem;

/**
 * Created by Mark on 3/8/2015.
 */
public class ConversationItemAdapter extends RecyclerView.Adapter<ConversationItemAdapter.ItemAdapterViewHolder> {

    public static interface Delegate {
        public void onItemClicked(ConversationItemAdapter itemAdapter, ConversationItem convoItem);
    }

    private WeakReference<Delegate> delegate;

    public Delegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }
    public void setDelegate(Delegate delegate) {
        this.delegate = new WeakReference<Delegate>(delegate);
    }

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

    class ItemAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        ImageView photo;
        TextView unreadMgs;
        ConversationItem convoItem;

        public ItemAdapterViewHolder(View itemView) {
            super(itemView);

            name = (TextView)itemView.findViewById(R.id.conversation_item_name);
            photo = (ImageView) itemView.findViewById(R.id.conversation_item_image);
            unreadMgs = (TextView) itemView.findViewById(R.id.conversation_item_unread_msgs);

            itemView.setOnClickListener(this);
        }

        void update(ConversationItem conversationItem){
            convoItem = conversationItem;
            name.setText(conversationItem.getName());
            unreadMgs.setText(Integer.toString(conversationItem.getUnreadMsgCount()));


            if(conversationItem.getPhoto() != null){
                photo.setImageURI(conversationItem.getPhoto());
            }else{
                photo.setImageResource(R.mipmap.conversation_item_user);
            }
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                if (getDelegate() != null) {
                    getDelegate().onItemClicked(ConversationItemAdapter.this, convoItem);
                }
            }
        }
    }
}
