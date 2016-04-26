package es.nekosoft.amiathome.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import es.nekosoft.amiathome.R;
import es.nekosoft.amiathome.model.LogMH;


public class LogMHAdapter extends RecyclerView.Adapter<LogMHAdapter.ViewHolder> {

    private List<LogMH> logs;

    //Access to elments for each row
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvSubTitle;

        public ViewHolder(View v) {
            super(v);

            //Get RelativeLayout of the row
            ViewGroup layout = (ViewGroup) v;

            //Get elements
            ivIcon = (ImageView) layout.getChildAt(0);
            tvTitle = (TextView) layout.getChildAt(1);
            tvSubTitle = (TextView) layout.getChildAt(3);
        }
    }

    //Constructor
    public LogMHAdapter(List<LogMH> logs) {

        this.logs = logs;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LogMHAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //Put info row to the layout
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LogMH log = logs.get(position);
        holder.ivIcon.setImageResource(LogMH.getIdIcon(log.getType()));
        holder.tvTitle.setText(log.getTitle());

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = format.format(log.getDate());
        holder.tvSubTitle.setText(strDate);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        if(logs == null) return 0;
        return logs.size();
    }

    public void setAdapter(List<LogMH> logs){

        this.logs = logs;
    }

}