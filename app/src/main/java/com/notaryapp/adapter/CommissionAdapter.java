package com.notaryapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.notaryapp.R;
import com.notaryapp.model.CommissionItems;
import com.notaryapp.ui.activities.LicenseListActivity;
import com.notaryapp.ui.fragments.dashboard.DashBoard_AddLicenseFragment;
import com.notaryapp.utils.FragmentViewUtil;

import java.util.ArrayList;

public class CommissionAdapter extends ArrayAdapter<CommissionItems> {
    private ArrayList<CommissionItems> commissionItems = new ArrayList<>();
    String licenceNumber, state_name;

    public CommissionAdapter(Context context, int textViewResourceId, ArrayList<CommissionItems> data){
        super(context, textViewResourceId, data);
        commissionItems = data;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.items_commission_row_data, null);
        ConstraintLayout comm_item_cLayout = (ConstraintLayout)view.findViewById(R.id.comm_item_cLayout);
        TextView stateName = (TextView)view.findViewById(R.id.stateName);
        TextView LicenseNo = (TextView)view.findViewById(R.id.LicenseNo);
        ImageView editComm = (ImageView)view.findViewById(R.id.editComm);
        ImageView warningComm = (ImageView)view.findViewById(R.id.warningComm);
        stateName.setText(commissionItems.get(position).getCommission_state_name());
        LicenseNo.setText(commissionItems.get(position).getCommission_number());
        //licenceNumber= LicenseNo.getText().toString().trim();
        //state_name = stateName.getText().toString().trim();

        String commStatus = commissionItems.get(position).getCommission_status();
        if (commStatus.equalsIgnoreCase("warning")){
            warningComm.setVisibility(View.VISIBLE);
            try{
                stateName.setTextColor(getContext().getColor(R.color.errorStateLicense));
                LicenseNo.setTextColor(getContext().getColor(R.color.errorStateLicense));
            } catch (Exception e){
                //e.printStackTrace();
            }

        } else if (commStatus.equalsIgnoreCase("ok")){
            try{
                stateName.setTextColor(getContext().getColor(R.color.okState));
                LicenseNo.setTextColor(getContext().getColor(R.color.okState));
            } catch (Exception e){
                //e.printStackTrace();
            }
        } else if(commStatus.equalsIgnoreCase("missing")){
            warningComm.setVisibility(View.VISIBLE);
            try{
                stateName.setTextColor(getContext().getColor(R.color.errorStateLicense));
                LicenseNo.setTextColor(getContext().getColor(R.color.errorStateLicense));
            } catch (Exception e){
                //e.printStackTrace();
            }

        }else {
            warningComm.setVisibility(View.VISIBLE);
            try{
                stateName.setTextColor(getContext().getColor(R.color.errorStateLicense));
                LicenseNo.setTextColor(getContext().getColor(R.color.errorStateLicense));
            } catch (Exception e){
                //e.printStackTrace();
            }
        }

        comm_item_cLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), LicenseNo.getText().toString(), Toast.LENGTH_SHORT).show();
                loadFragment(new DashBoard_AddLicenseFragment(LicenseNo.getText().toString(), stateName.getText().toString()));
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentViewUtil.replaceFragment((FragmentActivity) getContext(), LicenseListActivity.REF_VIEW_CONTAINER, fragment, true);
    }
}
