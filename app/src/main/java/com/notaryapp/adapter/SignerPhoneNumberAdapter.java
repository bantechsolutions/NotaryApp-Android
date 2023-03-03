package com.notaryapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.roomdb.entity.SignerReg;

import java.util.List;

public class SignerPhoneNumberAdapter extends RecyclerView.Adapter<SignerPhoneNumberAdapter.MyView>{

    private List<SignerReg> list;
    private Context context;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView itemTitle;


        private MyView(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.itemTitle);

        }

    }


    public SignerPhoneNumberAdapter(List<SignerReg> horizontalList, Context context) {
        this.list = horizontalList;
        this.context = context;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phoneno_list, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final SignerReg stepsModel = list.get(position);
        String number = stepsModel.getPhoneNo().replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
        holder.itemTitle.setText(number);

        holder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //  Toast.makeText(getActivity(), "phone clicked", Toast.LENGTH_SHORT).show();
                Uri u = Uri.parse("tel:" + stepsModel.getPhoneNo());

                // Create the intent and set the data for the
                // intent as the phone number.
                Intent i = new Intent(Intent.ACTION_DIAL, u);

                try
                {
                    // Launch the Phone app's dialer with a phone
                    // number to dial a call.
                    context.startActivity(i);
                }
                catch (SecurityException s)
                {
                    // show() method display the toast with
                    // exception message.
                    Toast.makeText(context, s.getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
