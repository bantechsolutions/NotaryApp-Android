package com.notaryapp.ui.activities.notaryapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.notaryapp.R;
import com.notaryapp.model.ReportItems;
import com.notaryapp.ui.activities.notaryapp.notaryappReportDetailsActivity;

import java.util.Date;
import java.util.List;

public class TSRecyclerViewAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private List<ReportItems> list;
    private Context mContext;
    private int itemCount;
    Date date;

    public TSRecyclerViewAdapter1(List<ReportItems> horizontalList, Context mContext){
        this.list = horizontalList;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ts_report_cancelled, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading_ts, parent, false);
            return new LoadingViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder){
            populateItemRows((ItemViewHolder) viewHolder, position);
        } else {
            showLoadingView((LoadingViewHolder) viewHolder, position);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    private void populateItemRows(final ItemViewHolder viewHolder, final int position) {
        final ReportItems reportItems = list.get(position);

        /*viewHolder.setFullname(reportItems.getUser_name());
        viewHolder.setAddressText(reportItems.getFull_address());
        viewHolder.setReport_id_ts(reportItems.getReport_id());
        String updated_Date = reportItems.getPlan_updated_on();
        //2021-02-26 20:07:02
        //31-Dec-1998 23:37:50
        //dd-MMM-yyyy HH:mm:ss
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        try {
            date = simpleDateFormat.parse(updated_Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Thu Dec 31 00:00:00 IST 1998
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        String cDate = dateFormat.format(date);

        String dayData = cDate.substring(0,2);
        String myData = cDate.substring(3,6) + ", "+ cDate.substring(7,11);


        viewHolder.setReportDay(dayData);
        viewHolder.setReportMonthYear(myData);*/
        viewHolder.setFullname(reportItems.getUser_name());
        viewHolder.setAddressText(reportItems.getFull_address());
        viewHolder.setReport_id_ts(reportItems.getReport_id());
        String updated_Date = reportItems.getPlan_updated_on();
        /*if (updated_Date.length()==11){
            updated_Date = "0"+updated_Date;
        }*/
        String dayData = updated_Date.substring(0,2);
        String myData = updated_Date.substring(3,12);
        viewHolder.setReportDay(dayData);
        viewHolder.setReportMonthYear(myData);

        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,notaryappReportDetailsActivity.class);
                intent.putExtra("ReportDetails", reportItems);
                intent.putExtra("ReportStatus", "Cancel");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        TextView fullname;
        TextView addressText;
        TextView report_id_ts;
        TextView reportDay;
        TextView reportMonthYear;
        public ItemViewHolder(View itemView){
            super(itemView);
            fullname = itemView.findViewById(R.id.fullname);
            addressText = itemView.findViewById(R.id.addressText);
            report_id_ts = itemView.findViewById(R.id.report_id_ts);
            constraintLayout = itemView.findViewById(R.id.conLayout);
            reportDay = itemView.findViewById(R.id.reportDay);
            reportMonthYear = itemView.findViewById(R.id.reportMonthYear);

            /*constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, notaryappReportDetailsActivity.class);
                    intent.putExtra("ReportDetails",(Serializable) list);
                }
            });*/
        }

        void setFullname(String title) {fullname.setText(title);}
        void setAddressText(String title) {addressText.setText(title);}
        void setReport_id_ts(String title) {report_id_ts.setText(title);}
        void setReportDay(String title){reportDay.setText(title);}
        void setReportMonthYear(String title){reportMonthYear.setText(title);}
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
    }
}
