package com.notaryapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.model.Faq;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.MyView>{

    private List<Faq> list;
    private Activity mActivity;

    static class MyView extends RecyclerView.ViewHolder {

        private TextView question,answer;
        private ConstraintLayout qnView;
        private ImageView questionPlay;


        private MyView(View view) {
            super(view);
            question = view.findViewById(R.id.question);
            answer = view.findViewById(R.id.answer);
            qnView = view.findViewById(R.id.qnView);
            questionPlay = view.findViewById(R.id.questionPlay);
        }

        void setQuestion(String title) {
            question.setText(title);

        }


        void setAnswer(String title) {
            answer.setText(title);
        }

        private void bind(Faq faq) {
            // Get the state
            boolean expanded = faq.isExpanded();
            // Set the visibility based on state
            answer.setVisibility(expanded ? View.GONE : View.VISIBLE);
        }
    }

    public FAQAdapter(List<Faq> horizontalList, Activity mActivity) {
        this.list = horizontalList;
        this.mActivity = mActivity;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        final Faq faqmodel = list.get(position);
        holder.setQuestion(faqmodel.getQuestions());
        holder.setAnswer(faqmodel.getAnswers());

        holder.qnView.setOnClickListener(v -> {
            // Get the current state of the item
            boolean expanded = faqmodel.isExpanded();

            holder.bind(faqmodel);
            // Change the state
            faqmodel.setExpanded(!expanded);
            if(expanded) {
                holder.questionPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_play));
            }else {
                holder.questionPlay.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.latest_arrow));
            }
            // Notify the adapter that item has changed
            notifyItemChanged(position);
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
